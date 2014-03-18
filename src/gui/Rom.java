package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import logikk.SpringUtilities;


public class Rom extends JFrame {

	protected JList j;
	protected JFrame frame;
	RomLogikk rl;
	JButton b1;
	JButton b2;

	public Rom(String dato, String starttid, String sluttid, int deltakere) {

		JPanel p = new JPanel(new SpringLayout());

		rl = new RomLogikk();

		String[] ledige = rl.finnLedige(dato, starttid, sluttid, deltakere);

		j = new JList(ledige);	
		JScrollPane listScroller = new JScrollPane(j);
		listScroller.setPreferredSize(new Dimension(300, 80));
		p.add(listScroller);

		b1 = new JButton("Godkjenn");
		p.add(b1);
		
		b2 = new JButton("Fjern valg");
		p.add(b2);

		SpringUtilities.makeCompactGrid(p,
				3, 1, 		 //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		frame = new JFrame("Velg Rom");
		p.setOpaque(true); 
		frame.setContentPane(p);
		frame.pack();
		frame.setVisible(true);
	}


}
