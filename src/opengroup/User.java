package opengroup;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class User implements Runnable {
    private final MulticastSocket socket;
    private final InetAddress multicastIP;
    private final int porta;

    public User(String multicastAddress, int porta) throws IOException {
        this.multicastIP = InetAddress.getByName(multicastAddress);
        this.porta = porta;
        this.socket = new MulticastSocket(porta);
        NetworkInterface networkInterface = NetworkInterface.getByName("wlp2s0"); // Adaptar à interface de rede correta
        socket.joinGroup(new InetSocketAddress(multicastIP, porta), networkInterface);
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            while (true) {
                final var packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                final var data = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                System.out.println("Usuario recebeu: " + data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            socket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        final var user = new User("225.7.8.9", 55555);
        new Thread(user).start();
    }
}