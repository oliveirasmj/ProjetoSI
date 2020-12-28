/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author diogo
 */
public class testPrivKeyAutor {
     
    public PublicKey publicKey;
    public PrivateKey privateKey;

    public testPrivKeyAutor() throws NoSuchAlgorithmException, IOException, FileNotFoundException, SignatureException, InvalidKeyException {
        
        int comrpimetoChave = 2048;

        KeyPairGenerator KPG = KeyPairGenerator.getInstance("RSA");

        KPG.initialize(comrpimetoChave);

        KeyPair keyPair = KPG.generateKeyPair();

        getPriv(keyPair);
        getPub(keyPair);
    }
    
    
    
    public void getPriv(KeyPair keyPair) throws FileNotFoundException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException{
        String ficheiroChavePrivado = "authorPrivKey";

        
        privateKey = keyPair.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        System.out.println("private key: " + privateKeyBytes);
        
        FileOutputStream fos1 = new FileOutputStream(ficheiroChavePrivado);
        fos1.write(privateKeyBytes);
        fos1.close();
        
        getSignature();

    }
    
    public void getPub(KeyPair keyPair) throws FileNotFoundException, IOException{
        String ficheiroChavePublica = "authorPubKey";


        publicKey = keyPair.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        System.out.println("public key: " + publicKeyBytes);

        FileOutputStream fos2 = new FileOutputStream(ficheiroChavePublica);
        fos2.write(publicKeyBytes);
        fos2.close();

    }
    
    public void getSignature() throws NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException{
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);

        
        InputStream in = null;
        try {
            in = new ByteArrayInputStream( "test".getBytes());
            byte[] buf = new byte[2048];
            int len;
            while ((len = in.read(buf)) != -1) {
            sign.update(buf, 0, len);
            }
        } finally {
            if ( in != null ) in.close();
        }

        OutputStream out = null;
        try {
            out = new FileOutputStream("signature");
            byte[] signature = sign.sign();
            out.write(signature);
        } finally {
            if ( out != null ) out.close();
        }
    }
    
    public void verifySignature() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException{
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(publicKey);

        InputStream in = null;
        try {
            in = new ByteArrayInputStream("test".getBytes());
            byte[] buf = new byte[2048];
            int len;
            while ((len = in.read(buf)) != -1) {
            sign.update(buf, 0, len);
            }
        } finally {
            if ( in != null ) in.close();
        }

        /* Read the signature bytes from file */

        Path path = Paths.get("signature");
	byte[] bytes = Files.readAllBytes(path);
        
        System.out.println("Signature " +
           (sign.verify(bytes) ? "OK" : "Not OK"));
    }
    
    public static void main(String[] args) throws KeyStoreException {
        try {
            
            
            testPrivKeyAutor test = new testPrivKeyAutor();
            
            
            test.verifySignature();
            
            
        } catch (IOException ex) {
            Logger.getLogger(testPrivKeyAutor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(testPrivKeyAutor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(testPrivKeyAutor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(testPrivKeyAutor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
