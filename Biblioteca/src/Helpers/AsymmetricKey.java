/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import static Helpers.globalMethods.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
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
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;


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
    
    
	public static PublicKey getAuthorPublicKey() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		// chave publica de autor guardada na raiz da pasta biblioteca
		byte[] authorPubKeyBytes = readFromFile("validadorPubKey");
		X509EncodedKeySpec ks = new X509EncodedKeySpec(authorPubKeyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(ks);
	}
	
	public static PublicKey getAppPubKey(byte[] appPubKey) throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		X509EncodedKeySpec ks = new X509EncodedKeySpec(appPubKey);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(ks);
	}
	
	 public static void geraParDeChaves(String caminhoPrivKey, String caminhoPubKey,String pass,String caminhoIv) throws Exception{
	    	int keyLength = 2048;
	    	

	        KeyPairGenerator KPG = KeyPairGenerator.getInstance("RSA");
	        

	        KPG.initialize(keyLength);

	        KeyPair keyPair = KPG.generateKeyPair();
	     
	        
	    	PrivateKey privateKey = keyPair.getPrivate();
	        byte[] privateKeyBytes = privateKey.getEncoded();
	        
	       /* FileOutputStream os = new FileOutputStream(caminhoPrivKey);
	        os.write(privateKeyBytes);
	        os.close();*/
	        encryptyAppKeyPair(privateKeyBytes, caminhoPrivKey+"encrypted", pass,caminhoIv);
	      //  encryptyAppKeyPair(caminhoPrivKey, caminhoPrivKey+"encrypted", pass,caminhoIv);
	   

	        PublicKey publicKey = keyPair.getPublic();
	        byte[] publicKeyBytes = publicKey.getEncoded();
	        
	        FileOutputStream oss = new FileOutputStream(caminhoPubKey);
	        oss.write(publicKeyBytes);
	        oss.close();
	        
	        
	        File eliminarPrivKey = new File(caminhoPrivKey);
	        eliminarPrivKey.delete();
	        
	    }
	 
	 
	 //  public static void encryptyAppKeyPair(String caminhoParaChaveAEncryptar,String nomeFicheiroEncryptado,String password,String pathToIV) throws Exception{
		   public static void encryptyAppKeyPair(byte[] caminhoParaChaveAEncryptar,String nomeFicheiroEncryptado,String password,String pathToIV) throws Exception{
		
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");
		SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);


		Random random = new Random();
		
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), "AES");

		byte[] iv = new byte[128/8];
		random.nextBytes(iv);
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		
	
		writeToFile(pathToIV+"salt", salt);
		writeToFile(pathToIV+"iv", iv);
		

		
		Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);
		
	
		writeToFile(nomeFicheiroEncryptado,	ci.doFinal(caminhoParaChaveAEncryptar) );
		
	}
	   
	   
		
		public static byte[] decryptAppPairKey(String caminhoParaChaveEncryptada,String nomeFicheiroDesencryptado,String password, String caminhoDir)  {
			byte[] privKeyDesencriptada = null;
			try {
				SecretKeyFactory factory;
				byte[] salt = new byte[8], iv = new byte[128/8];
				
				salt = readFromFile(caminhoDir+"salt");
				iv = readFromFile(caminhoDir+"iv");
				factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			
				KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
				SecretKey tmp = factory.generateSecret(spec);
				SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), "AES");

			
				Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
				ci.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));
				
				
				privKeyDesencriptada = ci.doFinal(readFromFile(caminhoParaChaveEncryptada));
				
				//writeToFile(nomeFicheiroDesencryptado, privKeyDesencriptada);
				
			}catch (Exception e) {
				//e.printStackTrace();
				System.out.println("Palavra Pass errada. Volte a tentar.");
			}
			
			return privKeyDesencriptada;
		}
 
    
}