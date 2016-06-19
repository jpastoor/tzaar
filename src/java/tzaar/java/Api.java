package tzaar.java;

import tzaar.java.util.ClojureNamespace;

public class Api {
    private Api() { }

    public static final Board DEFAULT_BOARD = Board.standard();

    public static Board randomBoard() {
        return Board.random();
    }

    public static final tzaar.player.Player COMMAND_LINE_PLAYER =
            (tzaar.player.Player) ClojureNamespaces.JAVA_API.deref("command-line-player");
    public static final tzaar.player.Player RANDOM_BUT_LEGAL_PLAYER =
            (tzaar.player.Player) ClojureNamespaces.JAVA_API.deref("random-but-legal-ai");

    public static void playGame(tzaar.player.Player whitePlayer,
                                tzaar.player.Player blackPlayer,
                                Board board) {
        ClojureNamespaces.COMMAND_LINE.function("command-line-game")
                .invoke(whitePlayer, blackPlayer, board);
    }
}
