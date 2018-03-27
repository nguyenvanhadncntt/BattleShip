/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverAndDbPkg;

import java.util.ArrayList;

/**
 *
 * @author Thehap Rok
 */
public class SingletonListPlayerAccessServer {
    private static SingletonListPlayerAccessServer instance = new SingletonListPlayerAccessServer();
    private ArrayList<Player> listPlayerAccess = new ArrayList<>();
    
    private SingletonListPlayerAccessServer(){}
    
    public static SingletonListPlayerAccessServer getInstance(){
        return instance;
    }
    
    public void setListPlayerAccessToServer(ArrayList<Player> listPlayerAccess){
        this.listPlayerAccess = listPlayerAccess;
    }
    
    public ArrayList<Player> getListPlayerAccessToServer(){
        return this.listPlayerAccess;
    }
    
    public void addPlayerToList(Player player){
        this.listPlayerAccess.add(player);
    }
}
