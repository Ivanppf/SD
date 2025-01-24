import java.util.List;

public interface ChatServidor {

    public String cadastrar(Usuario c);

    public Usuario buscarUsuario(String email);

    public List<Usuario> listarUsuarios();

}
