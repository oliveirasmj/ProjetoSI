package Home;


import java.util.Scanner;

import javax.sound.midi.Soundbank;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.main.BibControlo;




public class Program extends JFrame {

	public static void main(String[] args) {
			
		BibControlo licenca = new BibControlo();
		
		boolean ficheiroComLicenca = false;
		//procurar ficheiro de licenca
		

		if(ficheiroComLicenca) {
			//tem ficheiro com licenca
			//depois e validar
			Program home = new Program();
			home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			home.setSize(500, 500);
			home.setVisible(true);
			home.setTitle("Home");
			home.setLocationRelativeTo(null);
			
			JLabel lblText = new JLabel(System.getProperty("os.name"));
			lblText.setBounds(125,125,300,125);
			home.add(lblText);
		}else {
			//nao encontra a licenca
			//questionar se quer criar uma nova licenca
			
			Scanner sc = new java.util.Scanner(System.in);
			System.out.println("Não foi encontrada nenhuma licenaça válida deseja criar uma? ");
			System.out.println("Se sim prima 1. Caso deseje terminar a execução prima 0");
			int opcao = sc.nextInt();
			switch(opcao) {
			  case 1:
				  licenca.criaLicenca();
			    break;
			  case 0:
				  System.exit(0);
			    break;
			 
			}
			
		}
		
		
		
		
		
	}

}
