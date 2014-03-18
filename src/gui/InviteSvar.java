package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import logikk.EndreAvtaleLogic;
import logikk.SpringUtilities;
import database.Database;

public class InviteSvar extends JPanel implements ActionListener {

	Database db;
	String bruker;
	int id;
	ArrayList<String> info;
	EndreAvtaleLogic eal;
	InviteSvarLogic isl;
	
	JLabel opprettetav;
	JTextField oatf;
	JLabel dato;
	JTextField datotf;
	JLabel start;
	JTextField starttf;
	JLabel slutt;
	JTextField slutttf;
	JLabel beskrivelse;
	JTextField btf;
	JLabel sted;
	JTextField stedtf;
	JLabel deltakere;
	JTextArea dtf;
	
	JLabel svar;
	JLabel tom;
	JButton ja;
	JButton nei;
	JLabel skjul;
	JButton s;
	
	public InviteSvar(String bruker, String avtaleid, Database db){
		this.db = db;
		this.bruker = bruker;
		this.id = Integer.parseInt(avtaleid);
		eal = new EndreAvtaleLogic();
		isl = new InviteSvarLogic(bruker, avtaleid, db);
		info = eal.getInfo(Integer.toString(this.id));
		
		printPanel();
	}
	
	public void printPanel(){
		setLayout(new SpringLayout());
		
		opprettetav = new JLabel("Opprettet av: ");
		this.add(opprettetav);
		
		oatf = new JTextField();
		oatf.setText(info.get(7));
		oatf.setEditable(false);
		this.add(oatf);
		
		dato = new JLabel("Dato:");
		this.add(dato);
		
		datotf = new JTextField();
		datotf.setText(info.get(0));
		datotf.setEditable(false);
		this.add(datotf);
		
		start = new JLabel("Starttid:");
		this.add(start);
		
		starttf = new JTextField();
		starttf.setText(info.get(1));
		starttf.setEditable(false);
		this.add(starttf);
		
		slutt = new JLabel("Sluttid:");
		this.add(slutt);
		
		slutttf = new JTextField();
		slutttf.setText(info.get(2));
		slutttf.setEditable(false);
		this.add(slutttf);
		
		beskrivelse = new JLabel("Beskrivelse:");
		this.add(beskrivelse);
		
		btf = new JTextField();
		btf.setText(info.get(3));
		btf.setEditable(false);
		this.add(btf);
		
		sted = new JLabel("Sted:");
		this.add(sted);
		
		stedtf = new JTextField();
		if (info.get(4) == null){
			stedtf.setText(info.get(5));
		} else {
			stedtf.setText("Rom " + info.get(4));
		}
		stedtf.setEditable(false);
		this.add(stedtf);
		
		deltakere = new JLabel("Deltakere");
		this.add(deltakere);
		
		dtf = new JTextArea();
		dtf.setText(isl.getDeltakere(info.get(6)));
		dtf.setEditable(false);
		this.add(dtf);
		
		svar = new JLabel("Svar på invitasjon");
		this.add(svar);
		
		tom = new JLabel(" ");
		this.add(tom);
		
		ja = new JButton("Ja");
		ja.addActionListener(this);
		this.add(ja);
		
		nei = new JButton("Nei");
		nei.addActionListener(this);
		this.add(nei);
		
		skjul = new JLabel("Skjul avslåtte avtaler:");
		this.add(skjul);
		
		s = new JButton("Skjul");
		s.addActionListener(this);
		this.add(s);
		
		SpringUtilities.makeCompactGrid(this,
				10, 2, 		 //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad
		
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ja){
			isl.kommer(true, this.id);
		} else if (e.getSource() == nei) {
			isl.kommer(false, this.id);
		} else if (e.getSource() == s) {
			isl.synlig(false, this.id);
		}
	}
	
}
