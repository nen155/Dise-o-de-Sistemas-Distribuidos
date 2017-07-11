/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatrmi;
import java.util.Properties;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupID;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author NeN
 */
public class InterfazGrafica extends javax.swing.JFrame {
    private Cliente_I cliente;
    private ServidorChat_I servidor; 
    private static String nick = null;
    private String localizacionServer=null;
    private static DefaultListModel listModel = new DefaultListModel();
    private static Registry registry;
    //UNA PRUEBA DE PUNTO A PUNTO
    private static ArrayList<Cliente_I> misContactos = new ArrayList();

    

    public InterfazGrafica() {
        if(System.getSecurityManager()==null){
                System.setSecurityManager(new SecurityManager());
            }
		try {
                        localizacionServer= JOptionPane.showInputDialog("Introduce la direccion del server");
                        
                        if(localizacionServer==null || localizacionServer.compareTo("")==0){
                            JOptionPane.showMessageDialog(null, "No has elegido server, adios!");
                            System.exit(0);
                        }
                        nick = JOptionPane.showInputDialog("Introduce tu nick");
                        
                            if(nick==null || nick.compareTo("")==0 || !nick.matches("\\w+\\d*")){
                                JOptionPane.showMessageDialog(null, "No has elegido nick o el nick es incorrecto adios!");
                                System.exit(0);
                            }
                        
                        
                        registry = LocateRegistry.getRegistry(localizacionServer,1099);

                        servidor= (ServidorChat_I)registry.lookup("Servidor");
                        
                        initComponents();

                    try {
                        cliente = new Cliente(nick);
                         ///Pongo el formato a para que pueda ser enlazado al registry
                         UnicastRemoteObject.unexportObject(cliente, true);
                        Cliente_I stub = (Cliente_I) UnicastRemoteObject.exportObject(cliente, 0);
                        registry.rebind(cliente.getNick(), stub);
                        System.out.println("El cliente introducido en el registry");
                    } catch (RemoteException ex) {
                        Logger.getLogger(InterfazGrafica.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, "El servidor puede que esté caido");
                    }
                                               
                    try {
                        if(!servidor.iniciaSesion(nick))
                        {
                             
                             JOptionPane.showMessageDialog(null, "No has elegido nick o el nick es incorrecto o está en uso adios!");
                             System.exit(0);
                        }
                    } catch (RemoteException ex) {
                        Logger.getLogger(InterfazGrafica.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, "El servidor puede que esté caido");
                    }

                    jlUsuario.setText(nick);
                    jListUsuarios.addListSelectionListener(new ListSelectionListener(){
                        @Override
                        public void valueChanged(ListSelectionEvent e) {
                            if(jListUsuarios!=null && jListUsuarios.getSelectedValue()!=null && !jListUsuarios.getSelectedValue().equals("")){
                                int i=0;
                                for(i=0;i<jTabs.getTabCount() && !jTabs.getTitleAt(i).equals(jListUsuarios.getSelectedValue());++i);
                                if(i==jTabs.getTabCount())
                                    crearTabPersonal(jListUsuarios.getSelectedValue());
                                else
                                    jTabs.setSelectedIndex(i);
                                ///CONTINUAR CON PRUEBAS
                                ////////////////////////
                                try {
                                    Cliente_I temporal = (Cliente_I)registry.lookup(jListUsuarios.getSelectedValue());
                                    misContactos.add(temporal);
                                } catch (RemoteException ex) {
                                    Logger.getLogger(InterfazGrafica.class.getName()).log(Level.SEVERE, null, ex);
                                    JOptionPane.showMessageDialog(null, "El servidor puede que esté caido");
                                } catch (NotBoundException ex) {
                                    Logger.getLogger(InterfazGrafica.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }

                    });


                    this.addWindowListener(new WindowAdapter(){
                            public void windowClosing(WindowEvent e){
                                try {
                                    servidor.cerrarSesion(nick);
                                } catch (RemoteException ex) {
                                    Logger.getLogger(InterfazGrafica.class.getName()).log(Level.SEVERE, null, ex);
                                    JOptionPane.showMessageDialog(null, "El servidor puede que esté caido");
                                }
                                System.exit(0);//cierra aplicacion
                            }
                        });

		} catch (Exception e) {
                        System.err.println("El servidor no esta disponible");
                        e.printStackTrace();
                        System.exit(0);
			
		}

        }
        public static void cerrarSesion(String nick){

            for(int i=0;i<misContactos.size();++i){
                Cliente_I c = misContactos.get(i);
                try {
                if(c.getNick().equals(nick))
                     misContactos.remove(i);
                } catch (RemoteException ex) {
                    Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if(listModel.contains(nick))
                listModel.removeElement(nick);
            int i=0;
                //Busco si alguna pestaña Tab que tenga el nombre del que cierra sesion
            for(i=0;i<jTabs.getTabCount() && !jTabs.getTitleAt(i).equals(nick);++i);
            if(i!=jTabs.getTabCount()){
                jTabs.remove(i);
                jTabs.repaint();
            }
            jTAMensajesGlobales.append(nick + ":  Se ha salido del chat \n");
        }
	public static void mostrarMensajeEnGeneral(String mensaje, String nick) {
		if (nick.compareTo("")!=0)
			jTAMensajesGlobales.append(nick + ": " + mensaje + "\n");
		else 
                    jTAMensajesGlobales.append(mensaje + "\n");
	}
        public static void mostrarMensajeEn(String mensaje,String delUsuario,String alUsuario){
            JTextArea areaPersonal=null;
            //Si es a mi a quien se le mando el mensaje
            if(alUsuario.equals(nick)){
                int i=0;
                //Busco si alguna pestaña Tab que se llame como el usuario que me envia el mensaje
                for(i=0;i<jTabs.getTabCount() && !jTabs.getTitleAt(i).equals(delUsuario);++i);
                
                ///Sino la hay la pestaña la creo
                if(i==jTabs.getTabCount()){
                    areaPersonal = crearTabPersonal(delUsuario);
                    //En el caso de que sea el que recibe el mensaje puede que no tenga a
                    //este usuario entre mis contactos así que lo añado a mi array de contactos
                    int j=0;
                    try {
                        for(j=0;j<misContactos.size() && !misContactos.get(j).getNick().equals(delUsuario);++j);
                        if(j==misContactos.size()){
                             Cliente_I temporal = (Cliente_I)registry.lookup(delUsuario);
                             misContactos.add(temporal);
  
                        }
                    } catch (RemoteException ex) {
                        Logger.getLogger(InterfazGrafica.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, "El servidor puede que esté caido");
                    } catch (NotBoundException ex) {
                        Logger.getLogger(InterfazGrafica.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else///Si hay alguna pestaña la obtengo
                {
                    areaPersonal= (JTextArea) ((JScrollPane) ((JPanel)jTabs.getComponentAt(i)).getComponent(0)).getViewport().getView();
                    
                }
               
            //Si por el contrario soy quien mando el mensaje
            }else if(delUsuario.equals(nick)){
                    int i=0;
                    //Vuelvo a buscar el tab del usuario al que mando por si lo he cerrado 
                    //Y lo vuelvo a crear en ese caso
                    for(i=0;i<jTabs.getTabCount() && !jTabs.getTitleAt(i).equals(alUsuario);++i);
                    if(i==jTabs.getTabCount())
                        areaPersonal = crearTabPersonal(alUsuario);
                    else
                        areaPersonal= (JTextArea) ((JScrollPane) ((JPanel)jTabs.getComponentAt(i)).getComponent(0)).getViewport().getView();
                    //areaPersonal.append(delUsuario + ": " + mensaje + "\n");
            }
             //Añado el mensaje del usuario
                areaPersonal.append(delUsuario + ": " + mensaje + "\n");
        }
	public static void registraUsuario(String nick) {
                listModel.addElement(nick);
                if(jListUsuarios!=null)
		jListUsuarios.setModel(listModel);
	}
        private void enviarMensaje(KeyEvent evt){
            //Si lo ha hecho desde el enter
            String mensajeA = jTabs.getTitleAt(jTabs.getSelectedIndex());
            if (((evt !=null  && evt.getKeyCode() == KeyEvent.VK_ENTER) || evt==null) && !jTAEscribirMensaje.getText().equals("")) {
                
                try {
                    if(mensajeA.equals("General"))
                        servidor.enviarMensajeATodos(jTAEscribirMensaje.getText(),nick);
                    else{
                        //Busco al cliente en mi array de mis contactos personales
                        //y le envio el mensaje
                        cliente.enviarMensajeA(jTAEscribirMensaje.getText(), nick, mensajeA);
                        Cliente_I recibe = null;
                        for(int i=0;i<misContactos.size();++i){
                            if(misContactos.get(i).getNick().equals(mensajeA)){
                               recibe = misContactos.get(i);
                            }
                        }
                        //Si encuentro al cliente le envio el mensaje
                        if(recibe!=null){
                            recibe.enviarMensajeA(jTAEscribirMensaje.getText(), nick, mensajeA);
                        }
                        //He creado una funcion que puede hacer la conexion entre dos clientes
                        //a través del servidor
                        //servidor.enviarMensajeA(jTAEscribirMensaje.getText(), nick, mensajeA);
                    }
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "El servidor puede que esté caido");
                    Logger.getLogger(InterfazGrafica.class.getName()).log(Level.SEVERE, null, ex);
                }
		jTAEscribirMensaje.setText("");
                ///Si lo ha realizado desde el boton enviar
            }
        }

         private static JTextArea crearTabPersonal(String delUsuario){
             String nickChatPersonal=delUsuario;
                JPanel panelPersonal = new JPanel();
                
                JTextArea textAreaPersonal = new JTextArea(); 
                textAreaPersonal.setEditable(false);
                textAreaPersonal.setColumns(20);
                textAreaPersonal.setRows(5);
                
                JList usuarioUnico = new JList();
                usuarioUnico.setToolTipText("");
                
                JScrollPane jScrollPaneUnico = new JScrollPane();
                JScrollPane jScrollPanePersonal = new JScrollPane();
                jScrollPaneUnico.setViewportView(usuarioUnico);
                jScrollPanePersonal.setViewportView(textAreaPersonal);
                
                DefaultListModel listModel = new DefaultListModel();
                
                personalizarPanel(panelPersonal,jScrollPaneUnico,jScrollPanePersonal);
   
                listModel.addElement(nickChatPersonal);
                jTabs.addTab(nickChatPersonal, panelPersonal);
                jTabs.setSelectedIndex(jTabs.getTabCount()-1);
                return textAreaPersonal;
         }
         private static void personalizarPanel(JPanel p,JScrollPane jScrollPane2,JScrollPane jScrollPane4 ){
            javax.swing.GroupLayout jPanelGenericoLayout = new javax.swing.GroupLayout(p);
            p.setLayout(jPanelGenericoLayout);
            jPanelGenericoLayout.setHorizontalGroup(
                jPanelGenericoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelGenericoLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                    .addGap(18, 18, 18)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );
            jPanelGenericoLayout.setVerticalGroup(
                jPanelGenericoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelGenericoLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanelGenericoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }
        //Evita tener que añadir los parámetros al ejecutar la aplicación 
        //NO ESTÁ EN USO PORQUE PROBOCA FALLOS
        private static void createActivationGroup() 
         {
             Properties props = new Properties();
             props.put("java.security.policy", "file:./politicaseguridad.policy");
             props.put("sun.rmi.transport.connectionTimeout", 10000);
             props.put("java.rmi.server.hostname","localhost");
             props.put("java.rmi.server.codebase","file:./");
             System.setProperties(props);
         }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabs = new javax.swing.JTabbedPane();
        jPanelGenerico = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTAMensajesGlobales = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListUsuarios = new javax.swing.JList<>();
        jBtEnviar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTAEscribirMensaje = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jlUsuario = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTAMensajesGlobales.setEditable(false);
        jTAMensajesGlobales.setColumns(20);
        jTAMensajesGlobales.setRows(5);
        jScrollPane4.setViewportView(jTAMensajesGlobales);

        jListUsuarios.setToolTipText("");
        jScrollPane2.setViewportView(jListUsuarios);

        javax.swing.GroupLayout jPanelGenericoLayout = new javax.swing.GroupLayout(jPanelGenerico);
        jPanelGenerico.setLayout(jPanelGenericoLayout);
        jPanelGenericoLayout.setHorizontalGroup(
            jPanelGenericoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGenericoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelGenericoLayout.setVerticalGroup(
            jPanelGenericoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGenericoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGenericoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabs.addTab("General", jPanelGenerico);

        jBtEnviar.setText("Enviar");
        jBtEnviar.setName("jEnviar"); // NOI18N
        jBtEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtEnviarActionPerformed(evt);
            }
        });

        jTAEscribirMensaje.setColumns(20);
        jTAEscribirMensaje.setRows(5);
        jTAEscribirMensaje.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTAEscribirMensajeKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(jTAEscribirMensaje);

        jLabel1.setForeground(new java.awt.Color(0, 153, 204));
        jLabel1.setText("Usuario:");

        jlUsuario.setText("usuario");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jlUsuario)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane3)
                                .addGap(18, 18, 18)
                                .addComponent(jBtEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 701, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(19, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jlUsuario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtEnviarActionPerformed
        // TODO add your handling code here:
        enviarMensaje(null);
    }//GEN-LAST:event_jBtEnviarActionPerformed

    private void jTAEscribirMensajeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTAEscribirMensajeKeyPressed
        // TODO add your handling code here:
        enviarMensaje(evt);
    }//GEN-LAST:event_jTAEscribirMensajeKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(InterfazGrafica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InterfazGrafica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InterfazGrafica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterfazGrafica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        //createActivationGroup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InterfazGrafica().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtEnviar;
    private javax.swing.JLabel jLabel1;
    private static javax.swing.JList<String> jListUsuarios;
    private javax.swing.JPanel jPanelGenerico;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTAEscribirMensaje;
    private static javax.swing.JTextArea jTAMensajesGlobales;
    private static javax.swing.JTabbedPane jTabs;
    private javax.swing.JLabel jlUsuario;
    // End of variables declaration//GEN-END:variables
}
