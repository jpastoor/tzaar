(ns tzaar.core
  (require [tzaar.parser :as parser]
           [clojure.string :as string]))

(def empty-board (parser/read-board "empty-board"))
(def default-board (parser/read-board "default-board"))

(def move-types #{:attack :stack :pass})
(defn attack-move? [move] (= :attack (:move-type move)))
(defn stack-move? [move] (= :stack (:move-type move)))
(def pass-move {:move-type :pass})
(defn pass-move? [move] (= move pass-move))

(def stack-types #{:tzaar :tzarra :tott})
(defn single-stack [color type] [[color type]])
(defn top-piece [stack] (first stack))
(defn stack-color [stack] (first (top-piece stack)))
(defn stack-color? [color stack] (= color (stack-color stack)))
(defn stack-type [stack] (second (top-piece stack)))
(defn stack-size [stack] (count stack))
(defn stack? [slot] (sequential? slot))

(defn update-position [board [x y] new-slot]
  (assoc-in board [y x] new-slot))

(defn lookup-slot [board [x y]]
  (or (get-in board [y x]) :nothing))

(defn iterate-slots [board]
  (for [y (range 0 (count board))
        x (range 0 (count (nth board y)))]
    {:position [x y]
     :slot (lookup-slot board [x y])}))

(defn iterate-stacks [color board]
  (->> board
       iterate-slots
       (filter #(and (stack? (:slot %))
                     (stack-color? color (:slot %))))))

(defn apply-move
  [board {:keys [from to move-type] :as move}]
  (let [from-stack (lookup-slot board from)
        to-stack (lookup-slot board to)
        new-stack (case move-type
                    :attack from-stack
                    :stack (concat from-stack to-stack))]
    (-> board
        (update-position from :empty)
        (update-position to new-stack))))

(defn stack-type-missing?
  [board color]
  (let [stacks (->> board
                    (iterate-stacks color)
                    (map :slot)
                    (map stack-type))]
    (not= stack-types (set stacks))))

(defn neighbors [board position]
  (letfn [(neighbor [{:keys [xfn yfn]}]
            (let [positions (iterate (fn [[x y]] [(xfn x) (yfn y)])
                                     position)]
              (->> positions
                   (remove #(= position %))
                   (map #(assoc {} :slot (lookup-slot board %)
                                   :position %))
                   (remove #(= :empty (:slot %)))
                   first)))]
    [; Horizontal
      (neighbor {:xfn inc :yfn identity})
      (neighbor {:xfn dec :yfn identity})

      ; Vertical
      (neighbor {:xfn identity :yfn inc})
      (neighbor {:xfn identity :yfn dec})

      ; Diagonal
      (neighbor {:xfn dec :yfn dec})
      (neighbor {:xfn inc :yfn inc})]))

(defn moves [board position]
  (if (stack? (lookup-slot board position))
    (let [stack (lookup-slot board position)
          color (when (stack? stack) (stack-color stack))]
      (->> (neighbors board position)
           (remove #(= :nothing (:slot %)))
           (map #(assoc {}
                  :from position
                  :to (:position %)
                  :move-type (if (stack-color? color (:slot %))
                                :stack
                                :attack)))
           ; Remove stacks that cannot be attacked
           (remove (fn [move]
                     (and (attack-move? move)
                          (let [enemy-stack (lookup-slot board (:to move))]
                            (< (stack-size stack) (stack-size enemy-stack))))))
           ; Remove moves that would kill yourself
           (remove (fn [move]
                     (and (stack-move? move)
                          (stack-type-missing?
                            (apply-move board move)
                            color))))))
      []))

(defn attack-moves [board position]
  (-> (moves board position)
      (filter attack-move?)))

(defn all-moves [board color]
  (->> board
       (iterate-stacks color)
       (map :position)
       (mapcat #(moves board %))))

; Optionally add under which condition the player has lost
(defn lost?
  [board color]
  (let [moves (all-moves board color)
        attack-moves (filter attack-move? moves)]
    (or (stack-type-missing? board color)
        (empty? attack-moves))))

(defn random-board []
  (let [color-stacks (fn [color] (map #(single-stack color %)
                                      (concat (repeat 6 :tzaar)
                                              (repeat 9 :tzarra)
                                              (repeat 15 :tott))))
        shuffled-stacks (shuffle (concat (color-stacks :white)
                                         (color-stacks :black)))
        empty-positions (->> empty-board
                             iterate-slots
                             (filter #(= :empty (:slot %)))
                             (map :position))]
    (loop [board empty-board
           stacks shuffled-stacks
           empty-positions empty-positions]
      (if (empty? stacks)
        board
        (recur (update-position board
                                (first empty-positions)
                                (first stacks))
               (rest stacks)
               (rest empty-positions))))))

(defn board-to-str [board]
  (letfn [(stack-to-str [stack]
            (str (if (< 1 (stack-size stack))
                   (stack-size stack)
                   \space)
                 (case (top-piece stack)
                       [:white :tott] "w1"
                       [:white :tzarra] "w2"
                       [:white :tzaar] "w3"
                       [:black :tott] "b1"
                       [:black :tzarra] "b2"
                       [:black :tzaar] "b3")))
          (slot-to-str [slot]
            (cond
              (stack? slot) (stack-to-str slot)
              (= :empty slot) " e "
              (= :nothing slot) " n "))]
    (let [row-strs (for [row board]
                     (->>
                       row
                       (map slot-to-str)
                       (string/join \space)))]
      (string/join \newline row-strs))))