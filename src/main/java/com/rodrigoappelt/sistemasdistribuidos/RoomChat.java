package com.rodrigoappelt.sistemasdistribuidos;

import com.rodrigoappelt.sistemasdistribuidos.interfaces.IRoomChat;
import com.rodrigoappelt.sistemasdistribuidos.interfaces.IUserChat;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class RoomChat extends UnicastRemoteObject implements IRoomChat, Serializable {

    private String roomName;
    private Map<String, IUserChat> userList;

    @Override
    public void sendMsg(String senderName, String message) throws RemoteException {
        for (IUserChat user : userList.values()) {
            user.deliverMsg(senderName, message); // Notify all clients
        }
    }

    @Override
    public void joinRoom(String userName, IUserChat user) throws RemoteException {
        if (userList.containsKey(userName)) {
            System.out.println("User already in the room");
            return;
        }
        userList.put(userName, user);
        sendMsg(userName, "joined the room");
    }

    @Override
    public void leaveRoom(String usrName) throws RemoteException {
        if (!userList.containsKey(usrName)) {
            System.out.println("User not in the room");
            return;
        }
        userList.remove(usrName);
        sendMsg(usrName, "left the room");
    }

    @Override
    public String getRoomName() throws RemoteException {
        return roomName;
    }

    @Override
    public void closeRoom() throws RemoteException {
        for (Map.Entry<String, IUserChat> entry : userList.entrySet()) {
            try {
                entry.getValue().deliverMsg("Server", "Room closed");
            } catch (Exception e) {
                System.out.println("Error notifying user " + entry.getKey());
            }
        }
        userList.clear();
    }

    public RoomChat(String name) throws RemoteException {
        super();
        this.roomName = name;
        this.userList = new java.util.HashMap<>();

        System.out.println("Room " + name + " created");

    }
}
