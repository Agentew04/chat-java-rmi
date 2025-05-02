package com.rodrigoappelt.sistemasdistribuidos;

import com.rodrigoappelt.sistemasdistribuidos.interfaces.IRoomChat;
import com.rodrigoappelt.sistemasdistribuidos.interfaces.IUserChat;

import java.io.Serializable;
import java.util.Map;

public class RoomChat implements IRoomChat, Serializable {

    private String roomName;
    private Map<String, IUserChat> userList;

    @Override
    public void sendMsg(String usrName, String msg) {
        for (Map.Entry<String, IUserChat> entry : userList.entrySet()) {
            try {
                entry.getValue().deliverMsg(usrName, msg);
            } catch (Exception e) {
                System.out.println("Error sending message to " + entry.getKey());
            }
        }
    }

    @Override
    public void joinRoom(String userName, IUserChat user) {
        if (userList.containsKey(userName)) {
            System.out.println("User already in the room");
            return;
        }
        userList.put(userName, user);
        sendMsg(userName, "joined the room");
    }

    @Override
    public void leaveRoom(String usrName) {
        if (!userList.containsKey(usrName)) {
            System.out.println("User not in the room");
            return;
        }
        userList.remove(usrName);
        sendMsg(usrName, "left the room");
    }

    @Override
    public String getRoomName() {
        return roomName;
    }

    @Override
    public void closeRoom() {
        for (Map.Entry<String, IUserChat> entry : userList.entrySet()) {
            try {
                entry.getValue().deliverMsg("Server", "Room closed");
            } catch (Exception e) {
                System.out.println("Error notifying user " + entry.getKey());
            }
        }
        userList.clear();
    }

    public RoomChat(String name){
        this.roomName = name;
        this.userList = new java.util.HashMap<>();

        System.out.println("Room " + name + " created");

    }
}
