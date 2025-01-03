import java.util.Map;

public class Parser {
    // Had class kaydir parsing dyal tokens, katban w kat-handle expressions w variables
    private TokenManager tm; // TokenManager li kay manage tokens
    private String tc; // Current token
    private Map<String, Double> variables; // HashMap li kaykhzn variables w values dyalhom

    // Constructor: tm hwa TokenManager, variables hya map li katkhzn l variables
    public Parser(TokenManager tm, Map<String, Double> variables) {
        this.tm = tm;
        this.variables = variables;
        avancer(); // Kaydi current token
    }

    // Method bach tmshi l token li baad mn current
    private void avancer() {
        tc = tm.suivant(); // Update current token
    }

    // Method li tconsommer token, ila khsat tkhdm exception
    private void consommer(String attendu) {
        if (tc.equals(attendu)) { // Ila current token hwa attendu
            avancer(); // Dir avancer
        } else {
            throw new RuntimeException("Erreur: attendu '" + attendu + "' mais trouvé '" + tc + "'");
        }
    }

    // Main method li kat-parsi expression kaml
    public void parse() {
        try {
            // Hna kaycheck ila l token print, kaandiro traitement dyalo
            if (tc.equals("print")) {
                Print(); // Kat-handle print
            } else {
                Variables(); // Kat-handle l variables w l expressions
            }

            // Check ila baqi tokens mamzianash
            if (!tc.equals("#")) {
                throw new RuntimeException("Erreur: caractères restants après l'analyse");
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    // Method bach handle variables ou expressions
    private void Variables() {
        if (tc.equals("print")) { // Handle print command
            consommer("print");
            consommer("(");
            double result = Expression(); // Hitach print katkhsm expression
            consommer(")");
            System.out.println("Result: " + result); // T-print result
        } else if (isVariable(tc)) { // Check ila l token variable
            String varName = tc; // Smya dyal variable
            System.out.println("variable: " + varName);
            consommer(tc); // T-consomme l variable
            if (tc.equals(":=")) { // Check ":="
                consommer(":="); // Tconsommer ':='
                double value = Calculat(); // Tcalculi value dyal expression
                variables.put(varName, value); // Tstori variable f map
            } else {
                throw new RuntimeException("Erreur: attendu ':=', mais trouvé '" + tc + "'");
            }
        } else {
            Calculat(); // Ila ma kanch variable kay calculate expression
        }
    }

    // Traitement dyal command print
    public void Print() {
        consommer("print");
        consommer("(");
        double result = Calculat(); // Tcalculi expression li dakhl print
        consommer(")");
        System.out.println("Result: " + result);
    }

    // Calculation dyal expression kaml
    private double Calculat() {
        return Expression();
    }

    // Hna katdir handle + w -
    private double Expression() {
        double result = Term();
        while (tc.equals("+") || tc.equals("-")) {
            String op = tc;
            consommer(op);
            double value = Term();
            if (op.equals("+")) {
                result += value;
            } else if (op.equals("-")) {
                result -= value;
            }
        }
        return result;
    }

    // Hna katdir handle * w /
    private double Term() {
        double result = Factor();
        while (tc.equals("*") || tc.equals("/")) {
            String op = tc;
            consommer(op);
            double value = Factor();
            if (op.equals("*")) {
                result *= value;
            } else if (op.equals("/")) {
                result /= value;
            }
        }
        return result;
    }

    // Handle dyal "^" (power)
    private double Factor() {
        double result = Base();
        if (tc.equals("^")) {
            consommer("^");
            result = Math.pow(result, Factor());
        }
        return result;
    }

    // Hna kay handle numbers, parentheses, variables, ou print expressions
    private double Base() {
        if (tc.equals("print")) {
            consommer("print");
            consommer("(");
            double result = Expression();
            consommer(")");
            System.out.println("Result: " + result);
            return result;
        } else if (tc.equals("(")) {
            consommer("(");
            double result = Expression();
            consommer(")");
            return result;
        } else if (isNumber(tc)) {
            return Number();
        } else if (isVariable(tc)) {
            String varName = tc;
            consommer(tc);
            if (!variables.containsKey(varName)) {
                return 0.0; // Valeur par défaut hiya 0  
            }
            return variables.get(varName);
        } else {
            throw new RuntimeException("Erreur: attendu un nombre, '(', ou 'print', mais trouvé '" + tc + "'");
        }
    }

    // Handle numbers
    private double Number() {
        if (isNumber(tc)) {
            String num = tc;
            consommer(tc);
            return Double.parseDouble(num);
        } else {
            throw new RuntimeException("Erreur: attendu un nombre, mais trouvé '" + tc + "'");
        }
    }

    // Helper: Check ila token number
    private boolean isNumber(String token) {
        return token.matches("[0-9]+(\\.[0-9]+)?");
    }

    // Helper: Check ila token variable
    private boolean isVariable(String token) {
        return token.matches("[A-Za-z_][A-Za-z0-9_]*");
    }
}
