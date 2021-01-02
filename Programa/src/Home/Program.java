package Home;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.Scanner;

import javax.sound.midi.Soundbank;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.json.JSONException;

import com.main.BibControlo;

import UserInf.UserInfo;

public class Program extends JFrame {

	public static void main(String[] args) throws CertificateException, IOException, ParseException, JSONException {

		BibControlo cb = new BibControlo(); //instancia a biblioteca importada

		if (cb.verificaSeLicencaExiste()) {
			// tem ficheiro com licenca
			// depois e validar
			Program home = new Program();
			home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			home.setSize(500, 500);
			home.setVisible(true);
			home.setTitle("Home");
			home.setLocationRelativeTo(null);

			JLabel lblText = new JLabel(System.getProperty("os.name"));
			lblText.setBounds(125, 125, 300, 125);
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
