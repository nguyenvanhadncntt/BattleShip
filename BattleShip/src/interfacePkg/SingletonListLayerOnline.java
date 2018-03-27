/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfacePkg;

import java.util.ArrayList;
import serverAndDbPkg.Player;

/**
 *
 * @author Thehap Rok
 */
public class SingletonListLayerOnline {
    private static SingletonListLayerOnline instance = new SingletonListLayerOnline();
    private ArrayList<Player> listPlayerOnline;
    private Player you;
    private SingletonListLayerOnline(){}
    
    public static SingletonListLayerOnline getInstance(){
        return instance;
    }

    
    
    
    public void setInforOfYou(Player inforPlayer){
        this.you=inforPlayer;
    }
    
    public Player getInfor(){
        return you;
    }
    
    public void setListPlayerOnline(ArrayList<Player> listPlayerOnline){
        this.listPlayerOnline=listPlayerOnline;
    }
    
    public ArrayList<Player> getListPlayerOnlien(){
        return this.listPlayerOnline;
    }
}
