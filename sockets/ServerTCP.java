import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public class ServerTCP {

    private static Object[] messages;
    private static int messagesCount;
    private static int msgSize = 100;

    public static void main(String[] args) throws IOException {

        messages = readFile("sockets/msgs.txt").toArray();
        messagesCount = Integer.parseInt(messages[0].toString());
        messages = Arrays.copyOfRange(messages, 1, messagesCount + 1);

        connection();

    }

    public static Stream<String> readFile(String diretorio) throws IOException {

        Path path = Paths.get(diretorio);
        return Files.lines(path);
    }

    private static void connection() {

        try {
            // cria socket na porta 4444
            ServerSocket tomadaServidora = new ServerSocket(4444);
            System.out.println("Waiting for connections");

            while (true) {
                Socket tomadaCliente = tomadaServidora.accept();
                System.out.println("Connected" + tomadaCliente.getInetAddress());

                servidor(tomadaCliente, tomadaServidora);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void servidor(Socket tomadaCliente, ServerSocket tomadaServidora) {

        new Thread(() -> {
            try {
                /*
                 * cria dois buffers de array de bytes, um para enviar e
                 * outro para receber a conexao com o cliente
                 */
                OutputStream bufferSaida = tomadaCliente.getOutputStream();
                InputStream bufferEntrada = tomadaCliente.getInputStream();

                while (true) {

                    byte[] textoAEnviar = new byte[msgSize]; // verificar tamanho da mensagem antes de enviar
                    // tamanho de entrada 8 por se tratar apenas de números pequenos
                    byte[] textoRecebido = new byte[16];

                    bufferEntrada.read(textoRecebido);
                    String[] request = new String(textoRecebido).trim().split(" ");
                    String message;

                    if (request.length != 2) {
                        message = "Error: Wrong arguments";
                    } else if (request[1].equals("n") || request[1].equals("y")) {
                        try {
                            int posicao = Integer.parseInt(request[0]);

                            if (posicao < 0 || posicao > messagesCount) {
                                message = "Error: Invalid index! Available messages between 0 and " + (messagesCount);
                            } else if (posicao == 0) {
                                int aleatorio = new Random().nextInt(messagesCount);
                                message = "Message: " + messages[aleatorio].toString();
                            } else {
                                message = "Message: " + messages[posicao - 1].toString();
                            }
                        } catch (Exception e) {
                            message = "Error: Something went wrong";
                            e.printStackTrace();
                        }

                        if (request[1].equals("y")) {
                            if (message.getBytes().length > msgSize) {

                                message = message.substring(0, 100);
                            }

                            textoAEnviar = message.getBytes();
                            bufferSaida.write(textoAEnviar);
                            bufferSaida.flush();
                            break;
                        }

                    } else {
                        message = "Error: Invalid option";
                    }
                    if (message.getBytes().length > msgSize) {
                        message = message.substring(0, 100);
                    }
                    textoAEnviar = message.getBytes();
                    bufferSaida.write(textoAEnviar);
                    bufferSaida.flush();

                }

                bufferSaida.close();
                bufferEntrada.close();
                tomadaCliente.close();
                tomadaServidora.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }).start();
    }
}
