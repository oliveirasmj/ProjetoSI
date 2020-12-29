package com.main;

import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.json.JSONObject;

import AppInfo.ApplicationInfo;
import Helpers.WriteToFile;
import SystemInfo.SystemInfo;
import UserInf.UserInfo;
import static globalMethods.AsymmetricKey.asymmetricCipher;
import static globalMethods.SymmetricKey.cipher;
import static globalMethods.SymmetricKey.generateKey;
import static globalMethods.globalMethods.bytesEncodeBase64;
import static globalMethods.globalMethods.stringDecodeBase64;
import static globalMethods.globalMethods.readFromFile;
import static globalMethods.globalMethods.stringDecodeBase64;
import static globalMethods.globalMethods.writeToFile;

public class Licenca {
	
	private UserInfo user;
    private SystemInfo system;
    private ApplicationInfo app;

    
    //private date //later
    private SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date startDate;
    private Date endDate;
    
    public Licenca() {}
    

	
	 public boolean novaLicenca(String dirParaJar, double appVersion) throws IOException{
			system = new SystemInfo();
			user = new UserInfo();
			
			//Dir para jar a gerar hash . TODO tem de ser o jar do programa e nao da libraria
			//esta variavel tem de vir do programa com a sua localização
			// String dirParaJar = System.getProperty("user.dir") + "/BibliotecaJar.jar";
			app = new ApplicationInfo(dirParaJar, appVersion, "SHA-256");
			boolean crioulouLicenca = geraFicheirosNovaLicenca();
			return crioulouLicenca;
	    }
	 
	 
	 public boolean geraFicheirosNovaLicenca(){
	       try{
	    	   
	    	   //instancia classes
	    	   WriteToFile fw = new WriteToFile();
	           
	    	   //Junta todos os jsons
	    	    JSONObject licenseInfo = new JSONObject();
	            licenseInfo.put("user", user.userJson());
	            licenseInfo.put("system", system.systemJSON());
	            licenseInfo.put("app", app.toJSON());
	            
	            String path = "licencas/" +user.getName() + "/";
	            String pathTemp = "licencas/" +user.getName() + "/temp/";
	            
	            
	            //Cria pasta com nome do utilizador do cartao de cidadao caso não exista 
	            File dir = new File(path);
	            if(!dir.exists())
	                dir.mkdir();
	            
	            File dirTemp = new File(pathTemp);
	            if(!dirTemp.exists())
	                dirTemp.mkdir();
	            
	            
	            byte[] licenseBytes = licenseInfo.toString().getBytes();
	          
	            
	            byte[] licenseSignatureBytes = user.uc.getSignatureOfData(licenseBytes);
	            fw.writeToFileByte(pathTemp + user.getName() + " - signature", licenseSignatureBytes);

	            JSONObject signature = new JSONObject();
	            signature.put("signature", bytesEncodeBase64(licenseSignatureBytes));

	            JSONObject license = new JSONObject();
	            license.put("licenseInfo", licenseInfo);
	            license.put("signature", signature);
	            writeToFile(pathTemp + user.getName() + " - licenca", license.toString().getBytes());

	            
	            //cifrar com chave simetrica
	            byte[] key = generateKey();
	            writeToFile(path + "SymmetricKey", key);
	            writeToFile(path + "cipheredLicenseRequest", cipher(key, license.toString().getBytes()));
	            
	            // ----- segundo pacote ----- 
	          //devia ser assinado para garantir que não foi alterado (inetgridade)
	            JSONObject theKey = new JSONObject();
	            theKey.put("key", bytesEncodeBase64(key));
	            
	            
	            //cifrar com chave assimetrica
	           
	            byte[] authorPublicKey = getAuthorPublicKey().getEncoded();
	            writeToFile(path + "cipheredKeyRequest", asymmetricCipher(authorPublicKey, theKey.toString().getBytes()));
	            
	            System.out.println("\n\nFicheiro criados");
	            return true;
	       } catch (Exception e){
	           System.out.println("error in generate new files : " + e.toString());
	           return false;
	       }
	   }
	 
	 private PublicKey getAuthorPublicKey() throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, IOException{
	        byte[] authorPubKeyBytes = readFromFile("authorPubKey");
	        X509EncodedKeySpec ks = new X509EncodedKeySpec(authorPubKeyBytes);
	        KeyFactory kf = KeyFactory.getInstance("RSA");
	        return kf.generatePublic(ks);
	    }
	 
	 /*
	 public void readLicenseFromJsonString(String licenseString) throws CertificateException, ParseException{
	        JSONObject jsonLicense = new JSONObject(licenseString);
	        
	        this.licenseInfo = jsonLicense.getJSONObject("licenseInfo");
	        
	        JSONObject user =  licenseInfo.getJSONObject("user");
	        this.user = new UserInfo(user.getString("name"),
	                user.getString("email"), 
	                user.getString("nic"));
	        this.user.uc.setLicenseCertificate(user.getString("certificate"));
	        
	        JSONObject system = licenseInfo.getJSONObject("system");
	        this.system = new SystemInfo(system.getString("macAdress"),
	                system.getString("motherboardserial"),
	                system.getString("username"),
	                system.getString("hostname"),
	                system.getString("cpuserial"));
	        
	        JSONObject app =  licenseInfo.getJSONObject("app");
	        
	        this.app = new ApplicationInfo(app.getString("appName"),
	                app.getString("hash"),
	                app.getDouble("version"));

	        this.authorSignatureBytes= stringDecodeBase64(jsonLicense.getJSONObject("signature").getString("signature"));
	        
	        
	        this.startDate = formatter.parse(licenseInfo.getJSONObject("dates").getString("startDate"));
	        this.endDate = formatter.parse(licenseInfo.getJSONObject("dates").getString("endDate"));

	        
	    }
	*/
	
}
