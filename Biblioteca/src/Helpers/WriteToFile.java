package Helpers;

import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;

public class WriteToFile {

	
	/*
	 * Escreve ficheiros
	 * Basta passar nome e caminho a guardar
	 */
	public boolean writeFile(JSONObject json, String caminho) {
		try {
			//FileWriter writer = new FileWriter("C:\\Users\\miguel.oliveira\\Downloads\\file.json");
			FileWriter writer = new FileWriter(caminho);
			writer.write(json.toJSONString());
			writer.close();
			System.out.println("Ficheiro criado com: "+ json + " criado com sucesso em: " + caminho );
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
}
