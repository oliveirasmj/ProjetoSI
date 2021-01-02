package Models;


import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;





public class Validador {
	 private int id;
	    private String username, password, name;
	    public PublicKey publicKey;
	    public PrivateKey privateKey;

	    
	    public Validador(String username, String password, String name) {
	        this.username = username;
	        this.password = password;
	        this.name = name;
	    }
	    
	    public void setPrivateKey() throws FileNotFoundException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException{
	    	int keyLength = 2048;
	    	
	    	//SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	    	
	        KeyPairGenerator KPG = KeyPairGenerator.getInstance("RSA");
	        
	       // KPG.initialize(keyLength, random);

	        KPG.initialize(keyLength);

	        KeyPair keyPair = KPG.generateKeyPair();
	    	
	    	String privateKeyFilePath = "authorPrivKey";
	        
	    	PrivateKey privateKey = keyPair.getPrivate();
	        byte[] privateKeyBytes = privateKey.getEncoded();
	        
	        FileOutputStream os = new FileOutputStream(privateKeyFilePath);
	        os.write(privateKeyBytes);
	        os.close();
	    }
	    
	 
	    
	    public void setPublicKey() throws FileNotFoundException, IOException, NoSuchAlgorithmException{
	    	int keyLength = 2048;
	    	
	    	//SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	    	
	        KeyPairGenerator KPG = KeyPairGenerator.getInstance("RSA");
	        
	        
	       // KPG.initialize(keyLength, random);

	        KPG.initialize(keyLength);

	        KeyPair keyPair = KPG.generateKeyPair();
	    	
	    	String publicKeyFilePath = "authorPubKey";

	        PublicKey publicKey = keyPair.getPublic();
	        byte[] publicKeyBytes = publicKey.getEncoded();

	        FileOutputStream os = new FileOutputStream(publicKeyFilePath);
	        os.write(publicKeyBytes);
	        os.close();
	    }
	    
	    public Signature getSignature(String key) throws NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException{
	        Signature sign = Signature.getInstance("SHA256withRSA");
	        sign.initSign(privateKey);

	        InputStream in = null;
	        try {
	            in = new ByteArrayInputStream(key.getBytes());
	            byte[] buf = new byte[2048];
	            int len;
	            while ((len = in.read(buf)) != -1) {
	            sign.update(buf, 0, len);
	            }
	        } finally {
	            if ( in != null ) in.close();
	        }

	        return sign;
	    }


	    public int getId() {
	        return id;
	    }

	    public String getUsername() {
	        return this.username;
	    }

	    public String getPassword() {
	        return this.password;
	    }

	    public String getName() {
	        return this.name;
	    }

	    public void setUsername(String username) {
	        this.username = username;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    @Override
	    public String toString() {
	    	
	        return "Admin{" + "\nid=" + this.id + ", \nusername=" + this.username + ", \npassword=" + this.password + ", \nname=" + this.name + '}';
	    }


}
