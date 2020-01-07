import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

public class Syntax {

    private final int NUM_OF_PRODUTIONS = 500; // Number of production

    public static class production {
        String left = ""; // 产生式左端
        List<String> right = new ArrayList<String>(); // 产生式右端
    }
    public static class Quaternary {
        String First;
        String Second;
        String Third;
        String Fourth;
    }

    List<String[]> code = new ArrayList<String[]>(); // 存放目标代码
    production[] prod = new production[NUM_OF_PRODUTIONS];
    LinkedList<Quaternary> qt = new LinkedList<Quaternary>();
    LinkedList<Quaternary> ActiveLable = new LinkedList<Quaternary>(); // 活跃信息表，和四元式一一对应
    int size_of_prod = 0;
    Set<String> terminators = new HashSet<String>(); // 终结符集合
    Set<String> non_terminators = new HashSet<String>(); // 非终结符集合
    Map<String, Set<String>> First = new HashMap<String, Set<String>>(); // first集合
    Map<String, Set<String>> Follow = new HashMap<String, Set<String>>(); // follow集合
    Map<Integer, Set<String>> Select = new HashMap<Integer, Set<String>>();
    Map<String, Boolean> is_deduced_epsilon = new HashMap<String, Boolean>(); // 非终结符是否可以推空
    Map<String, Boolean> visited = new HashMap<String, Boolean>();
    Map<String, List<String>> tmpStorage = new HashMap<String, List<String>>();
    Map<Pair<String, String>, Integer> LL1List = new HashMap<Pair<String, String>, Integer>();
    Map<String, Integer> Action_map = new HashMap<String, Integer>();
    Stack<Pair<String, Integer>> SEM = new Stack<Pair<String, Integer>>(); // 语义栈

}
