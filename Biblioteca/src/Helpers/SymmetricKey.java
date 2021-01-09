/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;





public class SymmetricKey {
    /**
     * function used to generate a symmetric key , to be used to cipher a new license
     * @return byte array of the key
     */
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
    
    
    /**
     * function used to cipher with a symmetric key
     * @param secretKey symmetric key to use to cipher
     * @param content content to be ciphered
     * @return byte array with the content ciphered
     * @throws IOException 
     */
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
    
  

	/**
     * function used to decipher with a symmetric key
     * @param secretKey symmetric key used to decipher the content
     * @param content content to be deciphered
     * @return byte array of the deciphered content
     */
    public static byte[] decipher(byte[] secretKey, byte[] content,String pathToIV){
    	
    	
    	 byte[] iV = null;
    	
        try {
            SecretKey sk = new SecretKeySpec(secretKey,"AES");
            
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
            iV = readFromFile(pathToIV);
            
            c.init(Cipher.DECRYPT_MODE, sk,new IvParameterSpec(iV));
            return c.doFinal(content);
            
        } catch (Exception ex) {
            Logger.getLogger(SymmetricKey.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } 
    }
    
    
    
 
}