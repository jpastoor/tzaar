package tzaar.java;

public abstract class Move {

    private Move() {}

    public boolean isPass() {
        return this == Pass;
    }
    public abstract boolean isAttack();
    public abstract boolean isStack();

    public static class Attack extends Move {
        public Position from;
        public Position to;

        public Attack(final Position from, final Position to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public boolean isStack() {
            return false;
        }

        @Override
        public String toString() {
            return String.format("[Attack from: %s, to: %s]", from, to);
        }
    }

    public static class Stack extends Move {
        public Position from;
        public Position to;

        public Stack(final Position from, final Position to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean isAttack() {
            return false;
        }

        @Override
        public boolean isStack() {
            return true;
        }

        @Override
        public String toString() {
            return String.format("[Stack from: %s, to: %s]", from, to);
        }
    }

    public static final Move Pass = new Move() {

        @Override
        public boolean isAttack() {
            return false;
        }

        @Override
        public boolean isStack() {
            return false;
        }

        @Override
        public String toString() {
            return "Pass";
        }
    };
}
