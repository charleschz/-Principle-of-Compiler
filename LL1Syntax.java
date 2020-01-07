import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.*;
import java.util.Iterator;

public class LL1Syntax {

    public static void main(String[] args) throws IOException {
        Syntax Cgramma = new Syntax();
        CreateLList(Cgramma);

        System.out.println("PAUSE");
    }

    public static void preProcess(Syntax Cgramma) throws IOException {
        String line; // 用来保存每行读取的内容
        File filename = new File("Cgrammer.txt");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建一个输入流对象reader
        BufferedReader br = new BufferedReader(reader);// 建立一个对象，它把文件内容转成计算机能读懂的语言

        line = br.readLine();
        int index_of_prod = 0;
        while (line != null) { // 如果 line 为空说明读完了
            char[] chars = line.toCharArray();
            int ptr_of_rules = 0; // 一行文法规则内每个字符的访问指针
            Cgramma.prod[index_of_prod] = new Syntax.production();
            while (chars[ptr_of_rules] == ' ' || chars[ptr_of_rules] == '\t') ptr_of_rules++;//滤掉空白符
            if (chars[ptr_of_rules] == ';') {
                line = br.readLine();
                continue;
            }
            if (Character.isLowerCase(chars[ptr_of_rules]) || Character.isUpperCase(chars[ptr_of_rules])) {
                String nonTerm = ""; // 临时存储非终结符
                while (chars[ptr_of_rules] != ' ' && chars[ptr_of_rules] != '\t') { // 分割出一个非终结符
                    nonTerm += chars[ptr_of_rules];
                    ptr_of_rules++;
                }
                Cgramma.non_terminators.add(nonTerm); // 将该非终结符加入到非终结符表中
                Cgramma.is_deduced_epsilon.put(nonTerm, false);
                Cgramma.prod[index_of_prod].left = nonTerm;
                Cgramma.size_of_prod++;
                while (chars[ptr_of_rules] == ' ' || chars[ptr_of_rules] == '\t' || chars[ptr_of_rules] == ':')
                    ptr_of_rules++; // 滤掉冒号和空白符
                while (ptr_of_rules < chars.length) { // 记录产生式右部符号，顺便填写终结符集合
                    // 一直走到该行结束
                    String tmp = ""; // 拼出右部一个符号
                    if (chars[ptr_of_rules] == '\'') { // 当前出现了终结符，需要记录该终结符
                        ptr_of_rules++; // 略去单引号
                        while (chars[ptr_of_rules] != '\'') {
                            tmp += chars[ptr_of_rules];
                            ptr_of_rules++;
                        } // 拼出一个终结符
                        Cgramma.terminators.add(tmp); // 将该终结符记录在终结符表中
                        Set<String> tmpset = new HashSet<String>(); // 临时集合，用于存储终结符的First集
                        tmpset.add(tmp);
                        Cgramma.First.put(tmp, tmpset);
                        Cgramma.prod[index_of_prod].right.add(tmp);
                        ptr_of_rules += 2; // 略去终结符后面的单引号和空格
                    } else { // 当前符号是一个非终结符或是一个语义动作标志
                        while (ptr_of_rules < chars.length && chars[ptr_of_rules] != ' ') {
                            tmp += chars[ptr_of_rules];
                            ptr_of_rules++;
                        } // 拼出一个非终结符
                        ptr_of_rules++; // 略去文法符号之间的空格
                        Cgramma.prod[index_of_prod].right.add(tmp); // 将该文法符号或语义动作符号存到产生式右部vector中
                        if (tmp.equals("epsilon")) {
                            // 该产生式左部的非终结符可以直接推空
                            Cgramma.is_deduced_epsilon.put(Cgramma.prod[index_of_prod].left, true);
                        }
                    }
                }
                index_of_prod++;
                line = br.readLine();
            } else if (chars[ptr_of_rules] == '|') {
                //一个非终结符推出的其他产生式
                ptr_of_rules += 2; //略去'|'和空格
                Cgramma.prod[index_of_prod].left = Cgramma.prod[index_of_prod - 1].left; //该产生式左部必定和上一个产生式是一样的
                Cgramma.size_of_prod++;
                while (ptr_of_rules < chars.length) { //记录产生式右部符号，顺便填写终结符集合
                    //一直走到该行结束
                    String tmp = ""; //拼出右部一个符号
                    if (chars[ptr_of_rules] == '\'') {
                        //当前出现了终结符，需要记录该终结符
                        ptr_of_rules++;
                        while (chars[ptr_of_rules] != '\'') {
                            tmp += chars[ptr_of_rules];
                            ptr_of_rules++;
                        }
                        Cgramma.terminators.add(tmp); //将该终结符记录在终结符表中
                        Set<String> tmpset = new HashSet<String>();
                        tmpset.add(tmp);
                        Cgramma.First.put(tmp, tmpset);
                        Cgramma.prod[index_of_prod].right.add(tmp); //产生式右部记录该符号
                        ptr_of_rules += 2; //略去终结符后面的单引号和空格
                    } else { //当前符号是一个非终结符或语义动作标志
                        while (ptr_of_rules < chars.length && chars[ptr_of_rules] != ' ') {
                            tmp += chars[ptr_of_rules];
                            ptr_of_rules++;
                        }//拼出一个非终结符
                        ptr_of_rules++; //略去文法符号之间的空格
                        Cgramma.prod[index_of_prod].right.add(tmp); //将该文法符号存到产生式右部vector中
                        if (tmp.equals("epsilon")) {
                            //该产生式左部的非终结符可以直接推空
                            Cgramma.is_deduced_epsilon.put(Cgramma.prod[index_of_prod].left, true);
                        }
                    }
                }
                index_of_prod++;
                line = br.readLine();
            }
        }
        Set<String> Temp1 = new HashSet<String>(); // 临时集合，用于存储终结符的First集
        Cgramma.terminators.add("int_const"); //额外添加几个终结符
        Temp1.add("int_const");
        Cgramma.First.put("int_const", Temp1);

        Set<String> Temp2 = new HashSet<String>();
        Cgramma.terminators.add("char_const");
        Temp2.add("char_const");
        Cgramma.First.put("char_const", Temp2);

        Set<String> Temp3 = new HashSet<String>();
        Cgramma.terminators.add("id");
        Temp3.add("id");
        Cgramma.First.put("id", Temp3);

        Set<String> Temp4 = new HashSet<String>();
        Cgramma.terminators.add("float_const");
        Temp4.add("float_const");
        Cgramma.First.put("float_const", Temp4);

        Set<String> Temp5 = new HashSet<String>();
        Cgramma.terminators.add("string");
        Temp5.add("string");
        Cgramma.First.put("string", Temp5);

        reader.close();
    }

