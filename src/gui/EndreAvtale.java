package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import logikk.AvtaleLogic;
import logikk.EndreAvtaleLogic;
import logikk.SpringUtilities;

public class EndreAvtale extends JPanel implements ActionListener{

	private JTextField textField0;
	private JTextField textField1;
	private JTextField textField2;
	private JTextField textField3;
	private JTextField textField4;
	private JTextField textField5;
	private JTextField textField6;
	private JTextField textField7;
	private JTextField textField8;
	JList alist;
	private JList dlist;
	private JLabel alisthead;
	private JLabel dlisthead;
	JButton b2;
	JButton b5;
	Rom r;
	private EndreAvtaleLogic eal;
	private String bruker;
	private JButton b1;
	JPanel avtaleliste;
	String[] avtaler;
	AvtaleLogic al;

	public EndreAvtale(String bruker, AvtaleLogic al) {
		eal = new EndreAvtaleLogic();
		this.al = al;
		String[] labels = {"AvtaleID: ", "Dato: (DDMM≈≈≈≈)", "Starttid: (TTMM)", "Sluttid: (TTMM)", "Beskrivelse: ", "RomID: ", "Sted: ", "Deltakere: "};
		setBruker(bruker);

		setLayout(new SpringLayout());
		setSize(400,400);

		JPanel tekstbokser = new JPanel(new SpringLayout());
		this.add(tekstbokser, SpringLayout.NORTH);

		avtaleliste = new JPanel(new SpringLayout());
		this.add(avtaleliste, SpringLayout.SOUTH);

		// Dato
		JLabel l1 = new JLabel(labels[1], JLabel.TRAILING);
		tekstbokser.add(l1);
		textField1 = new JTextField(15);
		l1.setLabelFor(textField1);
		tekstbokser.add(textField1);
		textField1.addActionListener(this); 

		// Starttid
		JLabel l2 = new JLabel(labels[2], JLabel.TRAILING);
		tekstbokser.add(l2);
		textField2 = new JTextField(10);
		l2.setLabelFor(textField2);
		tekstbokser.add(textField2);
		textField2.addActionListener(this); 

		// Sluttid
		JLabel l3 = new JLabel(labels[3], JLabel.TRAILING);
		tekstbokser.add(l3);
		textField3 = new JTextField(10);
		l3.setLabelFor(textField3);
		tekstbokser.add(textField3);
		textField3.addActionListener(this); 

		// Beskrivelse
		JLabel l4 = new JLabel(labels[4], JLabel.TRAILING);
		tekstbokser.add(l4);
		textField4 = new JTextField(10);
		l4.setLabelFor(textField4);
		tekstbokser.add(textField4);
		textField4.addActionListener(this); 

		// Sted
		JLabel l6 = new JLabel(labels[6], JLabel.TRAILING);
		tekstbokser.add(l6);
		textField6 = new JTextField(10);
		l6.setLabelFor(textField6);
		tekstbokser.add(textField6);
		textField6.addActionListener(this);

		// RomID
		b5 = new JButton("Finn Rom");
		tekstbokser.add(b5);
		textField5 = new JTextField(10);
		tekstbokser.add(textField5);
		textField5.addActionListener(this); 
		textField5.setEditable(false);
		b5.addActionListener(this); 

		JLabel l8 = new JLabel("Ekstern brukeremail**:", JLabel.TRAILING);
		tekstbokser.add(l8);
		textField8 = new JTextField(10);
		l8.setLabelFor(textField8);
		tekstbokser.add(textField8);
		textField8.addActionListener(this);

		// Deltakere
		JLabel l7 = new JLabel(labels[7], JLabel.TRAILING);
		tekstbokser.add(l7);
		textField7 = new JTextField(10);
		l7.setLabelFor(textField7);
		tekstbokser.add(textField7);
		textField7.addActionListener(this);

		//Lag-avtale knapp
		b1 = new JButton("Lagre endringer");
		b2 = new JButton("Slett Avtale");
		tekstbokser.add(b1);
		tekstbokser.add(b2);
		b1.addActionListener(this);

		alisthead = new JLabel("Dine Avtaler");
		avtaleliste.add(alisthead);

		createAlist(false);

		dlisthead = new JLabel("Brukere");
		avtaleliste.add(dlisthead);

		createDlist();

		SpringUtilities.makeCompactGrid(tekstbokser,
				9, 2, 		 //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		SpringUtilities.makeCompactGrid(avtaleliste,
				4, 1, 		 //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		SpringUtilities.makeCompactGrid(this,
				2, 1, 		 //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad
	}

	private void createDlist() {
		String[] s = al.getAnsatte(bruker);

		dlist = new JList(s);
		JScrollPane listScroller = new JScrollPane(dlist);
		listScroller.setPreferredSize(new Dimension(300, 100));
		avtaleliste.add(listScroller);

		dlist.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting() && !al.textContains(textField7.getText(), dlist.getSelectedValue().toString())) {
					textField7.setText(textField7.getText() + dlist.getSelectedValue().toString() + ", ");
					if(!textField5.getText().equals("")){
						if(al.sjekkPlass(textField5.getText(), textField7.getText().split(", ").length)) {
							textField5.setText("");
							Component c = null;
							JOptionPane.showMessageDialog(c,"For mange deltagere, velg et annet rom !","Feil",JOptionPane.WARNING_MESSAGE);
						}
					}
				}
			}

		});
	}

	private void createAlist(boolean refresh) {
		avtaler = eal.getAvtaler(bruker);

		alist = new JList(avtaler);
		JScrollPane listScroller = new JScrollPane(alist);
		listScroller.setPreferredSize(new Dimension(300, 100));
		avtaleliste.add(listScroller);
		alist.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					String id = alist.getSelectedValue().toString().substring(3, 7);
					ArrayList<String> info = new ArrayList<String>();
					info = eal.getInfo(id);
					textField1.setText(info.get(0));
					textField2.setText(info.get(1));
					textField3.setText(info.get(2));
					textField4.setText(info.get(3));
					textField5.setText(info.get(4));
					textField6.setText(info.get(5));
					textField7.setText(info.get(6)+ ", ");
				}
			}

		});

	}

	public void setBruker(String bruker) {
		this.bruker = bruker;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		eal = new EndreAvtaleLogic();

		if(e.getSource() == b1){
			if (alist.getSelectedValue() == null){
				Component frame = null;
				JOptionPane.showMessageDialog(frame,"Ingen endring gjort!\nDu MÂ velge avtale.","Feil",JOptionPane.WARNING_MESSAGE);
				return;
			}
			int avtaleid = Integer.parseInt(alist.getSelectedValue().toString().substring(3, 7));
			String dato = textField1.getText();
			String starttid = textField2.getText();
			String sluttid = textField3.getText();
			String beskrivelse = textField4.getText();
			Object romid;
			if (textField5.getText().equals("")) {
				romid = null;
			} else {
				romid = Integer.parseInt(textField5.getText());
			}
			String sted = textField6.getText();
			String[] deltakere = textField7.getText().split(", ");
			String[] eksterne = textField8.getText().split(" ");

			boolean success = eal.endreAvtale(avtaleid, dato, starttid, sluttid, beskrivelse, romid, sted, bruker, deltakere, eksterne);
			eal.printAvtale(success);
		} 
		else if(e.getSource() == b5) {
			String dato = textField1.getText();
			String starttid = textField2.getText();
			String sluttid = textField3.getText();
			if(dato.length() == 8 && starttid.length() == 4 && sluttid.length() == 4) {
				int deltakere = textField7.getText().split(", ").length;
				r = new Rom(dato, starttid, sluttid, deltakere);
				r.b1.addActionListener(this);
				r.b2.addActionListener(this);
			} else {
				Component c = null;
				JOptionPane.showMessageDialog(c, "Du mÂ fylle inn alle *-feltene!", "Feil", JOptionPane.INFORMATION_MESSAGE);
			} 
		}
		else if (e.getSource() == r.b1 ) {

			String s = (String) r.j.getSelectedValue();
			if(!(s == null)) {
				textField5.setText(s.substring(4,8).replaceAll("\\D+",""));
				r.frame.setVisible(false);
			}
		} else if (e.getSource() == r.b2 ) {
			textField5.setText("");
			r.frame.setVisible(false);
		}
	}

	public void slettAvtale() {
		int avtaleid = Integer.parseInt(alist.getSelectedValue().toString().substring(3, 7));
		String[] deltakere = textField7.getText().split(", ");
		eal.slettAvtale(avtaleid, this.bruker, deltakere, al);
	}

}
