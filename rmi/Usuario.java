import java.io.Serializable;

public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nome;
	private String enderecoIp;
	private int porta;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEnderecoIp() {
		return enderecoIp;
	}

	@Override
	public String toString() {
		return "Usuario [nome=" + nome + ", enderecoIp=" + enderecoIp + ", porta=" + porta + "]";
	}

	public void setEnderecoIp(String enderecoIp) {
		this.enderecoIp = enderecoIp;
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}
}