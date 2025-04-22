package com.rodrigoappelt.sistemasdistribuidos;

import javax.swing.*;

public class Main {
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
        } else if (choice == 1) {
            System.out.println("Modo cliente");
        }
    }
}