import java.io.IOException;
import java.util.Stack;

public class SyntaxAnalysi {
    public static int i = 0;
    public static Boolean flag = true;

    public static boolean LL1_Analyse(Syntax Cgramma, lexAna lex, SymbolTable Table) throws IOException {
        LL1Syntax.CreateLList(Cgramma);
        Stack<String> SYN = new Stack<String>();
        SYN.push("#");
        SYN.push(Cgramma.prod[0].left);
        int pos = 0;
        String curstate = "";
        while (!(SYN.peek().equals("#") && lex.lexList.get(pos).get("value").equals("#"))) {
            curstate = SYN.peek();
            SYN.pop();
            if (Cgramma.terminators.contains(curstate)) {
                if ((curstate.equals("id") && lex.lexList.get(pos).get("type").equals("id")) ||
                        (curstate.equals("float_const") && lex.lexList.get(pos).get("type").equals("num")) ||
                        (curstate.equals("char_const") && lex.lexList.get(pos).get("type").equals("char")) ||
                        (curstate.equals("string") && lex.lexList.get(pos).get("type").equals("string")) ||
                        (curstate.equals(lex.lexList.get(pos).get("value"))
                        )) {
                    pos++;
                } else {
                    return false;
                }
            } else if (Cgramma.non_terminators.contains(curstate)) {
                int id_of_prod;
                if (lex.lexList.get(pos).get("type").equals("id")) {
                    Pair<String, String> _pair = new Pair<String, String>(curstate, "id");
                    id_of_prod = Cgramma.LL1List.get(_pair);
                } else if (lex.lexList.get(pos).get("type").equals("num")) {
                    Pair<String, String> _pair = new Pair<String, String>(curstate, "float_const");
                    id_of_prod = Cgramma.LL1List.get(_pair);
                } else if (lex.lexList.get(pos).get("type").equals("char")) {
                    Pair<String, String> _pair = new Pair<String, String>(curstate, "char_const");
                    id_of_prod = Cgramma.LL1List.get(_pair);
                } else if (lex.lexList.get(pos).get("type").equals("string")) {
                    Pair<String, String> _pair = new Pair<String, String>(curstate, "string");
                    id_of_prod = Cgramma.LL1List.get(_pair);
                } else {
                    String curstr = (String) lex.lexList.get(pos).get("value");
                    Pair<String, String> _pair = new Pair<String, String>(curstate, curstr);
                    id_of_prod = Cgramma.LL1List.get(_pair);
                }
                if (id_of_prod == -1) {
                    System.out.println("SYNTAX ERROR!");
                    return false;
                }
                if (id_of_prod != -1) {
                    if (Cgramma.prod[id_of_prod].right.get(0).equals("epsilon")) {
                        for (int i = Cgramma.prod[id_of_prod].right.size() - 1; i >= 0; i--) {//逆序压栈
                            if (!Cgramma.prod[id_of_prod].right.get(i).equals("epsilon")) {
                                SYN.push(Cgramma.prod[id_of_prod].right.get(i));
                            }
                        }
                        continue;
                    } //continue;
                    for (int i = Cgramma.prod[id_of_prod].right.size() - 1; i >= 0; i--) {//逆序压栈
                        SYN.push(Cgramma.prod[id_of_prod].right.get(i));
                    }
                } else {
                    return false;
                }
            } else {
                SemanticAnalysis.Call(curstate, pos, (String) lex.lexList.get(pos - 1).get("value"), Cgramma, Table);
            }
        }
        if(!flag){
            //System.exit(0);
        }
        return true;
    }

    public static void main(String[] args) throws IOException {

    }
}
