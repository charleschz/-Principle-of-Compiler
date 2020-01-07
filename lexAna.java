import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * @auther 陈含章 20174459
 * 词法分析
 */
public class lexAna {
    static char optSingle[] = {'+', '-', '*', '/', '>', '<', '=', '%', '!', ';', ',', '[', ']', '#'};//单字符界符
    static String optDouble[] = {"++", "--", ">=", "<=", "=="};//双字符界符
    static char edge[] = {';', '{', '}', '(', ')'};//界符
    static String keyWords[] = {"if", "else", "while", "int", "switch", "break", "float", "string", "return", "struct", "char", "for", "void"};//关键字
    public List<String> I = new ArrayList<String>();//存储标识符
    public List<String> C = new ArrayList<String>();//存储字符
    public List<String> S = new ArrayList<String>();//存储字符串
    public List<String> N = new ArrayList<String>();//存储数字 由于可能有小数 所以用String存放
    public List<String> P = new ArrayList<String>();//存储符号
    public List<String> D = new ArrayList<String>();//存储双目运算符
    public List<Map> lexList = new ArrayList<Map>();//用来存放map,即存放单词和其对应的类型
    //Map<String,String> resultTemp = new HashMap<String,String>();//用来存储结果

    /**
     * 读取文件的内容
     */
    public List<String> readFile(String fileName) throws IOException {
        List<String> fileList = new ArrayList<String>();//创建容器
        FileReader fread = new FileReader(fileName);//必须加上throws IOException
        BufferedReader bfr = new BufferedReader(fread);
        String strLine = bfr.readLine();//分行读取
        int i = 1;
        while (strLine != null) {
            System.out.println("第" + i + "行:" + strLine);
            fileList.add(strLine);//将每行加入容器内
            strLine = bfr.readLine();//继续读取
            i++;
        }
        bfr.close();
        fread.close();
        return fileList;
    }

