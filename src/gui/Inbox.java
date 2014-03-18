package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import logikk.InboxLogic;
import logikk.SpringUtilities;

public class Inbox extends JPanel {
	
	ArrayList<String> varsel = new ArrayList<String>();
	ArrayList<String> alarm = new ArrayList<String>();
	InboxLogic il;
	JList varselvis;
	JList alarmvis;
	JLabel alarmlabe;
	JLabel varsellabe;
	JPanel buts;
	JButton slettvarsel;
	JButton endrealarm;
	
	public Inbox(InboxLogic il){
		setLayout(new SpringLayout());
		setSize(400,400);
		
		this.il = il;
//		varsel = il.getVarsel();
//		alarm = il.getAlarm();
		
		varsellabe = new JLabel("Varsel");
		this.add(varsellabe);
		
		String[] alarmlist = il.getAlarm();
		String[] varsellist = il.getVarsel();
		
		varselvis = new JList(varsellist);
		JScrollPane scrollvarsel = new JScrollPane(varselvis);
		scrollvarsel.setPreferredSize(new Dimension(400,100));
		this.add(scrollvarsel);
		
		alarmlabe = new JLabel("Alarmer");
		this.add(alarmlabe);
		
		alarmvis = new JList(alarmlist);
		JScrollPane scrollalarm = new JScrollPane(alarmvis);
		scrollalarm.setPreferredSize(new Dimension(400,100));
		this.add(scrollalarm);
		
		buts = new JPanel(new SpringLayout());
		buts.setSize(400, 100);
		buts.setVisible(true);
		this.add(buts);
		
		slettvarsel = new JButton("Slett Varsel");
		buts.add(slettvarsel);
		
		endrealarm = new JButton("Endre Alarm");
		buts.add(endrealarm);
		
		
		SpringUtilities.makeCompactGrid(buts,
				1, 2, 		 //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad
		
		
//		for (int i = 0; i<varsel.size();i++){
//			varselvis.append(varsel.get(i));
//		}
//		for (int i = 0; i<alarm.size();i++){
//			alarmvis.append(alarm.get(i));
//		}
		SpringUtilities.makeCompactGrid(this,
				5, 1, 		 //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad
	}
}
