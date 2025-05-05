import java.io.Serial;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementacao concreta do servidor de chat
 */
public class ServerChat extends UnicastRemoteObject implements IServerChat, Serializable {

    private final Map<String, IRoomChat> roomList;

    Registry registry;

    @Override
    public ArrayList<String> getRooms() {
        return new ArrayList<>(roomList.keySet());
    }

    @Override
    public void createRoom(String roomName) throws RemoteException {
        // RFA2: n pode ter sala repetida
        if (roomList.containsKey(roomName)) {
            System.out.println("Sala já existe");
            // TODO: jogar excecao
            try {
                throw new Exception("Sala já existe");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // RFA2: criar sala
        RoomChat room = new RoomChat(roomName, registry);
        registry.rebind(roomName, room);
        roomList.put(roomName, room);
        System.out.println("Sala " + roomName + " criada");
    }

    public ServerChat(Registry registry) throws RemoteException {
        super();
        this.roomList = new HashMap<>();
        this.registry = registry;
    }
}
