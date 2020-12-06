// TRuong Tuan Hung
// ?18020586
package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.lang.Runnable;
import java.lang.Thread;
import java.security.MessageDigest;
import java.math.BigInteger;

public class Client {
    private static int PORT = 2000;
    private static String IP = "127.0.0.1";
    private String userName;
    private String address;
    private int port;

    public Client(String address, int port){
        this.address = address;
        this.port = port;
    }

    public void run(){
        try{
            Socket socket = new Socket(address, port);
            System.out.println("Connected");

            
            PrintStream toServer = new PrintStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter a user name: ");
            this.userName = scanner.nextLine();
            toServer.println(this.userName);
            
            Thread thread = new Thread(new ReceivedMessagesHandler(socket.getInputStream(), this));
            thread.start();
            // gui mes den server
            while(scanner.hasNextLine()){
                String mess = scanner.nextLine();
                // end chat
                if(mess.equals("Bye All")){
                    thread.stop();
                    toServer.println("*** " + this.userName + " has left *** \n");
                    break;
                }
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
            }

            toServer.close();
            scanner.close();
            socket.close();
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

    public static void main(String args[]) {
        try {
            Scanner scan = new Scanner(System.in);
            System.out.print("Enter Port: ");
            PORT = scan.nextInt();
            Client client = new Client(IP, PORT);
            client.run();
        } catch (Exception x) {
            System.out.println(x);
        }
    }

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
                System.out.println(mess);
            }
        }
        scanner.close();
    }
}
