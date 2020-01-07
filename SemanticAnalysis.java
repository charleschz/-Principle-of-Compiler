import java.util.Stack;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticAnalysis {

    public static String arrNum = "";
    public static int thelevel=0;

    public static void Syntax(Syntax Cgramma) {
        Cgramma.Action_map.put("PUSH", 1);
        Cgramma.Action_map.put("GEQA", 2);
        Cgramma.Action_map.put("GEQS", 3);
        Cgramma.Action_map.put("GEQM", 4);
        Cgramma.Action_map.put("GEQD", 5);
        Cgramma.Action_map.put("ASSI", 6);
        Cgramma.Action_map.put("GEQG", 7);
        Cgramma.Action_map.put("GEQL", 8);
        Cgramma.Action_map.put("GEQE", 9);
        Cgramma.Action_map.put("GEQGE", 10);
        Cgramma.Action_map.put("GEQLE", 11);
        Cgramma.Action_map.put("IF", 12);
        Cgramma.Action_map.put("EL", 13);
        Cgramma.Action_map.put("IEFIR", 14);
        Cgramma.Action_map.put("IESEC", 15);
        Cgramma.Action_map.put("WH", 16);
        Cgramma.Action_map.put("DO", 17);
        Cgramma.Action_map.put("WE", 18);
        Cgramma.Action_map.put("PUSHNUM", 19);
        Cgramma.Action_map.put("LEVELA", 20);
        Cgramma.Action_map.put("LEVELS", 21);
    }

    public static void Call(String Action, int TokenIndex, String curstr, Syntax Cgramma, SymbolTable table) throws IOException {
        SemanticAnalysis.Syntax(Cgramma);
        switch (Cgramma.Action_map.get(Action)) {
            case 1: { // PUSH动作
                Pair<String, Integer> _pair = new Pair<String, Integer>(curstr, TokenIndex);
                Cgramma.SEM.push(_pair);
                break;
            }
            case 2: { // GEQA动作
                String str1 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                String str2 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "+";
                tem.Second = str2;
                tem.Third = str1;

                if (!isdefined(tem.Second, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Second + "未定义");
                } else if (!isdefined(tem.Third, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Third + "未定义");
                }
                tem.Fourth = "t" + String.valueOf(SyntaxAnalysi.i);
                Cgramma.qt.add(tem);
                GetTemLable(tem, table);
                Pair<String, Integer> _pair = new Pair<String, Integer>("t" + String.valueOf(SyntaxAnalysi.i), TokenIndex);
                Cgramma.SEM.push(_pair);
                SyntaxAnalysi.i++;
                break;
            }
            case 3: { // GEQS动作
                String str1 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                String str2 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "-";
                tem.Second = str2;
                tem.Third = str1;

                if (!isdefined(tem.Second, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Second + "未定义");

                } else if (!isdefined(tem.Third, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Third + "未定义");
                }
                tem.Fourth = "t" + String.valueOf(SyntaxAnalysi.i);
                Cgramma.qt.add(tem);
                GetTemLable(tem, table);
                Pair<String, Integer> _pair = new Pair<String, Integer>("t" + String.valueOf(SyntaxAnalysi.i), TokenIndex);
                Cgramma.SEM.push(_pair);
                SyntaxAnalysi.i++;
                break;
            }
            case 4: { // GEQM动作
                String str1 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                String str2 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "*";
                tem.Second = str2;
                tem.Third = str1;

                if (!isdefined(tem.Second, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Second + "未定义");
                } else if (!isdefined(tem.Third, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Third + "未定义");
                }
                tem.Fourth = "t" + String.valueOf(SyntaxAnalysi.i);
                Cgramma.qt.add(tem);
                GetTemLable(tem, table);
                Pair<String, Integer> _pair = new Pair<String, Integer>("t" + String.valueOf(SyntaxAnalysi.i), TokenIndex);
                Cgramma.SEM.push(_pair);
                SyntaxAnalysi.i++;
                break;
            }
            case 5: { // GEQD动作
                String str1 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                String str2 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "/";
                tem.Second = str2;
                tem.Third = str1;

                if (!isdefined(tem.Second, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Second + "未定义");
                } else if (!isdefined(tem.Third, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Third + "未定义");
                }
                tem.Fourth = "t" + String.valueOf(SyntaxAnalysi.i);
                Cgramma.qt.add(tem);
                GetTemLable(tem, table);
                Pair<String, Integer> _pair = new Pair<String, Integer>("t" + String.valueOf(SyntaxAnalysi.i), TokenIndex);
                Cgramma.SEM.push(_pair);
                SyntaxAnalysi.i++;
                break;
            }
            case 6: { // ASSI动作
                String tem1 = Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                String tem2 = Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "=";
                tem.Second = tem1;
                tem.Third = "_";
                if (!arrNum.equals("")) {
                    tem2 = tem2 + "[" + arrNum + "]";
                }
                tem.Fourth = tem2;

                if (!isdefined(tem.Second, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Second + "未定义");
                } else if (!isdefined(tem.Fourth, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Fourth + "未定义");
                }
                Cgramma.qt.add(tem);
                arrNum = "";
                break;
            }
            case 7: { // GEQG动作
                String str1 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                String str2 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = ">";
                tem.Second = str2;
                tem.Third = str1;
                tem.Fourth = "t" + String.valueOf(SyntaxAnalysi.i);
                if (!isdefined(tem.Second, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Second + "未定义");
                } else if (!isdefined(tem.Third, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Third + "未定义");
                }
                Cgramma.qt.add(tem);
                Pair<String, Integer> _pair = new Pair<String, Integer>("t" + String.valueOf(SyntaxAnalysi.i), TokenIndex);
                Cgramma.SEM.push(_pair);
                SyntaxAnalysi.i++;
                break;
            }
            case 8: { // GEQL动作
                String str1 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                String str2 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "<";
                tem.Second = str2;
                tem.Third = str1;
                tem.Fourth = "t" + String.valueOf(SyntaxAnalysi.i);
                if (!isdefined(tem.Second, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Second + "未定义");
                } else if (!isdefined(tem.Third, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Third + "未定义");
                }
                Cgramma.qt.add(tem);
                Pair<String, Integer> _pair = new Pair<String, Integer>("t" + String.valueOf(SyntaxAnalysi.i), TokenIndex);
                Cgramma.SEM.push(_pair);
                SyntaxAnalysi.i++;
                break;
            }
            case 9: { // GEQE动作
                String str1 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                String str2 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "==";
                tem.Second = str2;
                tem.Third = str1;
                tem.Fourth = "t" + String.valueOf(SyntaxAnalysi.i);
                if (!isdefined(tem.Second, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Second + "未定义");
                } else if (!isdefined(tem.Third, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Third + "未定义");
                }
                Cgramma.qt.add(tem);
                Pair<String, Integer> _pair = new Pair<String, Integer>("t" + String.valueOf(SyntaxAnalysi.i), TokenIndex);
                Cgramma.SEM.push(_pair);
                SyntaxAnalysi.i++;
                break;
            }
            case 10: { // GEQGE动作
                String str1 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                String str2 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = ">=";
                tem.Second = str2;
                tem.Third = str1;
                tem.Fourth = "t" + String.valueOf(SyntaxAnalysi.i);
                if (!isdefined(tem.Second, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Second + "未定义");
                } else if (!isdefined(tem.Third, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Third + "未定义");
                }
                Cgramma.qt.add(tem);
                Pair<String, Integer> _pair = new Pair<String, Integer>("t" + String.valueOf(SyntaxAnalysi.i), TokenIndex);
                Cgramma.SEM.push(_pair);
                SyntaxAnalysi.i++;
                break;
            }
            case 11: { // GEQLE动作
                String str1 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                String str2 = (String) Cgramma.SEM.peek().getFirst();
                Cgramma.SEM.pop();
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "<=";
                tem.Second = str2;
                tem.Third = str1;
                tem.Fourth = "t" + String.valueOf(SyntaxAnalysi.i);
                if (!isdefined(tem.Second, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Second + "未定义");
                } else if (!isdefined(tem.Third, table)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println(tem.Third + "未定义");
                }
                Cgramma.qt.add(tem);
                Pair<String, Integer> _pair = new Pair<String, Integer>("t" + String.valueOf(SyntaxAnalysi.i), TokenIndex);
                Cgramma.SEM.push(_pair);
                SyntaxAnalysi.i++;
                break;
            }
            case 12: { // IF动作
                String str1 = (String) Cgramma.SEM.peek().getFirst();
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "if";
                tem.Second = str1;
                tem.Third = "_";
                tem.Fourth = "_";
                tem.level=thelevel;
                Cgramma.qt.add(tem);
                Cgramma.SEM.pop();
                break;
            }
            case 13: { // EL动作
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "el";
                tem.Second = "_";
                tem.Third = "_";
                tem.Fourth = "_";
                tem.level=thelevel;
                Cgramma.qt.add(tem);
                for (int i = Cgramma.qt.size() - 1; i >= 0; i--) {
                    if (Cgramma.qt.get(i).First.equals("if") && Cgramma.qt.get(i).Fourth.equals("_")&&Cgramma.qt.get(i).level==thelevel) {
                        Syntax.Quaternary tem1 = new Syntax.Quaternary();
                        tem1.First = "if";
                        tem1.Second = Cgramma.qt.get(i).Second;
                        tem1.Third = "_";
                        tem1.Fourth = String.valueOf(Cgramma.qt.size());
                        Cgramma.qt.set(i, tem1);
                        break;
                    }
                }
                break;
            }
            case 14: { // IEFIR动作
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "ie";
                tem.Second = "_";
                tem.Third = "_";
                tem.Fourth = "_";
                tem.level=thelevel;
                Cgramma.qt.add(tem);
                for (int i = Cgramma.qt.size() - 1; i >= 0; i--) {
                    if (Cgramma.qt.get(i).First.equals("if") && Cgramma.qt.get(i).Fourth.equals("_")) {
                        Syntax.Quaternary tem1 = new Syntax.Quaternary();
                        tem1.First = "if";
                        tem1.Second = Cgramma.qt.get(i).Second;
                        tem1.Third = "_";
                        tem1.Fourth = String.valueOf(Cgramma.qt.size() - 1);
                        Cgramma.qt.set(i, tem1);
                        break;
                    }
                }
                break;
            }

            case 15: { // IESEC动作
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "ie";
                tem.Second = "_";
                tem.Third = "_";
                tem.Fourth = "_";
                tem.level=thelevel;
                Cgramma.qt.add(tem);
                int Number = 0;
                for (int i = Cgramma.qt.size() - 1; i >= 0; i--) {
//                    if (Cgramma.qt.get(i).First.equals("el") && Number >= 0) {
//                        Number++;
//                        if (Cgramma.qt.get(i).Fourth.equals("_")) {
//                            Syntax.Quaternary tem1 = new Syntax.Quaternary();
//                            tem1.First = "el";
//                            tem1.Second = "_";
//                            tem1.Third = "_";
//                            tem1.Fourth = String.valueOf(Cgramma.qt.size() - 1);
//                            Cgramma.qt.set(i, tem1);
//                        }
//                    }
//                    if (Cgramma.qt.get(i).First.equals("if")) {
//                        Number--;
//                        if (Number < 0) {
//                            break;
//                        }
//                    }
                    if(Cgramma.qt.get(i).First.equals("el")&&Cgramma.qt.get(i).Fourth.equals("_")&&Cgramma.qt.get(i).level==thelevel){
                        Syntax.Quaternary tem1 = new Syntax.Quaternary();
                        tem1.First = "el";
                        tem1.Second = "_";
                        tem1.Third = "_";
                        tem1.Fourth = String.valueOf(Cgramma.qt.size() - 1);
                        Cgramma.qt.set(i, tem1);
                    }
                }
                break;
            }
            case 16: { // WH动作
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "wh";
                tem.Second = "_";
                tem.Third = "_";
                tem.Fourth = "_";
                Cgramma.qt.add(tem);
                break;
            }
            case 17: { // DO动作
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = "do";
                tem.Second = Cgramma.SEM.peek().getFirst();
                tem.Third = "_";
                tem.Fourth = "_";
                Cgramma.qt.add(tem);
                Cgramma.SEM.pop();
                break;
            }
            case 18: { // WE动作
                int isFindDo = 0;//如果找到空DO则变为1
                for (int i = Cgramma.qt.size() - 1; i >= 0; i--) {
                    if (Cgramma.qt.get(i).First.equals("do") && Cgramma.qt.get(i).Fourth.equals("_") && isFindDo == 0) {
                        isFindDo = 1;
                        Syntax.Quaternary tem1 = new Syntax.Quaternary();
                        tem1.First = "do";
                        tem1.Second = Cgramma.qt.get(i).Second;
                        tem1.Third = "_";
                        tem1.Fourth = String.valueOf(Cgramma.qt.size() + 1);
                        Cgramma.qt.set(i, tem1);
                    }
                    if (isFindDo == 1 && Cgramma.qt.get(i).First.equals("wh")) {
                        Syntax.Quaternary tem1 = new Syntax.Quaternary();
                        tem1.First = "we";
                        tem1.Second = "_";
                        tem1.Third = "_";
                        tem1.Fourth = String.valueOf(i + 1);
                        Cgramma.qt.add(tem1);
                        break;
                    }
                }
                break;
            }
            case 19: { // PUSHNUM动作
                arrNum = curstr;
                break;
            }

            case 20: { // LEVELA动作
                thelevel++;
                break;
            }
            case 21: { // LEVELS动作
                thelevel--;
                break;
            }
        }
    }

    public static void GetTemLable(Syntax.Quaternary tem, SymbolTable table) throws IOException {
        SymbolTable.Var newtemp = new SymbolTable.Var();
        if (tem.First.equals("+") || tem.First.equals("-") || tem.First.equals("*") || tem.First.equals("/")) {
            newtemp.name = tem.Fourth;
            newtemp.offset = 0;
            newtemp.tp = 0;
            String type1 = "";
            String type2 = "";
            if (IsNumber(tem.Second)) {
                type1 = "int";
            } else if (tem.Second.charAt(0) == '\'') {
                type1 = "char";
            } else {
                for (SymbolTable.Var v : table.Synbl) {
                    if (v.name.equals(tem.Second)) {
                        type1 = v.type;
                        break;
                    }
                }
            }
            if (IsNumber(tem.Third)) {
                type2 = "int";
            } else if (tem.Third.charAt(0) == '\'') {
                type2 = "char";
            } else {
                for (SymbolTable.Var v : table.Synbl) {
                    if (v.name.equals(tem.Third)) {
                        type2 = v.type;
                        break;
                    }
                }
            }
            if ((type1.contains("int")) && (type2.contains("int"))) {
                newtemp.type = "int";
            } else if ((type1.contains("char")) && type2.contains("char")) {
                newtemp.type = "char";
            } else if ((type1.contains("int") && type2.contains("char")) || (type1.contains("char") && type2.contains("int"))) {
                newtemp.type = "int";
            } else {
                if ((isdefined(tem.Second, table) && isdefined(tem.Third, table)) && !Istempvar(tem.Second) && !Istempvar(tem.Third)) {
                    SyntaxAnalysi.flag = false;
                    System.out.println("类型不匹配");
                }
                newtemp.type = "";
            }
            if (!newtemp.type.equals("")) {
                int size = 0;
                switch (table.Synbl.get(table.Synbl.size() - 1).type) {
                    case "int":
                        size = 1;
                        break;
                    case "char":
                        size = 1;
                        break;
                    case "float":
                        size = 2;
                        break;
                    case "String":
                        size = 50;
                        break;
                }
                newtemp.offset = table.Synbl.get(table.Synbl.size() - 1).offset + size;
                table.Synbl.add(newtemp);
            }
        }
    }

    static boolean isdefined(String name, SymbolTable table) {
        if (IsNumber(name)) {
            return true;
        } else if (name.charAt(0) == '\'') {
            return true;
        } else if (Istempvar(name)) {
            return true;
        }
        else if(name.contains("[")){
            String[] s=name.split("\\[");
            String[] ss=s[1].split("\\]");
            for (SymbolTable.Var v : table.Synbl) {
                if (v.name.equals(s[0])) {
                    if(Integer.valueOf(ss[0])>=v.tp){
                        System.out.println(name+"数组越界");
                        SyntaxAnalysi.flag=false;
                    }
                    return true;
                }
            }
        }
        else {
            for (SymbolTable.Var v : table.Synbl) {
                if (v.name.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean IsNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) return true;
        else
            return false;
    }

    public static Boolean Istempvar(String str) {
        Pattern pattern = Pattern.compile("t[0-9]+");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) return true;
        else
            return false;
    }

    public static void PrintQt(Syntax Cgramma) {
        for (int i = 0; i < Cgramma.qt.size(); i++) {
            String str1 = Cgramma.qt.get(i).First;
            String str2 = Cgramma.qt.get(i).Second;
            String str3 = Cgramma.qt.get(i).Third;
            String str4 = Cgramma.qt.get(i).Fourth;
            System.out.println(i + " (" + str1 + "," + str2 + "," + str3 + "," + str4 + ")");
        }
    }
}
