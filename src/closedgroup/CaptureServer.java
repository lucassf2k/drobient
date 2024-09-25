package closedgroup;

import database.InFile;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class CaptureServer implements Runnable {
    private final int id;
    private final int portaRecepcao;
    private final InetAddress multicastIP;
    private final int portaMulticast;

    public CaptureServer(int id, int portaRecepcao, String multicastIP, int portaMulticast) throws IOException {
        this.id = id;
        this.portaRecepcao = portaRecepcao;
        this.multicastIP = InetAddress.getByName(multicastIP);
        this.portaMulticast = portaMulticast;
    }

    @Override
    public void run() {
        try (MulticastSocket ms = new MulticastSocket(portaRecepcao)) {
            System.out.println("Servidor " + id + " aguardando dados na porta " + portaRecepcao);

            while (true) {
                // Recebe dados do drone
                final var dadosRecepcao = new byte[1024];
                final var  pacoteRecepcao = new DatagramPacket(dadosRecepcao, dadosRecepcao.length);
                ms.receive(pacoteRecepcao);

                final var dados = new String(pacoteRecepcao.getData(), 0, pacoteRecepcao.getLength(), StandardCharsets.UTF_8);
                InFile.Write(dados);
                System.out.println("Servidor " + id + " recebeu: " + dados);

                // Retransmite para o grupo fechado
                final var pacoteEnvio = new DatagramPacket(dadosRecepcao, pacoteRecepcao.getLength(), multicastIP, portaMulticast);
                ms.send(pacoteEnvio);
                System.out.println("Servidor " + id + " retransmitiu dados para o grupo.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        // Inicializa servidores de captação
        new Thread(new CaptureServer(1, 5000, "225.7.8.9", 56789)).start();
        new Thread(new CaptureServer(2, 5001, "225.7.8.9", 56789)).start();
    }
}
