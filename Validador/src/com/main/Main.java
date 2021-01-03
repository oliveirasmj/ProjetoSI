package com.main;


import static Helpers.globalMethods.*;
import static Helpers.globalMethods.readFromFile;

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



public class Main {
	
	 
	    JSONObject jsonLicense = null;
	    JSONObject licenseInfo = null;
	    JSONObject authorSig = null;
	    Signature authorSignature = null;
	    private Provider ccProvider; 
	    private KeyStore ks;
	    Validador admin;
	    byte[] requestKey;

	public static void main(String[] args) throws InvalidKeyException, FileNotFoundException, NoSuchAlgorithmException, SignatureException, IOException {
		// TODO Auto-generated method stub
		/*Validador vl = new Validador("Hp", "0000", "Hugo");
		vl.setPrivateKey();
		vl.setPublicKey();*/
		 try {
	            
	            byte[] symmetricKeyBytes = readFromFile("cipheredKeyRequest");
	            byte[] authorPrivateKeyBytes = readFromFile("authorPrivKey");
	            System.out.println("privKeysize : " + authorPrivateKeyBytes.length);
	            System.out.println("cipheredKeyRequest : " + symmetricKeyBytes.length);
	            
	            byte[] decryptedSymmetricKeyBytes = asymmetricDecipher(authorPrivateKeyBytes, symmetricKeyBytes);
	            String decryptedSymmetricKeyString = bytesToStringPrint(decryptedSymmetricKeyBytes);
	            JSONObject symmetricKeyJSON = new JSONObject(decryptedSymmetricKeyString);
	            byte[] key = stringDecodeBase64(symmetricKeyJSON.getString("key").toString());
	            System.out.println("key size : " + key.length);
	            
	            byte[] encryptedLicenseByte = readFromFile("cipheredLicenseRequest");
	            byte[] licenseBytes = decipher(key, encryptedLicenseByte);
	            System.out.println("generated license file");
	            
	            JSONObject licenseJSON = new JSONObject(bytesToStringPrint(licenseBytes));
	            JSONObject licenseInfo = licenseJSON.getJSONObject("licenseInfo");
	            
	             //chech cliente siganture
	            byte[] clienteSignatureBytes = stringDecodeBase64(licenseJSON.getJSONObject("signature").getString("signature"));
	            byte[] clienteCertificate = stringDecodeBase64(licenseInfo.getJSONObject("user").getString("certificate"));
	            Signature clientSignature = Signature.getInstance("SHA256withRSA");
	            clientSignature.initVerify(getClientCertificate(clienteCertificate));        
	            clientSignature.update(licenseInfo.toString().getBytes());
	            if(!clientSignature.verify(clienteSignatureBytes)){
	                System.out.println("not valid license");
	                System.exit(1);
	            }
	            else{
	                System.out.println("License by cliente verified");
	            }            
	            
	            //change licenseInfo
	            JSONObject date    = new JSONObject();
	            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            Date startDate = new Date(System.currentTimeMillis());
	            Date endDate = new Date(System.currentTimeMillis() + 360000000); //1hora

	            date.put("startDate", formatter.format(startDate));
	            date.put("endDate", formatter.format(endDate));
	            licenseJSON.getJSONObject("licenseInfo").put("dates", date);
	            
	            // get signature of author of liceseInfo
	            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(authorPrivateKeyBytes);
	            KeyFactory kf = KeyFactory.getInstance("RSA");
	            PrivateKey privateKey = kf.generatePrivate(ks);

	            Signature sig = Signature.getInstance("SHA256withRSA");

	            sig.initSign(privateKey); //chave privada
	            sig.update(licenseJSON.getJSONObject("licenseInfo").toString().getBytes()); //texto a assinar
	            byte[] authorSiganture = sig.sign();
	            licenseJSON.getJSONObject("signature").put("signature", bytesEncodeBase64(authorSiganture));
	            
	            
	            System.out.println("generating accepted license");
	            byte[] ciphered = cipher(key, licenseJSON.toString().getBytes());
	            writeToFile("encryptedLicense", ciphered);
	            
	            File fileEncryptedLicense = new File("cipheredLicenseRequest");        
	            File fileKey = new File("cipheredKeyRequest");
	            fileEncryptedLicense.delete();
	            fileKey.delete();
	            
	            System.exit(0);
	            
		 }catch (Exception e) {
			System.out.println("Error: " + e);
		}
	    }
	    
	        
	    
	    private static X509Certificate getClientCertificate(byte[] certificateBytes) throws CertificateException{
	        InputStream is = new ByteArrayInputStream(certificateBytes);
	        CertificateFactory fact = CertificateFactory.getInstance("X.509");
	        return (X509Certificate) fact.generateCertificate(is);
	    }
	        
