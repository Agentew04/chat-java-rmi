package com.rodrigoappelt.sistemasdistribuidos.gui;

import com.rodrigoappelt.sistemasdistribuidos.ServerChat;
import com.rodrigoappelt.sistemasdistribuidos.interfaces.IRoomChat;
import com.rodrigoappelt.sistemasdistribuidos.interfaces.IServerChat;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerChatGui extends JFrame {
    private IServerChat server;
    private DefaultComboBoxModel<String> roomsModel;
    private Map<String, IRoomChat> roomList = new HashMap<>();
    private JPanel roomPanel;
    private JTextArea messagesArea;
    private JList<String> usersList;
    private DefaultListModel<String> usersModel;
    private JButton refreshRoomsButton;
    private Registry registry;

    public ServerChatGui(IServerChat server, Registry registry) throws RemoteException {
        this.server = server;
        this.registry = registry;
        setTitle("Servidor Chat Admin");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Esquerda - ComboBox de salas
        roomsModel = new DefaultComboBoxModel<>();
        JComboBox<String> roomComboBox = new JComboBox<>(roomsModel);
        roomComboBox.setBorder(BorderFactory.createTitledBorder("Salas"));

        // Direita - Painel da sala
        roomPanel = new JPanel(new BorderLayout());
        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        usersModel = new DefaultListModel<>();
        usersList = new JList<>(usersModel);

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel roomTitle = new JLabel("Sala selecionada");
        JButton closeRoomButton = new JButton("Fechar sala");

        refreshRoomsButton = new JButton("Atualizar Salas");
        refreshRoomsButton.addActionListener(e -> {
            try {
                refreshRooms();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        topPanel.add(roomTitle, BorderLayout.WEST);
        topPanel.add(closeRoomButton, BorderLayout.EAST);

        JPanel rightTop = new JPanel(new GridLayout(1, 2));
        rightTop.add(new JScrollPane(messagesArea));
        rightTop.add(new JScrollPane(usersList));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshRoomsButton);

        roomPanel.add(topPanel, BorderLayout.NORTH);
        roomPanel.add(rightTop, BorderLayout.CENTER);

        // Atualizar salas ao iniciar
        refreshRooms();

        // Seleção no ComboBox
        roomComboBox.addActionListener(e -> {
            String selectedRoom = (String) roomComboBox.getSelectedItem();
            if (selectedRoom != null) {
                roomTitle.setText("Sala: " + selectedRoom);
                // Simula carregar mensagens e usuários
                loadRoomData(selectedRoom);
            }
        });

        // Botão de fechar sala
        closeRoomButton.addActionListener(e -> {
            closeSelectedRoom();
        });

        // Monta a GUI
        add(roomComboBox, BorderLayout.WEST);
        add(roomPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void refreshRooms() throws RemoteException {
        try {
            List<String> rooms = server.getRooms();
            roomsModel.removeAllElements();
            roomList.clear(); // Limpa o mapa antes de atualizar

            for (String room : rooms) {
                roomsModel.addElement(room);
                // Adiciona a sala ao mapa usando o Registry passado
                IRoomChat roomChat = (IRoomChat) registry.lookup(room);
                roomList.put(room, roomChat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeSelectedRoom() {
        String selectedRoom = (String) roomsModel.getSelectedItem();
        if (selectedRoom != null) {
            try {
                IRoomChat room = roomList.get(selectedRoom);
                room.sendMsg("Servidor", "Sala fechada pelo servidor");
                room.closeRoom();
                roomList.remove(selectedRoom);
                refreshRooms();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadRoomData(String roomName) {

    }

}
