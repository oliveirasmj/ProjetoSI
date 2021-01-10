package com.main;

import static Helpers.AsymmetricKey.getAuthorPublicKey;
import static Helpers.AsymmetricKey.asymmetricCipher;
import static Helpers.SymmetricKey.cipher;
import static Helpers.SymmetricKey.generateKey;
import static Helpers.globalMethods.bytesEncodeBase64;
import static Helpers.globalMethods.readFromFile;
import static Helpers.globalMethods.stringDecodeBase64;
import static Helpers.globalMethods.writeToFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import AppInfo.ApplicationInfo;
import SystemInfo.SystemInfo;
import UserInf.UserCrypto;
import UserInf.UserInfo;

public class Licenca {

	private UserInfo user;
	private SystemInfo system;
	private ApplicationInfo app;

	// private date //later
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Date startDate;
	private Date endDate;
	private Date currentDate;

	// private siganture
	private byte[] validadorSignatureBytes;
	private JSONObject licenseInfo; // use for cheking author siganture

	public Licenca() {
	}

	//public boolean geraNovaLicenca(String dirParaJar, double appVersion, byte[] appPubKey) throws IOException {
	public boolean geraNovaLicenca(String dirParaJar, double appVersion, BibControlo bib) throws IOException {
		try {
			Scanner sc = new Scanner(System.in);
			System.out.println("Introduza o email do utilizador:");
			String email = sc.nextLine();
			
			
			user = new UserInfo(email);
			
			//Cria pasta com CC user
			String root = "licencas/" + user.getNic() + "/";
			
			String caminhoPastaAValidar = root + "pedido="+ user.getNic() + "/";
			
			String pathToUserAppPairKeys = root + "chavesAplicacao/";

			// Cria pasta com nome do utilizador do cartao de cidadao caso não exista
			File dir = new File(root);
			if (!dir.exists())
				dir.mkdir();
			
			// cria dir temp com dados de licenca caso nao exista
			File dirAValidar = new File(caminhoPastaAValidar);
				if (!dirAValidar.exists()) 
					dirAValidar.mkdir();
				
				
				
				File dirParChavesUserApp = new File(pathToUserAppPairKeys);
				if (!dirParChavesUserApp.exists()) 
					dirParChavesUserApp.mkdir();
						
			
			
			system = new SystemInfo();
			
			
			bib.verificaSeExisteParDeChavesDeUser(pathToUserAppPairKeys+"appPrivKey", pathToUserAppPairKeys+"appPubKey", pathToUserAppPairKeys);
			
			String stringAppPubKey = bytesEncodeBase64(bib.getAppPubKey());
			
			
		//	String stringAppPubKey = bytesEncodeBase64(appPubKey);
			
			app = new ApplicationInfo(dirParaJar, appVersion, "SHA-256",stringAppPubKey );


			// Junta todos os jsons
			JSONObject licenseInfo = new JSONObject();
			licenseInfo.put("user", user.userJson());
			licenseInfo.put("system", system.systemJSON());
			licenseInfo.put("app", app.toJSON());

		
			
			
			
				

			// torna objectoJson da licenca em array de bytes - licenseInfo(user, system, app)
			byte[] licenseBytes = licenseInfo.toString().getBytes();

			// torna array de bytes da licenca em array de byte para Assinatura do cartao
			// a assintura é um hash da licenca encriptado com a chave privada para comprovar que a licenca nao foi alterada
			byte[] licenseSignatureBytes = user.uc.getSignatureOfData(licenseBytes);
			// cria ficheiro com nome do utilziador - signatura assinatura
		//	writeToFile(root + user.getName() + " - signature", licenseSignatureBytes);

			// cria um objeto json com a assinatura
			JSONObject signature = new JSONObject();
			signature.put("signature", bytesEncodeBase64(licenseSignatureBytes));

			// cria um json licenca, onde tem a assinatura e informaçao da licenca
			JSONObject license = new JSONObject();
			license.put("licenseInfo", licenseInfo);
			license.put("signature", signature);
			
			
		 //	writeToFile(root + user.getName() + " - licenca", license.toString().getBytes());
			

			// gera chave simetrica
			byte[] key = generateKey();
			// guarda no ficheiro symmetrickey a chave simetrica
		//	writeToFile(root + "SymmetricKey", key);
			
			// cifra com a chave simetrica CBC
			writeToFile(caminhoPastaAValidar + "cipheredLicenseRequest", cipher(key, license.toString().getBytes(),caminhoPastaAValidar + "iv"));


			// cifra a chave simetrica que guardou a licenca com chave a chave assimetrica
			// guarda o ficheiro cifrado com o nome ciphredKeyRequest
			byte[] authorPublicKey = getAuthorPublicKey().getEncoded();

		
			writeToFile(caminhoPastaAValidar + "cipheredKeyRequest", asymmetricCipher(authorPublicKey, key));
	         

			System.out.println("\n\nFicheiro criados");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error in generate new files : " + e.toString());
			return false;
		}
	}


	
	
	
	
	
	
	
	public void readLicenseFromJsonString(String licenseString)
			throws CertificateException, ParseException, JSONException, IOException, UnrecoverableKeyException, InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeySpecException {
		// le json da licenca
		JSONObject jsonLicense = new JSONObject(licenseString);

		this.licenseInfo = jsonLicense.getJSONObject("licenseInfo");
		// cria objeto user atraves da licenca
		JSONObject user = licenseInfo.getJSONObject("user");
		this.user = new UserInfo(user.getString("name"), user.getString("email"), user.getString("nic"));

		// vai validar certificado em licenca se é o mesmo do cc do user
		this.user.uc.setLicenseCertificate(user.getString("certificate"));
		
		boolean userCCVerification = this.user.uc.VerifyUserByCC(this.user.uc.getLicenseCertificate());
		
		
		//Le info do sistema e valida em construtor
		JSONObject system = licenseInfo.getJSONObject("system");

		this.system = new SystemInfo(system.getString("macAdress"), system.getString("motherBoardSerial"), system.getString("userName"), system.getString("hostName"), system.getString("cpuSerial"));

		JSONObject app = licenseInfo.getJSONObject("app");

		this.app = new ApplicationInfo(app.getString("appName"), app.getString("hash"), app.getDouble("version"), app.getString("appPubKey"));
		//obtem assinatura de autor atraves de json
		this.validadorSignatureBytes = stringDecodeBase64(jsonLicense.getJSONObject("signature").getString("signature"));

		// chech cliente siganture
		
	      //Verifying the signature
		Signature validadorSignature = Signature.getInstance("SHA256withRSA");
		byte[] validadorSignatureBytes = stringDecodeBase64(jsonLicense.getJSONObject("signature").getString("signature"));
	      //Calculating the signature
	     

	      //Initializing the signature
	      
	      validadorSignature.initVerify(getAuthorPublicKey());
	      validadorSignature.update(jsonLicense.getJSONObject("licenseInfo").toString().getBytes());
	      

		if (!validadorSignature.verify(validadorSignatureBytes)) {
			System.out.println("A Assinatura desta licença não é válida");
			System.exit(1);
		} else {
			System.out.println("A Assinatura é válida");
		}
		
		
		
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 currentDate = new Date(System.currentTimeMillis());

				
		  this.startDate = (Date)
		  formatter.parse(licenseInfo.getJSONObject("dates").getString("startDate"));
		  this.endDate = (Date)
		  formatter.parse(licenseInfo.getJSONObject("dates").getString("endDate"));
		  
		  if(currentDate.after(endDate)) {
			  System.out.println("A data de uso da sua licenca já expirou.\n Faça um novo pedido de licença.");
			  System.exit(1);
		  }
		 

	}
	
	
	private static X509Certificate getClientCertificate(byte[] certificateBytes) throws CertificateException {
		InputStream is = new ByteArrayInputStream(certificateBytes);
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		return (X509Certificate) fact.generateCertificate(is);
	}

}
