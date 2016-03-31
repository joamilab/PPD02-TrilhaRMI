/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacao;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/** EXECUTE THIS FIRST
 *
 * @author Joamila
 * Created: 11/03/2016
 * Last Modified: 16/03/2016
 */
public class Server{
    Trilha server;
    Registry registry;
    
    public Server() throws RemoteException{
        try{
            server = new Trilha();
            registry = LocateRegistry.createRegistry(12345);
            registry.rebind("ServerTrilha", server);
            System.out.println("Servidor registrado com sucesso!");
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
    public static void main(String args[]) {
        try {
            Server serverTrilha = new Server();
        } catch (RemoteException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
