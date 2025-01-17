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

        // le a quantidade e as mensagens do arquivo msgs.txt e adiciona em um array
        messages = readFile("sockets/msgs.txt").toArray();
        messagesCount = Integer.parseInt(messages[0].toString());
        messages = Arrays.copyOfRange(messages, 1, messagesCount + 1);

        connection();

    }

    // metodo para ler mensagens de um arquivo
    public static Stream<String> readFile(String diretorio) throws IOException {

        Path path = Paths.get(diretorio);
        return Files.lines(path);
    }

    // metodo responsavel pela conexao do cliente
    private static void connection() {

        try {
            // cria socket na porta 4444
            ServerSocket tomadaServidora = new ServerSocket(4444);
            System.out.println("Waiting for connections");

            while (true) {
                // espera por um cliente e aceita e conexao
                Socket tomadaCliente = tomadaServidora.accept();
                System.out.println("Connected" + tomadaCliente.getInetAddress());

                servidor(tomadaCliente, tomadaServidora);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // metodo para receber e tratar das requisicoes do cliente
    private static void servidor(Socket tomadaCliente, ServerSocket tomadaServidora) {

        //inicia uma nova thread para permitir novas conexoes
        new Thread(() -> {
            try {
                /*
                 * cria dois buffers de array de bytes, um para enviar e
                 * outro para receber a conexao com o cliente
                 */
                OutputStream bufferSaida = tomadaCliente.getOutputStream();
                InputStream bufferEntrada = tomadaCliente.getInputStream();
                boolean isRunning = true;

                while (isRunning) {

                    byte[] textoAEnviar = new byte[msgSize];
                    byte[] textoRecebido = new byte[16];

                    bufferEntrada.read(textoRecebido);
                    // recebe e separa a mensagem em um array
                    String[] request = new String(textoRecebido).trim().split(" ");
                    String message;

                    // verifica o numero de argumentos recebidos
                    if (request.length != 2) {
                        message = "Error: Wrong arguments";

                        // valida os argumentos recebidos
                    } else if (!(request[1].equals("n") || request[1].equals("y"))) {
                        message = "Error: Invalid option";

                    } else {
                        try {
                            int posicao = Integer.parseInt(request[0]);

                            // valida o indice da mensagem recebida
                            if (posicao < 0 || posicao > messagesCount) {
                                message = "Error: Invalid index! Available messages between 0 and " + (messagesCount);

                                // envia uma mensagem aleatoria caso o indice for 0
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

                        // para o servidor se o argumento for y
                        if (request[1].equals("y")) {
                            isRunning = false;
                        }

                    }
                    // corta a mensagem se esta ultrapassar o tamanho maximo
                    if (message.getBytes().length > msgSize) {
                        message = message.substring(0, 100);
                    }

                    // envia a mensagem para o cliente
                    textoAEnviar = message.getBytes();
                    bufferSaida.write(textoAEnviar);
                    bufferSaida.flush();

                }

                // Fecha os streams e a conexão
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
