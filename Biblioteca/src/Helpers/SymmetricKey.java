/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class SymmetricKey {
    /**
     * function used to generate a symmetric key , to be used to cipher a new license
     * @return byte array of the key
     */
    public static byte[] generateKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("DES");
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
     */
    public static byte[] cipher(byte[] secretKey, byte[] content){
        try {
            SecretKey sk = new SecretKeySpec(secretKey,"DES");
            
            Cipher c = Cipher.getInstance("DES/ECB/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, sk);
            return c.doFinal(content);
            
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
    public static byte[] decipher(byte[] secretKey, byte[] content){
        try {
            SecretKey sk = new SecretKeySpec(secretKey,"DES");
            
            Cipher c = Cipher.getInstance("DES/ECB/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, sk);
            return c.doFinal(content);
            
        } catch (Exception ex) {
            Logger.getLogger(SymmetricKey.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}