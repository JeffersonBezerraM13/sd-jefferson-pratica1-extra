package labsockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiUserChatServer {
    private ServerSocket serverSocket;
    protected static final Set<ClientHandler> clients = new HashSet<>();
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    protected static boolean running;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("[SERVER] Servidor iniciado na porta " + port);

        // Thread para aceitar novas conexões
        new Thread(this::acceptConnections).start();

        // Thread para enviar mensagens do servidor para todos os clientes
        new Thread(this::serverMessageHandler).start();
    }

    private void acceptConnections() {
        try {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] Nova conexão: " + clientSocket.getRemoteSocketAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            if (running) {
                System.out.println("[SERVER] Erro ao aceitar conexões: " + e.getMessage());
            }
        }
    }

    private void serverMessageHandler() {
        Scanner scanner = new Scanner(System.in);
        try {
            while (running) {
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("sair")) {
                    // Envia mensagem de despedida para todos os clientes
                    broadcast("[SERVER] O servidor está sendo desligado. Conexão será encerrada.");
                    stop();
                    break;
                }

                // Envia mensagem do servidor para todos os clientes
                broadcast("[SERVER] " + message);
                System.out.println("[SERVER] Mensagem enviada para todos os clientes");
            }
        } finally {
            scanner.close();
        }
    }

    public static void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    public void stop() {
        running = false;
        try {
            threadPool.shutdown();
            for (ClientHandler client : clients) {
                client.stop();
            }
            clients.clear();
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 6666;
        try {
            MultiUserChatServer server = new MultiUserChatServer();
            server.start(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}