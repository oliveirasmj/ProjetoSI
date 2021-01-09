package Home;

import static Helpers.AsymmetricKey.geraParDeChaves;
import static Helpers.globalMethods.readFromFile;
import static Helpers.AsymmetricKey.decryptAppPairKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.sound.midi.Soundbank;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.json.JSONException;

import com.main.BibControlo;

import UserInf.UserInfo;

public class Program extends JFrame {

	private  byte[] appPrivKey;
	private  byte[] appPubKey;

	public static void main(String[] args) throws Exception {

		
		Program home = null;

		String caminhoDirAppKey = "chavesAplicacao/";
		File dirChaves = new File(caminhoDirAppKey);
		if (!dirChaves.exists())
			dirChaves.mkdir();

		String caminhoPrivKey = caminhoDirAppKey + "appPrivKey";
		String caminhoPubKey = caminhoDirAppKey + "appPubKey";
		File fileAppPrivKey = new File(caminhoPrivKey + "encrypted");
		File fileAppPubKey = new File(caminhoPubKey);

		Scanner sc = new Scanner(System.in);
		System.out.println("Introduza a palavra chave:");

		String pass = sc.nextLine();

		if (!fileAppPrivKey.exists() || !fileAppPubKey.exists()) {
			try {
				geraParDeChaves(caminhoPrivKey, caminhoPubKey, pass,caminhoDirAppKey);
				home = new Program(decryptAppPairKey(caminhoPrivKey + "encrypted", caminhoPrivKey+"desencript", pass,caminhoDirAppKey), readFromFile(caminhoPubKey));
			} catch (Exception e) {
				System.out.println("Não foi possivel gerar um par de chaves da aplicação." + e);
			}
		} else {
			home = new Program(decryptAppPairKey(caminhoPrivKey + "encrypted", caminhoPrivKey+"desencript", pass, caminhoDirAppKey), readFromFile(caminhoPubKey));
		}
		
		BibControlo cb = null;
		if(home != null) {
			 cb = new BibControlo(home.getAppPubKey(),home.getAppPrivKey(),pass);
		}else {
			System.out.println("Erro ao construir o programa ou a biblioteca");
			System.exit(0);
		}
		

		if (cb.verificaSeLicencaExiste()) {
			// Program home = new Program();
			home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			home.setSize(500, 500);
			home.setVisible(true);
			home.setTitle("Home");
			home.setLocationRelativeTo(null);

			JLabel lblText = new JLabel("Este programa parece seguro");
			lblText.setBounds(250, 250, 300, 300);
			home.add(lblText);
		} else {
		
			System.out.println("Não foi encontrada nenhuma licenaça válida deseja criar uma? ");
			System.out.println("Se sim prima 1. Caso deseje terminar a execução prima 0");
			int opcao = sc.nextInt();
			switch (opcao) {
			case 1:
				cb.registarNovaLicenca();
				System.out.println("Licenca criada, necessário autor validar a sua licenca");
				break;
			case 0:
				System.exit(0);
				break;

			}
		}
	}

	public byte[] getAppPrivKey() {
		return appPrivKey;
	}

	public void setAppPrivKey(byte[] appPrivKey) {
		this.appPrivKey = appPrivKey;
	}

	public byte[] getAppPubKey() {
		return appPubKey;
	}

	public void setAppPubKey(byte[] appPubKey) {
		this.appPubKey = appPubKey;
	}

	public Program(byte[] privKey, byte[] pubKey) {
		this.appPrivKey = privKey;
		this.appPubKey = pubKey;
	}

}
