package com.rodrigoappelt.sistemasdistribuidos;

import com.rodrigoappelt.sistemasdistribuidos.interfaces.IUserChat;

public class UserChat implements IUserChat {
    @Override
    public void deliverMsg(String senderName, String msg) {
        System.out.println("Message from " + senderName + ": " + msg);

    }
}
