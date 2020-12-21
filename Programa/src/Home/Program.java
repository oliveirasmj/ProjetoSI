package Home;


import javax.swing.JFrame;
import javax.swing.JLabel;

import com.main.BibControlo;


public class Program extends JFrame {

	public static void main(String[] args) {
			
		BibControlo licenca = new BibControlo();
		
		if( licenca.temLicenca(false) ) {
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
			Program home = new Program();
			home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			home.setSize(500, 500);
			home.setVisible(true);
			home.setTitle("Home");
			home.setLocationRelativeTo(null);
			
			JLabel lblText = new JLabel("Não tem licenca para estar aqui");
			lblText.setBounds(125,125,300,125);
			home.add(lblText);
		}
		
		
		
		
	}

}
