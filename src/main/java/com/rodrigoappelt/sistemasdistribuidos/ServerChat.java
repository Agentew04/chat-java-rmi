package com.rodrigoappelt.sistemasdistribuidos;

import com.rodrigoappelt.sistemasdistribuidos.interfaces.IRoomChat;
import com.rodrigoappelt.sistemasdistribuidos.interfaces.IServerChat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementacao concreta do servidor de chat
 */
public class ServerChat implements IServerChat, Serializable {

    private Map<String, IRoomChat> roomList = new HashMap<>();

    @Override
    public ArrayList<String> getRooms() {
        return new ArrayList<>(roomList.keySet());
    }

    @Override
    public void createRoom(String roomName) {
        // RFA2: n pode ter sala repetida
        if (roomList.containsKey(roomName)) {
            System.out.println("Sala já existe");
            // TODO: jogar excecao
            return;
        }

    }
}
