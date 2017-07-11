package chatrmi;

import chatrmi.InterfazGrafica;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import chatrmi.Cliente_I;


public class Cliente extends UnicastRemoteObject implements Cliente_I {

        private String nick;
	public Cliente(String nick) throws RemoteException {
            this.nick = nick;
	}

	public  void enviarMensajeAll(String mensaje, String nick) {
		InterfazGrafica.mostrarMensajeEnGeneral(mensaje, nick);
	}
        public void registrarListado(String nick){
            InterfazGrafica.registraUsuario(nick);
        }
        public void cerrarSesion(String nick){
            InterfazGrafica.cerrarSesion(nick);
        }

        public String getNick() {
            return nick;
        }
        public void enviarMensajeA(String mensaje, String delUsuario,String alUsuario){
            InterfazGrafica.mostrarMensajeEn(mensaje, delUsuario, alUsuario);
        }
        
}
