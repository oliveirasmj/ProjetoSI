package Helpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;


public class globalMethods {

    public static byte[] readFromFile(String fileName) throws IOException{
        try{
            FileInputStream fis = new FileInputStream(fileName);
            byte[] fileBytes = new byte[fis.available()];
            fis.read(fileBytes);
            fis.close();
            return fileBytes;
        } catch (Exception e) {
            System.out.println("erro a ler ficheiro: " + e);
            throw new IOException();

        }
        
    }
    
  
    public static void writeToFile(String nomeFicheiro, byte[] contiudo) throws IOException{
        try {
            FileOutputStream fos = new FileOutputStream(nomeFicheiro);
            fos.write(contiudo);
            fos.close();
        } catch (Exception e) {
            System.out.println("erro a escrever ficheiro: " + e);
            throw new IOException();
        }
        
    }
    
 
    public static String bytesToStringPrint(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Erro em bytesToString");
            return "";
        }
    }
    
 
    public static String bytesEncodeBase64(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }
  
    public static byte[] stringDecodeBase64(String base64String){
        return Base64.getDecoder().decode(base64String);
    }
}
