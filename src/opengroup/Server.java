package opengroup;

import database.InFile;
import database.LA;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class Server implements Runnable {
    private final MulticastSocket multicastSocket;
    private final InetAddress multicastIP;
    private final int multicastPort;
    private final int dronePort;

    public Server(int dronePort, String multicastAddress, int multicastPort) throws IOException {
        this.dronePort = dronePort;
        this.multicastIP = InetAddress.getByName(multicastAddress);
        this.multicastPort = multicastPort;
        this.multicastSocket = new MulticastSocket(dronePort); // Porta para receber dados do drone
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(receivedPacket);

                String dados = new String(receivedPacket.getData(), 0, receivedPacket.getLength(), StandardCharsets.UTF_8);
//                LA.db.add(dados);
                InFile.Write(dados);
                System.out.println("Servidor recebeu do Drone: " + dados);
                // Enviando para o grupo multicast
                DatagramPacket sendPacket = new DatagramPacket(dados.getBytes(StandardCharsets.UTF_8),
                        dados.length(),
                        multicastIP,
                        multicastPort);
                multicastSocket.send(sendPacket);
                System.out.println("Servidor enviou ao grupo multicast: " + dados);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            multicastSocket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        final var server = new Server(56789, "225.7.8.9", 55555);
        new Thread(server).start();
    }
}
