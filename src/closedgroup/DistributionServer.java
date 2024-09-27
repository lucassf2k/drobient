package closedgroup;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class DistributionServer implements Runnable {
    private final int id;
    private final InetAddress multicastIP;
    private final int portaMulticast;
    private final InetAddress externalMulticastIP; // Endereço multicast para usuários externos
    private final int portaExternal;
    private final Set<String> allowedIPs = new HashSet<>();

    public DistributionServer(int id, String multicastIP, int portaMulticast, String externalMulticastIP, int portaExternal) throws IOException {
        this.id = id;
        this.multicastIP = InetAddress.getByName(multicastIP);
        this.portaMulticast = portaMulticast;
        this.externalMulticastIP = InetAddress.getByName(externalMulticastIP); // Multicast externo
        this.portaExternal = portaExternal; // Porta externa

        allowedIPs.add("192.168.0.0/24");
    }

    @Override
    public void run() {
        try (MulticastSocket ms = new MulticastSocket(portaMulticast)) {
            final var networkInterface = NetworkInterface.getByName("wlp2s0");
            final var grupo = new InetSocketAddress(multicastIP, portaMulticast);

            ms.joinGroup(grupo, networkInterface);
            System.out.println("Servidor " + id + " conectado ao grupo multicast " + multicastIP);

            while (true) {
                // Recebe dados do grupo fechado
                final var dadosRecepcao = new byte[1024];
                final var pacoteRecepcao = new DatagramPacket(dadosRecepcao, dadosRecepcao.length);
                ms.receive(pacoteRecepcao);

                if (!isIPAllowed(pacoteRecepcao.getAddress())) {
                    System.out.println("IP " + pacoteRecepcao.getAddress() + " bloqueado no servidor de distribuição.");
                    continue;  // Ignora a mensagem se o IP não for permitido
                }

                final var dados = new String(pacoteRecepcao.getData(), 0, pacoteRecepcao.getLength(), StandardCharsets.UTF_8);
                System.out.println("Servidor " + id + " recebeu do grupo: " + dados);

                // Aqui, os dados podem ser processados e disponibilizados para usuários externos.
                sendToUsers(dados);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendToUsers(final String dados) throws IOException {
        // Cria um MulticastSocket para enviar dados para o grupo externo
        try (final var msExternal = new MulticastSocket()) {
            final var dadosEnvio = dados.getBytes(StandardCharsets.UTF_8);
            // Cria pacote para enviar para o grupo multicast externo
            final var pacoteEnvio = new DatagramPacket(dadosEnvio, dadosEnvio.length, externalMulticastIP, portaExternal);
            // Envia os dados
            msExternal.send(pacoteEnvio);
            System.out.println("Servidor " + id + " retransmitiu dados para o grupo externo: " + dados);
        }
    }

    private boolean isIPAllowed(InetAddress ip) {
        String ipStr = ip.getHostAddress();
        for (String allowedIP : allowedIPs) {
            if (ipStr.startsWith(allowedIP.replace("/24", ""))) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        // Inicializa servidores de distribuição
        new Thread(new DistributionServer(3, "225.7.8.9", 56789, "226.7.8.10", 56790)).start();
        new Thread(new DistributionServer(4, "225.7.8.9", 56789, "226.7.8.10", 56790)).start();
    }
}
