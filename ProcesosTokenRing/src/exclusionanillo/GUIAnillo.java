/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exclusionanillo;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NeN
 */
public class GUIAnillo extends javax.swing.JFrame {

    /**
     * Creates new form GUIAnillo
     */
    Registry registry = null;
    ArrayList<ProcesoAnillo_I> procesos;
    MyCanvas canvas;

    public GUIAnillo() {
        initComponents();
        init();
    }

    public void init() {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUIAnillo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUIAnillo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUIAnillo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUIAnillo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        int IDInicial = 0;
        int numProcesos = 1;
        Frame dialogo = new Frame();
        DialogInputData datos = new DialogInputData(dialogo, true);

        datos.setVisible(true);

        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException ex) {
            Logger.getLogger(GUIAnillo.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (datos.getReturnStatus() == 1) {
            procesos = new ArrayList();
            numProcesos = datos.getNumProcesos();
            IDInicial = datos.getIdInicial();

            inicializaProcesos(numProcesos, IDInicial, registry);
            if (!procesos.isEmpty()) {
                setVisible(true);
                dibujarNodos(numProcesos,IDInicial);
                try {
                   
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GUIAnillo.class.getName()).log(Level.SEVERE, null, ex);
                }
                 //Ejecuto las tareas de cada proceso simulando que cada proceso puede ejecutar código util mientras
                    //Lo hacen sus hermanos pero no puede acceder a la sección crítica mientras no le pase el token
                int j=0;
                for (int i = 0; i < 5; ++i) {
                    final ProcesoAnillo_I p = procesos.get(j);
                    Thread t = new Thread() {
                        public void run() {
                            try {
                                p.ejecutarProceso();
                            } catch (RemoteException ex) {
                                Logger.getLogger(GUIAnillo.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    };
                    t.start();
                    j=(j+1)%procesos.size();
                    canvas.updateGraphics();
                }

            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Exclusion Mutua Distribuida en Anillo");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(171, 171, 171)
                .addComponent(jLabel1)
                .addContainerGap(222, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addContainerGap(362, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        if (procesos != null) {
            for (int i = 0; i < procesos.size(); ++i) {
                try {
                    registry.unbind(String.valueOf(procesos.get(i).getIdProceso()));
                } catch (RemoteException ex) {
                    Logger.getLogger(GUIAnillo.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotBoundException ex) {
                    Logger.getLogger(GUIAnillo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_formWindowClosing

    public void inicializaProcesos(int numProcesos, int IDInicial, Registry registry) {
        for (int i = 0; i < numProcesos; ++i) {
            try {
                ProcesoAnillo_I p = new ProcesoAnillo(IDInicial + i, numProcesos, IDInicial);
                UnicastRemoteObject.unexportObject(p, true);
                ProcesoAnillo_I stub = (ProcesoAnillo_I) UnicastRemoteObject.exportObject(p, 0);
                if (registry != null) {
                    registry.rebind(String.valueOf(p.getIdProceso()), stub);
                    procesos.add(p);
                    if (p.getIdProceso() == IDInicial) {
                        p.setTokenSeccionCritica(1);
                    } else {
                        p.setTokenSeccionCritica(-1);
                    }
                }
            } catch (RemoteException ex) {
                Logger.getLogger(GUIAnillo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void dibujarNodos(int numProcesos, int IDInicial) {
        canvas = new MyCanvas(numProcesos,procesos,IDInicial);
        
        this.add(canvas);
        

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
