
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clientShashki;
import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;
/**
 *
 * @author Александра
 */
public class ServerListener implements Runnable {
    static final int PORT = 8080;
    final private Socket socket;
    final private BufferedReader in;
    final private PrintWriter out;
    private volatile boolean running = true;
    private ServerEventListener listener;
    public ServerListener(Socket s, ServerEventListener listener) throws IOException{
        socket =s;
        this.listener = listener;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }
    public void setListener(ServerEventListener listener){
        this.listener=listener;
    }
    @Override
    public void run(){
        try {
            String line;
            while (running &&(line = in.readLine()) != null) {
                String[] parts = line.split("\\|");
                String type = parts[0];
                switch (type) {
                    case "EXIT":
                        System.err.println("Server closed connection");
                        running = false;
                        break;
                    case "LOGIN_SUCCESSFUL":
                        SwingUtilities.invokeLater(listener::onLoginSuccess);
                        break;
                    case "LOGUP_SUCCESSFUL":
                        SwingUtilities.invokeLater(listener::onLogupSuccess);
                        break;
                    case "JOINED_ROOM":
                        SwingUtilities.invokeLater(listener::onJoinSuccess);
                        break;
                    case "TURN":
                        SwingUtilities.invokeLater(() -> listener.onYourTurn(parts[1]));
                        break;
                    case "MOVE":
                        SwingUtilities.invokeLater(() -> listener.onMoveReceived(parts[1]));
                        break;
                    case "CONTINUE_CAPTURE":
                        SwingUtilities.invokeLater(() -> listener.onContinueCapture(parts[1]));
                        break;
                    case "CAPTURE":
                        SwingUtilities.invokeLater(() -> listener.onCaptureReceived(parts[1]));
                        break;
                    case "GAME_OVER":
                        SwingUtilities.invokeLater(() -> listener.onGameOver(parts[1]));
                        break;
                    case "GAME_START_WHITE":
                        SwingUtilities.invokeLater(() -> listener.onStartGame(true, parts[1]));
                        break;
                    case "GAME_START_BLACK":
                        SwingUtilities.invokeLater(() -> listener.onStartGame(false, parts[1]));
                        break; 
                    case "ERROR":
                        SwingUtilities.invokeLater(() -> listener.onErrorReceived(parts[1]));
                        break;
                    default:
                        System.out.println("Unknown message type: " + type);
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected");
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                System.out.println("Socket close error");
            }
        }
    }
}
