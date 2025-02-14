import java.util.List;

public interface ChatServidor {

    public String cadastrar(Usuario c) throws RuntimeException;

    public Usuario buscarUsuario(String email);

    public List<Usuario> listarUsuarios();

}
