import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ClientTCP {

    public static void main(String[] args) {
        try {
            // Conecta ao servidor na porta 4444
            Socket socket;
            if (args.length == 1) {
                socket = new Socket(args[0], 4444);
            } else {
                socket = new Socket("localhost", 4444);
            }
            System.out.println("Connected to the server!");

            // Cria buffers de entrada e saída
            OutputStream bufferSaida = socket.getOutputStream();
            InputStream bufferEntrada = socket.getInputStream();

            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            // Loop para enviar mensagens para o servidor
            while (true) {
                System.out
                        .print("Type a number (0 for a random message) and y/n if you want to finish the connection: ");
                String mensagem = teclado.readLine();

                // Envia a mensagem para o servidor
                bufferSaida.write(mensagem.getBytes());
                bufferSaida.flush();

                // Recebe a resposta do servidor
                byte[] respostaRecebida = new byte[100];
                int bytesLidos = bufferEntrada.read(respostaRecebida);
                if (bytesLidos == -1) {
                    break;
                }

                // Converte a resposta para string e exibe
                String resposta = new String(respostaRecebida, 0, bytesLidos).trim();
                System.out.println("Server replied: " + resposta);

                // Se a mensagem for "y", encerra a conexão
                if (mensagem.split(" ").length == 2 && mensagem.split(" ")[1].equals("y")) {
                    break;
                }
            }

            // Fecha os streams e a conexão
            bufferSaida.close();
            bufferEntrada.close();
            socket.close();
            System.out.println("Connection closed.");

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

}