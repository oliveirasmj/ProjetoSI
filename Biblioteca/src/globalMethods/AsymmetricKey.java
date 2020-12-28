/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package globalMethods;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

/**
 *
 * @author diogo
 */
public class AsymmetricKey {
    /**
     * function used cipher content using a asymmetric key
     * @param publicKeyBytes byte array that represents the public key to be used
     * @param content content to be ciphered
     * @return byte array of the ciphered content
     */
    public static byte[] asymmetricCipher(byte[] publicKeyBytes, byte[] content){
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = factory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, publicKey);
            return c.doFinal(content);
            
        } catch (Exception e) {
            Logger.getLogger(AsymmetricKey.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        
    }
    
    /**
     * function used to deciphered content using a asymmetric key
     * @param privateKeyBytes byte array that represents the private key to be used
     * @param content content to be deciphered
     * @return byte array with the deciphered content
     */
    public static byte[] asymmetricDecipher(byte[] privateKeyBytes, byte[] content){
        try {            
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = factory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, privateKey);
            return c.doFinal(content);
            
        } catch (Exception e) {
            Logger.getLogger(AsymmetricKey.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }
}