import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ClientTCP {

    public static void main(String[] args) {
        try {
            /*
             * Conecta ao servidor na porta 4444
             * o ip do servidor pode ser passado externamente, caso contrario,
             * ira procurar o servidor no localhost
             */
            Socket socket;
            if (args.length == 1) {
                socket = new Socket(args[0], 4444);
            } else {
                socket = new Socket("localhost", 4444);
            }
            System.out.println("Connected to the server!");

            // Cria buffers de entrada e saída
            OutputStream outputBuffer = socket.getOutputStream();
            InputStream inputBuffer = socket.getInputStream();

            BufferedReader KBinput = new BufferedReader(new InputStreamReader(System.in));

            // Loop para enviar mensagens para o servidor
            while (true) {
                System.out
                        .print("Type a number (0 for a random message) and y/n if you want to finish the connection: ");
                String textToSend = KBinput.readLine();

                // Envia a mensagem para o servidor
                outputBuffer.write(textToSend.getBytes());
                outputBuffer.flush();

                // Recebe a resposta do servidor
                byte[] serverMessage = new byte[100];
                int bytesRead = inputBuffer.read(serverMessage);
                if (bytesRead == -1) {
                    break;
                }

                // Converte a resposta para string e exibe
                String textReceived = new String(serverMessage, 0, bytesRead).trim();
                System.out.println("Server replied: " + textReceived);

                // Se a mensagem for "y", encerra a conexão
                if (textToSend.split(" ").length == 2 && textToSend.split(" ")[1].equals("y")) {
                    break;
                }
            }

            // Fecha os streams e a conexão
            outputBuffer.close();
            inputBuffer.close();
            socket.close();
            System.out.println("Connection closed.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}