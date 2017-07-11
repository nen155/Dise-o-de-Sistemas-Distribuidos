/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exclusionanillo;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NeN
 */
public class MyCanvas extends Canvas {
    
    private int numProcesos;
    ArrayList<ProcesoAnillo_I> procesos;
    private int IDInicial;
    
    public MyCanvas (int numProcesos,ArrayList<ProcesoAnillo_I> procesos,int IDInicial) {
         setBackground (Color.GRAY);
         setSize(500, 300);
         setLocation(50,50);
         this.numProcesos =numProcesos;
         this.procesos = procesos;
         this.IDInicial = IDInicial;
      }

      public void paint (Graphics g) {
         Graphics2D g2;
         g2 = (Graphics2D) g;
         double alpha = 0;
        double x = 0;
        double y = 0;
        double Originx = 150;
        double Originy = 150;
        
        for (int i = 0; i < numProcesos; ++i) {
            alpha += (2 * Math.PI) / numProcesos*50;
            x = Originx + Math.cos(Math.toRadians(alpha))*100;
            y = Originy + Math.sin(Math.toRadians(alpha))*100;
             try {
                 if(procesos.get(i).getTokenSeccionCritica()==1){
                     g2.setColor(Color.BLUE);
                 }
                 else
                     g2.setColor(Color.red);
             } catch (RemoteException ex) {
                 Logger.getLogger(MyCanvas.class.getName()).log(Level.SEVERE, null, ex);
             }
            
            
            g2.fillOval((int)x, (int)y, 50, 50);
            g2.setColor(Color.white);
            g2.drawString(String.valueOf(IDInicial+i), (float)x+25, (float)y+25);
            
        }
        
        
      }
      public void updateGraphics(){
          repaint();
      }
      
    
}
