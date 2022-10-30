import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient implements Runnable {
    private final String HOST = "localhost";
    private ClientSocket clientSocket;
    private Scanner ler;

    public ChatClient() {
        this.ler = new Scanner(System.in);
    }

    @Override
    public void run() {
        String msg;
        while ((msg = clientSocket.getMessage()) != null) {
            System.out.printf("Msg recebida do servidor: %s\n", msg);
        }
    }

    public void start() throws IOException {
        try {
            clientSocket = new ClientSocket(new Socket(HOST, ChatServer.PORT));

            System.out.println("Cliente conctado ao servidor pela porta " + ChatServer.PORT);
            new Thread(this).start();
            messegeLoop();
        } finally {
            clientSocket.close();
        }

    }

    public void messegeLoop() throws IOException {
        String msg;
        do {
            System.out.print("Digite uma mensagem (ou sair para finalzar): ");
            msg = ler.nextLine();
            clientSocket.sendMsg(msg);
        } while (!msg.equalsIgnoreCase("sair"));
    }

    public static void main(String[] args) {
        try {
            ChatClient chatClient = new ChatClient();
            chatClient.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Cliente Finalizado");
    }
}
