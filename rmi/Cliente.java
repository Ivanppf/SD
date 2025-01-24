import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Cliente {

	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static int msgSize;

	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {

		String host = "127.0.0.1";

		if (args.length == 1) {
			host = args[0];
		}

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}

		String nomeRemoto = "//" + host + "/Servidor";

		ServidorRemoto servidor = (ServidorRemoto) Naming.lookup(nomeRemoto);

		// escreve mensagem no servidor, chamando m�todo dele
		// servidor.escreveMsg("Hello, fellows!!!!");
		BufferedReader KBinput = new BufferedReader(new InputStreamReader(System.in));
		Usuario usuario = new Usuario();


		try {
			
			System.out.print("Digite seu nome: ");
			usuario.setNome(KBinput.readLine());
			usuario.setEnderecoIp("localhost");
			servidor.cadastrar(usuario);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		Thread serverThread = new Thread(() -> {
			try {
				startServer(usuario.getPorta());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		serverThread.start();

		while (true) {

			try {

				System.out.println("""
						1 - Ver usuários online
						2 - Conectar a um usuário
						3 - Sair
						->
						""");

				int escolha = Integer.parseInt(KBinput.readLine());

				if (escolha == 1) {
					List<Usuario> usuarios = servidor.listarUsuarios();
					usuarios.forEach(System.out::println);
				} else if (escolha == 2) {
					OutputStream outputBuffer = clientSocket.getOutputStream();

					while (true) {
						System.out.print("Digite o nome do usuário: ");
						String nomeUsuario = KBinput.readLine();
						Usuario novoUsuario = servidor.buscarUsuario(nomeUsuario);
						System.out.print("Digite a mensagem para enviar (ou 'sair' para encerrar):");
						String message = KBinput.readLine();
						if (message.equalsIgnoreCase("sair")) {
							break;
						}
						outputBuffer.write(message.getBytes());
						outputBuffer.flush();
					}
					

				} else if (escolha == 3) {
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// recebe a data de hoje do servidor, executando m�todo l� nele
		// Date dataDeHoje = servidor.dataDeHoje();
		// System.out.println("A data/hora do servidor �: " + dataDeHoje.toString());
	}

	private static void startServer(int porta) throws IOException {
		ServerSocket serverSocket = new ServerSocket(porta);
		System.out.println("Servidor iniciado. Aguardando mensagens...");
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				new Thread(new ClientHandler(clientSocket)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



	// Handler para tratar a recepção de mensagens
	private static class ClientHandler implements Runnable {
		private Socket socket;

		public ClientHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				String message;
				while ((message = in.readLine()) != null) {
					System.out.println("Mensagem recebida: " + message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
