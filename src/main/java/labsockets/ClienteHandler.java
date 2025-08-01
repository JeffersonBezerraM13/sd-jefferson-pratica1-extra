package labsockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static labsockets.MultiUserChatServer.clients;
import static labsockets.MultiUserChatServer.running;

class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String username;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
    }

    public void sendMessage(String message) {
        try {
            output.writeUTF(message);
            output.flush();
        } catch (IOException e) {
            System.out.println("[SERVER] Erro ao enviar mensagem para " + username);
        }
    }

    @Override
    public void run() {
        try {
            // Primeira mensagem é o nome de usuário
            username = input.readUTF();
            System.out.println("[SERVER] " + username + " entrou no chat.");
            MultiUserChatServer.broadcast(username + " entrou no chat.");

            while (running) {
                String message = input.readUTF();
                if (message.equalsIgnoreCase("sair")) {
                    break;
                }
                System.out.println(username + ": " + message);
                MultiUserChatServer.broadcast(username + ": " + message);
            }
        } catch (IOException e) {
            System.out.println("[SERVER] " + username + " desconectado abruptamente.");
        } finally {
            try {
                if (username != null) {
                    System.out.println("[SERVER] " + username + " saiu do chat.");
                    MultiUserChatServer.broadcast(username + " saiu do chat.");
                }
                clients.remove(this);
                stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}