package com.rodrigoappelt.sistemasdistribuidos.gui;

import com.rodrigoappelt.sistemasdistribuidos.ServerChat;
import com.rodrigoappelt.sistemasdistribuidos.interfaces.IRoomChat;
import com.rodrigoappelt.sistemasdistribuidos.interfaces.IServerChat;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class ServerChatGui extends JFrame {
    private IServerChat server;
    private DefaultListModel<String> roomsModel;
    private JList<String> roomList;
    private JPanel roomPanel;
    private JTextArea messagesArea;
    private JList<String> usersList;
    private DefaultListModel<String> usersModel;

    public ServerChatGui(IServerChat server) {
        this.server = server;
        System.out.println("A");
        setTitle("Servidor Chat Admin - v1 brainrot");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Esquerda - Lista de salas
        roomsModel = new DefaultListModel<>();
        roomList = new JList<>(roomsModel);
        JScrollPane roomsScroll = new JScrollPane(roomList);
        roomsScroll.setBorder(BorderFactory.createTitledBorder("Salas"));

        // Direita - Painel da sala
        roomPanel = new JPanel(new BorderLayout());
        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        usersModel = new DefaultListModel<>();
        usersList = new JList<>(usersModel);

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel roomTitle = new JLabel("Sala selecionada");
        JButton closeRoomButton = new JButton("FECHAR");

        topPanel.add(roomTitle, BorderLayout.WEST);
        topPanel.add(closeRoomButton, BorderLayout.EAST);

        JPanel rightTop = new JPanel(new GridLayout(1, 2));
        rightTop.add(new JScrollPane(messagesArea));
        rightTop.add(new JScrollPane(usersList));

        roomPanel.add(topPanel, BorderLayout.NORTH);
        roomPanel.add(rightTop, BorderLayout.CENTER);

        // Atualizar salas ao iniciar
        refreshRooms();

        // Clique na lista
        roomList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedRoom = roomList.getSelectedValue();
                roomTitle.setText("Sala: " + selectedRoom);
                // Simula carregar mensagens e usuários
                loadRoomData(selectedRoom);
            }
        });

        // Botão de fechar sala
        closeRoomButton.addActionListener(e -> {
            String selectedRoom = roomList.getSelectedValue();
            if (selectedRoom != null) {
                try {
                    // Pega a instância da sala e fecha
                    IRoomChat room = (IRoomChat) LocateRegistry.getRegistry().lookup(selectedRoom);
                    room.closeRoom();
                    refreshRooms();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Monta a GUI
        add(roomsScroll, BorderLayout.WEST);
        add(roomPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void refreshRooms() {
        try {
            ArrayList<String> rooms = server.getRooms();
            roomsModel.clear();
            for (String room : rooms) {
                roomsModel.addElement(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRoomData(String roomName) {
        // Aqui tu pode buscar mensagens/usuários reais com base no roomName
        // Por enquanto, é simulação
        messagesArea.setText("Mensagens da sala " + roomName + ":\n- Hello\n- World");
        usersModel.clear();
        usersModel.addElement("User1");
        usersModel.addElement("User2");
    }

    // main para testar
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            IServerChat server = (IServerChat) registry.lookup("ServidorCentral");

            SwingUtilities.invokeLater(() -> new ServerChatGui(server));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
