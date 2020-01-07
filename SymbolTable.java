import java.io.IOException;
import java.util.ArrayList;

public class SymbolTable {
    public static class Var {
        String name; // 变量名
        String type; // 变量类型，int,char,float,string，struct
        int tp; // 若数组则为数组单元个数，若结构体则为该结构体在结构体表的起始位置
        int offset; // 偏移量
    }

    class Type {
        char tval;
        int tp;
    }

    class Struc {
        String name; // 变量名
        String type; // 变量类型，int,char,float,string
        int tp; // 若数组则为数组单元个数
        int offset; // 偏移量
    }

    public ArrayList<Var> Synbl = new ArrayList<Var>(); //变量表
    public ArrayList<Struc> Rinfl = new ArrayList<Struc>(); //结构体表，用来存放结构体中的所有变量

    private int state = 0;
    private String stype;
    private String sname;
    private int off = 0;
    private int offs = 0;
    private boolean isArr = false;
    private int len = 0;

    int SearchSynbl(String s) {
        int i = 0;
        for (Var v : Synbl) {
            if (v.name.equals(s)) {
                return i;
            }
            i++;
        }
        return 10000; // 查找一个变量在变量表中的位置，若表中没有这个变量则返回10000
    }

    int SearchRinfi(int location, String s) {
        for (int i = location; i < Rinfl.size(); i++) {
            if (Rinfl.get(i).name.equals(s)) {
                return i;
            }
            i++;
        }
        return 10000; // 查找一个变量在结构体表中的位置，若表中没有这个变量则返回10000
    }

    void addSynbl() {
        Var v = new Var();
        v.name = sname;
        v.type = stype;
        int size = 0;
        switch (stype) {
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
        if (isArr) {
            v.tp = len;
            v.type = stype + "[]";
        }
        if (!sname.equals("main")) {
            if (SearchSynbl(sname) == 10000) {
                off = offs;
                v.offset = off;
                offs = off + size * len;//加入该变量后末尾地址等于原偏移地址加上该变量的长度
                Synbl.add(v);
            } else {
                System.out.println(sname + "重定义");
            }

        }
    }

    void addRinfi() {
        Struc s = new Struc();
        s.name = sname;
        s.type = stype;
        int size = 0;
        switch (stype) {
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
        if (isArr) {
            s.tp = len;
            s.type = stype + "[]";
        }
        int location = Synbl.get(Synbl.size() - 1).tp;
        if (SearchRinfi(location, sname) == 10000) {
            off = offs;
            s.offset = off;
            offs = off + size * len;//加入该变量后末尾地址等于原偏移地址加上该变量的长度
            Rinfl.add(s);
        } else if (isArr) {
            System.out.println(stype + " " + sname + "[" + len + "]" + "重定义");
        }
    }

    void getTable(lexAna lex) throws IOException {
        state = 1;
        int i = 0;
        while (i < lex.lexList.size()) {
            String s = (String) lex.lexList.get(i).get("value");
            state = changeState(s, state);//自动机状态变换
            i++;
        }
    }

    int changeState(String s, int state) {
        switch (state) {
            case 1:
                if (s.equals("int") || s.equals("char") || s.equals("String") || s.equals("float")) {
                    stype = s;
                    state = 2;
                } else if (s.equals("struct")) {
                    stype = s;
                    state = 7;
                } else
                    state = 1;
                break;
            case 2:
                sname = s;
                state = 3;
                break;
            case 3:
                if (s.equals(",")) {
                    isArr = false;
                    len = 1;
                    addSynbl();
                    state = 2;
                } else if (s.equals("[")) {
                    state = 4;
                    isArr = true;
                } else if (!(s.equals(";") || s.equals("(") || s.equals(")"))) {
                    state = 3;
                } else {
                    isArr = false;
                    len = 1;
                    addSynbl();
                    state = 1;
                }
                break;
            case 4:
                len = Integer.parseInt(s);
                state = 5;
                break;
            case 5:
                if (s.equals("]")) {
                    addSynbl();
                    state = 6;
                }
                break;
            case 6:
                if (s.equals(",")) {
                    state = 2;
                } else {
                    state = 1;
                }
                break;
            case 7:
                sname = s;
                Var v = new Var();
                v.name = sname;
                v.type = stype;
                v.tp = Rinfl.size();
                v.offset = offs;
                Synbl.add(v);
                state = 8;
                break;
            case 8:
                if (s.equals("{")) {
                    state = 9;
                } else state = 8;
                break;
            case 9:
                if (s.equals("}")) {
                    state = 1;
                } else {
                    stype = s;
                    state = 10;
                }
                break;
            case 10:
                sname = s;
                state = 11;
                break;
            case 11:
                if (s.equals(",")) {
                    isArr = false;
                    len = 1;
                    addRinfi();
                    state = 10;
                } else if (s.equals("[")) {
                    state = 13;
                } else if (s.equals("}")) {
                    isArr = false;
                    len = 1;
                    addRinfi();
                    state = 1;
                } else if (!(s.equals(";") || s.equals("(") || s.equals(")"))) {
                    state = 11;
                } else {
                    isArr = false;
                    len = 1;
                    addRinfi();
                    state = 9;
                }
                break;
            case 12:
                state = 1;
                break;
            case 13:
                len = Integer.parseInt(s);
                state = 14;
                break;
            case 14:
                if (s.equals("]")) {
                    isArr = true;
                    addRinfi();
                    state = 15;
                }
                break;
            case 15:
                if (s.equals(",")) {
                    state = 10;
                } else if (s.equals(";")) {
                    state = 9;
                }
                break;
        }
        return state;
    }

    void printtable() throws IOException {
        for (Var v : Synbl) {
            System.out.printf("%10s", v.name);
            System.out.printf("%10s", v.type);
            System.out.printf("%10s", v.offset);
            System.out.printf("%10s", v.tp);
            System.out.println();
        }
        System.out.println();
        for (Struc s : Rinfl) {
            System.out.printf("%10s", s.name);
            System.out.printf("%10s", s.type);
            System.out.printf("%10s", s.offset);
            System.out.printf("%10s", s.tp);
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {

    }
}