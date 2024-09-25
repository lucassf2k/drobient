package closedgroup;

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
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String dados = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                final var table = formatarTabela(dados);
                System.out.println("Usuario recebeu: " + table);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            socket.close();
        }
    }

    private static String formatarTabela(String dados) {
        // Expressão regular para capturar os dados
        String regex = "ID:\\s*(\\w+),\\s*Pressão:\\s*(\\d+)\\s*hPa,\\s*Radiação:\\s*(\\d+)\\s*W/m\\^2,\\s*Temperatura:\\s*(\\-?\\d+)\\s*°C,\\s*Umidade:\\s*(\\d+)%";

        // Usar a classe Pattern para compilar a regex
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);

        // Matcher para procurar os dados
        java.util.regex.Matcher matcher = pattern.matcher(dados);

        if (!matcher.find()) {
            return dados;
        }

        // Capturar os valores
        String id = matcher.group(1);
        String pressao = matcher.group(2);
        String radiacao = matcher.group(3);
        String temperatura = matcher.group(4);
        String umidade = matcher.group(5);

        // Construir a tabela com alinhamento
        StringBuilder tabela = new StringBuilder();
        tabela.append("+----------------+-------------+\n");
        tabela.append(String.format("| %-14s | %-11s |\n", "Campo", "Valor"));
        tabela.append("+----------------+-------------+\n");
        tabela.append(String.format("| %-14s | %-11s |\n", "ID", id));
        tabela.append(String.format("| %-14s | %-5s hPa   |\n", "Pressão", pressao));
        tabela.append(String.format("| %-14s | %-5s W/m^2 |\n", "Radiação", radiacao));
        tabela.append(String.format("| %-14s | %-5s °C    |\n", "Temperatura", temperatura));
        tabela.append(String.format("| %-14s | %-5s %%     |\n", "Umidade", umidade));
        tabela.append("+----------------+-------------+\n");

        return tabela.toString();
    }

    public static void main(String[] args) throws IOException {
        final var user = new opengroup.User("225.7.8.9", 55555);
        new Thread(user).start();
    }
}