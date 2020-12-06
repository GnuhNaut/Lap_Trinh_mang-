package server;

import java.net.Socket;

class User {
    private int Id;
    private Socket socket;
    private String userName;

    public User(int Id, String userName, Socket socket){
        this.Id = Id;
        this.userName = userName;
        this.socket = socket;
    }
    public int getId() {
        return this.Id;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
