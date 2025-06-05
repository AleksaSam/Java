/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kursov.shashki;
import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/**
 *
 * @author Александра
 */
public class Room {
    private String id;
    private Socket player1 = null;
    private Socket player2 = null;
    private String name1;
    private String name2;
    public BufferedReader in1, in2;
    public PrintWriter out1, out2;
    public BlockingQueue<String> queue1 = new LinkedBlockingQueue<>();
    public BlockingQueue<String> queue2 = new LinkedBlockingQueue<>();
    
    public Room(String id){
        this.id= id;
    }
    
    public synchronized boolean isFull(){
        return player1 != null && player2 != null;
    }
    
    public synchronized boolean addPlayer(Socket socket, String username, BufferedReader in, PrintWriter out) {
        if (socket.equals(player1) || socket.equals(player2)) {
            return false;
        }
        if (player1 == null) {
        player1 = socket;
        in1 = in;
        out1 = out;
        name1 = username;
        return true;
        } else if (player2 == null) {
            player2 = socket;
            in2 = in;
            out2 = out;
            name2 = username;
            return true;
        } else {
            return false;
        }
    }
    public BlockingQueue<String> getQueueForSocket(Socket socket) {
        if (socket.equals(player1)) return queue1;
        else return queue2;
    }

    public Socket getOpponentSocket(Socket socket) {
        return socket.equals(player1) ? player2 : player1;
    }
    
    public String getRoomId(){
        return id;
    }
    
    public String getPlayerName1(){
        return name1;
    }
    
    public String getPlayerName2(){
        return name2;
    }
}
