package opengroup;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

public class Drone implements Runnable {
    private final DatagramSocket socket;
    private final InetAddress serverIP;
    private final int serverPort;
    private final UUID id = UUID.randomUUID();

    public Drone(String serverAddress, int port) throws IOException {
        this.socket = new DatagramSocket();
        this.serverIP = InetAddress.getByName(serverAddress);
        this.serverPort = port;
    }

    private String getData() {
        final var random = new Random();
        final var pressure = 950 + random.nextInt(101); // Exemplo: 950-1050 hPa
        final var radiation = 200 + random.nextInt(801); // Exemplo: 200-1000 W/m^2
        final var temperature = -10 + random.nextInt(51); // Exemplo: -10 a 40 °C
        final var humidity = random.nextInt(101); // Exemplo: 0-100%

        return String.format("ID: %s, Pressão: %d hPa, Radiação: %d W/m^2, Temperatura: %d °C, Umidade: %d%%",
                id, pressure, radiation, temperature, humidity);
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 60; i++) { // Simula 60 envios de dados (3 minutos com intervalo de 3 segundos)
                final var data = getData();
                byte[] buffer = data.getBytes(StandardCharsets.UTF_8);
                final var packet = new DatagramPacket(buffer, buffer.length, serverIP, serverPort);
                socket.send(packet);
                System.out.println("Drone enviou dados: " + data);
                Thread.sleep(3000); // Intervalo de 3 segundos
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            socket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        final var drone = new Drone("localhost", 56789);
        final var drone2 = new Drone("localhost", 56789);
        new Thread(drone).start();
        new Thread(drone2).start();
    }
}