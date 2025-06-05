/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kursov.shashki;
import clientShashki.Piece;
import java.util.List;
import java.util.stream.IntStream;
/**
 *
 * @author Александра
 */

public class MoveLogic {

    public static boolean isValidMove(Piece[][] pieces, int fromRow, int fromCol, int toRow, int toCol, boolean isCurrentPlayerWhite) {
        if (!isValidCell(pieces, fromRow, fromCol, toRow, toCol, isCurrentPlayerWhite)) return false;

        Piece movingPiece = pieces[fromRow][fromCol];
        int dRow = toRow - fromRow;
        int dCol = toCol - fromCol;

        boolean globalCaptureAvailable = isCaptureAvailable(pieces, isCurrentPlayerWhite);

        if (globalCaptureAvailable) {
            if (movingPiece.isPKing()) {
                return isKingCaptureMove(pieces, fromRow, fromCol, toRow, toCol, isCurrentPlayerWhite);
            } else {
                return isCaptureMove(pieces, fromRow, fromCol, toRow, toCol, isCurrentPlayerWhite);
            }
        }

        if (movingPiece.isPKing()) {
            return isValidKingMove(pieces, fromRow, fromCol, toRow, toCol);
        }

        if (Math.abs(dRow) == 1 && Math.abs(dCol) == 1) {
            return isCurrentPlayerWhite ? dRow == -1 : dRow == 1;
        }

        if (Math.abs(dRow) == 2 && Math.abs(dCol) == 2) {
            return isCaptureMove(pieces, fromRow, fromCol, toRow, toCol, isCurrentPlayerWhite);
        }

        return false;
    }

    private static boolean isValidCell(Piece[][] pieces, int fromRow, int fromCol, int toRow, int toCol, boolean isCurrentPlayerWhite) {
        if (!inBounds(fromRow, fromCol) || !inBounds(toRow, toCol)) return false;
        Piece piece = pieces[fromRow][fromCol];
        if (piece == null || piece.isPWhite() != isCurrentPlayerWhite) return false;
        if ((toRow + toCol) % 2 == 0 || pieces[toRow][toCol] != null) return false;
        return true;
    }

    public static boolean inBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private static boolean isEnemy(Piece piece, boolean isWhite) {
        return piece != null && piece.isPWhite() != isWhite;
    }

    public static boolean isCaptureAvailable(Piece[][] pieces, boolean isCurrentPlayerWhite) {
        boolean result = IntStream.range(0, 8)
            .anyMatch(row -> IntStream.range(0, 8)
                .anyMatch(col -> {
                    Piece piece = pieces[row][col];
                    return piece != null &&
                           piece.isPWhite() == isCurrentPlayerWhite &&
                           canCaptureFrom(pieces, row, col, isCurrentPlayerWhite);
                }));
        return result;
    }

    public static boolean canCaptureFrom(Piece[][] pieces, int row, int col, boolean isWhite) {
        Piece piece = pieces[row][col];
        if (piece == null) return false;

        if (piece.isPKing()) return isKingCaptureAvailable(pieces, row, col, isWhite);

        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir : directions) {
            int midRow = row + dir[0];
            int midCol = col + dir[1];
            int endRow = row + 2 * dir[0];
            int endCol = col + 2 * dir[1];

            if (inBounds(endRow, endCol) && inBounds(midRow, midCol)) {
                Piece mid = pieces[midRow][midCol];
                Piece end = pieces[endRow][endCol];
                if (mid != null && isEnemy(mid, isWhite) && end == null) return true;
            }
        }
        
        return false;
    }

    public static boolean isCaptureMove(Piece[][] pieces, int fromRow, int fromCol, int toRow, int toCol, boolean isWhite) {
        int dRow = toRow - fromRow;
        int dCol = toCol - fromCol;
        if (Math.abs(dRow) == 2 && Math.abs(dCol) == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            Piece mid = pieces[midRow][midCol];
            return mid != null && isEnemy(mid, isWhite);
        }
        return false;
    }

    public static void makeMove(Piece[][] board, int fromRow, int fromCol, int toRow, int toCol, boolean isWhiteTurn, List<String> capturedCoords) {
        Piece piece = board[fromRow][fromCol];
        board[toRow][toCol] = piece;
        board[fromRow][fromCol] = null;

        if (piece.isPKing()) {
            int dRow = Integer.compare(toRow, fromRow);
            int dCol = Integer.compare(toCol, fromCol);
            int row = fromRow + dRow;
            int col = fromCol + dCol;

            while (row != toRow && col != toCol) {
                if (board[row][col] != null && board[row][col].isPWhite() != isWhiteTurn) {
                    capturedCoords.add(row + " " + col);
                    board[row][col] = null;
                    break;
                }
                row += dRow;
                col += dCol;
            }
        } else {
            // Обычная фигура: проверка захвата
            if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
                int capRow = (fromRow + toRow) / 2;
                int capCol = (fromCol + toCol) / 2;
                capturedCoords.add(capRow + " " + capCol);
                board[capRow][capCol] = null;
            }
        }

        promoteToKing(piece, toRow);
    }

    private static void promoteToKing(Piece piece, int row) {
        if (!piece.isPKing()) {
            if ((piece.isPWhite() && row == 0) || (!piece.isPWhite() && row == 7)) {
                piece.makeKing();
            }
        }
    }

    private static boolean isValidKingMove(Piece[][] pieces, int fromRow, int fromCol, int toRow, int toCol) {
        if (Math.abs(toRow - fromRow) != Math.abs(toCol - fromCol)) return false;

        int dRow = Integer.compare(toRow, fromRow);
        int dCol = Integer.compare(toCol, fromCol);
        int row = fromRow + dRow;
        int col = fromCol + dCol;

        while (row != toRow && col != toCol) {
            if (pieces[row][col] != null) return false;
            row += dRow;
            col += dCol;
        }
        return true;
    }

    public static boolean canContinueCapture(Piece[][] pieces, int row, int col, boolean isWhite) {
        return canCaptureFrom(pieces, row, col, isWhite);
    }

    private static boolean isKingCaptureAvailable(Piece[][] pieces, int row, int col, boolean isWhite) {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];
            boolean enemyFound = false;

            while (inBounds(r, c)) {
                Piece current = pieces[r][c];
                if (current != null) {
                    if (!enemyFound && isEnemy(current, isWhite)) {
                        enemyFound = true;
                    } else {
                        break;
                    }
                } else {
                    if (enemyFound) return true;
                }

                r += dir[0];
                c += dir[1];
            }
        }
        return false;
    }

    public static boolean isKingCaptureMove(Piece[][] pieces, int fromRow, int fromCol, int toRow, int toCol, boolean isWhite) {
        if (Math.abs(fromRow - toRow) != Math.abs(fromCol - toCol)) return false;

        int dRow = Integer.compare(toRow, fromRow);
        int dCol = Integer.compare(toCol, fromCol);
        int row = fromRow + dRow;
        int col = fromCol + dCol;
        boolean enemyFound = false;

        while (row != toRow && col != toCol) {
            Piece current = pieces[row][col];
            if (current != null) {
                if (isEnemy(current, isWhite)) {
                    if (enemyFound) return false; // более одного врага
                    enemyFound = true;
                } else {
                    return false; // союзная фигура
                }
            }
            row += dRow;
            col += dCol;
        }

        return enemyFound;
    }
}
