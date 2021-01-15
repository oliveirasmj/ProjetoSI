package Helpers;

import static Helpers.globalMethods.readFromFile;
import static Helpers.globalMethods.writeToFile;

import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class AsymmetricKey {
  
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
    
    public static PublicKey getAppPubKey(byte[] appPubKey) throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		X509EncodedKeySpec ks = new X509EncodedKeySpec(appPubKey);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(ks);
	}
    
    
    
	 
	   public static void encryptyAppKeyPair(byte [] chavePrivAEncryptar,String nomeFicheiroEncryptado,String password,String pathToIV) throws Exception{
	
		
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");
		SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);


		Random random = new Random();
		
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
		SecretKey tmp = factory.generateSecret(spec); //hash de pass + salt 
		SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), "AES"); // criacao de pass atraves do  ->  hash de pass + salt 

		byte[] iv = new byte[128/8];
		random.nextBytes(iv);
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		
	
		writeToFile(pathToIV+"salt", salt);
		writeToFile(pathToIV+"iv", iv);
		

		
		Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);
		
	
		writeToFile(nomeFicheiroEncryptado,	ci.doFinal(chavePrivAEncryptar) );
		

		
	}
	   
	   
		
		public static byte[] decryptAppPairKey(String caminhoParaChaveEncryptada,String nomeFicheiroDesencryptado,String password, String caminhoIv) throws Exception {

			byte[] salt = new byte[8], iv = new byte[128/8];
			
			salt = readFromFile(caminhoIv+"salt");
			iv = readFromFile(caminhoIv+"iv");
			
			
			
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
				KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
				SecretKey tmp = factory.generateSecret(spec);
				SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), "AES");

			
				Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
				ci.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));
				
				
				byte[] privKeyDesencriptada = ci.doFinal(readFromFile(caminhoParaChaveEncryptada));
		
				
				return privKeyDesencriptada;

			
		}

    
  
}