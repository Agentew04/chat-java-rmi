import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerChatGui extends JFrame {
    private ServerChat server;
    private DefaultComboBoxModel<String> roomsModel;
    private Map<String, IRoomChat> roomList = new HashMap<>();
    private JButton refreshRoomsButton;
    private JButton closeRoomButton;
    private Registry registry;

    public ServerChatGui(ServerChat server, Registry registry) throws RemoteException {
        this.server = server;
        this.registry = registry;
        setTitle("Servidor Chat Admin");
        setSize(500, 200); // Tamanho menor da janela
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5)); // Adiciona espaçamento entre componentes

        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ComboBox de salas - agora com tamanho melhor ajustado
        roomsModel = new DefaultComboBoxModel<>();
        JComboBox<String> roomComboBox = new JComboBox<>(roomsModel);
        roomComboBox.setPreferredSize(new Dimension(300, 25)); // Tamanho mais adequado
        roomComboBox.setBorder(BorderFactory.createTitledBorder("Selecionar Sala"));

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        refreshRoomsButton = new JButton("Atualizar Salas");
        closeRoomButton = new JButton("Fechar Sala");

        // Adiciona ação ao botão de atualizar
        refreshRoomsButton.addActionListener(e -> {
            try {
                refreshRooms();
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar salas: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Adiciona ação ao botão de fechar sala
        closeRoomButton.addActionListener(e -> closeSelectedRoom());

        // Adiciona componentes ao painel de botões
        buttonPanel.add(refreshRoomsButton);
        buttonPanel.add(closeRoomButton);

        // Adiciona componentes ao painel principal
        mainPanel.add(roomComboBox, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Adiciona painel principal à janela
        add(mainPanel);

        // Atualiza salas ao iniciar
        try {
            refreshRooms();
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar salas: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }

    public void refreshRooms() throws RemoteException {
        try {
            List<String> rooms = server.getRooms();
            roomsModel.removeAllElements();
            roomList.clear();

            for (String room : rooms) {
                roomsModel.addElement(room);
                IRoomChat roomChat = (IRoomChat) registry.lookup(room);
                roomList.put(room, roomChat);
            }
        } catch (Exception e) {
            throw new RemoteException("Erro ao atualizar lista de salas: " + e.getMessage());
        }
    }

    public void closeSelectedRoom() {
        String selectedRoom = (String) roomsModel.getSelectedItem();
        if (selectedRoom != null) {
            try {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Tem certeza que deseja fechar a sala '" + selectedRoom + "'?",
                        "Confirmar", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    IRoomChat room = roomList.get(selectedRoom);
                    room.closeRoom();
                    server.removeRoomFromServer(room);
                    roomList.remove(selectedRoom);
                    refreshRooms();
                    JOptionPane.showMessageDialog(this, "Sala fechada com sucesso!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao fechar sala: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nenhuma sala selecionada!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
}