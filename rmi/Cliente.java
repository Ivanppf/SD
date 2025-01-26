import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Cliente {

	private static ServidorRemoto servidor;
	private static final int msgSize = 100;
	private static Usuario usuario;

	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {

		String host = "127.0.0.1";

		if (args.length == 1) {
			host = args[0];
		}

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}

		String nomeRemoto = "//" + host + "/Servidor";

		servidor = (ServidorRemoto) Naming.lookup(nomeRemoto);

		BufferedReader KBinput = new BufferedReader(new InputStreamReader(System.in));
		usuario = new Usuario();

		boolean continua = true;
		while (continua) {
			try {
				System.out.print("Digite seu nome: ");
				String nome = KBinput.readLine();
				usuario.setNome(nome);
				servidor.cadastrar(usuario);
				usuario = servidor.buscarUsuario(nome);
				continua = false;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		Thread serverThread = new Thread(() -> {
			try {
				startServer(usuario.getPorta());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		});
		serverThread.start();

		while (true) {

			try {

				System.out.println("""
						1 - Ver usuários online
						2 - Conectar a um usuário
						3 - Enviar mensagem para grupo
						4 - Sair
						->
						""");

				int escolha = Integer.parseInt(KBinput.readLine());

				if (escolha == 1) {
					List<Usuario> usuarios = servidor.listarUsuarios();
					usuarios.forEach(System.out::println);
				} else if (escolha == 2) {
					System.out.print("Digite o nome do usuário: ");
					String nomeUsuario = KBinput.readLine();
					Usuario novoUsuario = servidor.buscarUsuario(nomeUsuario);
					while (true) {
						System.out.println("Digite a mensagem para enviar (ou 'sair' para encerrar): ");
						String mensagem = KBinput.readLine();
						if (mensagem.equalsIgnoreCase("sair")) {
							break;
						}
						enviarMensagem(novoUsuario, mensagem);
					}

				} else if (escolha == 3) {
					while (true) {
						System.out.println("Digite a mensagem para enviar (ou 'sair' para encerrar): ");
						String mensagem = KBinput.readLine();
						if (mensagem.equalsIgnoreCase("sair")) {
							break;
						}
						enviarMensagemParaTodos(mensagem);
					}
				} else if (escolha == 4) {
					break;
				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		try {
			KBinput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void startServer(int porta) throws IOException {
		ServerSocket serverSocket = new ServerSocket(porta);
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				InputStream inputBuffer = clientSocket.getInputStream();

				new Thread(() -> {
					// Handler para tratar a recepção de mensagens
					try {
						while (true) {
							// Recebe a resposta do servidor
							byte[] mensagemDoServidor = new byte[msgSize];
							int bytesRead = inputBuffer.read(mensagemDoServidor);

							// Converte a resposta para string e exibe
							String textReceived = new String(mensagemDoServidor, 0, bytesRead).trim();
							System.out.println(">> " + textReceived);
						}

					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void enviarMensagemParaTodos(String mensagem) throws RemoteException {
		List<Usuario> usuarios = servidor.listarUsuarios();
		for (Usuario usuario : usuarios) {
			enviarMensagem(usuario, mensagem);
		}

	}

	public static void enviarMensagem(Usuario novoUsuario, String mensagem) {

		try {
			Socket socket = new Socket(novoUsuario.getEnderecoIp(), novoUsuario.getPorta());
			OutputStream outputBuffer = socket.getOutputStream();
			
			mensagem = usuario.getNome() + ": " + mensagem;
			outputBuffer.write(mensagem.getBytes());
			outputBuffer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
