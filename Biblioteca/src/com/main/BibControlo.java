package com.main;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;

import org.json.JSONException;
import org.json.JSONObject;



import AppInfo.ApplicationInfo;
import Helpers.WriteToFile;
import SystemInfo.SystemInfo;
import UserInf.UserInfo;
import static globalMethods.globalMethods.*;
import static globalMethods.SymmetricKey.*;
import static globalMethods.AsymmetricKey.*;

public class BibControlo {
	private Licenca license;
//	private String pathToDirLicenca;
	
	// private UserInfo currentUser;
	public BibControlo() {
	}


	public static void main(String[] args) throws IOException, JSONException, CertificateException {
		
		//verifica se existe a pasta de licensas
		//se não existir cria a pasta
		File licenseDir = new File("licencas");
        if(!licenseDir.exists())
            licenseDir.mkdir();
        
        //verifica se a licenca existe, senao cria
        verificaSeLicencaExiste(System.getProperty("user.dir") + "/BibliotecaJar.jar");
	}

	
	/**
	 * 
	 * É aqui que chama as classes para obter os dados das licencas e para criar os ficheiros
	 *  Na classe de licenca e que a mesma é criada e os ficheiros gerados
	 * 
	 * @param pathToUser caminho do jar a criar o hash
	 * @return boolean caso consiga criar a licenca ou se existir ou nao 
	 * @throws IOException
	 * @throws CertificateException
	 */
	public static boolean verificaSeLicencaExiste(String pathToUser) throws IOException, CertificateException{
        File licenseFile = new File(pathToUser + "license");
        File encryptedLicenseFile = new File(pathToUser + "encryptedLicense");
        
        //Não percebi muito bem esta logica, mas é aqui que chama a criação dos ficheiros de licenca, na funcao novaLicenca da classe licensa
        if(!licenseFile.exists() && !encryptedLicenseFile.exists()){
            System.out.println("No license found!\ncreating a new license request");
            Licenca license = new Licenca();
            boolean criouLicenca = license.novaLicenca(pathToUser, 0);
           return criouLicenca;
        }else{
        	
        	//TODO NAO SEI O QUE AINDA FAZ
            if(encryptedLicenseFile.exists()){
                System.out.println("decrypting license file");
                //decript file
                byte[] SymmetricKey = readFromFile(pathToUser + "SymmetricKey");
                byte[] encryptedLicense = readFromFile(pathToUser + "encryptedLicense");
                byte[] decryptedLicense = decipher(SymmetricKey, encryptedLicense);
                writeToFile(pathToUser + "license", decryptedLicense);
                encryptedLicenseFile.delete();
                //TODO: assinar licença nova e verificar se esta se mantem // ou se calhar não
            }
            
            byte[] licenseBytes = readFromFile(pathToUser + "license");
            String licenseString = bytesToStringPrint(licenseBytes);

            Licenca license = new Licenca();
            //TODO tenta fazer o read do json
          //  license.readLicenseFromJsonString(licenseString);
            return true;
        }
    }

}