/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clientShashki;

/**
 *
 * @author Александра
 */
public class Piece {
    private boolean isWhite;
    private boolean isKing;
    
    public Piece(boolean isWhite){
        this.isWhite=isWhite;
        isKing=false;
    }
    
    public boolean isPWhite(){
        return isWhite;
    }
    
    public boolean isPKing(){
        return isKing;
    }
    
    public void makeKing(){
        isKing=true;
    }
}
