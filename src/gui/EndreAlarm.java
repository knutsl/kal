package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import logikk.EndreAlarmLogic;
import logikk.SpringUtilities;

public class EndreAlarm extends JFrame{
	
	JPanel pane1;
	JPanel pane2;
	JButton but;
	JLabel labe;
	JLabel head;
	JLabel dato;
	JLabel tidspunkt;
	JLabel beskrivelse;
	JLabel sted;
	JLabel alarmtid;
	JTextField datotf;
	JTextField tidspunkttf;
	JTextField beskrivelsetf;
	JTextField stedtf;
	public JTextField alarmtidtf;
	JList alarmliste;
	EndreAlarmLogic eal;
	JLabel tom;
	String alarmid;
	String[] info;
	
	public EndreAlarm(String[] varselid, final EndreAlarmLogic eal){
		this.setSize(500, 350);
		this.setTitle("Endre Alarm");
		pane1 = new JPanel(new SpringLayout());
		pane2 = new JPanel(new SpringLayout());
		this.add(pane1, BorderLayout.NORTH);
		this.add(pane2, BorderLayout.CENTER);
		
		this.eal = eal;
		
		JLabel labe = new JLabel("Alarmer");
		pane1.add(labe);
		
		String[] alarmer = eal.getAlarmer(varselid);
		alarmliste = new JList(alarmer);	
		JScrollPane listScroller = new JScrollPane(alarmliste);
		listScroller.setPreferredSize(new Dimension(300, 80));
		pane1.add(listScroller);
		
		alarmliste.addListSelectionListener(new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				alarmid = alarmliste.getSelectedValue().toString().substring(0, 4);
				info = eal.getInfo(alarmid);
				datotf.setText(info[0].substring(0, 2)+"."+info[0].substring(2, 4)+"."+info[0].substring(4));
				tidspunkttf.setText(info[1]);
				beskrivelsetf.setText(info[2]);
				stedtf.setText(info[3]);
				alarmtidtf.setText(info[4]);
			}
		}

	});
		
		head = new JLabel("Avtale");
		pane1.add(head);
		
		dato = new JLabel("Dato:");
		pane2.add(dato);
		
		datotf = new JTextField();
		pane2.add(datotf);
		datotf.setEditable(false);
		
		tidspunkt = new JLabel("Tidspunkt:");
		pane2.add(tidspunkt);
		
		tidspunkttf = new JTextField();
		pane2.add(tidspunkttf);
		tidspunkttf.setEditable(false);
		
		beskrivelse = new JLabel("Beskrivelse:");
		pane2.add(beskrivelse);
		
		beskrivelsetf = new JTextField();
		pane2.add(beskrivelsetf);
		beskrivelsetf.setEditable(false);
		
		sted = new JLabel("Sted:");
		pane2.add(sted);
		
		stedtf = new JTextField();
		pane2.add(stedtf);
		stedtf.setEditable(false);
		
		alarmtid = new JLabel("Alarm starter:");
		pane2.add(alarmtid);
		
		alarmtidtf = new JTextField();
		pane2.add(alarmtidtf);
		
		but = new JButton("OK");
		pane2.add(but);
		
		tom = new JLabel();
		pane2.add(tom);
		
		SpringUtilities.makeCompactGrid(pane1,
				3, 1, 		 //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad
		
		SpringUtilities.makeCompactGrid(pane2,
				6, 2, 		 //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad
		
		this.setVisible(true);
		this.setResizable(true);
	}
}
