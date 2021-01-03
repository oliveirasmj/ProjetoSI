package com.main;

import static Helpers.AsymmetricKey.*;
import static Helpers.SymmetricKey.*;
import static Helpers.globalMethods.*;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.text.ParseException;

import javax.crypto.BadPaddingException;

import org.json.JSONException;
import org.json.JSONObject;

import AppInfo.ApplicationInfo;
import Helpers.WriteToFile;
import SystemInfo.SystemInfo;
import UserInf.UserInfo;

public class BibControlo {
	private Licenca license;
//	private String pathToDirLicenca;

	// private UserInfo currentUser;
	public BibControlo() {
	}

	public static void main(String[] args) throws IOException, JSONException, CertificateException, ParseException {

		// verifica se existe a pasta de licencas
		File licenseDir = new File("licencas");
		if (!licenseDir.exists())
			licenseDir.mkdir();

		// verifica se a licenca existe, senao cria

		// UserInfo currentUser = new UserInfo();
		// String pathToUser = "licencas/" +currentUser.getName() + "/";
		// registarNovaLicenca();
		// verificaSeLicencaExiste( );
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
	 * @throws IOException
	 * @throws CertificateException
	 * @throws ParseException
	 * @throws JSONException
	 */
	public boolean verificaSeLicencaExiste() throws IOException, CertificateException, ParseException, JSONException, SecurityException, BadPaddingException {

		// verifica se existe a pasta de licencas
		File licenseDir = new File("licencas");

		if (!licenseDir.exists())
			licenseDir.mkdir(); //se não existir cria a pasta "licencas"
		UserInfo currentUser = null;
		String pathToUser = "";
		try {
			currentUser = new UserInfo(); //vai buscar info do User do CC ligado + Certificado do User	
			pathToUser = "licencas/" + currentUser.getName() + "/"; //cria string com path
		}catch (Exception e) {
			System.out.println("Não foi possivel aceder aos dados do CC");
			System.exit(0);
		}
		
		

		File licenseFile = new File(pathToUser + "license"); //instancia um ficheiro com o path/licence
		File encryptedLicenseFile = new File(pathToUser + "encryptedLicense"); //instancia um ficheiro para o caminho de licenca encriptada

		if (!licenseFile.exists() && !encryptedLicenseFile.exists()) { //senao existir licenca nem licenca encriptada
			System.out.println("No license found!\ncreating a new license request");
			return false;
		} else {
		

			if (encryptedLicenseFile.exists()) {
				System.out.println("decrypting license file");
				// decript file
				byte[] SymmetricKey = readFromFile(pathToUser + "SymmetricKey");
				byte[] encryptedLicense = readFromFile(pathToUser + "encryptedLicense");
				try {
					byte[] decryptedLicense = decipher(SymmetricKey, encryptedLicense);
					writeToFile(pathToUser + "license", decryptedLicense);
					encryptedLicenseFile.delete();
				}catch(IOException e) {
					System.out.println("Erro a desencriptar a licença.A chave simetrica utilizada para decifra não corresponde a chave utilizada para cifrar.");
					System.exit(0);
				}catch(Exception ex) {
					System.out.println("Erro a desencriptar a licença.A chave simetrica utilizada para decifra não corresponde a chave utilizada para cifrar.");
					System.exit(0);
				}
				
				
				
				// TODO: assinar licença nova e verificar se esta se mantem // ou se calhar não
			}

			byte[] licenseBytes = readFromFile(pathToUser + "license");
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
			// String pathToJar = System.getProperty("user.dir") + "/BibliotecaJar.jar";
			String pathToJar = System.getProperty("user.dir") + "/ProgramJar.jar";

			license = new Licenca();
			license.geraNovaLicenca(pathToJar, 0.0);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

}