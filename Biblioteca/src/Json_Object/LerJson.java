package Json_Object;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

//classe com exemplo de uso do JSON
//converte objeto Filme para uma string JSON
public class LerJson {

	public static void main(String[] args) {

		//string com informação representada em JSON
		//String aux = "{\"titulo\":\"JSON James\",\"ano\":2012,\"generos\":[\"Western\"]}";

		//instancia um objeto da classe Gson
		Gson gson = new Gson();

		//instancia um Filme e preenche seus dados com a informação vinda da string JSON
		//Filme f = gson.fromJson(aux, Filme.class);

		//imprime os resultados
		//System.out.println(f.titulo);
		//System.out.println(f.ano);
		//System.out.println(f.generos.toString());
		
		//ler ficheiro
		try {

			BufferedReader br = new BufferedReader(new FileReader("./filme.json"));
			//BufferedReader br = new BufferedReader(new FileReader("./dados2.json"));

			//Converte String JSON para objeto Java
			Filme obj = gson.fromJson(br, Filme.class);
			System.out.println("O ficheiro contem: ");
			System.out.println(obj.titulo);
			System.out.println(obj.ano);
			System.out.println(obj.generos.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
