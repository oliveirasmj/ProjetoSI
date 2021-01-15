package com.main;

import static Helpers.globalMethods.*;

import org.json.JSONException;
import org.json.JSONObject;

import Helpers.globalMethods;
import Models.Validador;

import static Helpers.SymmetricKey.*;
import static Helpers.AsymmetricKey.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {

	JSONObject jsonLicense = null;
	JSONObject licenseInfo = null;
	JSONObject authorSig = null;
	Signature authorSignature = null;
	private Provider ccProvider;
	private KeyStore ks;
	Validador admin;
	byte[] requestKey;

	public static void main(String[] args) throws Exception {

		String rootPubKey = "validadorPubKey";
		File pubKey = new File(rootPubKey);

		String rootPrivKey = "validadorPrivKey";
		File privKeyEncrypted = new File(rootPrivKey+"encrypted");

		
		Scanner sc = new Scanner(System.in);
		System.out.println("Introduza a sua palavra pass ! ");
		String password = sc.nextLine();

		if (!pubKey.exists() || !privKeyEncrypted.exists()) {
			
			Validador vl = new Validador("Hp", password, "Hugo");
			vl.geraParDeChaves();
		} else {
			System.out.println("Introduza o numero do BI para gerar uma licenca.");
			
			String nBI = sc.nextLine();

			String rootParaDirBI = "pedido=" + nBI;
			File pastaAValidar = new File(rootParaDirBI);

			if (!pastaAValidar.exists()) {
				System.out.println("Não existe nenhuma pasta com o nº de BI introduzido.");
				System.exit(0);
			} else {
				try {
					 String privateKeyFilePath = "validadorPrivKey";
					
					
					//DEcifra assimetrica - para obter chave
					byte[] symmetricKeyBytes = readFromFile(rootParaDirBI+"/cipheredKeyRequest");
					
					byte[] authorPrivateKeyBytes = decryptAppPairKey(privateKeyFilePath+"encrypted", privateKeyFilePath, password, privateKeyFilePath);
					
					//byte[] authorPrivateKeyBytes = readFromFile("validadorPrivKey");
					byte[] key = asymmetricDecipher(authorPrivateKeyBytes, symmetricKeyBytes);

					//decifra simetrica
					byte[] encryptedLicenseByte = readFromFile(rootParaDirBI+"/cipheredLicenseRequest");
					byte[] licenseBytes = decipher(key, encryptedLicenseByte,rootParaDirBI);
					System.out.println("Foi gerado o ficheiro da licenca.");

					JSONObject licenseJSON = new JSONObject(bytesToStringPrint(licenseBytes));
					JSONObject licenseInfo = licenseJSON.getJSONObject("licenseInfo");

					// chech cliente siganture
					byte[] clienteSignatureBytes = stringDecodeBase64(licenseJSON.getJSONObject("signature").getString("signature"));
					byte[] clienteCertificate = stringDecodeBase64( licenseInfo.getJSONObject("user").getString("certificate"));
					byte[] appPubKey = stringDecodeBase64( licenseInfo.getJSONObject("app").getString("appPubKey"));
					
					Signature clientSignature = Signature.getInstance("SHA256withRSA");
					//Initializing the signature
					clientSignature.initVerify(getClientCertificate(clienteCertificate));
					clientSignature.update(licenseInfo.toString().getBytes());
					  //Verifying the signature
					if (!clientSignature.verify(clienteSignatureBytes)) {
						System.out.println("A licenca não é válida.");
						System.exit(1);
					} else {
						System.out.println("A Licenca foi validada com sucesso.");
					}
					
					//comparar HASH pedido com hash da nossa final da aplicação
					String appHash = generateHash("ProgramJar.jar","SHA-256");
					String jsonHash = licenseInfo.getJSONObject("app").getString("hash");
					if(!appHash.equals(jsonHash)) {
						System.out.println("Foi pedida uma licenca para uma aplicação que não é a nossa.");
						System.exit(1);
					}

					// change licenseInfo
					JSONObject date = new JSONObject();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date startDate = new Date(System.currentTimeMillis());
					Date endDate = new Date(System.currentTimeMillis() + 3600000); // 1hora

					date.put("startDate", formatter.format(startDate));
					date.put("endDate", formatter.format(endDate));
					licenseJSON.getJSONObject("licenseInfo").put("dates", date);

					// ASSINA COM PRIVATE KEY DO AUTOR
					PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(authorPrivateKeyBytes);
					KeyFactory kf = KeyFactory.getInstance("RSA");
					PrivateKey privateKey = kf.generatePrivate(ks);

					Signature sig = Signature.getInstance("SHA256withRSA");

					sig.initSign(privateKey); // chave privada
					sig.update(licenseJSON.getJSONObject("licenseInfo").toString().getBytes()); // texto a assinar
					byte[] authorSiganture = sig.sign();// aqui assina
					licenseJSON.getJSONObject("signature").put("signature", bytesEncodeBase64(authorSiganture));

					System.out.println("Pedido de nova licenca aceite!");
					
					String rootParaLicenca = "pedido=" + nBI + "/licenca/";
					File pastaLicenca = new File(rootParaLicenca);

					if (!pastaLicenca.exists()) 
						pastaLicenca.mkdir();
					
					
				
					
					//Gerar chave simetrica para validar LICENCA
					byte[] novaChaveSimetrica = generateKey();
					
					
					//Encriptar licenca com nova simetrica
					writeToFile(rootParaLicenca+"encryptedLicense", cipher(novaChaveSimetrica, licenseJSON.toString().getBytes(),rootParaLicenca + "iv"));

					
					// encriptar simetrica com chave pucblica da app
					byte[] appPublicKey = getAppPubKey(appPubKey).getEncoded();
					writeToFile(rootParaLicenca+"NovaChaveSimetricaEncrypted", asymmetricCipher(appPublicKey, novaChaveSimetrica));

					File fileEncryptedLicense = new File("cipheredLicenseRequest");
					File fileKey = new File("cipheredKeyRequest");
					fileEncryptedLicense.delete();
					fileKey.delete();

					System.exit(0);

				} catch (Exception e) {
					System.out.println("Error: " + e);
				}
			}
		}

	}

	private static X509Certificate getClientCertificate(byte[] certificateBytes) throws CertificateException {
		InputStream is = new ByteArrayInputStream(certificateBytes);
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		return (X509Certificate) fact.generateCertificate(is);
	}


	  private static String generateHash(String filename, String algorithm){
	        
	        if(!algorithm.equals("MD5") && !algorithm.equals("SHA-1") && !algorithm.equals("SHA-256")){
	            System.out.println("invalide algorithm");
	            return "";
	        }
	        
	        try{
	            byte[] fileToHash = globalMethods.readFromFile(filename);
	            
	            MessageDigest digest = MessageDigest.getInstance(algorithm);

	            byte[] hashed = digest.digest(fileToHash);

	            return BytesToHexString(hashed);
	        } catch (Exception ex) {
	            return "";
	        }
	    }

	  private static  String BytesToHexString(byte[] arrayBytes) {
		    StringBuffer stringBuffer = new StringBuffer();
		        for (int i = 0; i < arrayBytes.length; i++) {
		            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
		        }
		    return stringBuffer.toString();
		}

}