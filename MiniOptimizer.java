import java.util.List;

class MiniOptimizer {

    static void mini_optimize(List<Quad> quads) {
        // They will be implemented fully in another lifetime

        //quadPrinter(quads);

        condition_jump(quads);

        boolean flag;
        do {
            flag = simple_constant_propagation(quads);
            flag |= simple_copy_propagation(quads);
            flag |= constant_folding(quads);
        } while (flag);

        //quadPrinter(quads);
    }

    private static boolean constant_folding(List<Quad> parquads) {
        boolean flag = false;
        for (Quad quad : parquads) {
            switch (quad.opcode) {
                case "+":
                case "-":
                case "*":
                case "/":
                case "%":
                    Integer a = isInteger(quad.op1);
                    Integer b = isInteger(quad.op2);
                    if (a != null && b != null) {
                        quad.op1 = calculate(a, quad.opcode, b);
                        quad.opcode = "<-";
                        quad.op2 = "_";
                        flag = true;
                    }
                    break;
            }
        }
        return flag;
    }

    private static boolean simple_constant_propagation(List<Quad> quads) {
        boolean flag = false;
        for (Quad quad : quads) {
            if (quad.opcode.equals("<-")) {
                if (pointerQuad(quad.dest, quads)) continue;
                if ((isInteger(quad.op1) != null || quad.op1.contains("\'")) // op1 is constant if integer or char
                        && assignOnlyValToDest(quad.op1, quad.dest, quads)) {
                    replaceAll(quad.dest, quad.op1, quads);
                    flag = true;
                }
            }
        }
        return flag;
    }

    private static boolean simple_copy_propagation(List<Quad> quads) {
        boolean flag = false;
        for (Quad quad : quads) {
            if (quad.opcode.equals("<-")) {
                if (pointerQuad(quad.dest, quads)) continue;
                if (!assignmentOnDest(quad.op1, quads) && assignOnlyValToDest(quad.op1, quad.dest, quads)) {
                    replaceAll(quad.dest, quad.op1, quads);
                    flag = true;
                }
            }
        }
        return flag;
    }

    private static boolean pointerQuad(String dest, List<Quad> quads) {
        if (dest.contains("*")) return true;
        for (Quad quad : quads) {
            if (quad.dest == null || quad.op1 == null || quad.op2 == null) continue;
            if (quad.dest.contains(dest + "*"))
                return true;
            if (quad.op1.contains(dest + "*"))
                return true;
            if (quad.op2.contains(dest + "*"))
                return true;
        }
        return false;
    }

    private static void condition_jump(List<Quad> quads) {
        for (int i = 0; i < quads.size() - 1; i++) {
            Quad quad = quads.get(i);
            Quad next = quads.get(i + 1);
            boolean flag = true;
            switch (quad.opcode) {
                case ">":
                    quad.opcode = "<=";
                    break;
                case "<=":
                    quad.opcode = ">";
                    break;
                case "<":
                    quad.opcode = ">=";
                    break;
                case ">=":
                    quad.opcode = "<";
                    break;
                case "=":
                    quad.opcode = "#";
                    break;
                case "#":
                    quad.opcode = "=";
                    break;
                default:
                    flag = false;
                    break;
            }
            if (flag) {
                quad.dest = next.dest;
                next.opcode = "nop";
            }
        }
    }

    private static void replaceAll(String dest, String val, List<Quad> quads) {
        for (Quad quad : quads) {
            if (quad.opcode.equals("<-") && quad.dest.equals(dest)) {
                quad.opcode = "nop";
            } else {
                if (quad.op1.equals(dest))
                    quad.op1 = val;
                if (quad.op2.equals(dest))
                    quad.op2 = val;
            }
        }
    }

    private static boolean assignmentOnDest(String dest, List<Quad> quads) {
        for (Quad quad : quads) {
            if (quad.opcode.equals("<-") && quad.dest.equals(dest)) {
                return false;
            }
        }
        return false;
    }

    private static boolean assignOnlyValToDest(String val, String dest, List<Quad> quads) {
        for (Quad quad : quads) {
            if (quad.opcode.equals("<-") && quad.dest.equals(dest) && !quad.op1.equals(val)) {
                return false;
            }
        }
        return true;
    }

    private static Integer isInteger(String str) {
        Integer num;
        try {
            num = Integer.parseInt(str);
            return num;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String calculate(Integer a, String opcode, Integer b) {
        switch (opcode) {
            case "+":
                return String.valueOf(a + b);
            case "-":
                return String.valueOf(a - b);
            case "*":
                return String.valueOf(a * b);
            case "/":
                return String.valueOf(a / b);
            case "%":
                return String.valueOf(a % b);
            default:
                return null;
        }
    }

    private static void quadPrinter(List<Quad> quads) {
        System.out.println("---Quads---");
        for (Quad quad : quads) {
            quad.print();
        }
    }
}
