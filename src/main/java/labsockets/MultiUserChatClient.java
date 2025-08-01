package labsockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MultiUserChatClient {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private boolean running;
    private String username;

    public void start(String serverIp, int serverPort) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite seu nome de usuário: ");
        this.username = scanner.nextLine();

        System.out.println("[CLIENT] Conectando ao servidor " + serverIp + ":" + serverPort);
        socket = new Socket(serverIp, serverPort);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

        // Envia o nome de usuário primeiro
        output.writeUTF(username);
        running = true;

        // Thread para receber mensagens do servidor
        new Thread(() -> {
            try {
                while (running) {
                    String message = input.readUTF();
                    System.out.println(message);
                }
            } catch (IOException e) {
                if (running) {
                    System.out.println("[CLIENT] Conexão com o servidor perdida.");
                }
            } finally {
                stop();
            }
        }).start();

        // Thread para enviar mensagens para o servidor
        try {
            while (running) {
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("sair")) {
                    running = false;
                    output.writeUTF("sair");
                } else {
                    output.writeUTF(message);
                }
                output.flush();
            }
        } catch (IOException e) {
            System.out.println("[CLIENT] Erro ao enviar mensagem.");
        } finally {
            scanner.close();
            stop();
        }
    }

    public void stop() {
        running = false;
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String serverIp = "localhost";
        int serverPort = 6666;
        try {
            MultiUserChatClient client = new MultiUserChatClient();
            client.start(serverIp, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}