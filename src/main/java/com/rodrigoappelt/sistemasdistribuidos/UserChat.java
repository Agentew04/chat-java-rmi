package com.rodrigoappelt.sistemasdistribuidos;

import com.rodrigoappelt.sistemasdistribuidos.gui.ClientChatGui;
import com.rodrigoappelt.sistemasdistribuidos.interfaces.IUserChat;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UserChat extends UnicastRemoteObject implements IUserChat, Serializable {
    private final ClientChatGui clientChatGui;

    public UserChat(ClientChatGui clientChatGui) throws RemoteException {
        this.clientChatGui = clientChatGui; // Initialize the reference
    }

    @Override
    public void deliverMsg(String senderName, String msg) throws RemoteException {
        System.out.println("Message from " + senderName + ": " + msg);
        // Assuming you have a reference to the ClientChatGui instance
        clientChatGui.receiveMessage(senderName, msg);
    }


}
