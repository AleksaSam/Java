/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package clientShashki;

/**
 *
 * @author Александра
 */
public interface ServerEventListener {
    void onLoginSuccess();
    void onLogupSuccess();
    void onJoinSuccess();
    void onMoveReceived(String move);
    void onErrorReceived(String error);
    void onContinueCapture(String message);
    void onGameOver(String result);
    void onYourTurn(String turn);
    void onStartGame(boolean turn, String name);
    void onCaptureReceived(String message);
}
