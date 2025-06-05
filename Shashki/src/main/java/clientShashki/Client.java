/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clientShashki;
import java.io.*;
import java.net.*;
/**
 *
 * @author Александра
 */
public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    ServerListener serverListener;
    public Client(ServerEventListener listener){
        try {
        socket = new Socket(SERVER_HOST, SERVER_PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        Thread receiverThread = new Thread(serverListener = new ServerListener(socket, listener));
        receiverThread.start();
        } 
        catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
    public Client(){
        
    }
    public void setListener(ServerEventListener listener){
        serverListener.setListener(listener);
    }
    public void Login(String username, String password) throws IOException {
        out.println("LOGIN|" + username + "|" + password+ "|");
    }

    public void Logup(String username, String password) throws IOException {
        out.println("LOGUP|" + username + "|" + password+ "|");
    }
    
    public void sendJoin(String roomId, String username) throws IOException {
        out.println("JOIN|" + roomId + "|" + username + "|");
    }
    
    public void sendExitGame(String username) throws IOException {
        out.println("EXIT_GAME|" + username + "|");
    }
    
    public void sendExit() throws IOException {
        out.println("EXIT|");
    }
    
    public void sendMove(int fromRow, int fromCol, int toRow, int toCol) {
        out.println("MOVE|" + fromRow + " " + fromCol + " " + toRow + " " + toCol);
    }
}
