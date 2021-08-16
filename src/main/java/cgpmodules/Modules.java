package cgpmodules;

public class Modules {
    private Modules() {
    }

//    public static class LineOutModule implements Module {
//        private final LineOut lineOut;
//
//        public LineOutModule(final LineOut lineOut) {
//            this.lineOut = lineOut;
//        }
//
//        public LineOutModule() {
//            this(new LineOut());
//        }
//
//        @Override
//        public int getInputsNumber() {
//            return 1;
//        }
//
//        @Override
//        public UnitOutputPort getOutput() {
//            throw new UnsupportedOperationException("LineOut has no output");
//        }
//
//        @Override
//        public UnitInputPort getInput(int i) {
//            if (i == 0) {
//                return lineOut.input;
//            } else {
//                throw invalidArgsNumberError(this, "LineOut");
//            }
//        }
//
//        @Override
//        public UnitGenerator getUnitGenerator() {
//            return lineOut;
//        }
//    }


    public static IllegalArgumentException invalidArgsNumberError(final Module module, final String name) {
        return new IllegalArgumentException(name + " has only " + module.getInputsNumber() + " inputs");
    }
}
