/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.i;

import java.io.File;

/**
 *
 * @author ASUS
 */
public class VirtualMachineMAster {
    
 public void Convertir (String ruta){
     if (ruta.length() < 0){

            System.out.println("No ingreso una ruta valida");

        }else{
            File filein = new File(ruta);
            Parser parser = new Parser(filein);
            String rutaSalida = ruta.substring(0,ruta.length()-3); 
            File fileOut = new File(rutaSalida +".asm");
            CodeWriter writer;
            writer = new CodeWriter(fileOut);
            int type = -1;
            
            //empieza el parseo
            
            while(parser.hasMoreCommands()){
            
                parser.advance();
                
                type = parser.commandType();
                
                if (type == Parser.ARITHMETIC) {
                    writer.writeArithmetic(parser.arg1()); 
                }else if(type == Parser.POP || type == Parser.PUSH){
                  writer.writePushPop(type, parser.arg1(), parser.arg2());
                }
                
            }
            writer.close();
            System.out.println("File created : " + rutaSalida +".asm");
            
            
     }
 }
    
}
