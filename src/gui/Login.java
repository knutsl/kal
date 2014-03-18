package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import database.Database;
import logikk.LoginLogic;
import logikk.SpringUtilities;

public class Login extends JFrame implements ActionListener {

	private JTextField textField1;
	private JPasswordField textField2;
	private LoginLogic ll;
	private JFrame frame;
	public String bruker;
	JButton b2;

	public Login() {
		Database db = new Database();
//		Kalender k = new Kalender("lol", db);
		String[] labels = {"Brukernavn: ", "Passord: "};

		JPanel p = new JPanel(new SpringLayout());

		// Brukernavn
		JLabel l1 = new JLabel(labels[0], JLabel.TRAILING);
		p.add(l1);
		textField1 = new JTextField(10);
		l1.setLabelFor(textField1);
		p.add(textField1);
		textField1.addActionListener(this);

		// Passord
		JLabel l2 = new JLabel(labels[1], JLabel.TRAILING);
		p.add(l2);
		textField2 = new JPasswordField(10);
		l2.setLabelFor(textField2);
		p.add(textField2);
		textField2.addActionListener(this);

		//Logg-inn knapp
		JButton b1 = new JButton("Logg inn");
		b2 = new JButton("Registrer bruker");
		p.add(b1);
		p.add(b2);
		b1.addActionListener(this);
		b2.addActionListener(this);

		SpringUtilities.makeCompactGrid(p,
				3, 2, 		 //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		frame = new JFrame("Login");
		frame.add(p);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		p.setOpaque(true); 
		frame.setLocationRelativeTo(null);
		frame.setContentPane(p);
		frame.pack();
		frame.setVisible(true);
	}

	public String getBruker() {
		return bruker;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		ll = new LoginLogic(textField1.getText());

		char[] arr = textField2.getPassword();
		String output ="";
		for(char str: arr)
			output=output+str;

		if(e.getSource() == b2) {
			if (textField1.getText().equals("") || output.equals("")){
				Component frame = null;
				JOptionPane.showMessageDialog(frame,"Ett eller flere felter var tomme!","Feil",JOptionPane.WARNING_MESSAGE);
			}
			else {
				boolean check = ll.registrer(textField1.getText(),output);
				if (check == false) {
					Component frame = null;
					JOptionPane.showMessageDialog(frame,"Brukernavn er opptatt!","Feil",JOptionPane.WARNING_MESSAGE);
				} else {
					Component frame = null;
					JOptionPane.showMessageDialog(frame,"Bruker er registrert!","Suksess",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		} else {

			if(output != "") {
				if (ll.isTrue(output) == true){
					bruker = textField1.getText();
					frame.dispose();
					//new CalenderGUI();
					//new Avtale(getBruker());
					ll.createNullAvtale();
					new Hovedside(getBruker(), ll.getDB());
				} else {
					Component frame = null;
					JOptionPane.showMessageDialog(frame,"Feil brukernavn/passord!","Feil",JOptionPane.WARNING_MESSAGE);
				}
			} else {
				Component frame = null;
				JOptionPane.showMessageDialog(frame,"Feil brukernavn/passord!","Feil",JOptionPane.WARNING_MESSAGE);			}
		}
	}
}
