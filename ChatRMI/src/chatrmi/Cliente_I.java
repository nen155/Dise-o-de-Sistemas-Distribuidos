package chatrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public interface Cliente_I extends Remote {
    
        public void enviarMensajeA(String mensaje, String delUsuario,String alUsuario) throws RemoteException;

	public void enviarMensajeAll(String mensaje, String nick) throws RemoteException;
        
        public void registrarListado(String nick) throws RemoteException;
        
        public void cerrarSesion(String nick) throws RemoteException;
        
        public String getNick() throws RemoteException;
}
