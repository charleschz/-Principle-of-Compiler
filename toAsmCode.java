import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cgrammma采用传入方式
 *
 */
public class toAsmCode {
    public ArrayList<String>asmCode = new ArrayList<String>();//存储汇编代码
    public ArrayList<String>preAsmCode = new ArrayList<String>();//存储汇编代码
    public String RDL = "";
    public int asmCount = 0;//跳转计数器
    public int[] asmJump;//记录每个四元式对应的第一条指令位置
    public String[] jumpWord={"JAE","JMP","JB","JNE","JA","JBE"};
    //public int index = 0;


    public void cToAsm(Syntax Cgramma,SymbolTable table){
        asmJump = new int[Cgramma.qt.size()+1];
        ObjectCode.Blocked(Cgramma);
        preAsmCode.add("ASSUME CS:CODE,DS:DATAS");
        int whereEnds = -1;
        for(int i=0;i<table.Synbl.size();i++){
            if(table.Synbl.get(i).type.equals("struct")){//如果是struct的情况
                preAsmCode.add(table.Synbl.get(i).name+" STRUC");
                whereEnds = i;
                for(int j=0;j<table.Rinfl.size();j++){
                    if(table.Rinfl.get(j).type.equals("int")||table.Rinfl.get(j).type.equals("char")){
                        //如果是int或者char型 则分配一个字节
                        preAsmCode.add(table.Rinfl.get(j).name+"   DB   ?");
                    }
                    else if(table.Rinfl.get(j).type.equals("int[]")||table.Rinfl.get(j).type.equals("char[]")){
                        //如果是数组类型的变量
                        preAsmCode.add(table.Rinfl.get(j).name+"   DB   "+table.Rinfl.get(j).tp+" DUP(0)");
                    }
                }
            }
        }
        if(whereEnds!=-1){
            preAsmCode.add(table.Synbl.get(whereEnds).name+" ENDS");
        }


        preAsmCode.add("DATAS SEGMENT");
        for(int i=0;i<table.Synbl.size();i++){
            if(table.Synbl.get(i).type.contains("[")){
                preAsmCode.add(table.Synbl.get(i).name+"   DB   "+table.Synbl.get(i).tp+" DUP(0)");
            }
        }
        for(int i=0;i<table.Synbl.size();i++){
            if(table.Synbl.get(i).type.equals("int")||table.Synbl.get(i).type.equals("char")){
                preAsmCode.add(table.Synbl.get(i).name+"   DB   ?");
            }
        }
        preAsmCode.add("DATAS ENDS");
        preAsmCode.add("CODE SEGMENT");
        preAsmCode.add("START:MOV AX,DATAS");
        preAsmCode.add("      MOV DS,AX");

        for(int i=0;i<Cgramma.qt.size();i++){
            Syntax.Quaternary temp = Cgramma.qt.get(i);
            if(temp.First.equals("+")){//如果四元式的首符号是+
                if(RDL.equals("")){
                    asmCode.add("      MOV AL,"+temp.Second);
                    asmCode.add("      ADD AL,"+temp.Third);
                    asmJump[i]=asmCount;
                    asmCount+=2;
                }
                else if(RDL.equals(temp.Second)){
                    if(Cgramma.ActiveLable.get(i).Second.equals("Active")){
                        asmCode.add("      MOV "+temp.Second+",AL");
                        asmCode.add("      ADD AL,"+temp.Third);
                        asmJump[i]=asmCount;
                        asmCount+=2;
                    }
                    else{
                        asmCode.add("      ADD AL,"+temp.Third);
                        asmJump[i]=asmCount;
                        asmCount+=1;

                    }

                }
                else if(RDL.equals(temp.Third)){
                    if(Cgramma.ActiveLable.get(i).Third.equals("Active")){
                        //THIRD活跃 要存入内存中 后面还会使用
                        asmCode.add("      MOV "+temp.Third+",AL");
                        asmCode.add("      ADD AL,"+temp.Second);
                        asmJump[i]=asmCount;
                        asmCount+=2;
                    }
                    else{
                        asmCode.add("      ADD AL,"+temp.Second);
                        asmJump[i]=asmCount;
                        asmCount+=1;
                    }
                }
                else{
                    String s=getIsActive(Cgramma,RDL);
                    if(s.equals("Active")){
                        if(RDL.contains("[")){
                            //如果是数组，则需要特殊处理
                            String[] getArrayInfo = RDL.split("\\[");//用[分隔开
                            int arrayLength = getArrayInfo[1].length();
                            String arrayPos = getArrayInfo[1].substring(0,arrayLength-1);//获取数组内数字
                            asmCode.add("     MOV BX,OFFSET "+getArrayInfo[0]);
                            asmCode.add("     ADD BX,"+arrayPos);
                            asmCode.add("     MOV [BX],AL");
                            asmCode.add("     MOV AL,"+Cgramma.qt.get(i).Second);
                            asmCode.add("     ADD AL,"+Cgramma.qt.get(i).Third);
                            asmJump[i]=asmCount;
                            asmCount+=5;
                        }
                        else{
                            asmCode.add("      MOV "+RDL+", AL");
                            asmCode.add("      MOV AL,"+Cgramma.qt.get(i).Second);
                            asmCode.add("      ADD AL,"+Cgramma.qt.get(i).Third);
                            asmJump[i]=asmCount;
                            asmCount+=3;
                        }

                    }
                    else if(s.equals("NonActive")){//???nosense
                        asmCode.add("      MOV AL,"+Cgramma.qt.get(i).Second);
                        asmCode.add("      ADD AL,"+Cgramma.qt.get(i).Third);
                        asmJump[i]=asmCount;
                        asmCount+=2;
                    }
                    //可能活跃 要存入内存中 后面还会使用
                }
                RDL = temp.Fourth;
            }


            else if(temp.First.equals("-")){//如果四元式的首符号是-
                if(RDL.equals("")){
                    asmCode.add("      MOV AL,"+temp.Second);
                    asmCode.add("      SUB AL,"+temp.Third);
                    asmJump[i]=asmCount;
                    asmCount+=2;
                }
                else if(RDL.equals(temp.Second)){
                    if(Cgramma.ActiveLable.get(i).Second.equals("Active")){
                        asmCode.add("      MOV "+temp.Second+",AL");
                        asmCode.add("      SUB AL,"+temp.Third);
                        asmJump[i]=asmCount;
                        asmCount+=2;
                    }
                    else{
                        asmCode.add("      SUB AL,"+temp.Third);
                        asmJump[i]=asmCount;
                        asmCount+=1;
                    }
                }
                else{
                    String s=getIsActive(Cgramma,RDL);
                    if(s.equals("Active")){
                        if(RDL.contains("[")){
                            //如果是数组，则需要特殊处理
                            String[] getArrayInfo = RDL.split("\\[");//用[分隔开
                            int arrayLength = getArrayInfo[1].length();
                            String arrayPos = getArrayInfo[1].substring(0,arrayLength-1);//获取数组内数字
                            asmCode.add("     MOV BX,OFFSET "+getArrayInfo[0]);
                            asmCode.add("     ADD BX,"+arrayPos);
                            asmCode.add("     MOV [BX],AL");
                            asmCode.add("     MOV AL,"+Cgramma.qt.get(i).Second);
                            asmCode.add("     SUB AL,"+Cgramma.qt.get(i).Third);
                            asmJump[i]=asmCount;
                            asmCount+=5;
                        }
                        else{
                            asmCode.add("      MOV "+RDL+", AL");
                            asmCode.add("      MOV AL,"+Cgramma.qt.get(i).Second);
                            asmCode.add("      SUB AL,"+Cgramma.qt.get(i).Third);
                            asmJump[i]=asmCount;
                            asmCount+=3;
                        }
                    }
                    else if(s.equals("NonActive")){//???nosense
                        asmCode.add("      MOV AL,"+Cgramma.qt.get(i).Second);
                        asmCode.add("      SUB AL,"+Cgramma.qt.get(i).Third);
                        asmJump[i]=asmCount;
                        asmCount+=2;
                    }
                    //可能活跃 要存入内存中 后面还会使用
                }
                RDL = temp.Fourth;
            }

            else if(temp.First.equals("*")){//如果四元式的首符号是*
                if(RDL.equals("")){
                    asmCode.add("      MOV AX,0000H");
                    asmCode.add("      MOV AL,"+temp.Second);
                    asmCode.add("      MUL "+temp.Third);
                    asmJump[i]=asmCount;
                    asmCount+=3;
                }
                else if(RDL.equals(temp.Second)){
                    if(Cgramma.ActiveLable.get(i).Second.equals("Active")){
                        asmCode.add("      XOR AH,AH");
                        asmCode.add("      MOV "+temp.Second+",AL");
                        asmCode.add("      MUL "+temp.Third);
                        asmJump[i]=asmCount;
                        asmCount+=3;
                    }
                    else{
                        asmCode.add("      XOR AH,AH");
                        asmCode.add("      MUL "+temp.Third);
                        asmJump[i]=asmCount;
                        asmCount+=2;

                    }

                }
                else if(RDL.equals(temp.Third)){
                    if(Cgramma.ActiveLable.get(i).Third.equals("Active")){
                        //THIRD活跃 要存入内存中 后面还会使用
                        asmCode.add("      XOR AH,AH");
                        asmCode.add("      MOV "+temp.Third+",AL");
                        asmCode.add("      MUL "+temp.Second);
                        asmJump[i]=asmCount;
                        asmCount+=3;
                    }
                    else{
                        asmCode.add("      XOR AH,AH");
                        asmCode.add("      MUL "+temp.Second);
                        asmJump[i]=asmCount;
                        asmCount+=2;
                    }
                }
                else{
                    String s=getIsActive(Cgramma,RDL);
                    if(s.equals("Active")){
                        if(RDL.contains("[")){
                            //如果是数组，则需要特殊处理
                            String[] getArrayInfo = RDL.split("\\[");//用[分隔开
                            int arrayLength = getArrayInfo[1].length();
                            String arrayPos = getArrayInfo[1].substring(0,arrayLength-1);//获取数组内数字
                            asmCode.add("     MOV BX,OFFSET "+getArrayInfo[0]);
                            asmCode.add("     ADD BX,"+arrayPos);
                            asmCode.add("     MOV [BX],AL");
                            asmCode.add("     MOV AL,"+Cgramma.qt.get(i).Second);
                            asmCode.add("     MUL "+Cgramma.qt.get(i).Third);
                            asmJump[i]=asmCount;
                            asmCount+=5;
                        }
                        else{
                            asmCode.add("      MOV "+RDL+", AL");
                            asmCode.add("      XOR AH,AH");
                            asmCode.add("      MOV AL,"+Cgramma.qt.get(i).Second);
                            asmCode.add("      MUL "+Cgramma.qt.get(i).Third);
                            asmJump[i]=asmCount;
                            asmCount+=4;
                        }
                    }
                    else if(s.equals("NonActive")){
                        asmCode.add("      XOR AH,AH");
                        asmCode.add("      MOV AL,"+Cgramma.qt.get(i).Second);
                        asmCode.add("      MUL "+Cgramma.qt.get(i).Third);
                        asmJump[i]=asmCount;
                        asmCount+=3;
                    }
                    //可能活跃 要存入内存中 后面还会使用
                }
                RDL = temp.Fourth;
            }


            else if(temp.First.equals("/")){//如果四元式的首符号是/
                if(RDL.equals("")){
                    asmCode.add("      MOV AX,0000H");
                    asmCode.add("      MOV AL,"+temp.Second);
                    asmCode.add("      DIV "+temp.Third);
                    asmJump[i]=asmCount;
                    asmCount+=3;
                }
                else if(RDL.equals(temp.Second)){
                    if(Cgramma.ActiveLable.get(i).Second.equals("Active")){
                        asmCode.add("      XOR AH,AH");
                        asmCode.add("      MOV "+temp.Second+",AL");
                        asmCode.add("      DIV "+temp.Third);
                        asmJump[i]=asmCount;
                        asmCount+=3;
                    }
                    else{
                        asmCode.add("      XOR AH,AH");
                        asmCode.add("      MUL "+temp.Third);
                        asmJump[i]=asmCount;
                        asmCount+=2;

                    }

                }
                else{
                    String s=getIsActive(Cgramma,RDL);
                    if(s.equals("Active")){
                        if(RDL.contains("[")){
                            //如果是数组，则需要特殊处理
                            String[] getArrayInfo = RDL.split("\\[");//用[分隔开
                            int arrayLength = getArrayInfo[1].length();
                            String arrayPos = getArrayInfo[1].substring(0,arrayLength-1);//获取数组内数字
                            asmCode.add("     MOV BX,OFFSET "+getArrayInfo[0]);
                            asmCode.add("     ADD BX,"+arrayPos);
                            asmCode.add("     MOV [BX],AL");
                            asmCode.add("     MOV AL,"+Cgramma.qt.get(i).Second);
                            asmCode.add("     DIV "+Cgramma.qt.get(i).Third);
                            asmJump[i]=asmCount;
                            asmCount+=5;
                        }
                        else{
                            asmCode.add("      MOV "+RDL+", AL");
                            asmCode.add("      XOR AH,AH");
                            asmCode.add("      MOV AL,"+Cgramma.qt.get(i).Second);
                            asmCode.add("      DIV "+Cgramma.qt.get(i).Third);
                            asmJump[i]=asmCount;
                            asmCount+=4;
                        }

                    }
                    else if(s.equals("NonActive")){
                        asmCode.add("      XOR AH,AH");
                        asmCode.add("      MOV AL,"+Cgramma.qt.get(i).Second);
                        asmCode.add("      DIV "+Cgramma.qt.get(i).Third);
                        asmJump[i]=asmCount;
                        asmCount+=3;
                    }
                    //可能活跃 要存入内存中 后面还会使用
                }
                RDL = temp.Fourth;
            }


            else if(Cgramma.qt.get(i).First.equals("=")){//赋值语句
                if(RDL.equals("")){
                    //if(Cgramma.qt.get(i).Fourth.contains("[")){
                    //如果是数组赋值
                    //String getArrayInfo[] = Cgramma.qt.get(i).Fourth.split(" ");
                    //int arrayLength = getArrayInfo[1].length();
                    // arrayPos = getArrayInfo[1].substring(1,arrayLength-1);
                    // int findWhere = Integer.valueOf(arrayPos);
                    //findWhere = findWhere*2;
                    //asmCode.add("      XOR AX,AX");//清零
                    //asmCode.add("      MOV AX,"+getArrayInfo[0]);//移入地址
                    //asmCode.add("      MOV DS,AX");
                    //asmCode.add("      MOV BX,0");
                    //asmCode.add("      ADD BX,"+findWhere);
                    // asmCode.add("      XOR AX,AX");
                    //.add("      MOV AL,"+Cgramma.qt.get(i).Second);
                    // asmCode.add("      MOV [BX],AL");
                    // }
                    asmCode.add("      XOR AX,AX");
                    asmCode.add("      MOV AL,"+Cgramma.qt.get(i).Second);
                    asmJump[i]=asmCount;
                    asmCount+=2;

                }
                else if(RDL.equals(Cgramma.qt.get(i).Second)){
                    if(Cgramma.ActiveLable.get(i).Second.equals("Active")){
                        asmCode.add("      MOV "+Cgramma.qt.get(i).Second+",AL");
                        asmJump[i]=asmCount;
                        asmCount+=1;
                    }

                }
                else{
                    String s = getIsActive(Cgramma,RDL);
                    if(s.equals("Active")){
                        if(RDL.contains("[")){
                            //如果是数组，则需要特殊处理
                            String[] getArrayInfo = RDL.split("\\[");//用[分隔开
                            int arrayLength = getArrayInfo[1].length();
                            String arrayPos = getArrayInfo[1].substring(0,arrayLength-1);//获取数组内数字
                            asmCode.add("     MOV BX,OFFSET "+getArrayInfo[0]);
                            asmCode.add("     ADD BX,"+arrayPos);
                            asmCode.add("     MOV [BX],AL");
                            asmCode.add("     MOV AL,"+Cgramma.qt.get(i).Second);
                            asmJump[i]=asmCount;
                            asmCount+=4;
                        }
                        else{
                            asmCode.add("      MOV "+RDL+", AL");
                            asmCode.add("      XOR AH,AH");
                            asmCode.add("      MOV AL,"+Cgramma.qt.get(i).Second);
                            asmJump[i]=asmCount;
                            asmCount+=3;
                        }
                    }

                    else if(s.equals("NonActive")){
                        asmCode.add("      XOR AH,AH");
                        asmCode.add("      MOV AL,"+Cgramma.qt.get(i).Second);
                        asmJump[i]=asmCount;
                        asmCount+=2;
                    }
                    //可能活跃 要存入内存中 后面还会使用
                }
                RDL=temp.Fourth;
            }



            else if(temp.First.equals("==")){
                if(RDL.equals("")){
                    asmCode.add("      MOV BL,"+temp.Second);
                    asmCode.add("      CMP BL,"+temp.Third);
                    asmCode.add("JNE "+Cgramma.qt.get(i+1).Fourth);
                    asmJump[i] = asmCount;
                    asmCount+=3;
                }
                else{
                    String s = getIsActive(Cgramma,RDL);
                    if(s.equals("Active")){
                        if(RDL.contains("[")){
                            //如果是数组，则需要特殊处理
                            String[] getArrayInfo = RDL.split("\\[");//用[分隔开
                            int arrayLength = getArrayInfo[1].length();
                            //System.out.println(arrayLength);
                            String arrayPos = getArrayInfo[1].substring(0,arrayLength-1);//获取数组内数字
                            asmCode.add("     MOV BX,OFFSET "+getArrayInfo[0]);
                            asmCode.add("     ADD BX,"+arrayPos);
                            asmCode.add("     MOV [BX],AL");
                            RDL="";
                            asmCode.add("      MOV BL,"+temp.Second);
                            asmCode.add("      CMP BL,"+temp.Third);
                            asmCode.add("JNE "+Cgramma.qt.get(i+1).Fourth);
                            asmJump[i] = asmCount+3;
                            asmCount+=6;
                        }
                        else{
                            asmCode.add("     MOV "+RDL+",AL");
                            RDL="";
                            asmCode.add("      MOV BL,"+temp.Second);
                            asmCode.add("      CMP BL,"+temp.Third);
                            asmCode.add("JNE "+Cgramma.qt.get(i+1).Fourth);
                            asmJump[i] = asmCount+1;
                            asmCount+=4;
                        }
                    }
                    RDL="";
                }
            }

            else if(temp.First.equals(">")){
                if(RDL.equals("")){
                    asmCode.add("      MOV BL,"+temp.Second);
                    asmCode.add("      CMP BL,"+temp.Third);
                    asmCode.add("JBE "+Cgramma.qt.get(i+1).Fourth);
                    asmJump[i] = asmCount;
                    asmCount+=3;
                }
                else{
                    String s = getIsActive(Cgramma,RDL);
                    if(s.equals("Active")){
                        if(RDL.contains("[")){
                            //如果是数组，则需要特殊处理
                            String[] getArrayInfo = RDL.split("\\[");//用[分隔开
                            int arrayLength = getArrayInfo[1].length();
                            String arrayPos = getArrayInfo[1].substring(0,arrayLength-1);//获取数组内数字
                            asmCode.add("     MOV BX,OFFSET "+getArrayInfo[0]);
                            asmCode.add("     ADD BX,"+arrayPos);
                            asmCode.add("     MOV [BX],AL");
                            RDL="";
                            asmCode.add("      MOV BL,"+temp.Second);
                            asmCode.add("      CMP BL,"+temp.Third);
                            asmCode.add("JBE "+Cgramma.qt.get(i+1).Fourth);
                            asmJump[i] = asmCount+3;
                            asmCount+=6;
                        }
                        else{
                            asmCode.add("     MOV "+RDL+",AL");
                            RDL="";
                            asmCode.add("      MOV BL,"+temp.Second);
                            asmCode.add("      CMP BL,"+temp.Third);
                            asmCode.add("JBE "+Cgramma.qt.get(i+1).Fourth);
                            asmJump[i] = asmCount+1;
                            asmCount+=4;
                        }
                    }
                    RDL="";
                }

            }

            else if(temp.First.equals("<")){
                if(RDL.equals("")){
                    asmCode.add("      MOV BL,"+temp.Second);
                    asmCode.add("      CMP BL,"+temp.Third);
                    asmCode.add("JAE "+Cgramma.qt.get(i+1).Fourth);
                    asmJump[i] = asmCount;
                    asmCount+=3;
                }
                else{
                    String s = getIsActive(Cgramma,RDL);
                    if(s.equals("Active")){
                        if(RDL.contains("[")){
                            //如果是数组，则需要特殊处理
                            String[] getArrayInfo = RDL.split("\\[");//用[分隔开
                            int arrayLength = getArrayInfo[1].length();
                            String arrayPos = getArrayInfo[1].substring(0,arrayLength-1);//获取数组内数字
                            asmCode.add("     MOV BX,OFFSET "+getArrayInfo[0]);
                            asmCode.add("     ADD BX,"+arrayPos);
                            asmCode.add("     MOV [BX],AL");
                            RDL="";
                            asmCode.add("      MOV BL,"+temp.Second);
                            asmCode.add("      CMP BL,"+temp.Third);
                            asmCode.add("JAE "+Cgramma.qt.get(i+1).Fourth);
                            asmJump[i] = asmCount+3;
                            asmCount+=6;
                        }
                        else{
                            asmCode.add("     MOV "+RDL+",AL");
                            RDL="";
                            asmCode.add("      MOV BL,"+temp.Second);
                            asmCode.add("      CMP BL,"+temp.Third);
                            asmCode.add("JAE "+Cgramma.qt.get(i+1).Fourth);
                            asmJump[i] = asmCount+1;
                            asmCount+=4;
                        }
                    }
                    RDL="";
                }

            }

            else if(temp.First.equals(">=")){
                if(RDL.equals("")){
                    asmCode.add("      MOV BL,"+temp.Second);
                    asmCode.add("      CMP BL,"+temp.Third);
                    asmCode.add("JB "+Cgramma.qt.get(i+1).Fourth);
                    asmJump[i] = asmCount;
                    asmCount+=3;
                }
                else{
                    String s = getIsActive(Cgramma,RDL);
                    if(s.equals("Active")){
                        if(RDL.contains("[")){
                            //如果是数组，则需要特殊处理
                            String[] getArrayInfo = RDL.split("\\[");//用[分隔开
                            int arrayLength = getArrayInfo[1].length();
                            String arrayPos = getArrayInfo[1].substring(0,arrayLength-1);//获取数组内数字
                            asmCode.add("     MOV BX,OFFSET "+getArrayInfo[0]);
                            asmCode.add("     ADD BX,"+arrayPos);
                            asmCode.add("     MOV [BX],AL");
                            RDL="";
                            asmCode.add("      MOV BL,"+temp.Second);
                            asmCode.add("      CMP BL,"+temp.Third);
                            asmCode.add("JB "+Cgramma.qt.get(i+1).Fourth);
                            asmJump[i] = asmCount+3;
                            asmCount+=6;
                        }
                        else{
                            asmCode.add("     MOV "+RDL+",AL");
                            RDL="";
                            asmCode.add("      MOV BL,"+temp.Second);
                            asmCode.add("      CMP BL,"+temp.Third);
                            asmCode.add("JB "+Cgramma.qt.get(i+1).Fourth);
                            asmJump[i] = asmCount+1;
                            asmCount+=4;
                        }
                    }
                    RDL="";
                }

            }

            else if(temp.First.equals("<=")){
                if(RDL.equals("")){
                    asmCode.add("      MOV BL,"+temp.Second);
                    asmCode.add("      CMP BL,"+temp.Third);
                    asmCode.add("JA "+Cgramma.qt.get(i+1).Fourth);
                    asmJump[i] = asmCount;
                    asmCount+=3;
                }
                else{
                    String s = getIsActive(Cgramma,RDL);
                    if(s.equals("Active")){
                        if(RDL.contains("[")){
                            //如果是数组，则需要特殊处理
                            String[] getArrayInfo = RDL.split("\\[");//用[分隔开
                            int arrayLength = getArrayInfo[1].length();
                            String arrayPos = getArrayInfo[1].substring(0,arrayLength-1);//获取数组内数字
                            asmCode.add("     MOV BX,OFFSET "+getArrayInfo[0]);
                            asmCode.add("     ADD BX,"+arrayPos);
                            asmCode.add("     MOV [BX],AL");
                            RDL="";
                            asmCode.add("      MOV BL,"+temp.Second);
                            asmCode.add("      CMP BL,"+temp.Third);
                            asmCode.add("JA "+Cgramma.qt.get(i+1).Fourth);
                            asmJump[i] = asmCount+3;
                            asmCount+=6;
                        }
                        else{
                            asmCode.add("     MOV "+RDL+",AL");
                            RDL="";
                            asmCode.add("      MOV BL,"+temp.Second);
                            asmCode.add("      CMP BL,"+temp.Third);
                            asmCode.add("JA "+Cgramma.qt.get(i+1).Fourth);
                            asmJump[i] = asmCount+1;
                            asmCount+=4;
                        }
                    }
                    RDL="";
                }
            }

            else if(temp.First.equals("ie")||temp.First.equals("wh")){
                String s=getIsActive(Cgramma,RDL);
                if(s.equals("Active")){
                    if(RDL.contains("[")){
                        //如果是数组，则需要特殊处理
                        String[] getArrayInfo = RDL.split("\\[");//用[分隔开
                        int arrayLength = getArrayInfo[1].length();
                        String arrayPos = getArrayInfo[1].substring(0,arrayLength-1);//获取数组内数字
                        asmCode.add("     MOV BX,OFFSET "+getArrayInfo[0]);
                        asmCode.add("     ADD BX,"+arrayPos);
                        asmCode.add("     MOV [BX],AL");
                        RDL="";
                        asmJump[i] = asmCount+3;
                        asmCount+=3;
                    }
                    else{
                        asmCode.add("      MOV "+RDL+", AL");
                        RDL="";
                        asmJump[i]=asmCount+1;
                        asmCount+=1;
                    }
                }
                else{
                    asmJump[i]=asmCount;
                }
            }


            else if(temp.First.equals("el")||temp.First.equals("we")){
                String s=getIsActive(Cgramma,RDL);
                if(s.equals("Active")){
                    if(RDL.contains("[")){
                        //如果是数组，则需要特殊处理
                        String[] getArrayInfo = RDL.split("\\[");//用[分隔开
                        int arrayLength = getArrayInfo[1].length();
                        String arrayPos = getArrayInfo[1].substring(0,arrayLength-1);//获取数组内数字
                        asmCode.add("     MOV BX,OFFSET "+getArrayInfo[0]);
                        asmCode.add("     ADD BX,"+arrayPos);
                        asmCode.add("     MOV [BX],AL");
                        RDL="";
                        asmCode.add("JMP "+temp.Fourth);
                        asmJump[i] = asmCount+3;
                        asmCount+=4;
                    }

                    else{
                        asmCode.add("      MOV "+RDL+", AL");
                        RDL="";
                        asmCode.add("JMP "+temp.Fourth);
                        asmJump[i]=asmCount+1;
                        asmCount+=2;
                    }

                }
                else {
                    asmCode.add("JMP "+temp.Fourth);
                    asmJump[i]=asmCount;
                    asmCount+=1;
                }
            }
        }
        String getFirCode = "";
        asmJump[Cgramma.qt.size()]=asmCode.size();
        asmCode.add("      MOV AH,4CH");
        asmCode.add("      INT 21H");
        asmCode.add("      CODE ENDS");
        asmCode.add("      END START");

        int jumpCount=0;//记录跳转位置
       /* for(int j=0;j<asmCode.size();j++){
            if(asmCode.get(j).charAt(0)=='J'){//如果跳转语句
                String getJumpNum[] = asmCode.get(j).split(" ");
                String toWhere = getJumpNum[1];
                if(toWhere.charAt(0)!='T'&&asmJump[Integer.valueOf(toWhere)]!=-1){
                    asmCode.set(j,getJumpNum[0]+" TURN"+jumpCount);
                    String temp = asmCode.get(Integer.valueOf(toWhere));
                    asmCode.set(asmJump[Integer.valueOf(toWhere)],"TURN"+jumpCount+": "+temp);
                    for(int k=j+1;k<asmCode.size();k++){
                        if(asmCode.get(k).charAt(0)=='J'){
                            String getJumpNum1[] = asmCode.get(k).split(" ");
                            String toWhere1 = getJumpNum1[1];
                            if(toWhere1.charAt(0)!='T'&&toWhere.charAt(0)!='T'&&asmJump[Integer.valueOf(toWhere)]==asmJump[Integer.valueOf(toWhere1)]&&asmJump[Integer.valueOf(toWhere1)]!=-1){
                                asmCode.set(k,getJumpNum1[0]+" TURN"+jumpCount);
                                asmJump[Integer.valueOf(toWhere1)]=-1;
                            }
                        }
                    }
                    jumpCount++;
                    asmJump[Integer.valueOf(toWhere)]=-1;
                }
            }
        }*/
       for(int j=0;j<asmCode.size();j++){
           if(asmCode.get(j).charAt(0)=='J'){
               //如果是跳转指令
               String getJumpNum[] = asmCode.get(j).split(" ");
               String toWhere = getJumpNum[1];
               if(asmCode.get(asmJump[Integer.valueOf(toWhere)]).charAt(0)=='T'){//如果该处已经填有标号
                   String getWhere[] = asmCode.get(asmJump[Integer.valueOf(toWhere)]).split(":");
                   asmCode.set(j,getJumpNum[0]+" "+getWhere[0]);
               }
               else{//如果该处没有标号
                   String temp = asmCode.get(asmJump[Integer.valueOf(toWhere)]);
                   asmCode.set(asmJump[Integer.valueOf(toWhere)],"TURN"+jumpCount+": "+temp);
                   asmCode.set(j,getJumpNum[0]+" TURN"+jumpCount);
                   jumpCount++;
               }
           }
       }


    }

    public String getIsActive(Syntax Cgramma,String newRDL){
        int findQt;
        int qtWhere=0;//first对应1 second对应2 以此类推
        for(findQt=Cgramma.qt.size()-1;findQt>=0;findQt--){
            //逆序遍历四元式
            if(Cgramma.qt.get(findQt).Second.equals(newRDL)){
                qtWhere=2;
                break;
            }
            else if(Cgramma.qt.get(findQt).Third.equals(newRDL)){
                qtWhere=3;
                break;
            }
            else if(Cgramma.qt.get(findQt).Fourth.equals(newRDL)){
                qtWhere=4;
                break;
            }
        }
        String s="";
        if(qtWhere==2){
            s=Cgramma.ActiveLable.get(findQt).Second;
        }
        else if(qtWhere==3){
            s=Cgramma.ActiveLable.get(findQt).Third;
        }
        else if(qtWhere==4){
            s=Cgramma.ActiveLable.get(findQt).Fourth;
        }
        return s;
    }
    public static Boolean IsNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) return true;
        else
            return false;
    }

    public static void main(String[] args)throws IOException {


    }
}