    /**
     * 用来进行词法分析 此处是核心代码
     */
    public void judge() throws IOException {
        List<String> fileList = this.readFile("C.txt");
        String firstType;
        String word;//用来记录当前单词
        for (String ele : fileList) {
            for (int i = 0; i < ele.length(); i++) {
                if (ele.charAt(i) == ' ') {
                    continue;
                }//遇到空格则滤掉
                else if ((ele.charAt(i) >= 'a' && ele.charAt(i) <= 'z') || (ele.charAt(i) >= 'A' && ele.charAt(i) <= 'Z')) {
                    //如果第一个字符是字母或者$和_的话那么可能是标识符也可能是关键字
                    firstType = "MaybeKeyOrID";
                } else if (ele.charAt(i) >= '0' && ele.charAt(i) <= '9') {
                    //如果第一个字符是数字那么可能是数字
                    firstType = "MaybeNumber";
                } else if (ele.charAt(i) == '"') {
                    //如果第一个字符是"那么可能是字符串
                    firstType = "MaybeString";
                } else if (ele.charAt(i) == '\'') {
                    //可能是字符
                    firstType = "MaybeChar";
                } else if (ele.charAt(i) == '/') {
                    //可能是注释
                    if (i < ele.length() - 1 && ele.charAt(i + 1) == '/') {
                        firstType = "MaybeNotice";
                    } else {
                        firstType = "MaybeOpt";
                    }
                } else {
                    //可能是符号
                    firstType = "MaybeOpt";
                }

                /**
                 * 接下来对每种情况进行细分
                 * 一共有六种情况
                 */
                if (firstType == "MaybeKeyOrID") {
                    Map<String, String> resultTemp = new HashMap<String, String>();//用来存储结果
                    //先判断是不是关键字
                    //再判断是不是ID
                    int type = 1;//1表示关键字或者ID 2表示ID -1表示报错
                    int j = i;
                    for (; j < ele.length(); j++) {
                        if ((ele.charAt(j) >= '0' && ele.charAt(j) <= '9') || ele.charAt(j) == '_' || ele.charAt(j) == '$') {
                            //出现数字以及_ $说明很可能是ID
                            type = 2;
                        } else if (ele.charAt(j) == ' ' || ele.charAt(j) == '(' || ele.charAt(j) == '{' || ele.charAt(j) == '+' || ele.charAt(j) == '-' || ele.charAt(j) == '*' || ele.charAt(j) == '/' ||
                                ele.charAt(j) == '%' || ele.charAt(j) == '=' || ele.charAt(j) == '>' || ele.charAt(j) == '<' || ele.charAt(j) == ')' || ele.charAt(j) == '}' || ele.charAt(j) == ';' ||
                                ele.charAt(j) == ',' || ele.charAt(j) == '[' || ele.charAt(j) == '#') {
                            //遇到空格或者界符就说明单词读完了
                            break;
                        } else if ((ele.charAt(j) >= 'a' && ele.charAt(j) <= 'z') || (ele.charAt(j) >= 'A' && ele.charAt(j) <= 'Z')) {
                            //读到字母则继续
                            continue;
                        } else {//否则出错
                            type = -1;
                        }
                    }
                    word = ele.substring(i, j);//获取读取到的单词
                    if (type == 1) {
                        if (isKeyWord(word) != -1) {
                            //result.put("keyword",Integer.toString(isKeyWord(word)));//放入结果中
                            resultTemp.put("type", "keyword");
                            resultTemp.put("index", Integer.toString(isKeyWord(word)));
                            resultTemp.put("value", word);
                            lexList.add(resultTemp);
                         /*for(int k=0;k<lexList.size();k++){
                             System.out.println(lexList.get(k));
                         }
                         System.out.println("完成一次");*/
                        } else if (isKeyWord(word) == -1) {//如果不是关键字
                            if (!I.contains(word)) {
                                I.add(word);
                            }
                            resultTemp.put("type", "id");
                            resultTemp.put("index", Integer.toString(I.indexOf(word)));
                            resultTemp.put("value", word);
                            lexList.add(resultTemp);
                        }
                    } else if (type == 2) {
                        if (!I.contains(word)) {
                            I.add(word);
                        }
                        resultTemp.put("type", "id");
                        resultTemp.put("index", Integer.toString(I.indexOf(word)));
                        resultTemp.put("value", word);
                        lexList.add(resultTemp);
                    } else if (type == -1) {
                        System.out.println(word + " 有误");
                    }
                    i = j - 1;
                } else if (firstType == "MaybeNumber") {
                    Map<String, String> resultTemp = new HashMap<String, String>();//用来存储结果
                    int j = i;
                    int type = 1;//-1有误
                    for (; j < ele.length(); j++) {
                        if (ele.charAt(j) == ';' || ele.charAt(j) == ' ' || ele.charAt(j) == '+' || ele.charAt(j) == '-' || ele.charAt(j) == '*' || ele.charAt(j) == '/' ||
                                ele.charAt(j) == '%' || ele.charAt(j) == '>' || ele.charAt(j) == '<' || ele.charAt(j) == '=' || ele.charAt(j) == ',' || ele.charAt(j) == ']'
                                || ele.charAt(j) == '#' || ele.charAt(j) == ')' || ele.charAt(j) == '(' || ele.charAt(j) == '{' || ele.charAt(j) == '}') {
                            break;
                        } else if (ele.charAt(j) == '.') {
                            continue;
                        } else {
                            type = -1;
                        }
                    }
                    word = ele.substring(i, j);//获取读取到的单词
                    if (isANum(word)) {
                     /* if(N.contains(word)){
                          //result.put("num",Integer.toString(N.indexOf(word)));
                          resultTemp.put("type","num");
                          resultTemp.put("index",Integer.toString(N.indexOf(word)));
                          lexList.add(resultTemp);
                      }
                      else{
                          N.add(word);
                          result.put("num",Integer.toString(N.indexOf(word)));
                      }*/
                        if (!N.contains(word)) {
                            N.add(word);
                        }
                        resultTemp.put("type", "num");
                        resultTemp.put("index", Integer.toString(N.indexOf(word)));
                        resultTemp.put("value", word);
                        lexList.add(resultTemp);
                    } else {
                        System.out.println(word + " 有误");
                    }
                    i = j - 1;
                } else if (firstType == "MaybeString") {
                    Map<String, String> resultTemp = new HashMap<String, String>();//用来存储结果
                    int j = i + 1;
                    for (; j < ele.length(); j++) {
                        if (ele.charAt(j) == '"') {
                            break;
                        }
                    }
                    word = ele.substring(i + 1, j);
                    if (!S.contains(word)) {
                        S.add(word);
                    }
                    resultTemp.put("type", "string");
                    resultTemp.put("index", Integer.toString(S.indexOf(word)));
                    resultTemp.put("value", word);
                    lexList.add(resultTemp);
                    i = j;
                } else if (firstType == "MaybeChar") {
                    Map<String, String> resultTemp = new HashMap<String, String>();//用来存储结果
                    int j = i + 1;
                    for (; j < ele.length(); j++) {
                        if (ele.charAt(j) == '\'') {
                            break;
                        }
                    }
                    word = ele.substring(i + 1, j);
                    if (word.length() > 1) {
                        System.out.println(word + "是非法字符");
                    } else {
                        if (!C.contains(word)) {
                            C.add(word);
                        }
                        resultTemp.put("type", "char");
                        resultTemp.put("index", Integer.toString(C.indexOf(word)));
                        resultTemp.put("value", "'" +word+ "'");
                        lexList.add(resultTemp);
                    }
                    i = j;
                } else if (firstType == "MaybeNotice") {
                    i = ele.length();
                    /**
                     * 此处也可以对注释做文章 但是由于时间关系不再细分
                     */
                }

                /**
                 * 运算符的情况
                 */
                else if (firstType == "MaybeOpt") {
                    Map<String, String> resultTemp = new HashMap<String, String>();//用来存储结果
                    if (i < ele.length() - 1) {
                        if ((ele.charAt(i + 1) == '+' && ele.charAt(i) == '+') || (ele.charAt(i + 1) == '-' && ele.charAt(i) == '-')
                                || (ele.charAt(i + 1) == '=' && ele.charAt(i) == '=')
                                || (ele.charAt(i + 1) == '=' && (ele.charAt(i) == '>' || ele.charAt(i) == '<'))) {
                            word = ele.substring(i, i + 2);
                        } else {
                            word = ele.substring(i, i + 1);
                        }
                    } else {
                        word = ele.substring(i, i + 1);
                    }
                    if (isOpt(ele.charAt(i)) != -1 && word.length() == 1) {
                        P.add(word);
                        //result.put("opt",Integer.toString(isOpt(ele.charAt(i))));
                        resultTemp.put("type", "opt");
                        resultTemp.put("index", Integer.toString(isOpt(ele.charAt(i))));
                        resultTemp.put("value", word);
                        lexList.add(resultTemp);
                    } else if (word.length() == 2 && isDoubleOpt(word) != -1) {
                        D.add(word);
                        resultTemp.put("type", "dopt");
                        resultTemp.put("index", Integer.toString(isDoubleOpt(word)));
                        resultTemp.put("value", word);
                        lexList.add(resultTemp);
                        i++;

                    } else {
                        System.out.println(word + " 有误");
                    }
                }
            }
        }
        Map<String, String> TheLast = new HashMap<String, String>();
        TheLast.put("type", "opt");
        TheLast.put("index", "17");
        TheLast.put("value", "#");
        lexList.add(TheLast);
    }

