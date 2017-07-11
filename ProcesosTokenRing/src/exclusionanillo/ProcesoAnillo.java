/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exclusionanillo;

import static java.lang.Thread.sleep;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NeN
 */
public class ProcesoAnillo extends UnicastRemoteObject implements ProcesoAnillo_I   {
    private int idProceso;
    private int tokenSeccionCritica;
    private int numProcesos;
    private boolean seccionCritica;
    private boolean codigoUtil;
    private Registry registry;
    private int IDInicial;
    
    @Override
    public boolean pasarToken(){
        boolean pasado=false;
        //Paso el token al hermano siguiente y sino se lo paso al inicial
        int idProcesoHermano=((idProceso+1) % (numProcesos+IDInicial));
        if(idProcesoHermano==0){
            idProcesoHermano=IDInicial;
        }
        try {
            System.out.println("Pasando token a PROCESO: "+idProceso+ " a "+idProcesoHermano+" ...");
            registry = LocateRegistry.getRegistry("localhost",1099);
            ProcesoAnillo_I hermano = (ProcesoAnillo_I)registry.lookup(String.valueOf(idProcesoHermano));
            hermano.setTokenSeccionCritica(tokenSeccionCritica);
            tokenSeccionCritica=-1;
            pasado=true;
            System.out.println("Token pasado PROCESO: "+idProceso+ " a "+idProcesoHermano+" ...");
        } catch (RemoteException ex) {
            Logger.getLogger(ProcesoAnillo.class.getName()).log(Level.SEVERE, null, ex);
            pasado=false;
        } catch (NotBoundException ex) {
            Logger.getLogger(ProcesoAnillo.class.getName()).log(Level.SEVERE, null, ex);
            pasado=false;
        }
        
        return pasado;
    }
    @Override
    public void ejecutarCodigoUtil(){
          //Código util
        Random r = new Random();
        try {
            codigoUtil=true;
            System.out.println("Ejecutando CÓDIGO UTIL PROCESO: "+idProceso+ " ...");
            Thread.sleep(r.nextInt(4)*1000);
            codigoUtil=false;
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcesoAnillo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void ejecutarSeccionCritica(){
        //Seccion crítica
        Random r = new Random();
        try {
            seccionCritica=true;
            System.out.println("Ejecutando la seccion critica PROCESO: "+idProceso+ " ...");
            Thread.sleep(r.nextInt(2)*1000);
            seccionCritica=false;
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcesoAnillo.class.getName()).log(Level.SEVERE, null, ex);
        }
        pasarToken();
    }
    @Override
    public void ejecutarProceso(){
        Random r = new Random();
        ejecutarCodigoUtil();
        //Es la forma menos útil y que proboca mas errores 
        //para sincronizar con el token los procesos pero no se me ocurria
        //otra cosa con tan poco tiempo y creo que syncronized no me valía
        while(codigoUtil || tokenSeccionCritica==-1 ){
            try {
                System.out.println("Esperando mi token : "+idProceso+ " ...");
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProcesoAnillo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
            ejecutarSeccionCritica();
    }
    @Override
    public int getIdProceso() {
        return idProceso;
    }

    @Override
    public void setIdProceso(int idProceso) {
        this.idProceso = idProceso;
    }

    @Override
    public int getTokenSeccionCritica() {
        return tokenSeccionCritica;
    }

    @Override
    public void setTokenSeccionCritica(int tokenSeccionCritica) {
        this.tokenSeccionCritica = tokenSeccionCritica;
    }

    @Override
    public int getNumProcesos() {
        return numProcesos;
    }

    @Override
    public void setNumProcesos(int numProcesos) {
        this.numProcesos = numProcesos;
    }

    @Override
    public Registry getRegistry() {
        return registry;
    }

    @Override
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
    public ProcesoAnillo(int id,int numProcesos,int IDInicial)  throws RemoteException{
        idProceso = id;
        this.numProcesos = numProcesos;
        tokenSeccionCritica=-1;
        this.IDInicial = IDInicial;
    }
    
}
