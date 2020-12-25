// VuTrongDuc
// 18020342
// package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.lang.Runnable;
import java.lang.Thread;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
public class Client extends JPanel{
    // private static int PORT = 2000;
    // private static String IP = "127.0.0.1";
    private String userName;
    private String address;
    private int port;

    private static final long serialVersionUID = 1L;
    private JTextArea textArea;
    private JButton btnNewButton;
    private JLabel lblNewLabel;
    private JLabel lblHistory;
    private JTextArea textArea_1;

    Thread thread;
    PrintStream toServer;

    public Client(String address, int port, String name){
        this.address = address;
        this.port = port;
        this.userName = name;
        try {
            GroupLayout groupLayout = new GroupLayout(this);
            groupLayout.setHorizontalGroup(
                            groupLayout.createParallelGroup(Alignment.LEADING)
                                            .addGroup(groupLayout.createSequentialGroup().addGap(332)
                                                            .addComponent(getLblHistory(), GroupLayout.DEFAULT_SIZE, 67,
                                                                            Short.MAX_VALUE)
                                                            .addGap(339))
                                            .addGroup(groupLayout
                                                            .createSequentialGroup().addGap(12)
                                                            .addComponent(getTextArea_1(), GroupLayout.DEFAULT_SIZE, 714,
                                                                            Short.MAX_VALUE)
                                                            .addGap(12))
                                            .addGroup(groupLayout.createSequentialGroup().addGap(12).addGroup(groupLayout
                                                            .createParallelGroup(Alignment.LEADING)
                                                            .addGroup(groupLayout.createSequentialGroup().addGap(168)
                                                                            .addComponent(getLblNewLabel(),
                                                                                            GroupLayout.DEFAULT_SIZE, 230,
                                                                                            Short.MAX_VALUE)
                                                                            .addGap(147))
                                                            .addComponent(getTextArea(), GroupLayout.DEFAULT_SIZE, 545,
                                                                            Short.MAX_VALUE))
                                                            .addGap(12).addComponent(getBtnNewButton(), GroupLayout.DEFAULT_SIZE,
                                                                            157, Short.MAX_VALUE)
                                                            .addGap(12)));
            groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
                            .createSequentialGroup().addComponent(getLblHistory(), GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                            .addGap(6).addComponent(getTextArea_1(), GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE).addGap(1)
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                            .addGroup(groupLayout.createSequentialGroup()
                                                            .addComponent(getLblNewLabel(), GroupLayout.PREFERRED_SIZE, 27,
                                                                            GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(getTextArea(), GroupLayout.DEFAULT_SIZE, 154,
                                                                            Short.MAX_VALUE))
                                            .addGroup(groupLayout.createSequentialGroup().addGap(26).addComponent(
                                                            getBtnNewButton(), GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)))
                            .addGap(13)));
            setLayout(groupLayout);
        } catch (Exception e) {
            System.out.println("Error while create Main Panel");
        }
    }
    private void initComponents() {
    }

    public JTextArea getTextArea() {
        if (textArea == null) {
            textArea = new JTextArea();
            textArea.setFont(new Font("Arial", Font.PLAIN, 26));
        }
        return textArea;
    }

    public JButton getBtnNewButton() {
        if (btnNewButton == null) {
            btnNewButton = new JButton("SEND");
            btnNewButton.setBackground(Color.CYAN);
            btnNewButton.setForeground(Color.RED);
            btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 26));
        }
        return btnNewButton;
    }

    public JLabel getLblNewLabel() {
        if (lblNewLabel == null) {
            lblNewLabel = new JLabel("Type the mess here to send");
            lblNewLabel.setForeground(Color.GRAY);
            lblNewLabel.setFont(new Font("Tahoma", Font.ITALIC, 18));
        }
        return lblNewLabel;
    }

    public JLabel getLblHistory() {
        if (lblHistory == null) {
            lblHistory = new JLabel("History");
            lblHistory.setForeground(Color.GRAY);
            lblHistory.setFont(new Font("Tahoma", Font.ITALIC, 18));
        }
        return lblHistory;
    }

    public JTextArea getTextArea_1() {
        if (textArea_1 == null) {
            textArea_1 = new JTextArea();
            textArea_1.setFont(new Font("Arial", Font.PLAIN, 30));
        }
        return textArea_1;
    }

    public JTextArea getMess() {
        return textArea_1;
    }

    public void run(){
        try{
            Socket socket = new Socket(address, port);
            System.out.println("Connected");
            toServer = new PrintStream(socket.getOutputStream());
            toServer.println(this.userName);
            
            thread = new Thread(new ReceivedMessagesHandler(socket.getInputStream(), this));
            thread.start();
            // gui mes den server
            btnNewButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                        if (textArea.getText().isEmpty()) return;
                        try {
                            String mess = textArea.getText();
                            textArea_1.append(userName + ": " + textArea.getText() + "\n");
                            textArea.setText("");
                            if(mess.contains("@SEND @")){
                                String revName = mess.substring(7, search(mess, ' ', 2));
                                // System.out.print("Enter a user name: " + revName);
                                String fileName = mess.substring(search(mess, ' ', 2) + 1);
                                // System.out.print("Enter a user name: " + fileName);
            
                                toServer.println("@SEND " + revName);
                                uploadFilePrivate(fileName, toServer);
                            } else if(mess.equals("#LIST") || mess.contains("#DOWNLOAD")){
                                toServer.println(mess);
                            } else if(mess.contains("#UPLOAD")){
                                String fileName = mess.substring(8);
                                if(openFile(fileName)){
                                    toServer.println(mess);
                                    uploadFile(fileName, toServer);
                                    System.out.println("complete");
                                } else {
                                    System.out.println("Error: " + fileName + " not found!");
                                }
                            } else if(mess.contains("-->@")){
                                toServer.println(userName + mess);
                            } else {
                                toServer.println("[All]" + userName + ": " + mess);
                            }
                        } catch (Exception e) {
                        System.out.println("Error while sendding messeger");
                        }
                    }
                });
            // toServer.close();
            // socket.close();
        }
        catch(Exception exception){
            System.out.println(exception);
        }
    }

    public String getUserName(){
        return this.userName;
    }

    private boolean openFile(String fileName) {
        try {
            File openFile = new File("File/" + fileName);
            if(openFile.exists()){
                return true;
            } 
            return false;
        } catch (Exception x){
            System.out.println(x);
            return false;
        }
    }

    private void uploadFile(String fileName,PrintStream toServer){
        try {
            File file = new File("File/" + fileName);
            Scanner readFile = new Scanner(file);
            String content = "";
            while(readFile.hasNextLine()) {
                content = readFile.nextLine() + "\n";
                toServer.println(content);
            }
            toServer.println("#COMPLETE");
            toServer.println("[All]" + userName + ": " + fileName + " uploaded!");
            readFile.close();
        } catch (Exception x) {
            System.out.println(x);
        }
    }

    private void uploadFilePrivate(String fileName,PrintStream toServer){
        try {
            File file = new File("File/" + fileName);
            Scanner readFile = new Scanner(file);
            toServer.println("@SENDING " + fileName);
            String content = "";
            while(readFile.hasNextLine()) {
                content = readFile.nextLine() + "\n";
                toServer.println(content);
            }
            toServer.println("@COMPLETE");
            readFile.close();
        } catch (Exception x) {
            System.out.println(x);
        }
    }



    public void saveFile(String fileName, String content, String userName){
        try {

            File file = new File("File/" + userName);
            if (!file.exists()) {
                file.mkdir();
            }
            System.out.println(file.getAbsolutePath());

            File x = new File("File/" + userName + "/"+ fileName);
            if (!x.exists()) {
                x.createNewFile();
            }
            FileWriter writeFile = new FileWriter(x, true);
            writeFile.write(content + "\n");
            // String md5 = "";
            // if(md5.equals(getMD5(file))){
            //     System.out.println("-->Complete");
            // } else {
            //     System.out.println("-->Fail");
            // }
            writeFile.close();
        } catch(Exception e){
            System.out.println(e);
        }
    }

    private String hashing(byte[] data){
        BigInteger number = new BigInteger(1, data);
        String hashtext = number.toString(16);
        while (hashtext.length() < 32) {
          hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    private String getMD5(File file){
        MessageDigest md5;
        try {
          md5 = MessageDigest.getInstance("MD5");
          FileInputStream fileInput = new FileInputStream(file);
          byte[] dataBytes = new byte[1024];
          int nread = 0;
          while ((nread = fileInput.read(dataBytes)) != -1) {
            md5.update(dataBytes, 0, nread);
          }
          byte[] byteData = md5.digest();
          fileInput.close();
          return hashing(byteData);
        } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e);
        }
    }

    // public static void main(String args[]) {
    //     try {
    //         Client client = new Client(IP, PORT);
    //         client.run();
    //     } catch (Exception x) {
    //         System.out.println(x);
    //     }
    // }

    private int search(String str, char c, int pos){
        try {
            int count = 0;
        for (int i = 0; i < str.length(); i++){
            if(Character.toString(str.charAt(i)).equals(Character.toString(c))){
                count++;
            }
            if(count == pos){
                return i;
            }
        }
        return -1;
        } catch(Exception exception){
            return -1;
        }
    }
}

class ReceivedMessagesHandler implements Runnable {

    private InputStream server;
    private Client client;

    public ReceivedMessagesHandler(InputStream server, Client client) {
        this.server = server;
        this.client = client;
    }
    @Override
    // nhan mess tu server
    public void run() {
        String mess;
        String userName = "[All]" + this.client.getUserName() + ":";
        Scanner scanner = new Scanner(server);
        while (scanner.hasNextLine()) {
            mess = scanner.nextLine();
            if(mess.contains("@SENDING")){
                String fileName = mess.substring(9);
                while(true){
                    String content = scanner.nextLine();
                   if(content.equals("@COMPLETE")){
                      break; 
                   } 
                   this.client.saveFile(fileName, content, this.client.getUserName());
                }
            } else if(mess.contains("#SENDING")){
                String fileName = mess.substring(9);
                while(true){
                    String content = scanner.nextLine();
                   if(content.equals("#COMPLETE")){
                      break; 
                   } 
                   this.client.saveFile(fileName, content, this.client.getUserName());
                }
            } else if(!mess.contains(userName)){
                this.client.getMess().append(mess + "\n");
            }
        }
        scanner.close();
    }
}