    /**
     * 此函数用来判断是不是关键字
     *
     * @return int
     */
    public int isKeyWord(String word) {
        //返回1表示是关键字  返回0不是关键字
        int isKW = -1;
        switch (word) {
            case "if"://如果处理的单词是if
                isKW = 0;
                break;

            case "else"://如果处理的单词是else
                isKW = 1;
                break;

            case "while"://如果处理的单词是while
                isKW = 2;
                break;

            case "int"://如果处理的单词是int
                isKW = 3;
                break;

            case "switch"://如果处理的单词是switch
                isKW = 4;
                break;

            case "break"://如果处理的单词是main
                isKW = 5;
                break;

            case "float"://如果处理的单词是float
                isKW = 6;
                break;

            case "string"://如果处理的单词是string
                isKW = 7;
                break;

            case "return"://如果处理的单词是return
                isKW = 8;
                break;

            case "struct"://如果处理的单词是struct
                isKW = 9;
                break;

            case "char"://如果处理的单词是char
                isKW = 10;
                break;

            case "for"://如果处理的单词是for
                isKW = 11;
                break;

            case "void"://如果处理的单词是string
                isKW = 12;
                break;
        }
        return isKW;
    }

    public boolean isANum(String word) {
        int isFloat = 0;//用来记录是不是小数，如果有两个小数点则表明是数据有误
        for (int i = 1; i < word.length(); i++) {
            //是数字的情况
            if (word.charAt(i) >= '0' && word.charAt(i) <= '9') {
                continue;
            } else if (word.charAt(i) == '.') {
                if (isFloat == 0) {//如果之前没出现过小数点
                    isFloat++;
                    continue;
                } else {
                    return false;
                }//出现过小数点后又出现了一遍则直接报错
            } else {
                return false;
            }//出现除数字和小数点之外的符号
        }
        return true;
    }

