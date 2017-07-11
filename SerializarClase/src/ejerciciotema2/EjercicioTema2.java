/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejerciciotema2;

import java.io.* ;

/**
 *
 * @author nen155
 */
public class EjercicioTema2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        // TODO code application logic here
         ClaseASerializar e;
         FileInputStream fileIn = new FileInputStream("fichero.salida");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         e = (ClaseASerializar) in.readObject();
         in.close();
         
    }
    
}