	    private boolean validateSignature(String requestAsymmKeyFilePath, String requestSymmLicenseFilePath) throws InvalidKeyException, CertificateException, FileNotFoundException, NoSuchAlgorithmException, SignatureException, IOException, JSONException {
	    
	        //read private key
	        
	    	requestKey = asymmetricDecipher(globalMethods.readFromFile("authorPrivKey"),
	                globalMethods.readFromFile(requestAsymmKeyFilePath));
	        
	        byte[] licenseContent = globalMethods.readFromFile(requestSymmLicenseFilePath);		
	        byte[] decipheredLicense = decipher(requestKey, licenseContent);		
	        String decipheredLicenseContent = bytesToStringPrint(decipheredLicense);
			
	        jsonLicense = new JSONObject(decipheredLicenseContent);
	        licenseInfo = jsonLicense.getJSONObject("licenseInfo");
	        //byte[] sig  = stringDecodeBase64(jsonLicense.getJSONObject("signature").getString("signature"));
		
	        //get cliente certificate
	        byte[] clientecertificate = stringDecodeBase64(licenseInfo.getJSONObject("user").getString("signature"));
	                
	        //chech cliente siganture
	        Signature clientSignature = Signature.getInstance("SHA256withRSA");
	        clientSignature.initVerify(getClientCertificate(clientecertificate));        
	        clientSignature.update(licenseInfo.toString().getBytes());
	            
	        return clientSignature.verify(clientecertificate);    		
	    }
	    
	    /**
	     * function used to get the signature byes of a data
	     * @param bytesToSign bytes of data to be signed
	     * @return byte array 
	     * @throws KeyStoreException
	     * @throws NoSuchAlgorithmException
	     * @throws UnrecoverableKeyException
	     * @throws InvalidKeyException
	     * @throws SignatureException 
	     */
	    private byte[] getSignatureOfData(byte[] bytesToSign) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, SignatureException, InvalidKeySpecException{
	        
	        byte[] requestKey = null;
			try {
				requestKey = globalMethods.readFromFile("authorPrivKey");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(requestKey);
	        KeyFactory kf = KeyFactory.getInstance("RSA");
	        PrivateKey pvt = kf.generatePrivate(ks);
	        
	        Signature sig = Signature.getInstance("SHA256withRSA");
	        sig.initSign(pvt); //chave privada
	        sig.update(bytesToSign); //texto a assinar
	        return sig.sign(); // assinar (é a assinatura)
	    }
	    
	    private X509Certificate getAuthorCertificate() throws CertificateException, IOException{
	        InputStream is = new ByteArrayInputStream(readFromFile("CertificadoAutor"));
	        CertificateFactory fact = CertificateFactory.getInstance("X.509");
	        return (X509Certificate) fact.generateCertificate(is);
	    }
	    
	    private boolean generateLicense(){
	        try{             
	             String path = "author_license/" +admin.getName() + "/";
	             //criar directoria se não existir
	             File dir = new File(path);
	             if(!dir.exists())
	                 dir.mkdir();             
	             
	            
	             
	             
	             JSONObject date    = new JSONObject();
	             SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	             Date startDate = new Date(System.currentTimeMillis());
	             Date endDate = new Date(System.currentTimeMillis() + 3600000); //1hora
	             
	             date.put("startDate", formatter.format(startDate));
	             date.put("endDate", formatter.format(endDate));
	             licenseInfo.put("dates", date);

	             
	                     
	                     
	             JSONObject license = new JSONObject();
	             license.put("licenseInfo", licenseInfo);
	             
	             
	             //get siganture
	             byte[] licenseBytes = licenseInfo.toString().getBytes();
	             
	             byte[] licenseSignatureBytes = getSignatureOfData(licenseBytes);
	             globalMethods.writeToFile(path + admin.getName() + " - signature", licenseSignatureBytes);
	             //System.out.println(bytesToStringPrint(licenseBytesSignature));
	             //System.out.println("size : " + licenseBytesSignature.length);
	             authorSig = new JSONObject();
	             authorSig.put("signature", bytesEncodeBase64(licenseSignatureBytes));
	             
	             //add siganture to json
	             license.put("signature", authorSig);
	             globalMethods.writeToFile(path + admin.getName()+ " - licenca", license.toString().getBytes());

	             
	             //cifrar com chave simetrica
	             globalMethods.writeToFile(path + "cifrado", cipher(requestKey, license.toString().getBytes()));
	             
	             return true;
	        } catch (Exception e){
	            return false;
	        }
	    }
	    
	    

	    /**
	     * Função do autor para emitir uma licença. Inicialmente é validado a
	     * assinatura do pedido de licença e depois emitido para a máquina que fez o
	     * pedido, desde que não exista já um registo desta mesma máquina na base de
	     * dados.
	     * @throws IOException 
	     * @throws SignatureException 
	     * @throws NoSuchAlgorithmException 
	     * @throws FileNotFoundException 
	     * @throws CertificateException 
	     * @throws HeadlessException 
	     * @throws InvalidKeyException 
	     * @throws JSONException 
	     */
	    public void validateRequestLicense() throws InvalidKeyException,CertificateException, FileNotFoundException, NoSuchAlgorithmException, SignatureException, IOException, JSONException {
	        // Se o pedido de licença for autêntico (validado), pergunta se quer gerar uma licença
	        if (validateSignature("request_files/cipheredKeyRequest.cer", "request_files/cipheredLicenseRequest.cer")) {
	      
	            System.out.println("O pedido de licença é autêntico! Está validado e devidamente assinado.\", \"Confirmação de autenticidade\",");
	           

	                // Iniciar o registo de uma licença
					if (generateLicense()) {
					   System.out.println("Licenca gerada com sucesso");
					} else {
					    System.out.println("Não foi gerada nova licença.");
					}
	            
	        }
	    }



}