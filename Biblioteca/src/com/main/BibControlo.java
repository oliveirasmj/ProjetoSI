package com.main;

import static Helpers.AsymmetricKey.*;
import static Helpers.SymmetricKey.*;
import static Helpers.globalMethods.*;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;

import org.json.JSONException;
import org.json.JSONObject;

import AppInfo.ApplicationInfo;
import Helpers.WriteToFile;
import SystemInfo.SystemInfo;
import UserInf.UserInfo;

public class BibControlo {
	private Licenca license;
	
	private static byte[] appPrivKey;
	private static byte[] appPubKey;
	private static String pass;
	
	public BibControlo(byte[] appPubKey, byte[] appPrivKey,String passDoPrograma) {
		this.appPrivKey = appPrivKey;
		this.appPubKey = appPubKey;
		this.pass = passDoPrograma;
	}
	
	/**
	 * Primeira funcao a ser chamada por programa princpial para verificar se nao
	 * existe licenca. Se não existir pergunta se quer criar com a funcao
	 * registarNovaLicenca
	 * 
	 * Se existir vai ler e verificar se está tudo ok
	 * 
	 * @param pathToUser caminho do jar a criar o hash
	 * @return boolean caso consiga criar a licenca ou se existir ou nao
	 * @throws Exception 
	 */
	public boolean verificaSeLicencaExiste() throws Exception {

		// verifica se existe a pasta de licencas
		File licenseDir = new File("licencas");

		if (!licenseDir.exists())
			licenseDir.mkdir(); //se não existir cria a pasta "licencas"
		UserInfo currentUser = null;
		String pathToUser = "";
		try {
			currentUser = new UserInfo(); //vai buscar info do User do CC ligado + Certificado do User	
			pathToUser = "licencas/" + currentUser.getNic() + "/"; //cria string com path
		}catch (Exception e) {
			System.out.println("Não foi possivel aceder aos dados do CC");
			System.exit(0);
		}
		
		String caminhoParaLicenca = pathToUser+"licenca/";
		

		File licenseFile = new File(caminhoParaLicenca + "license"); //instancia um ficheiro com o path/licence
		File encryptedLicenseFile = new File(caminhoParaLicenca + "encryptedLicense"); //instancia um ficheiro para o caminho de licenca encriptada

		if (!licenseFile.exists() && !encryptedLicenseFile.exists()) { //senao existir licenca nem licenca encriptada
			return false;
		} else {
		

			if (encryptedLicenseFile.exists()) {
				System.out.println("decrypting license file");
				// decript file
				//byte[] SymmetricKey = readFromFile(pathToUser + "SymmetricKey");
				
				//obter nova chave simetrica
				//DEcifra assimetrica - para obter chave
				byte[] symmetricKeyBytes = readFromFile(caminhoParaLicenca+"NovaChaveSimetricaEncrypted");
				
			
				//vem do construtor
				String caminhoDirAppKey = "chavesAplicacao/";
				String caminhoPrivKey = caminhoDirAppKey + "appPrivKeyencrypted";
				byte[] appPrivateKeyBytes = decryptAppPairKey(caminhoPrivKey, caminhoDirAppKey+"privKeyDecifrada", pass, caminhoDirAppKey);
				//byte[] appPrivateKeyBytes = readFromFile(caminhoPrivKey);
				
				
				byte[] key = asymmetricDecipher(appPrivateKeyBytes, symmetricKeyBytes);
				
				
				byte[] encryptedLicense = readFromFile(caminhoParaLicenca + "encryptedLicense");
				try {
					byte[] decryptedLicense = decipher(key, encryptedLicense,caminhoParaLicenca+"iv" );
					writeToFile(caminhoParaLicenca + "license", decryptedLicense);
					encryptedLicenseFile.delete();
				}catch(Exception ex) {
					System.out.println("Erro a desencriptar a licença.A chave simetrica utilizada para decifra não corresponde a chave utilizada para cifrar.");
					System.exit(0);
				}
				
			}

			byte[] licenseBytes = readFromFile(caminhoParaLicenca + "license");
			String licenseString = bytesToStringPrint(licenseBytes);

			Licenca license = new Licenca();
			// TODO tenta fazer o read do json
			license.readLicenseFromJsonString(licenseString);
			return true;
		}
	}

	/**
	 * Metodo chamado caso não exista licenca e user peça para criar uma licenca
	 * 
	 * @param email
	 * @return
	 */
	public boolean registarNovaLicenca() {
		Licenca license = new Licenca();
		
		try {
			String pathToJar = System.getProperty("user.dir") + "/ProgramJar.jar";
	
			license = new Licenca();
			license.geraNovaLicenca(pathToJar, 0.0, appPubKey);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

}