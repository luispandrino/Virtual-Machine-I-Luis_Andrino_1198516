/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.i;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author ASUS
 */
public class CodeWriter {
  private int JumpFlag;
  private PrintWriter outPrinter; 
  
  public CodeWriter (File fileOut){
      try{
           outPrinter = new PrintWriter(fileOut);
            JumpFlag = 0;
      }catch(FileNotFoundException e){
          e.printStackTrace();
      }
  }
  
  
    /**
     * Write the assembly code that is the translation of the given arithmetic command
     * @param command
     */
    public void writeArithmetic(String command){

        if (command.equals("add")){

            outPrinter.print(arithmeticTemplate1() + "M=M+D\n");

        }else if (command.equals("sub")){

            outPrinter.print(arithmeticTemplate1() + "M=M-D\n");

        }else if (command.equals("and")){

            outPrinter.print(arithmeticTemplate1() + "M=M&D\n");

        }else if (command.equals("or")){

            outPrinter.print(arithmeticTemplate1() + "M=M|D\n");

        }else if (command.equals("gt")){

            outPrinter.print(arithmeticTemplate2("JLE"));//not <=
            JumpFlag++;

        }else if (command.equals("lt")){

            outPrinter.print(arithmeticTemplate2("JGE"));//not >=
            JumpFlag++;

        }else if (command.equals("eq")){

            outPrinter.print(arithmeticTemplate2("JNE"));//not <>
            JumpFlag++;

        }else if (command.equals("not")){

            outPrinter.print("@SP\nA=M-1\nM=!M\n");

        }else if (command.equals("neg")){

            outPrinter.print("D=0\n@SP\nA=M-1\nM=D-M\n");

        }else {

            throw new IllegalArgumentException("Call writeArithmetic() for a non-arithmetic command");

        }

    }

    /**
     * tracude el comando a assembly
     * donde el comando es push o pop
     * @param command PUSH or POP
     * @param segment
     * @param index
     */
    public void writePushPop(int command, String segment, int index){

        if (command == Parser.PUSH){

            if (segment.equals("constant")){

                outPrinter.print("@" + index + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");

            }else if (segment.equals("local")){

                outPrinter.print(pushTemplate1("LCL",index,false));

            }else if (segment.equals("argument")){

                outPrinter.print(pushTemplate1("ARG",index,false));

            }else if (segment.equals("this")){

                outPrinter.print(pushTemplate1("THIS",index,false));

            }else if (segment.equals("that")){

                outPrinter.print(pushTemplate1("THAT",index,false));

            }else if (segment.equals("temp")){

                outPrinter.print(pushTemplate1("R5", index + 5,false));

            }else if (segment.equals("pointer") && index == 0){

                outPrinter.print(pushTemplate1("THIS",index,true));

            }else if (segment.equals("pointer") && index == 1){

                outPrinter.print(pushTemplate1("THAT",index,true));

            }else if (segment.equals("static")){

                outPrinter.print(pushTemplate1(String.valueOf(16 + index),index,true));

            }

        }else if(command == Parser.POP){

            if (segment.equals("local")){

                outPrinter.print(popTemplate1("LCL",index,false));

            }else if (segment.equals("argument")){

                outPrinter.print(popTemplate1("ARG",index,false));

            }else if (segment.equals("this")){

                outPrinter.print(popTemplate1("THIS",index,false));

            }else if (segment.equals("that")){

                outPrinter.print(popTemplate1("THAT",index,false));

            }else if (segment.equals("temp")){

                outPrinter.print(popTemplate1("R5", index + 5,false));

            }else if (segment.equals("pointer") && index == 0){

                outPrinter.print(popTemplate1("THIS",index,true));

            }else if (segment.equals("pointer") && index == 1){

                outPrinter.print(popTemplate1("THAT",index,true));

            }else if (segment.equals("static")){

                outPrinter.print(popTemplate1(String.valueOf(16 + index),index,true));

            }

        }else {

            throw new IllegalArgumentException("se llamo a un comando push pop de forma erronea");

        }

    }

    public void close(){

        outPrinter.close();

    }

    /**
     * plantilla para add sub y or
     * @return
     */
    private String arithmeticTemplate1(){

        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n";

    }

    /**
     * plantilla para gt lt eq
     * @param type JLE JGT JEQ
     * @return
     */
    private String arithmeticTemplate2(String type){

        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=M-D\n" +
                "@FALSE" + JumpFlag + "\n" +
                "D;" + type + "\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "@CONTINUE" + JumpFlag + "\n" +
                "0;JMP\n" +
                "(FALSE" + JumpFlag + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "(CONTINUE" + JumpFlag + ")\n";

    }


    /**
     * plantilla para push local,this,that,argument,temp,pointer,static
     * @param segment
     * @param index
     * @param isDirect 
     * @return
     */
    private String pushTemplate1(String segment, int index, boolean isDirect){

        //cuando es un puntero, solo lee la data puesta en THIS o THAT
        //cuando es estatico, solo lee la data en la dirrecion
        String noPointerCode = (isDirect)? "" : "@" + index + "\n" + "A=D+A\nD=M\n";

        return "@" + segment + "\n" +
                "D=M\n"+
                noPointerCode +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n";

    }

    /**
     * plantilla pop local,this,that,argument,temp,pointer,static
     * @param segment
     * @param index
     * @param isDirect 
     * @return
     */
    private String popTemplate1(String segment, int index, boolean isDirect){

  
        String noPointerCode = (isDirect)? "D=A\n" : "D=M\n@" + index + "\nD=D+A\n";

        return "@" + segment + "\n" +
                noPointerCode +
                "@R13\n" +
                "M=D\n" +
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@R13\n" +
                "A=M\n" +
                "M=D\n";

    }
}
