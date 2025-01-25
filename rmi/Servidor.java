import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Servidor extends UnicastRemoteObject implements ServidorRemoto {

	private List<Usuario> usuarios;
	private int porta;

	public Servidor() throws RemoteException {
		usuarios = new ArrayList<>();
		porta = 8080;
	}

	public void escreveMsg(String msg) throws RemoteException {
		System.out.println(msg);
	}

	public Date dataDeHoje() throws RemoteException {
		return new Date();
	}

	public Usuario cadastrar(Usuario u) throws RuntimeException {

		if (buscarUsuario(u.getNome()) != null) {
			throw new RuntimeException("Este nome de usuario ja foi cadastrado");
		} else {
			try {
				u.setEnderecoIp(getClientHost());
			} catch (Exception e) {
				e.printStackTrace();
			}
			u.setPorta(porta);
			usuarios.add(u);
			return u;
		}
	}

	public Usuario buscarUsuario(String nome) {
		List<Usuario> u = usuarios.stream()
				.filter(usuario -> nome.equals(usuario.getNome()))
				.collect(Collectors.toList());
		if (u.size() > 0 && u.size() < 2)
			return u.get(0);
		else
			return null;

	}

	public List<Usuario> listarUsuarios() {
		return usuarios;

	}

	public void conectarComUsuario(Usuario u, String nome) {
		// u.setPorta(8080);

	}

	public static void main(String[] args) throws RemoteException, MalformedURLException {

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}

		try {
			// Fazer o registro para a porta desejado
			java.rmi.registry.LocateRegistry.createRegistry(1099);
			System.out.println("RMI registry ready.");
		} catch (Exception e) {
			System.out.println("Exception starting RMI registry:");
			e.printStackTrace();
		}

		Servidor servidor = new Servidor();

		Naming.rebind("Servidor", servidor);
	}

}
