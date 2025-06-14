/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clientShashki;

import java.io.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Александра
 */
public class LoginMenu extends javax.swing.JFrame implements ServerEventListener {
    private static Client client;
    private Player player;
    /**
     * Creates new form LoginMenu
     */
    public LoginMenu() {
        initializeListener();
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        logTextField1 = new javax.swing.JTextField();
        logIn = new javax.swing.JButton();
        logUp = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(400, 275));
        setPreferredSize(new java.awt.Dimension(400, 275));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jPasswordField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 140, 170, -1));

        jLabel1.setText("Логин");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, -1, -1));

        jLabel2.setText("Пароль");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, -1, -1));
        getContentPane().add(logTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 170, -1));

        logIn.setText("Вход");
        logIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logInActionPerformed(evt);
            }
        });
        getContentPane().add(logIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 180, -1, -1));

        logUp.setText("Регистрация");
        logUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logUpActionPerformed(evt);
            }
        });
        getContentPane().add(logUp, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 180, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/back (1).jpg"))); // NOI18N
        jLabel3.setText("jLabel3");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -30, -1, 330));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void initializeListener(){
        client = new Client(this);
    }
    
    private void logInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logInActionPerformed
        // TODO add your handling code here:
        String login = logTextField1.getText().trim();
        char[] password = jPasswordField1.getPassword();
        if(!login.isEmpty() && password.length!=0){
            try {
                client.Login(login, new String(password));
                player = new Player(login);
            } catch (IOException e) {
                System.err.println("Client error: " + e.getMessage());
            }
        }else{
            JOptionPane.showMessageDialog(this, "Поля для ввода логина и пароля не могут быть пустыми!");
        }
    }//GEN-LAST:event_logInActionPerformed

    private void logUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logUpActionPerformed
        // TODO add your handling code here:
        String login = logTextField1.getText().trim();
        char[] password = jPasswordField1.getPassword();
        if(!login.isEmpty() && password.length!=0 && checkDataLength(login, password)){
            try {
                client.Logup(login, new String(password));
            } catch (IOException e) {
                System.err.println("Client error: " + e.getMessage());
            }
        }else{
            JOptionPane.showMessageDialog(this, "Поля для ввода логина и пароля не могут быть пустыми!");
        }
    }//GEN-LAST:event_logUpActionPerformed

    private boolean checkDataLength(String login, char[] password){
        return login.length()>=3 && password.length>=3;
    }
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing
    
    
    @Override
    public void onLoginSuccess(){
        JOptionPane.showMessageDialog(this, "Вход успешен");
        dispose();
        new GameMenu(client, player).setVisible(true);
    }
    @Override
    public void onLogupSuccess(){
        JOptionPane.showMessageDialog(this, "Регистрация прошла успешно");
    }
    @Override
    public void onJoinSuccess(){}
    @Override
    public void onMoveReceived(String move){}
    @Override
    public void onErrorReceived(String error){
        JOptionPane.showMessageDialog(this, error);
    }
    @Override
    public void onContinueCapture(String message){}
    @Override
    public void onGameOver(String result){}
    @Override
    public void onYourTurn(String turn){}
    @Override
    public void onStartGame(boolean turn, String name){}
    @Override
    public void onCaptureReceived(String message){}
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                new LoginMenu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JButton logIn;
    private javax.swing.JTextField logTextField1;
    private javax.swing.JButton logUp;
    // End of variables declaration//GEN-END:variables
}
