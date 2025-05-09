import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class ClientChatGui extends JFrame {
    private IServerChat server;
    private JTextArea messagesArea;
    private JTextField messageField;
    private JButton sendButton;
    private JList<String> usersList;
    private String usrName;
    private Registry registry;
    private JComboBox<String> roomsComboBox;
    private JButton joinRoomButton;
    private IUserChat userChat;
    private JButton createRoomButton;
    private IRoomChat currentRoom;
    private JButton leaveRoomButton;
    private ArrayList<String> rooms;

    /**
     * Timer que dispara a cada 5 segundos para atualizar manualmente
     * a lista de salas pois nao ha notificacao de sala fechada que o
     * usuario nao esta.
     */
    private Timer refreshTimer;

    public ClientChatGui(IServerChat server, String usrName, ArrayList<String> rooms, Registry registry) throws RemoteException {
        this.server = server;
        this.usrName = usrName;
        this.registry = registry;
        this.userChat = new UserChat(this);
        this.rooms = rooms;

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setTitle("Cliente Chat - Usuario: " + usrName);
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


        // ComboBox para selecionar salas
        roomsComboBox = new JComboBox<>(rooms.toArray(new String[0]));
        roomsComboBox.setBorder(BorderFactory.createTitledBorder("Salas"));
        roomsComboBox.setSelectedItem(null); // Nenhuma sala selecionada inicialmente

        roomsComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    refreshRoomList();
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(ClientChatGui.this, "Erro ao atualizar lista de salas: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Botão para entrar na sala
        joinRoomButton = new JButton("Entrar na Sala");
        joinRoomButton.addActionListener(e -> joinSelectedRoom());

        // Botão para criar nova sala
        createRoomButton = new JButton("Criar Nova Sala");
        createRoomButton.addActionListener(e -> createNewRoom(server));

        // Botão para sair da sala
        leaveRoomButton = new JButton("Sair da Sala");
        leaveRoomButton.addActionListener(e -> {
            if (currentRoom != null) {
                try {
                    currentRoom.leaveRoom(usrName);
                    currentRoom = null;
                    JOptionPane.showMessageDialog(this, "Você saiu da sala.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao sair da sala: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Você não está em nenhuma sala!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Painel para salas
        JPanel roomPanel = new JPanel(new BorderLayout());
        roomPanel.add(roomsComboBox, BorderLayout.CENTER);
        roomPanel.add(joinRoomButton, BorderLayout.EAST);
        roomPanel.add(createRoomButton, BorderLayout.SOUTH);
        roomPanel.add(leaveRoomButton, BorderLayout.WEST);

        // Adiciona componentes à janela
        add(messagesScroll, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(roomsComboBox, BorderLayout.NORTH);
        add(roomPanel, BorderLayout.WEST);

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

        roomsComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                try {
                    refreshRoomList();
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(ClientChatGui.this, "Erro ao atualizar salas: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        // timer de refresh
        refreshTimer = new Timer(5000, e -> {
            try {
                refreshRoomList();
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(ClientChatGui.this, "Erro ao atualizar salas: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        refreshTimer.start();

        setVisible(true);
    }

    private void joinSelectedRoom() {
        String selectedRoom = (String) roomsComboBox.getSelectedItem();
        if (selectedRoom == null || selectedRoom.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione uma sala válida!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Verifica se o registro está acessível
            if (registry == null) {
                JOptionPane.showMessageDialog(this, "Registro não está acessível!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tenta localizar a sala no registro
            IRoomChat room = (IRoomChat) registry.lookup(selectedRoom);

            // Entra na sala
            room.joinRoom(usrName, userChat);
            currentRoom = room;
            JOptionPane.showMessageDialog(this, "Você entrou na sala: " + selectedRoom, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Erro remoto ao entrar na sala: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (NotBoundException e) {
            JOptionPane.showMessageDialog(this, "Sala não encontrada no registro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createNewRoom(IServerChat serverChat) {
        String roomName = JOptionPane.showInputDialog(this, "Digite o nome da nova sala:", "Criar Nova Sala", JOptionPane.PLAIN_MESSAGE);
        if (roomName != null && !roomName.trim().isEmpty()) {
            try {
                server.createRoom(roomName);
                roomsComboBox.addItem(roomName);
                JOptionPane.showMessageDialog(this, "Sala criada com sucesso: " + roomName, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao criar sala: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            if (currentRoom == null) {
                JOptionPane.showMessageDialog(this, "Você não está em nenhuma sala!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                currentRoom.sendMsg(usrName, message); // Envia a mensagem para a sala atual
                messageField.setText(""); // Limpa o campo de entrada
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void receiveMessage(String senderName, String message) throws RemoteException {
        messagesArea.append(senderName + ": " + message + "\n");

        if (message != null && message.equals("Sala fechada pelo servidor")
            && senderName.equals("Servidor")) {
            this.rooms = server.getRooms(); // Obtém a lista de salas do servidor
            this.roomsComboBox.removeAllItems();
            for (String room : rooms) {
                this.roomsComboBox.addItem(room);
            }
        }
    }

    public void refreshRoomList() throws RemoteException {
        rooms = server.getRooms(); // Obtém a lista de salas do servidor
        roomsComboBox.removeAllItems();
        for (String room : rooms) {
            roomsComboBox.addItem(room);
        }
    }
}