import com.sun.source.tree.WhileLoopTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChatServer {
    public static final int PORT = 4000;
    private ServerSocket serverSocket;
    private String msg;
    private List<ClientSocket> clientes = new LinkedList<>();

    public void start() throws IOException {
        System.out.println("Servidor iniciado na porta " + PORT);
        serverSocket = new ServerSocket(PORT);
        clientConnectionLoop();
    }

    public void clientConnectionLoop() throws IOException {
        while (true) {
            ClientSocket clientSocket = new ClientSocket(serverSocket.accept());
            clientes.add(clientSocket);

            /*Runnable run = new Runnable() {
                @Override
                public void run() {
                    //";
                }
            };
            new Thread(run).start();*/
            new Thread(() -> clientMessageLoop(clientSocket)).start();
        }
    }

    private void sendMessageToAll(ClientSocket sender, String msg) {
        Iterator<ClientSocket> iterator = clientes.iterator();
        while (iterator.hasNext()) {
            ClientSocket clientSocket = iterator.next();
            if (!sender.equals(clientSocket)) {
                if (!clientSocket.sendMsg(msg)) {
                    iterator.remove();
                }
            }
        }
    }

    public void clientMessageLoop(ClientSocket clientSocket) {
        String msg;
        try {
            while ((msg = clientSocket.getMessage()) != null) {
                if ("sair".equalsIgnoreCase(msg)) {
                    return;
                }
                System.out.printf("Msg recebida do cliente %s\n ", msg);
                sendMessageToAll(clientSocket, msg);
            }
        } finally {
            clientSocket.close();
        }
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        try {
            chatServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Servidor fechado");
    }
}
