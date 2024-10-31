package pw.rxj.iron_quarry.types;

public enum DualPosition {
        FIRST(0, "FirstPosition"),
        SECOND(1, "SecondPosition");

        private final int id;
        private final String nbtLiteral;

        private DualPosition(int id, String nbtLiteral) {
            this.id = id;
            this.nbtLiteral = nbtLiteral;
        }

        public int getId() {
            return id;
        }
        public String getNbtLiteral() {
            return nbtLiteral;
        }
}
