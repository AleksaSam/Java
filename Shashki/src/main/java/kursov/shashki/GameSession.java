/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kursov.shashki;
import clientShashki.Piece;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
/**
 *
 * @author Александра
 */
public class GameSession extends Thread {
    private final Room room;
    private final int BOARD_SIZE = 8;
    private Piece[][] pieces = new Piece[BOARD_SIZE][BOARD_SIZE];
    private boolean isWhiteTurn = true;
    private final Map<String, Room> rooms;
    private String winnerName;

    public GameSession(Room room, Map<String, Room> rooms) {
        this.room = room;
        this.rooms = rooms;
        initializeBoard();
    }


    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 != 0) {
                    if (row < 3) pieces[row][col] = new Piece(false);
                    if (row > 4) pieces[row][col] = new Piece(true);
                }
            }
        }
    }

    @Override
    public void run() {
        PrintWriter out1 = room.out1;
        PrintWriter out2 = room.out2;

        out1.println("GAME_START_WHITE|" + room.getPlayerName2());
        out2.println("GAME_START_BLACK|" + room.getPlayerName1());

        boolean continueCapture = false;
        int lastRow = -1, lastCol = -1;

        try {
            while (true) {
                BlockingQueue<String> queue = isWhiteTurn ? room.queue1 : room.queue2;
                PrintWriter currentOut = isWhiteTurn ? out1 : out2;
                PrintWriter opponentOut = isWhiteTurn ? out2 : out1;

                String moveLine = queue.take();
                if(moveLine.equals("EXIT_GAME")||moveLine.equals("EXIT")) {
                    currentOut.println("GAME_OVER|Вы вышли из игры");
                    opponentOut.println("GAME_OVER|Противник вышел из игры");
                    break;
                }
                String[] coords = moveLine.trim().split(" ");

                if (coords.length != 4) {
                    currentOut.println("ERROR|Неверный формат хода");
                    continue;
                }

                int fromRow = Integer.parseInt(coords[0]);
                int fromCol = Integer.parseInt(coords[1]);
                int toRow = Integer.parseInt(coords[2]);
                int toCol = Integer.parseInt(coords[3]);

                if (!MoveLogic.inBounds(fromRow, fromCol) || !MoveLogic.inBounds(toRow, toCol)) {
                    currentOut.println("ERROR|Выход за границы доски");
                    continue;
                }

                Piece piece = pieces[fromRow][fromCol];
                if (piece == null) {
                    currentOut.println("ERROR|Нет шашек на выбранной клетке");
                    continue;
                }

                if (piece.isPWhite() != isWhiteTurn) {
                    currentOut.println("ERROR|Не ваша шашка");
                    continue;
                }

                if (continueCapture && (fromRow != lastRow || fromCol != lastCol)) {
                    currentOut.println("ERROR|Вы должны продолжить захват той же шашкой");
                    continue;
                }

                boolean mustCapture = MoveLogic.isCaptureAvailable(pieces, isWhiteTurn);
                boolean isCapture = piece.isPKing()
                        ? MoveLogic.isKingCaptureMove(pieces, fromRow, fromCol, toRow, toCol, isWhiteTurn)
                        : MoveLogic.isCaptureMove(pieces, fromRow, fromCol, toRow, toCol, isWhiteTurn);

                if (mustCapture && !isCapture) {
                    currentOut.println("ERROR|Есть шашка, которую необходиомо захватить");
                    continue;
                }

                if (!MoveLogic.isValidMove(pieces, fromRow, fromCol, toRow, toCol, isWhiteTurn)) {
                    currentOut.println("ERROR|Недопустимый ход");
                    continue;
                }

                List<String> capturedCoords = new ArrayList<>();
                MoveLogic.makeMove(pieces, fromRow, fromCol, toRow, toCol, isWhiteTurn, capturedCoords);

                String moveMessage = "MOVE|" + fromRow + " " + fromCol + " " + toRow + " " + toCol;
                currentOut.println(moveMessage);
                opponentOut.println(moveMessage);

                if (!capturedCoords.isEmpty()) {
                    String captureMessage = "CAPTURE|" + String.join(" ", capturedCoords);
                    currentOut.println(captureMessage);
                    opponentOut.println(captureMessage);
                }

                if (!capturedCoords.isEmpty() && MoveLogic.canContinueCapture(pieces, toRow, toCol, isWhiteTurn)) {
                    continueCapture = true;
                    lastRow = toRow;
                    lastCol = toCol;
                    currentOut.println("CONTINUE_CAPTURE|" + toRow + " " + toCol);
                } else {
                    continueCapture = false;
                    lastRow = lastCol = -1;
                    isWhiteTurn = !isWhiteTurn;
                    opponentOut.println("TURN|" + "1");
                    currentOut.println("TURN|" + "0");
                }

                if (isGameOver()) {
                    currentOut.println("GAME_OVER|" + winnerName + " выиграл!");
                    opponentOut.println("GAME_OVER|" + winnerName + " выиграл!");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[SERVER] Game error: " + e.getMessage());
        }finally {
            rooms.remove(room.getRoomId());
        }
    }

    private boolean isGameOver() {
        boolean whiteExists = false, blackExists = false;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece p = pieces[row][col];
                if (p != null) {
                    if (p.isPWhite()) whiteExists = true;
                    else blackExists = true;
                }
            }
        }

        if (!whiteExists || !blackExists) {
            if (whiteExists) {
                winnerName = room.getPlayerName1();
            } else if (blackExists) {
                winnerName = room.getPlayerName2();
            }
            return true;
        }

        return false;
    }
    
}
