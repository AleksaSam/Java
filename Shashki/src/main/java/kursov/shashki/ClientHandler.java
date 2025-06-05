/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kursov.shashki;
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import static kursov.shashki.Server.clients;
/**
 *
 * @author Александра
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Map<String, Room> rooms;
    private BlockingQueue<String> queue;

    private Room currentRoom = null;

    public ClientHandler(Socket s, Map<String, Room> rooms) throws IOException {
        this.socket = s;
        this.rooms = rooms;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }
    
    public void sendExit() throws IOException {
        out.println("EXIT|");
    }
    
    private void sendResult(String result) {
        out.println(result);
        out.flush();
    }

    private void joinRoom(String roomId, String username) {
         Room room = rooms.get(roomId); // сначала пытаемся получить существующую комнату
    
        if (room == null) {
            // комнаты нет - создаем новую
            room = new Room(roomId);
            rooms.put(roomId, room);
        } else if (room.isFull()) {
            // комната уже заполнена
            out.println("ERROR|Комната уже заполнена");
            return;
        }

        boolean added = room.addPlayer(socket, username, this.in, this.out);
        if (!added) {
            out.println("ERROR|Комната уже заполнена");
            return;
        }
        this.currentRoom = room;
        out.println("JOINED_ROOM|");

        if (room.isFull()) {
            new GameSession(room, rooms).start();
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\\|");
                String type = parts[0];
                String login, password;
                boolean result;

                switch (type) {
                    case "EXIT":
                        if (currentRoom != null) {
                            queue = currentRoom.getQueueForSocket(socket);
                            queue.put(parts[0]);
                        } else {
                            out.println("ERROR|Not in game");
                        }
                        System.out.println("[SERVER] Client requested exit");
                        socket.close();
                        return;

                    case "LOGIN":
                        login = parts[1];
                        password = parts[2];
                        result = DataBase.checkUser(login, password);
                        sendResult(result ? "LOGIN_SUCCESSFUL|" : "ERROR|Не удалось выполнить вход, возможно логин или пароль неверны.");
                        break;

                    case "LOGUP":
                        login = parts[1];
                        password = parts[2];
                        if(DataBase.checkBeforeAdd(login)){
                            result = DataBase.addUser(login, password);
                            sendResult(result ? "LOGUP_SUCCESSFUL|" : "ERROR|Не удалось выполнить регистрацию");
                        }else{
                            sendResult("ERROR|Пользователь с таким логином уже существует");
                        }
                        break;

                    case "JOIN":
                        String roomId = parts[1];
                        login = parts[2];
                        joinRoom(roomId, login);
                        break;

                    case "MOVE":
                        if (currentRoom != null) {
                            queue = currentRoom.getQueueForSocket(socket);
                            queue.put(parts[1]); // передаём ход в очередь
                        } else {
                            out.println("ERROR|Not in game");
                        }
                        break;
                    case "EXIT_GAME":
                        if (currentRoom != null) {
                            queue = currentRoom.getQueueForSocket(socket);
                            queue.put(parts[0]);
                        } else {
                            out.println("ERROR|Not in game");
                        }
                        break;
                    default:
                        System.out.println("[SERVER] Unknown message type: " + type);
                }
            }
        } catch (Exception e) {
            System.out.println("[SERVER] Client error: " + e.getMessage());
        } finally {
            try {
                clients.remove(socket);
                if (!socket.isClosed()) socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                System.out.println("[SERVER] Error closing socket: " + e.getMessage());
            }
        }
    }
}
