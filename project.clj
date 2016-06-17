(defproject tzaar "0.1.0-SNAPSHOT"
  :description "Clojure implementation of the abstract strategy game Tzaar by Kris Burm"
  :url "https://github.com/fversnel/tzaar"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha6"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/core.async "0.2.382"]
                 [cheshire "5.6.1"]
                 [camel-snake-kebab "0.4.0"]
                 [com.google.code.gson/gson "2.7"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :prep-tasks [["compile" "tzaar.player"]
               "javac" "compile"]
  ;:profiles {:precomp {:source-paths ["src/clojure"]
  ;                     :aot [tzaar.jsonapi tzaar.player]}}
  )
