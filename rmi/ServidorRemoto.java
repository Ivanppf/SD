import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface ServidorRemoto extends Remote {

	public void escreveMsg(String msg) throws RemoteException;

	public Date dataDeHoje() throws RemoteException;

	public String cadastrar(Usuario c) throws RemoteException;;

	public Usuario buscarUsuario(String email) throws RemoteException;;

	public List<Usuario> listarUsuarios() throws RemoteException;;
}
