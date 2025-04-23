package com.rodrigoappelt.sistemasdistribuidos.gui;

import com.rodrigoappelt.sistemasdistribuidos.interfaces.IServerChat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientChatGui extends JFrame {
    private IServerChat server;
    private JTextArea messagesArea;
    private JTextField messageField;
    private JButton sendButton;
    private DefaultListModel<String> usersModel;
    private JList<String> usersList;

    public ClientChatGui(IServerChat server) {
        this.server = server;

        setTitle("Cliente Chat");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Área de mensagens
        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        JScrollPane messagesScroll = new JScrollPane(messagesArea);
        messagesScroll.setBorder(BorderFactory.createTitledBorder("Mensagens"));

        // Campo de entrada de mensagem
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Enviar");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Lista de usuários
        usersModel = new DefaultListModel<>();
        usersList = new JList<>(usersModel);
        JScrollPane usersScroll = new JScrollPane(usersList);
        usersScroll.setBorder(BorderFactory.createTitledBorder("Usuários"));

        // Adiciona componentes à janela
        add(messagesScroll, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(usersScroll, BorderLayout.EAST);

        // Ação do botão enviar
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Ação ao pressionar Enter no campo de mensagem
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            try {
                // Envia a mensagem para o servidor
                // Exemplo: server.sendMessageToRoom("roomName", message);
                messagesArea.append("Você: " + message + "\n");
                messageField.setText("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void receiveMessage(String message) {
        messagesArea.append(message + "\n");
    }

    public void updateUsersList(String[] users) {
        usersModel.clear();
        for (String user : users) {
            usersModel.addElement(user);
        }
    }
}