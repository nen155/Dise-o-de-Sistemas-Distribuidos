/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exclusionanillo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

/**
 *
 * @author NeN
 */
public interface ProcesoAnillo_I extends Remote {
    public boolean pasarToken() throws RemoteException;
    public void ejecutarCodigoUtil() throws RemoteException;
    public void ejecutarSeccionCritica() throws RemoteException;
    public void ejecutarProceso() throws RemoteException;
    
    public int getIdProceso() throws RemoteException;

    public void setIdProceso(int idProceso) throws RemoteException;

    public int getTokenSeccionCritica() throws RemoteException;

    public void setTokenSeccionCritica(int tokenSeccionCritica) throws RemoteException;

    public int getNumProcesos() throws RemoteException;

    public void setNumProcesos(int numProcesos) throws RemoteException;

    public Registry getRegistry() throws RemoteException;

    public void setRegistry(Registry registry) throws RemoteException;
    
}
