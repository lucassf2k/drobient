package closedgroup;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Drone implements Runnable {
    private final String nome;
    private final InetAddress servidorIP;
    private final int porta;
    private final Random random = new Random();;
    private final UUID id = UUID.randomUUID();

    public Drone(String nome, String servidorIP, int porta) throws IOException {
        this.nome = nome;
        this.servidorIP = InetAddress.getByName(servidorIP);
        this.porta = porta;
    }

    @Override
    public void run() {
        try (MulticastSocket ms = new MulticastSocket()) {
            while (true) {
                // Simula coleta de dados
                final var data = getData();
                // Envia dados para o servidor
                byte[] dadosEnvio = data.getBytes(StandardCharsets.UTF_8);
                DatagramPacket pacoteEnvio = new DatagramPacket(dadosEnvio, dadosEnvio.length, servidorIP, porta);
                ms.send(pacoteEnvio);
                System.out.println(nome + " enviou: " + data);
                // Espera 3 segundos antes de coletar e enviar novamente
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (IOException | InterruptedException e) {
           throw new RuntimeException(e);
        }
    }

    private String getData() {
        final var north = Regions.NORTH;
        if (nome.equals(north.getValue())) return generateDataNorthRegion();
        return generateDataSouthRegion();
    }

    private String generateDataNorthRegion() {
        final var pressure = 1000 + random.nextInt(16); // Exemplo: 1000-1015 hPa
        final var radiation = 600 + random.nextInt(501); // Exemplo: 600-1100 W/m^2
        final var temperature = 24 + random.nextInt(17); // Exemplo: 24 a 40 °C
        final var humidity = 60 + random.nextInt(41); // Exemplo: 60-100%
        return String.format("ID: %s, Pressão: %d hPa, Radiação: %d W/m^2, Temperatura: %d °C, Umidade: %d%%",
                id, pressure, radiation, temperature, humidity);
    }

    private String generateDataSouthRegion() {
        final var pressure = 980 + random.nextInt(51); // Exemplo: 980-1030 hPa
        final var radiation = 400 + random.nextInt(501); // Exemplo: 400-900 W/m^2
        final var temperature = -5 + random.nextInt(41); // Exemplo: -5 a 35 °C
        final var humidity = 40 + random.nextInt(51); // Exemplo: 40-90%
        return String.format("ID: %s, Pressão: %d hPa, Radiação: %d W/m^2, Temperatura: %d °C, Umidade: %d%%",
                id, pressure, radiation, temperature, humidity);
    }

    public static void main(String[] args) throws IOException {
        // Inicializa drones
//        new Thread(new Drone("Norte", "localhost", 5000)).start(); // Envia para Servidor1
//        new Thread(new Drone("Sul", "localhost", 5001)).start();   // Envia para Servidor2
        final var executorService = Executors.newFixedThreadPool(2);
        // Submete as tarefas dos servidores de distribuição
        executorService.submit(new Drone("Norte", "localhost", 5000));
        executorService.submit(new Drone("Sul", "localhost", 5001));
    }
}