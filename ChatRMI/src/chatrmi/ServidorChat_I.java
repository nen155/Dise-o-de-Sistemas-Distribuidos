package chatrmi;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public interface ServidorChat_I extends Remote {
	

	public boolean iniciaSesion(String client)throws RemoteException;
        
        public void cerrarSesion(String client)throws RemoteException;

	public void enviarMensajeATodos(String mensaje,String delusuario) throws RemoteException;
        
        public void enviarMensajeA(String mensaje,String delUsuario,String alUsuario) throws RemoteException;
}

