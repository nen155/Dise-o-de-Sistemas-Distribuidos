package chatrmi;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServidorChat implements ServidorChat_I {

	private ArrayList<Cliente_I> clientes; 
        String clienteActual="";
        Registry registry; 

	public ServidorChat() {
            clientes = new ArrayList<Cliente_I>();
	}

	public void enviarMensajeATodos(String mensaje,String delusuario)  {
		for (int i = 0; i < clientes.size(); i++) {
			Cliente_I c = clientes.get(i);
                    try {
                        c.enviarMensajeAll(mensaje, delusuario);
                        
                    } catch (RemoteException ex) {
                        clientes.remove(c);
                        i--;
                        Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
                    }
		}
	}
        //Registro el usuario que ha iniciado sesion en todos los JList de los usuarios
        public void registrarseParaTodos(){
            for (int i = 0; i < clientes.size(); i++) {
			Cliente_I c1 = clientes.get(i);
                try {
                    ///INFORMO A TODOS LOS CLIENTES QUE YA HABIA DE QUE EXISTO
                    if(c1.getNick().equals(clienteActual)){
                        for(int j=0;j<clientes.size();++j){
                            Cliente_I c2 = clientes.get(j);
                            if(!c2.getNick().equals(c1.getNick())){        
                               c1.registrarListado(c2.getNick());
                            }
                        }
                    }else{///SOLO REGISTRO AL NOVATO
                        c1.registrarListado(clienteActual);
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        public void cerrarSesion(String nick){
            int indiceABorrar=0;
            for (int i = 0; i < clientes.size(); i++) {
			Cliente_I c = clientes.get(i);
                try {
                    ///INFORMO A TODOS LOS CLIENTES QUE YA NO EXISTO
                    if(!c.getNick().equals(nick))
                        c.cerrarSesion(nick);
                    else
                        indiceABorrar=i;
                } catch (RemoteException ex) {
                    Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //Elimino el cliente del servidor
            clientes.remove(indiceABorrar);
            try {
                registry.unbind(nick);
            } catch (RemoteException ex) {
                Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotBoundException ex) {
                Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
	
    private boolean comprobarClientes(String clienteActual){
       for (int i = 0; i < clientes.size(); i++) {
            Cliente_I c = clientes.get(i);
            try {
                if(c.getNick().equals(clienteActual))
                    return true;
            } catch (RemoteException ex) {
                Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
            }
       }                
       return false;
    }
    @Override
    public boolean iniciaSesion(String nickcliente) {
        if(!nickcliente.equals("Servidor") && !comprobarClientes(nickcliente)){

                try {
                    registry = LocateRegistry.getRegistry(1099);
                     //Registro al cliente en el servidor cuando inicio sesion
                    Cliente_I client = (Cliente_I)registry.lookup(nickcliente); 
                    enviarMensajeATodos("-->>> " + client.getNick() + " se ha unido al chat","");
                    clienteActual=nickcliente;
                    clientes.add(client);
                    //Me registro para todos los demas clientes
                    registrarseParaTodos();
                } catch (RemoteException ex) {
                    Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotBoundException ex) {
                    Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
                } 
           return true;
        }else{
            return false;
        }
        
    }
        
        //Esta función esta para ver que puedo mandar los mensajes que me dice un usuario
        //a otro a traés del servidor pero no la uso porque no sería peer to peer
        public void enviarMensajeA(String mensaje,String delUsuario,String alUsuario){
            Cliente_I usuarioEnvia=null;
            Cliente_I usuarioRecibe=null;
            for(int i=0;i<clientes.size();++i){
                try {
                    if(clientes.get(i).getNick().equals(delUsuario)){ 
                        usuarioEnvia = clientes.get(i);
                    }
                    if(clientes.get(i).getNick().equals(alUsuario)){ 
                        usuarioRecibe = clientes.get(i);
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(usuarioEnvia!=null){
                try {
                    usuarioEnvia.enviarMensajeA(mensaje, delUsuario,alUsuario);
                    usuarioRecibe.enviarMensajeA(mensaje, delUsuario,alUsuario);
                } catch (RemoteException ex) {
                    Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
	public static void main(String[] args) {
            //createActivationGroup();
            if(System.getSecurityManager()==null){
                System.setSecurityManager(new SecurityManager());
            }
		try {
                        Registry registry = LocateRegistry.createRegistry(1099);
                        //Me creo mi servidor
                        ServidorChat_I server = new ServidorChat();
                        
                        ///Pongo el formato a para que pueda ser enlazado al registry
                        ServidorChat_I stub = (ServidorChat_I) UnicastRemoteObject.exportObject(server, 0);
			registry.rebind("Servidor", stub);
			System.out.println("Comienza a escuchar peticiones el servidor");
		} catch (Exception e) {
                        System.err.println("Error en el servidor");
			e.printStackTrace();
		}
	}





}
