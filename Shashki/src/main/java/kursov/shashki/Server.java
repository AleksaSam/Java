/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package kursov.shashki;
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 *
 * @author Александра
 */
public class Server {
    private static final int PORT = 8080;
    public static Map<Socket, ClientHandler> clients = new ConcurrentHashMap<>();
    public static Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final int THREADS_LIMIT=100;
    private static ExecutorService threadPool; 
    public static void main(String[] args) throws IOException{
        ServerSocket socket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);
        threadPool = Executors.newFixedThreadPool(THREADS_LIMIT);
        try{
        while(true){
            Socket clientSocket = socket.accept();
            ClientHandler client = new ClientHandler(clientSocket, rooms);
            clients.put(clientSocket, client);
            threadPool.submit(client);
            System.out.println("Client connected");
        }
        }
        catch(IOException e){
            System.out.println("Server error: " + e.getMessage());
        }
        finally {
        // При штатном завершении, закрываем сервер
            try {
                for (ClientHandler client : clients.values()) {
                    client.sendExit();
                }
                if (!socket.isClosed()) {
                    socket.close();
                }
                clients.clear();
                threadPool.shutdown(); // Завершаем пул потоков корректно
            } catch (IOException e) {
                System.out.println("Error during server shutdown: " + e.getMessage());
            }
        }
    }
    
    
}
