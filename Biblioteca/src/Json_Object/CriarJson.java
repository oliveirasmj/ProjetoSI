package Json_Object;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;


//classe com exemplo de uso do JSON
//converte objeto Filme para uma string JSON
public class CriarJson {

	public static void main(String[] args) {

		// instancia um filme e preenche suas propriedades
		Filme f = new Filme();
		f.titulo = "JSON x XML";
		f.ano = 2012;
		f.generos = new ArrayList<String>();
		f.generos.add("Aventura");
		f.generos.add("Ação");
		f.generos.add("Ficção");

		// instancia um objeto da classe Gson
		Gson gson = new Gson();

		// pega os dados do filme, converte para JSON e armazena em string
		String aux = gson.toJson(f);

		// imprime os resultados
		System.out.println(aux);
		
		
		//Criar ficheiro
		//converte objetos Java para JSON e retorna JSON como String
		String json = gson.toJson(f);
		try {
			//Escreve Json convertido em arquivo chamado "file.json"
			//FileWriter writer = new FileWriter("C:\\Users\\miguel.oliveira\\Downloads\\file.json");
			FileWriter writer = new FileWriter("./filme.json");
			writer.write(json);
			writer.close();
			System.out.println("Ficheiro criado");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}