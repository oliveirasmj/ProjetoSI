package Json_Object;

import java.util.List;

public class Filme {
	public String titulo;
	public int ano;
	public List<String> generos;
	
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	public List<String> getGeneros() {
		return generos;
	}
	public void setGeneros(List<String> generos) {
		this.generos = generos;
	}
	
	@Override
	public String toString() {
		return "Filme [titulo=" + titulo + ", ano=" + ano + ", generos=" + generos + "]";
	}
	
}
