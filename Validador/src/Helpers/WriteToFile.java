package Helpers;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;



public class WriteToFile {

	
	/*
	 * Escreve ficheiros
	 * Basta passar nome e caminho a guardar
	 */
	public boolean writeFile(JSONObject json, String caminho) {
		try {
			//FileWriter writer = new FileWriter("C:\\Users\\miguel.oliveira\\Downloads\\file.json");
			FileWriter writer = new FileWriter(caminho);
			writer.write(json.toString());
			writer.close();
			System.out.println("Ficheiro criado com: "+ json + " criado com sucesso em: " + caminho );
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	 
    /**
     * function used to write to or a new file
     * @param nomeFicheiro name of the file to write
     * @param contiudo content of the file to be written
     */
    public static void writeToFileByte(String nomeFicheiro, byte[] conteudo) throws IOException{
        try {
            FileOutputStream fos = new FileOutputStream(nomeFicheiro);
            fos.write(conteudo);
            fos.close();
        } catch (Exception e) {
            System.out.println("erro a escrever ficheiro: " + e);
            throw new IOException();
        }
        
    }
}