    public static Boolean IsUpper(String str) {
        Pattern pattern = Pattern.compile("[A-Z_0-9]+");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) return true;
        else
            return false;
    }

    public static void dataRevision(Syntax Cgramma) {

        for (int i = Cgramma.size_of_prod - 1; i >= 0; i--) {
            if (!Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].left)) { //原可推空标志为false
                int index_of_right = 0;
                //bool isAction = false; //是否为语义动作标志
                while (index_of_right < Cgramma.prod[i].right.size() &&
                        (Cgramma.non_terminators.contains(Cgramma.prod[i].right.get(index_of_right))
                                || IsUpper(Cgramma.prod[i].right.get(index_of_right)))) {
                    //如果产生式右端为非终结符或语义动作标志
                    if (IsUpper(Cgramma.prod[i].right.get(index_of_right))) index_of_right++; //语义动作标志直接跳过
                    else { //是非终结符
                        if (!Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].right.get(index_of_right))) {
                            Cgramma.is_deduced_epsilon.put(Cgramma.prod[i].left, false);
                            break;
                        } else index_of_right++;
                    }
                }
                if (index_of_right >= Cgramma.prod[i].right.size()) { //产生式右端所有符号都可以推空
                    Cgramma.is_deduced_epsilon.put(Cgramma.prod[i].left, true);
                    Set<String> Temp = new HashSet<String>(); // 临时集合，用于存储终结符的First集
                    Temp.add("epsilon");
                    Cgramma.First.put(Cgramma.prod[i].left, Temp);
                }
            }
        }
        //再正着扫一遍
        for (int i = 0; i < Cgramma.size_of_prod; i++) {
            if (!Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].left)) { //原可推空标志为false
                int index_of_right = 0;
                while (index_of_right < Cgramma.prod[i].right.size() &&
                        (Cgramma.non_terminators.contains(Cgramma.prod[i].right.get(index_of_right))
                                || IsUpper(Cgramma.prod[i].right.get(index_of_right)))) {
                    //如果产生式右端为非终结符或语义动作标志
                    if (IsUpper(Cgramma.prod[i].right.get(index_of_right))) index_of_right++; //语义动作标志直接跳过
                    else { //是非终结符
                        if (!Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].right.get(index_of_right))) {
                            Cgramma.is_deduced_epsilon.put(Cgramma.prod[i].left, false);
                            break;
                        } else index_of_right++;
                    }
                }
                if (index_of_right >= Cgramma.prod[i].right.size()) { //产生式右端所有符号都可以推空
                    Cgramma.is_deduced_epsilon.put(Cgramma.prod[i].left, true);
                    Set<String> Temp = new HashSet<String>(); // 临时集合，用于存储终结符的First集
                    Temp.add("epsilon");
                    Cgramma.First.put(Cgramma.prod[i].left, Temp);
                }
            }
        }
    }

    public static void DFS_First(Syntax Cgramma) {
        for (String str : Cgramma.non_terminators) {
            Cgramma.visited.put(str, false);
        }
        for (String str : Cgramma.non_terminators) {
            if (!Cgramma.visited.get(str))
                getFirst(str, Cgramma);
        }
    }

    public static void getFirst(String curSymbol, Syntax Cgramma) {
        Cgramma.visited.put(curSymbol, true); //将该符号的访问标志置为true，表明已被访问
        for (int index_prod = 0; index_prod < Cgramma.size_of_prod; index_prod++) {    //遍历产生式组

            if (Cgramma.prod[index_prod].left.equals(curSymbol)) { //找到该符号对应的产生式
                int index_right = 0; //当前产生式右部符号集合索引
                if (IsUpper(Cgramma.prod[index_prod].right.get(index_right))) index_right++; //如果右部首符号是一个语义标志，跳过他
                if (Cgramma.non_terminators.contains(Cgramma.prod[index_prod].right.get(index_right))) {
                    //产生式右部首符号为非终结符
                    List<String> tmpvec = new ArrayList<String>();
                    tmpvec.add(Cgramma.prod[index_prod].right.get(index_right)); //格式转换 string -> vector<string>
                    Cgramma.tmpStorage.put(Cgramma.prod[index_prod].left, tmpvec); //暂存之
                    if (!Cgramma.visited.get(Cgramma.prod[index_prod].right.get(index_right))) //如果该符号未被访问过!!!!!!!!
                        getFirst(Cgramma.prod[index_prod].right.get(index_right), Cgramma); //获取该符号的first集合
                    Set<String> t_set = new HashSet<String>();
                    if (Cgramma.First.get(Cgramma.prod[index_prod].right.get(index_right)) != null) {
                        for (String str : Cgramma.First.get(Cgramma.prod[index_prod].right.get(index_right)))
                            t_set.add(str);
                    }
                    if (Cgramma.is_deduced_epsilon.get(Cgramma.prod[index_prod].right.get(index_right))) {
                        //如果该符号可以推空  First type is: map<string, set<string>>
                        t_set.remove("epsilon");
                    }
                    Set<String> f_set = new HashSet<String>();
                    if (Cgramma.First.get(Cgramma.prod[index_prod].left) != null) {
                        for (String str : Cgramma.First.get(Cgramma.prod[index_prod].left))
                            f_set.add(str);
                    }
                    if (f_set == null) {
                        Cgramma.First.put(Cgramma.prod[index_prod].left, t_set); //合并到左部符号的first集合
                    } else {
                        f_set.addAll(t_set);
                        Cgramma.First.put(Cgramma.prod[index_prod].left, f_set); //合并到左部符号的first集合
                    }
                    boolean bool = false;
                    while (bool || Cgramma.is_deduced_epsilon.get(Cgramma.prod[index_prod].right.get(index_right))) {
                        //如果该产生式右部首符号可以推空
                        index_right++; //向后走一个，看下一个符号
                        bool = false;
                        if (index_right < Cgramma.prod[index_prod].right.size()) {    //如果右部该符号后面还有符号
                            if (IsUpper(Cgramma.prod[index_prod].right.get(index_right))) {
                                //遇到语义动作标志,跳过
                                bool = true;
                            } else if (Cgramma.non_terminators.contains(Cgramma.prod[index_prod].right.get(index_right))) {
                                //如果后面这个符号也是非终结符
                                tmpvec.add(Cgramma.prod[index_prod].right.get(index_right));
                                ;
                                Cgramma.tmpStorage.put(Cgramma.prod[index_prod].left, tmpvec); //暂存之
                                if (!Cgramma.visited.get(Cgramma.prod[index_prod].right.get(index_right))) //如果该符号未被访问过!!!!!!!!
                                    getFirst(Cgramma.prod[index_prod].right.get(index_right), Cgramma); //获取该符号的first集合
                                //else { //如果该符号已被访问过
                                Set<String> set1 = new HashSet<String>();//
                                if (Cgramma.First.get(Cgramma.prod[index_prod].right.get(index_right)) != null) {
                                    for (String str : Cgramma.First.get(Cgramma.prod[index_prod].right.get(index_right)))
                                        set1.add(str);
                                }
                                if (Cgramma.is_deduced_epsilon.get(Cgramma.prod[index_prod].right.get(index_right))) {
                                    //如果该符号可以推空  First type is: map<string, set<string>>
                                    set1.remove("epsilon");
                                }
                                Set<String> set2 = new HashSet<String>();//
                                if (Cgramma.First.get(Cgramma.prod[index_prod].left) != null) {
                                    for (String str : Cgramma.First.get(Cgramma.prod[index_prod].left))
                                        set2.add(str);
                                }
                                if (set2 == null) {
                                    Cgramma.First.put(Cgramma.prod[index_prod].left, set1);
                                } else {
                                    set2.addAll(set1);
                                    Cgramma.First.put(Cgramma.prod[index_prod].left, set2);
                                }
                            } else {
                                Set<String> set3 = new HashSet<String>();
                                set3.add(Cgramma.prod[index_prod].right.get(index_right));
                                Cgramma.First.put(Cgramma.prod[index_prod].left, set3);
                                break;
                                //后面这个符号是终结符，存入左部符号first集合中
                            }
                        } else break;
                    }
                }
                //产生式右部首符号为终结符或epsilon, 将之加入左部符号的first集中
                else {
                    Set<String> temp = new HashSet<String>();//
                    if (Cgramma.First.get(Cgramma.prod[index_prod].left) != null) {
                        for (String str : Cgramma.First.get(Cgramma.prod[index_prod].left))
                            temp.add(str);
                    }
                    if (temp != null) {
                        temp.add(Cgramma.prod[index_prod].right.get(index_right));
                        Cgramma.First.put(Cgramma.prod[index_prod].left, temp);
                    } else {
                        Set<String> _Set = new HashSet<String>();
                        _Set.add(Cgramma.prod[index_prod].right.get(index_right));
                        Cgramma.First.put(Cgramma.prod[index_prod].left, _Set);
                    }
                }
            }
        }
    }

    public static Set<String> int_first(List<String> alpha, Syntax Cgramma) {
        int index_right = 0;
        if (IsUpper(alpha.get(index_right))) index_right++; //首符为语义动作标志，跳过
        if (alpha.get(index_right).equals("epsilon") || Cgramma.terminators.contains(alpha.get(index_right))
                || (Cgramma.non_terminators.contains(alpha.get(index_right)) && !Cgramma.is_deduced_epsilon.get(alpha.get(index_right)))) {
            //如果产生式右部首符号是ε 或 终结符 或 不可以推空的非终结符
            Set<String> tmpset = new HashSet<String>();
            if (alpha.get(index_right).equals("epsilon")) { //如果某个产生式右部仅有一个ε
                tmpset.add("epsilon");
                return tmpset;
            }
            return Cgramma.First.get(alpha.get(index_right));
        } else {//产生式右部首符号是可以推空的非终结符
            Set<String> tmpset = new HashSet<String>();
            while (index_right < alpha.size() &&
                    ((Cgramma.non_terminators.contains(alpha.get(index_right)) &&
                            Cgramma.is_deduced_epsilon.get(alpha.get(index_right))) || IsUpper(alpha.get(index_right)))) {
                //如果产生式右部是可以推空的非终结符
                if (IsUpper(alpha.get(index_right))) {
                    //遇到语义动作符号，跳过（直接匹配可以在一定程度上提高效率，减少程序跳转）
                    index_right++;
                    continue;
                }
                if (Cgramma.First.get(alpha.get(index_right)) != null) {
                    for (String str : Cgramma.First.get(alpha.get(index_right)))
                        tmpset.add(str);
                }
                tmpset.remove("epsilon");
                index_right++;
            }
            if (index_right >= alpha.size()) {
                //右部全可以推空
                tmpset.remove("epsilon");
            } else { //遇到了无法推空的符号
                if (Cgramma.First.get(alpha.get(index_right)) != null) {
                    for (String str : Cgramma.First.get(alpha.get(index_right)))
                        tmpset.add(str);
                }
            }
            return tmpset;
        }
    }

    public static void DFS_Follow(Syntax Cgramma) {
        for (String str : Cgramma.non_terminators) {
            Cgramma.visited.put(str, false);
        }
        for (String str : Cgramma.non_terminators) {
            if (!Cgramma.visited.get(str))
                getFollow(str, Cgramma);
        }
    }

    public static void getFollow(String curSymbol, Syntax Cgramma) {
        Cgramma.visited.put(curSymbol, true);
        if (curSymbol.equals(Cgramma.prod[0].left)) { //如果当前符号为开始符号，且该符号的follow集还未建立
            Set<String> _set = new HashSet<String>();
            _set.add("#");
            Cgramma.Follow.put(curSymbol, _set);
        }
        for (int i = 0; i < Cgramma.size_of_prod; i++) {
            //遍历产生式组
            int index_right = 0;
            int it = 0;
            boolean bool = false, equal = false;
            Iterator iter = Cgramma.prod[i].right.iterator();
            String next = "";
            for (; iter.hasNext(); it++) {
                next = (String) iter.next();
                if (next.equals(curSymbol)) break;
            }
            while (!equal && it < Cgramma.prod[i].right.size()) { //如果该产生式右部可以找到待求符号
                index_right = it; //迭代器转换为索引值
                index_right++;
                if (index_right < Cgramma.prod[i].right.size() && IsUpper(Cgramma.prod[i].right.get(index_right)))
                    index_right++; //该符号后面是一个语义标志，跳过
                if (index_right < Cgramma.prod[i].right.size() && Cgramma.terminators.contains(Cgramma.prod[i].right.get(index_right))) {
                    Set<String> _set = new HashSet<String>();
                    _set = Cgramma.Follow.get(curSymbol);
                    if (_set == null) {
                        Set<String> _Set = new HashSet<String>();
                        _Set.add(Cgramma.prod[i].right.get(index_right));
                        Cgramma.Follow.put(curSymbol, _Set);
                    } else {
                        _set.add(Cgramma.prod[i].right.get(index_right));
                        Cgramma.Follow.put(curSymbol, _set);
                    }
                    break;
                }
                while (index_right < Cgramma.prod[i].right.size() && (IsUpper(Cgramma.prod[i].right.get(index_right)) ||
                        Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].right.get(index_right)))) {
                    //如果产生式右部该非终结符后面还有符号 且后面的符号可以推空或者后面是一个语义标志
                    if (IsUpper(Cgramma.prod[i].right.get(index_right))) {
                        index_right++;
                        if (index_right < Cgramma.prod[i].right.size() && Cgramma.terminators.contains(Cgramma.prod[i].right.get(index_right))) {
                            Set<String> _set = new HashSet<String>();
                            _set = Cgramma.Follow.get(curSymbol);
                            if (_set == null) {
                                Set<String> _Set = new HashSet<String>();
                                _Set.add(Cgramma.prod[i].right.get(index_right));
                                Cgramma.Follow.put(curSymbol, _Set);
                            } else {
                                _set.add(Cgramma.prod[i].right.get(index_right));
                                Cgramma.Follow.put(curSymbol, _set);
                            }
                            bool = true;
                            break;
                        }
                    } //遇到语义动作标志，则跳过
                    Set<String> tmp = new HashSet<String>();
                    if (Cgramma.First.get(Cgramma.prod[i].right.get(index_right)) != null) {
                        for (String str : Cgramma.First.get(Cgramma.prod[i].right.get(index_right))) {
                            tmp.add(str);
                        }
                    }
                    tmp.remove("epsilon");
                    Cgramma.Follow.put(Cgramma.prod[i].right.get(it), tmp);
                    index_right++;
                    if (index_right < Cgramma.prod[i].right.size() && Cgramma.terminators.contains(Cgramma.prod[i].right.get(index_right))) {
                        Set<String> _set = new HashSet<String>();
                        if (Cgramma.Follow.get(curSymbol) != null) {
                            for (String str : Cgramma.Follow.get(curSymbol)) {
                                _set.add(str);
                            }
                        }
                        if (_set == null) {
                            Set<String> _Set = new HashSet<String>();
                            _Set.add(Cgramma.prod[i].right.get(index_right));
                            Cgramma.Follow.put(curSymbol, _Set);
                        } else {
                            _set.add(Cgramma.prod[i].right.get(index_right));
                            Cgramma.Follow.put(curSymbol, _set);
                        }
                        bool = true;
                        break;
                    }
                }
                if (bool)
                    break;
                if (index_right < Cgramma.prod[i].right.size() && !Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].right.get(index_right))) //如果右面遇到一个符号推不空了, 把它的First集也加进来
                {
                    Cgramma.Follow.put(Cgramma.prod[i].right.get(it), Cgramma.First.get(Cgramma.prod[i].right.get(index_right)));
                    for (; iter.hasNext(); it++) {
                        next = (String) iter.next();
                        if (next.equals(curSymbol)) break;
                    }
                }
                if (index_right >= Cgramma.prod[i].right.size()) //如果后面的符号全部可以推空
                {
                    equal = true;
                    if (!Cgramma.visited.get(Cgramma.prod[i].left))
                        getFollow(Cgramma.prod[i].left, Cgramma); //没有访问过，访问之
                    Set<String> _Set = new HashSet<String>();
                    if (Cgramma.Follow.get(Cgramma.prod[i].right.get(it)) != null) {
                        for (String str : Cgramma.Follow.get(Cgramma.prod[i].right.get(it))) {
                            _Set.add(str);
                        }
                    }
                    //_Set.add(Cgramma.Follow.get(Cgramma.prod[i].right.get(it)));
                    if (Cgramma.Follow.get(Cgramma.prod[i].right.get(it)) == null) {
                        Cgramma.Follow.put(Cgramma.prod[i].right.get(it), Cgramma.Follow.get(Cgramma.prod[i].left));
                    } else {
                        Set<String> temp = new HashSet<String>();
                        if (Cgramma.Follow.get(Cgramma.prod[i].left) != null) {
                            for (String str : Cgramma.Follow.get(Cgramma.prod[i].left)) {
                                temp.add(str);
                            }
                        }
                        if (temp != null) {
                            for (String str : temp) {
                                _Set.add(str);
                            }
                        }
                        Cgramma.Follow.put(Cgramma.prod[i].right.get(it), _Set);
                    }
                }
            }
        }
    }

    public static void getSelect(Syntax Cgramma) {

        for (int i = 0; i < Cgramma.size_of_prod; i++) {
            //遍历产生式集合
            //bool is_all_epsilon = false;
            int index_right = 0;
            while (index_right < Cgramma.prod[i].right.size()
                    && ((Cgramma.non_terminators.contains(Cgramma.prod[i].right.get(index_right))
                    && Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].right.get(index_right)))
                    || Cgramma.prod[i].right.get(0).equals("epsilon") || IsUpper(Cgramma.prod[i].right.get(index_right)))) {
                //如果该产生式右部首符号为非终结符 且 可以推空
                index_right++;
            }
            if (index_right >= Cgramma.prod[i].right.size()) //如果产生式右部符号全部可以推空, alpha =>* epsilon
            {
                Set<String> tmpset = new HashSet<String>();
                Set<String> first = new HashSet<String>();
                Set<String> follow = new HashSet<String>();
                if (int_first(Cgramma.prod[i].right, Cgramma) != null) {
                    for (String str : int_first(Cgramma.prod[i].right, Cgramma)) {
                        first.add(str);
                    }
                }
                first.remove("epsilon");
                if (Cgramma.Follow.get(Cgramma.prod[i].left) != null) {
                    for (String str : Cgramma.Follow.get(Cgramma.prod[i].left)) {
                        follow.add(str);
                    }
                }
                if (follow != null)
                    first.addAll(follow);
                tmpset.addAll(first);
                Cgramma.Select.put(i, tmpset);
            } else { //alpha ≠>* epsilon
                Cgramma.Select.put(i, int_first(Cgramma.prod[i].right, Cgramma));
            }
        }
    }

    public static int list_of_LL1(String A, String a, Syntax Cgramma) {
        for (int i = 0; i < Cgramma.size_of_prod; i++) {
            if (Cgramma.prod[i].left.equals(A) && Cgramma.Select.get(i).contains(a)) {
                return i;
            }
        }
        return -1;
    }

    public static void CreateLList(Syntax Cgramma) throws IOException {
        preProcess(Cgramma);
        dataRevision(Cgramma);
        DFS_First(Cgramma);
        DFS_Follow(Cgramma);
        getSelect(Cgramma);
        Set<String> tem = new HashSet<String>();
        Cgramma.terminators.add("#");
        Iterator iter1 = Cgramma.non_terminators.iterator();
        for (; iter1.hasNext(); ) {
            String str1 = (String) iter1.next();
            Iterator iter2 = Cgramma.terminators.iterator();
            for (; iter2.hasNext(); ) {
                String str2 = (String) iter2.next();
                Pair<String, String> _pair = new Pair<String, String>(str1, str2);
                Cgramma.LL1List.put(_pair, list_of_LL1(str1, str2, Cgramma));
            }
        }
    }
}