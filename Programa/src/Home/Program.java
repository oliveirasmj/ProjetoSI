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

	

	public static void main(String[] args) throws Exception {

		BibControlo cb = new BibControlo();

		if (cb.verificaSeLicencaExiste()) {
			Program home = new Program();
			home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			home.setSize(500, 500);
			home.setVisible(true);
			home.setTitle("Home");
			home.setLocationRelativeTo(null);

			JLabel lblText = new JLabel("Este programa está seguro, só pode aceder com uma licença válida.");
			lblText.setBounds(250, 250, 300, 300);
			home.add(lblText);
		} else {
			Scanner sc = new Scanner(System.in);
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



}
