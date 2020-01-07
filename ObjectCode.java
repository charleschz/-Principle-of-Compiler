import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectCode {

    public static void Blocked(Syntax Cgramma) {
        LinkedList<String> Block = new LinkedList<String>();
        String str = "";
        for (int i = 0; i < Cgramma.qt.size(); i++) {
            str = Cgramma.qt.get(i).First;
            if (str.equals("if") || str.equals("el") || str.equals("ie") || str.equals("wh") || str.equals("do") || str.equals("we")) {
                Block.add(str);
                str = Cgramma.qt.get(i).Second;
                Block.add(str);
                str = Cgramma.qt.get(i).Third;
                Block.add(str);
                str = Cgramma.qt.get(i).Fourth;
                Block.add(str);
                WriteAcTable(Cgramma, Block); // 调用WriteAcTable函数填活跃信息表
                Block.clear();
                continue;
            }
            Block.add(str);
            str = Cgramma.qt.get(i).Second;
            Block.add(str);
            str = Cgramma.qt.get(i).Third;
            Block.add(str);
            str = Cgramma.qt.get(i).Fourth;
            Block.add(str);
        }
        if (Block.size() != 0) {
            WriteAcTable(Cgramma, Block); // 调用WriteAcTable函数填活跃信息表
            Block.clear();
        }
    }

    public static void WriteAcTable(Syntax Cgramma, LinkedList<String> Block) {
        // 初始化活跃信息表
        String str1 = "";
        String str2 = "";
        String str3 = "";
        String str4 = "";
        Map<String, Boolean> SYMBL = new HashMap<String, Boolean>();
        for (int i = 0; i < Block.size(); i++) {
            if (i % 4 == 0) {
                if (IsKeyWord(Block.get(i)) || IsDelimiter(Block.get(i)) || IsNumber(Block.get(i)) || Block.get(i).equals("_")) {
                    str1 = "Nosense";
                } else if (IsTemvar(Block.get(i))) {
                    str1 = "Nonactive";
                    SYMBL.put(Block.get(i), false);
                } else {
                    str1 = "Active";
                    SYMBL.put(Block.get(i), true);
                }
            } else if (i % 4 == 1) {
                if (IsKeyWord(Block.get(i)) || IsDelimiter(Block.get(i)) || IsNumber(Block.get(i)) || Block.get(i).equals("_")) {
                    str2 = "Nosense";
                } else if (IsTemvar(Block.get(i))) {
                    str2 = "Nonactive";
                    SYMBL.put(Block.get(i), false);
                } else {
                    str2 = "Active";
                    SYMBL.put(Block.get(i), true);
                }
            } else if (i % 4 == 2) {
                if (IsKeyWord(Block.get(i)) || IsDelimiter(Block.get(i)) || IsNumber(Block.get(i)) || Block.get(i).equals("_")) {
                    str3 = "Nosense";
                } else if (IsTemvar(Block.get(i))) {
                    str3 = "Nonactive";
                    SYMBL.put(Block.get(i), false);
                } else {
                    str3 = "Active";
                    SYMBL.put(Block.get(i), true);
                }
            } else {
                if (IsKeyWord(Block.get(i)) || IsDelimiter(Block.get(i)) || IsNumber(Block.get(i)) || Block.get(i).equals("_")) {
                    str4 = "Nosense";
                } else if (IsTemvar(Block.get(i))) {
                    str4 = "Nonactive";
                    SYMBL.put(Block.get(i), false);
                } else {
                    str4 = "Active";
                    SYMBL.put(Block.get(i), true);
                }
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = str1;
                tem.Second = str2;
                tem.Third = str3;
                tem.Fourth = str4;
                Cgramma.ActiveLable.add(tem);
            }
        } // 初始化完成
        int number = Cgramma.ActiveLable.size();
        for (int i = Block.size() - 1; i >= 0; i--) {
            if (i % 4 == 3) {
                if (!Cgramma.ActiveLable.get(number - 1).Fourth.equals("Nosense")) {
                    boolean tem = false;
                    tem = SYMBL.get(Block.get(i));
                    if (tem) {
                        str4 = "Active";
                    } else {
                        str4 = "NonActive";
                    }
                    SYMBL.put(Block.get(i), false);
                } else {
                    str4 = "Nosense";
                }
            } else if (i % 4 == 2) {
                if (!Cgramma.ActiveLable.get(number - 1).Third.equals("Nosense")) {
                    boolean tem = false;
                    tem = SYMBL.get(Block.get(i));
                    if (tem) {
                        str3 = "Active";
                    } else {
                        str3 = "NonActive";
                    }
                    SYMBL.put(Block.get(i), true);
                } else {
                    str3 = "Nosense";
                }
            } else if (i % 4 == 1) {
                if (!Cgramma.ActiveLable.get(number - 1).Second.equals("Nosense")) {
                    boolean tem = false;
                    tem = SYMBL.get(Block.get(i));
                    if (tem) {
                        str2 = "Active";
                    } else {
                        str2 = "NonActive";
                    }
                    SYMBL.put(Block.get(i), true);
                } else {
                    str2 = "Nosense";
                }
            } else {
                str1 = Cgramma.ActiveLable.get(number - 1).First;
                Syntax.Quaternary tem = new Syntax.Quaternary();
                tem.First = str1;
                tem.Second = str2;
                tem.Third = str3;
                tem.Fourth = str4;
                Cgramma.ActiveLable.set(number - 1, tem);
                number--;
            }
        } // 逆序扫描基本块内的四元式
    }

    public static Boolean IsKeyWord(String str) {
        Pattern pattern = Pattern.compile("if|el|ie|wh|do|we");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) return true;
        else
            return false;
    }

    public static Boolean IsDelimiter(String str) {
        Pattern pattern = Pattern.compile("<|>|=|<=|>=|==|\\+|-|\\*|/");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) return true;
        else
            return false;
    }

    public static Boolean IsNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) return true;
        else
            return false;
    }

    public static Boolean IsTemvar(String str) {
        Pattern pattern = Pattern.compile("t[0-9]");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) return true;
        else
            return false;
    } // 以上四个正则表达式可能存在问题，需留意
}
