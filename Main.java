import java.io.IOException;

public class Main {
    static SymbolTable Table = new SymbolTable();

    public static void main(String[] args) throws IOException {
        lexAna Lex = new lexAna();
        Lex.judge();
        Table.getTable(Lex);
        Syntax Cgramma = new Syntax();
        if (SyntaxAnalysi.LL1_Analyse(Cgramma, Lex, Table)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
        SemanticAnalysis.PrintQt(Cgramma);
        Table.printtable();
        ObjectCode.Blocked(Cgramma);
        System.out.println("Success");
        toAsmCode asm = new toAsmCode();
        asm.cToAsm(Cgramma,Table);
        for(int i=0;i<asm.preAsmCode.size();i++){
            System.out.println(asm.preAsmCode.get(i));
        }
        for(int i=0;i<asm.asmCode.size();i++){
            System.out.println(asm.asmCode.get(i));
        }
        for(int j=0;j<asm.asmJump.length;j++){
            System.out.println(j+"  "+asm.asmJump[j]);
        }
    }
}
