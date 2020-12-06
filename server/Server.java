// TRuong Tuan Hung
// ?18020586
// server tao nhieu thread cho client ket noi
// moi thread ung voi 1 cong cua server
package server;

import java.io.*;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.security.MessageDigest;
import java.math.BigInteger;

public class Server {

    private static int PORT = 2000;
    private int port;
    private int id = 0;
    // cac goi socket
    private Socket socket = null;
    private ServerSocket server = null;
    // luong du lieu
    private List<User> users;
    private Thread thread;

    public void run(){
        try{
            this.server = new ServerSocket(port){
                protected void finalize() {
                    try{
                        this.close();
                    } catch(Exception e){
                        System.out.println(e);
                    }
                    
                }
            };
            System.out.println("Waiting for a client...");
            while (true)
            {
                try
                {
                    id++;
                    this.socket = this.server.accept();
                    String userName;
                    PrintStream printStream = new PrintStream(this.socket.getOutputStream());
                    Scanner scanner = new Scanner(this.socket.getInputStream());
                    userName = scanner.nextLine();
                    this.broadcast("***" + userName + " has joined ***");
                    this.getListUser(printStream, id);
                    User user = new User(id, userName, this.socket);
                    this.users.add(user);
                    this.thread = new Thread(new ClientHandler(this, this.socket.getInputStream(), id));
                    this.thread.start();
                }
                catch(Exception exception)
                {
                    System.out.println(exception);
                }
            }            
        }
        catch(Exception exception){
            System.out.println(exception);
        }
    }

    public void verifyName(){

    }

    public void getListUser(PrintStream stream, int id){
        for(User user: this.users){
            if(id != user.getId()){
                stream.println(user.getId() + ":" + user.getUserName());
            }
        }
    }

    public Server(int port){
        this.port = port;
        this.users = new ArrayList<User>();
    }

    public static void main(String args[]){
          Server server = new Server(PORT);
          server.run();
    }
    
    public void chatPrivate(String userName, String mess){
        try{
            PrintStream printStream;
            for(User user: this.users){
                if(userName.equals(user.getUserName())){
                    printStream = new PrintStream(user.getSocket().getOutputStream());
                    printStream.println(mess);
                }
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    // gui mess cho tat ca client dang active
    public void broadcast(String mess){
        try{
            PrintStream printStream;
            for (User user : this.users) {
                printStream = new PrintStream(user.getSocket().getOutputStream());
                printStream.println(mess);
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void saveFile(String fileName, String content){
        try {
            File file = new File("server/File/" + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writeFile = new FileWriter(file, true);
            writeFile.write(content + "\n");
            writeFile.close();
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public boolean openFile(String fileName) {
        try {
            File openFile = new File("server/File/" + fileName);
            if(openFile.exists()){
                return true;
            } 
            return false;
        } catch (Exception x){
            System.out.println(x);
            return false;
        }
    }

    public void getListFile(int id){
        try{
            PrintStream printStream;
            for (User user : this.users) {
                if(user.getId() == id){
                    printStream = new PrintStream(user.getSocket().getOutputStream());
                    File x = new File("server/File/");
                    File[] children = x.listFiles();
                    try{
                        for (File file : children) {
                            printStream.println("[Server]: " + file.getName());
                          }
                    } catch(Exception e){
                        System.out.print(e);

                    }
                    
                }
            }
        } catch(Exception e){
            System.out.print(e);
        }
    }

    public void sendFile(String fileName,int id){
        try {
            PrintStream printStream;
            File file = new File("server/File/" + fileName);
            Scanner readFile = new Scanner(file);
            for(User user: this.users){
                if(user.getId() == id){
                    printStream = new PrintStream(user.getSocket().getOutputStream());
                    printStream.println("#SENDING " + fileName);
                    String content = "";
                    while(readFile.hasNextLine()) {
                        content = readFile.nextLine();
                        printStream.println(content);
                    }
                    printStream.println("#COMPLETE");
                    // printStream.println(getMD5(file));
                    readFile.close();
                }
            }
        } catch (Exception x) {
            System.out.println(x);
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

    public void sendToUserById(int id, String mess){
        try{
            PrintStream printStream;
            for(User user: this.users){
                if(user.getId() == id){
                    printStream = new PrintStream(user.getSocket().getOutputStream());
                    printStream.println(mess);
                }
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void sendToUserByName(String userName, String mess){
        try{
            PrintStream printStream;
            for(User user: this.users){
                if(user.getUserName().equals(userName)){
                    printStream = new PrintStream(user.getSocket().getOutputStream());
                    printStream.println(mess);
                }
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }
}

class ClientHandler implements Runnable{
    private Server server;
    private InputStream mesClient;
    private int id;
    public ClientHandler(Server server, InputStream stream, int id){
        this.server = server;
        this.mesClient = stream;
        this.id = id;
    }
    // doc mess cua client va gui cho cac client khac
    @Override
    public void run(){
        String mess;
        Scanner scanner = new Scanner(this.mesClient);
        while(scanner.hasNextLine()){
            mess = scanner.nextLine();
            if(mess.contains("@SEND ")){
                String revName = mess.substring(6);
                while(true){
                    String content = scanner.nextLine();
                    this.server.sendToUserByName(revName, content);
                    if(content.equals("@COMPLETE")){
                        break; 
                    } 
                }      
            } else if(mess.contains("#DOWNLOAD")){
                String fileName = mess.substring(10);
                if(this.server.openFile(fileName)){
                    this.server.sendFile(fileName, id);
                } else {
                    this.server.sendToUserById(id, fileName + " not exist.");
                }
            }else if(mess.equals("#LIST")){
                this.server.getListFile(this.id);
            }else if(mess.contains("#UPLOAD")){
                String fileName = mess.substring(8);
                while(true){
                    String content = scanner.nextLine();
                   if(content.equals("#COMPLETE")){
                      break; 
                   } 
                   this.server.saveFile(fileName, content);
                }
            }else if(mess.contains("-->@")){
                String userName = mess.substring(search(mess, '@') + 1, search(mess, ':'));
                server.chatPrivate(userName, mess);
            } else {
                server.broadcast(mess);
            }
        }
        scanner.close();
    }
    private int search(String str, char c){
        try {
        for (int i = 0; i < str.length(); i++){
            if(Character.toString(str.charAt(i)).equals(Character.toString(c))){
                return i;
            }
        }
        return -1;
        } catch(Exception exception){
            return -1;
        }
    }
}