    public int isOpt(char opt) {
        //返回1表示是关键字  返回0不是关键字
        int isOP = -1;
        switch (opt) {
            case '+'://如果处理的单词是+
                isOP = 0;
                break;

            case '-'://如果处理的单词是-
                isOP = 1;
                break;

            case '*'://如果处理的单词是*
                isOP = 2;
                break;

            case '/'://如果处理的单词是/
                isOP = 3;
                break;

            case '>'://如果处理的单词是>
                isOP = 4;
                break;

            case '<'://如果处理的单词是<
                isOP = 5;
                break;

            case '='://如果处理的单词是=
                isOP = 6;
                break;

            case '%'://如果处理的单词是%
                isOP = 7;
                break;

            case '!'://如果处理的单词是!
                isOP = 8;
                break;

            case ';'://如果处理的单词是;
                isOP = 9;
                break;

            case '('://如果处理的单词是;
                isOP = 10;
                break;

            case ')'://如果处理的单词是;
                isOP = 11;
                break;

            case '{'://如果处理的单词是;
                isOP = 12;
                break;

            case '}'://如果处理的单词是;
                isOP = 13;
                break;

            case ','://如果处理的单词是，
                isOP = 14;
                break;

            case '['://如果处理的单词是[
                isOP = 15;
                break;

            case ']'://如果处理的单词是[
                isOP = 16;
                break;

            case '#'://如果处理的单词是[
                isOP = 17;
                break;
        }
        return isOP;
    }

    public int isDoubleOpt(String word) {
        int isDoubleOpt = -1;
        switch (word) {
            case "++":
                isDoubleOpt = 0;
                break;

            case "--":
                isDoubleOpt = 1;
                break;

            case ">=":
                isDoubleOpt = 2;
                break;

            case "<=":
                isDoubleOpt = 3;
                break;

            case "==":
                isDoubleOpt = 4;
                break;
        }
        return isDoubleOpt;
    }

    public static void main(String[] args) throws IOException {
        lexAna lex = new lexAna();
        lex.judge();
        for (int i = 0; i < lex.lexList.size(); i++) {
            System.out.println(lex.lexList.get(i).get("value")+": (" + lex.lexList.get(i).get("type") + "," + lex.lexList.get(i).get("index") + ")");
        }
        /* for(String elem:lex.D){
           System.out.println(elem);
        }  */
    }
}