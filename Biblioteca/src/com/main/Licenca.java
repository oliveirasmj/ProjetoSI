package com.main;

import static Helpers.AsymmetricKey.asymmetricCipher;
import static Helpers.SymmetricKey.cipher;
import static Helpers.SymmetricKey.generateKey;
import static Helpers.globalMethods.bytesEncodeBase64;
import static Helpers.globalMethods.readFromFile;
import static Helpers.globalMethods.stringDecodeBase64;
import static Helpers.globalMethods.writeToFile;

import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import AppInfo.ApplicationInfo;
import Helpers.WriteToFile;
import SystemInfo.SystemInfo;
import UserInf.UserInfo;

public class Licenca {

	private UserInfo user;
	private SystemInfo system;
	private ApplicationInfo app;

	// private date //later
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Date startDate;
	private Date endDate;

	// private siganture
	private byte[] authorSignatureBytes;
	private JSONObject licenseInfo; // use for cheking author siganture

	public Licenca() {
	}

	public boolean geraNovaLicenca(String dirParaJar, double appVersion) throws IOException {
		try {
			system = new SystemInfo();
			user = new UserInfo();
			// Dir para jar a gerar hash . TODO tem de ser o jar do programa e nao da
			// libraria
			app = new ApplicationInfo(dirParaJar, appVersion, "SHA-256");

			// instancia classes
			WriteToFile fw = new WriteToFile();

			// Junta todos os jsons
			JSONObject licenseInfo = new JSONObject();
			licenseInfo.put("user", user.userJson());
			licenseInfo.put("system", system.systemJSON());
			licenseInfo.put("app", app.toJSON());

			String path = "licencas/" + user.getName() + "/";
			String pathTemp = "licencas/" + user.getName() + "/temp/";

			// Cria pasta com nome do utilizador do cartao de cidadao caso n�o exista
			File dir = new File(path);
			if (!dir.exists())
				dir.mkdir();

			// cria dir temp com dados de licenca caso nao exista
			File dirTemp = new File(pathTemp);
			if (!dirTemp.exists())
				dirTemp.mkdir();

			// torna objectoJson da licenca em array de bytes - licenseInfo(user, system, app)
			byte[] licenseBytes = licenseInfo.toString().getBytes();

			// torna array de bytes da licenca em array de byte para Assinatura do cartao
			// a assintura � um hash da licenca encriptado com a chave privada para comprovar que a licenca nao foi alterada
			byte[] licenseSignatureBytes = user.uc.getSignatureOfData(licenseBytes);
			// cria ficheiro com nome do utilziador - signatura assinatura
			fw.writeToFileByte(pathTemp + user.getName() + " - signature", licenseSignatureBytes);

			// cria um objeto json com a assinatura
			JSONObject signature = new JSONObject();
			signature.put("signature", bytesEncodeBase64(licenseSignatureBytes));

			// cria um json licenca, onde tem a assinatura e informa�ao da licenca
			JSONObject license = new JSONObject();
			license.put("licenseInfo", licenseInfo);
			license.put("signature", signature);
			writeToFile(pathTemp + user.getName() + " - licenca", license.toString().getBytes());

			// gera chave simetrica
			byte[] key = generateKey();
			// guarda no ficheiro symmetrickey a chave simetrica
			writeToFile(path + "SymmetricKey", key);
			// cifra com a chave simetrica
			writeToFile(path + "cipheredLicenseRequest", cipher(key, license.toString().getBytes()));

			// ----- segundo pacote -----
			// devia ser assinado para garantir que n�o foi alterado (inetgridade)
			JSONObject theKey = new JSONObject();
			theKey.put("key", bytesEncodeBase64(key));

			// cifra a chave simetrica que guardou a licenca com chave a chave assimetrica
			// guarda o ficheiro cifrado com o nome ciphredKeyRequest
			byte[] authorPublicKey = getAuthorPublicKey().getEncoded();
			System.out.println("authorPublicKey : " + authorPublicKey.length);
			writeToFile(path + "cipheredKeyRequest", asymmetricCipher(authorPublicKey, theKey.toString().getBytes()));

			System.out.println("\n\nFicheiro criados");
			return true;
		} catch (Exception e) {
			System.out.println("error in generate new files : " + e.toString());
			return false;
		}
	}

	private PublicKey getAuthorPublicKey()
			throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		// chave publica de autor guardada na raiz da pasta biblioteca
		byte[] authorPubKeyBytes = readFromFile("authorPubKey");
		X509EncodedKeySpec ks = new X509EncodedKeySpec(authorPubKeyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(ks);
	}

	public void readLicenseFromJsonString(String licenseString)
			throws CertificateException, ParseException, JSONException {
		// le json da licenca
		JSONObject jsonLicense = new JSONObject(licenseString);

		this.licenseInfo = jsonLicense.getJSONObject("licenseInfo");
		// cria objeto user atraves da licenca
		JSONObject user = licenseInfo.getJSONObject("user");
		this.user = new UserInfo(user.getString("name"), null, user.getString("nic"));
		// vai validar certificado em licenca se � o mesmo do cc do user
		this.user.uc.setLicenseCertificate(user.getString("certificate"));

		JSONObject system = licenseInfo.getJSONObject("system");
		this.system = new SystemInfo(system.getString("macAdress"), system.getString("motherBoardSerial"),
				system.getString("userName"), system.getString("hostName"), system.getString("cpuSerial"));

		JSONObject app = licenseInfo.getJSONObject("app");

		this.app = new ApplicationInfo(app.getString("appName"), app.getString("hash"), app.getDouble("version"));
		//obtem assinatura de autor atraves de json
		this.authorSignatureBytes = stringDecodeBase64(jsonLicense.getJSONObject("signature").getString("signature"));

	

		
		  this.startDate = (Date)
		  formatter.parse(licenseInfo.getJSONObject("dates").getString("startDate"));
		  this.endDate = (Date)
		  formatter.parse(licenseInfo.getJSONObject("dates").getString("endDate"));
		 

	}

}
