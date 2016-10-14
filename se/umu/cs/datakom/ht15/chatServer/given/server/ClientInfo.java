package umu.cs.datakom.ht15.chatServer.given.server;

import java.net.Socket;

/**
 * Created by Åski on 03/10/2015.
 */
public class ClientInfo {
    private String nickName;
    private Socket socket;

    public ClientInfo(String nickName,Socket socket){
        this.nickName=nickName;
        this.socket=socket;
    }

    public String getNickName() {
        return nickName;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setNickName(String nickName){
        this.nickName =  nickName;
    }
}
