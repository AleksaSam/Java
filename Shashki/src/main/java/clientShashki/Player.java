/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clientShashki;

/**
 *
 * @author Александра
 */
public class Player {
    private final String nickName;
    private boolean color = false;
    public Player(String name){
        nickName=name;
    }
    
    public String getNickName(){
        return nickName;
    }
    
    public void setColor(boolean color){
        this.color = color;
    }
    
    public boolean getColor(){
        return color;
    }
}
