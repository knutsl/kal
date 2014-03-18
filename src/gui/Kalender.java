package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import logikk.CalenderLogic;
import logikk.SpringUtilities;
import database.Database;

public class Kalender extends JPanel implements ActionListener {
	
	Database db;
	String bruker;
	CalenderLogic cl;
	JPanel knapper;
	JPanel header;
	int maaned;
	int aar;
	int dag;
	JButton forige;
	JButton neste;
	JPanel avtaleliste;
	JButton svar;
	JList dineavtaler;
	InviteSvar is;
	
	public Kalender(String bruker, Database db){
		this.bruker = bruker;
		this.setLayout(new SpringLayout());
		this.db = db;
		cl = new CalenderLogic(db, bruker);
		maaned = cl.getMonth();
		aar = cl.getYear();
		dag = 0;
		
	
		printCalender(dag, maaned, aar);
		
	}

	public void printCalender(int dag, int month, int year){
		this.removeAll();
		this.dag = dag;
		knapper = new JPanel();
		knapper.setLayout(new GridLayout(7,7));
		
		header = new JPanel(new GridLayout(1,5));
		forige = new JButton("<---");
		JLabel tom1 = new JLabel(" ");
		JLabel tom2 = new JLabel(" ");
		JLabel maanedaar = new JLabel(cl.getMonthName(month)+", "+year);
		neste = new JButton("--->");
		forige.addActionListener(this);
		neste.addActionListener(this);
		header.add(forige);
		header.add(tom1);
		header.add(maanedaar);
		header.add(tom2);
		header.add(neste);
		
		this.add(header);
		header.setVisible(true);
		
		String[] weekdays = {"    Man", "    Tir", "    Ons", "    Tor", "   Fre", "   Lør", "   Søn"};
		for (String weekday : weekdays){
			JLabel daglabe = new JLabel(weekday);
			knapper.add(daglabe);
		}
		int[][] days = cl.getCalender(month, year);
		for (int[] week : days){
			for (final int day : week) {
				if (day == 0) {
					JLabel tom = new JLabel(" ");
					knapper.add(tom);
				} else {
					JButton dagbut = new JButton(Integer.toString(day));
					if (cl.harAvtale(day, maaned, aar)){
						dagbut.setBackground(Color.CYAN);
					} else if (cl.harAnsattAvtale(day, maaned, aar)) {
						dagbut.setBackground(Color.ORANGE);
					}
					knapper.add(dagbut);
					dagbut.addActionListener(new ActionListener() {

			            @Override
			            public void actionPerformed(ActionEvent e) {
			            	printCalender(day, maaned, aar);		            	

			            }
			        });
				}
			}
			
		}
		
		this.add(knapper);
		knapper.setVisible(true);
		
		printAvtaler(dag, maaned, aar);
		this.updateUI();
	}
	
	public void printAvtaler(int d, int m, int a){
		avtaleliste = new JPanel(new SpringLayout());
		
		JLabel da = new JLabel("Dine avtaler:");
		avtaleliste.add(da);
		
		dineavtaler = new JList(cl.getDineAvtaler(d, m, a));
		JScrollPane scrollda = new JScrollPane(dineavtaler);
		scrollda.setPreferredSize(new Dimension(400,100));
		avtaleliste.add(scrollda);
		
		svar = new JButton("Svar på invitasjon");
		svar.addActionListener(this);
		avtaleliste.add(svar);
		
		JLabel aa = new JLabel("Ansattes avtaler:");
		avtaleliste.add(aa);
		
		JList ansatteavtaler = new JList(cl.getAlleAvtaler(d, m, a));
		JScrollPane scrollaa = new JScrollPane(ansatteavtaler);
		scrollaa.setPreferredSize(new Dimension(400,100));
		avtaleliste.add(scrollaa);
		
		SpringUtilities.makeCompactGrid(avtaleliste,
				5, 1, 		 //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad
		
		this.add(avtaleliste);
		avtaleliste.setVisible(true);
		
		SpringUtilities.makeCompactGrid(this,
				3, 1, 		 //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad
		
		this.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == forige){
			this.removeAll();
			maaned = maaned - 1;
			if ( maaned == 0){
				maaned = 12;
				aar = aar - 1;
			}
			printCalender(dag, maaned,aar);
			this.updateUI();
		} else if (e.getSource() == neste) {
			this.removeAll();
			maaned = maaned +1;
			if (maaned == 13){
				maaned = 1;
				aar = aar + 1;
			}
			printCalender(dag, maaned, aar);
			this.updateUI();
		} else if (e.getSource() == svar) {
			String id = getSelectedAvtale();
			if (id.equals("-1")){
				Component frame = null;
				JOptionPane.showMessageDialog(frame,"Du må velge en \n av dine avtaler.","Feil",JOptionPane.WARNING_MESSAGE);
			} else {
				this.removeAll();
				this.updateUI();
				is = new InviteSvar(this.bruker, id, this.db);
				this.add(is);

				SpringUtilities.makeCompactGrid(this,
						1, 1, 		 //rows, cols
						6, 6,        //initX, initY
						6, 6);       //xPad, yPad
				this.setVisible(true);
			}
		}

	}

	public String getSelectedAvtale() {
		if (dineavtaler.getSelectedValue() == null){
			return "-1";
		}
		return dineavtaler.getSelectedValue().toString().substring(0,4);
	}
	
}
