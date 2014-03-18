package gui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import logikk.SpringUtilities;

public class Menu extends JPanel{
	
	protected JButton lagavtale;
	protected JButton kalender;
	protected JButton inbox;
	protected JButton endreavtale;
	protected JButton loggut;
	protected JLabel bruker;
	
	public Menu(String bruker2){
		setSize(100, 400);
		setLayout(new SpringLayout());
		
		String[] labels = {"Lag Avtale", "Kalender", "Inbox", "Endre Avtale", "Logg ut"};
		
		bruker = new JLabel(bruker2 + "'s Kalender");
		add(bruker);
		
		lagavtale = new JButton(labels[0]);
		add(lagavtale);
		
		endreavtale = new JButton(labels[3]);
		add(endreavtale);
		
		kalender = new JButton(labels[1]);
		add(kalender);
		
		inbox = new JButton(labels[2]);
		add(inbox);
		
		loggut = new JButton(labels[4]);
		add(loggut);
		
		SpringUtilities.makeCompactGrid(this, 6, 1, 6, 6, 6, 6);
	}
		
}
	
