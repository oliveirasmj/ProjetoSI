package Helpers;

import static Helpers.globalMethods.writeToFile;
import static Helpers.globalMethods.readFromFile;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;





public class SymmetricKey {
   
    public static byte[] generateKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            SecretKey sk = kg.generateKey();
            return sk.getEncoded();
            
        } catch (Exception ex){
            System.out.println("Error ao gerar a chave simetrica");
            return null;
        }
    }
    

    public static  byte[] cipher(byte[] secretKey, byte[] content,String pathToIV) throws IOException{
    
    	
        byte[] iV = null;
          
        try {
            SecretKey sk = new SecretKeySpec(secretKey,"AES");
            
                 
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
         
            cipher.init(Cipher.ENCRYPT_MODE, sk);
         
            AlgorithmParameters params = cipher.getParameters();
            iV = params.getParameterSpec(IvParameterSpec.class).getIV();
            writeToFile(pathToIV,iV);
            
            return cipher.doFinal(content);
            
        } catch (Exception ex) {
            Logger.getLogger(SymmetricKey.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }
    
  

    public static byte[] decipher(byte[] secretKey, byte[] content,String pathToIV){

        byte[] iV = null;
    	
        try {
            SecretKey sk = new SecretKeySpec(secretKey,"AES");
            
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
            iV = readFromFile(pathToIV+"/iv");
            
            c.init(Cipher.DECRYPT_MODE, sk,new IvParameterSpec(iV));
            return c.doFinal(content);
            
        } catch (Exception ex) {
            Logger.getLogger(SymmetricKey.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } 
    }
    
   
}