package Home;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.sound.midi.Soundbank;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.json.JSONException;

import com.main.BibControlo;

import UserInf.UserInfo;

public class Program extends JFrame {

	public static void main(String[] args) throws CertificateException, IOException, ParseException, JSONException, SecurityException, BadPaddingException {

		BibControlo cb = new BibControlo(); 

		if (cb.verificaSeLicencaExiste()) {
			Program home = new Program();
			home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			home.setSize(500, 500);
			home.setVisible(true);
			home.setTitle("Home");
			home.setLocationRelativeTo(null);

			JLabel lblText = new JLabel("Seja bem vindo ao programa mais seguro do mundo!");
			lblText.setBounds(250, 250, 300, 300);
			home.add(lblText);
		} else {
			Scanner sc = new java.util.Scanner(System.in);
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
