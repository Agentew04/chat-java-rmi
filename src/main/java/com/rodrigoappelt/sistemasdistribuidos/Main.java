package com.rodrigoappelt.sistemasdistribuidos;

import com.rodrigoappelt.sistemasdistribuidos.gui.ClientChatGui;
import com.rodrigoappelt.sistemasdistribuidos.gui.ServerChatGui;
import com.rodrigoappelt.sistemasdistribuidos.interfaces.IServerChat;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import javax.swing.*;

public class Main {

    private static final int RMI_PORT = 2020;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showInitialPopup);
    }

    private static void showInitialPopup() {
        String[] options = {"Hospedar Servidor", "Entrar em Servidor"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Qual modo você escolhe?",
                "Escolha uma opção",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Lidando com a escolha
        if (choice == 0) {
            System.out.println("Modo servidor");
            host();
        } else if (choice == 1) {
            System.out.println("Modo cliente");
            String ip = showIpPopup();
            if (ip == null) {
                System.out.println("Conexão cancelada pelo usuário.");
                return;
            }
            client(ip);
        }
    }

    private static Registry initializeRmi(){
        try {
            return LocateRegistry.createRegistry(RMI_PORT);
        }catch (RemoteException e){
            System.out.println("Nao foi possivel criar a registry RMI :( erro: " + e.getMessage());
            return null;
        }
    }

    private static void host(){
        Registry registry = initializeRmi();
        if(registry == null){
            // mensagem de erro ja printada pro initializeRmi
            return;
        }

        final ServerChat serverChat;
        try {
            serverChat = new ServerChat(registry);
        } catch (RemoteException e) {
            System.out.println("fudeu");
            throw new RuntimeException(e);
        }

        // bind
        try{
            registry.rebind("Servidor", serverChat);
        } catch (RemoteException e) {
            System.out.println("Erro ao bindar classe a registry RMI: " + e.getMessage());
            return;
        }

        System.out.println("Servidor de chat e RMI hospedado com sucesso na porta 2020! :)");
        SwingUtilities.invokeLater(() -> {
            try {
                new ServerChatGui(serverChat, registry);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("aaa");
    }

    private static String showIpPopup(){
        JTextField ipField = new JTextField();
        ipField.setText("localhost");
        Object[] message = {
                "Endereço IP do servidor:", ipField
        };

        int option = JOptionPane.showConfirmDialog(
                null,
                message,
                "Conectar ao Servidor",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            return ipField.getText().trim();
        } else {
            return null;
        }
    }

    private static void client(String ip) {
        final IServerChat serverChat;

        try {
            Registry registry = LocateRegistry.getRegistry(ip, RMI_PORT);
            serverChat = (IServerChat) registry.lookup("Servidor");

            System.out.println("Conectado ao servidor de chat com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
            return;
        }

        if (serverChat == null) {
            System.out.println("Objeto do chat nao encontrado no servidor RMI. Qual é a chave usada lá? Aqui é usado 'serverChat'");
            return;
        }

        String usrName = JOptionPane.showInputDialog(null, "Digite seu nome de usuário:", "Nome de Usuário", JOptionPane.PLAIN_MESSAGE);
        if (usrName == null || usrName.trim().isEmpty()) {
            System.out.println("Conexão cancelada: Nome de usuário inválido.");
            return;
        }

        try {
            Registry registry = LocateRegistry.getRegistry(ip, RMI_PORT);
            ArrayList<String> rooms = serverChat.getRooms();
            SwingUtilities.invokeLater(() -> {
                try {
                    new ClientChatGui(serverChat, usrName, rooms, registry);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            System.out.println("Erro ao obter lista de salas: " + e.getMessage());
        }

    }
}