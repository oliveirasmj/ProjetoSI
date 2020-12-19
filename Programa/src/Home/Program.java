package Home;


import javax.swing.JFrame;
import javax.swing.JLabel;

public class Program extends JFrame {

	public static void main(String[] args) {
		Program home = new Program();
		home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		home.setSize(500, 500);
		home.setVisible(true);
		home.setTitle("Home");
		home.setLocationRelativeTo(null);
		
		JLabel lblText = new JLabel("Hello World");
		lblText.setBounds(125,125,300,125);
		home.add(lblText);
	}

}
