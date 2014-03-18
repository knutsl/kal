package gui;

import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.joda.time.DateTime;

import database.Database;

public class InviteSvarLogic {

	Database db;
	String bruker;
	int avtaleid;
	
	public InviteSvarLogic(String bruker, String avtaleid, Database db){
		this.db = db;
		this.bruker = bruker;
		this.avtaleid = Integer.parseInt(avtaleid);
	}
	
	public String getDeltakere(String d){
		String[] deltakere = d.split(", ");
		String dms = "";
		int n = 1;
		for (String deltaker : deltakere){
			ResultSet rs = db.readQuery("SELECT kommer, brukernavn, avtaleid FROM erinviterttil WHERE brukernavn = '"+deltaker+"' "
					+ "AND avtaleid = "+this.avtaleid);
			try {
				rs.next();
				if (rs.getObject(1) == null){
					dms = dms + deltaker + " har ikke svart, ";
				} else if (!rs.getBoolean(1)){
					dms = dms + deltaker + " kommer ikke, ";
				} else {
					dms = dms + deltaker + " kommer, ";
				}
				if (dms.length() > 70*n) {
					dms = dms + "\n";
					n++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dms;
	}

	public void synlig(boolean b, int id) {
		if (b){
			db.updateQuery("UPDATE erinviterttil SET synlig = true WHERE avtaleid = "+id+" AND brukernavn = '"+this.bruker+"'");
		} else {
			ResultSet rs = db.readQuery("SELECT kommer FROM erinviterttil WHERE brukernavn = '"+this.bruker+"' "
					+ "AND avtaleid = "+id);
			try {
				rs.next();
				if(!rs.getBoolean(1)){
					db.updateQuery("UPDATE erinviterttil SET synlig = false WHERE avtaleid = "+id+" AND brukernavn = '"+this.bruker+"'");
					Component frame = null;
					JOptionPane.showMessageDialog(frame,"Avtalen er nå skjult.","Success",JOptionPane.INFORMATION_MESSAGE);
				} else {
					Component frame = null;
					JOptionPane.showMessageDialog(frame,"Kunne ikke skjule avtale., \n Du må avslå avtalen for å skjule den.","Feil",JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void kommer(boolean b, int id) {
		if (b) {
			String[] tid = datoklokke();
			db.updateQuery("UPDATE erinviterttil SET kommer = true WHERE avtaleid = "+id+" AND brukernavn = '"+this.bruker+"'");
			db.updateQuery("INSERT INTO varsel VALUES (null, 'jainvitevarsel "+this.bruker+"', '"+tid[0]+"', '"+tid[1]+"')");
			Component frame = null;
			JOptionPane.showMessageDialog(frame,"Du har nå svart ja på invitasjonen.","Svar",JOptionPane.INFORMATION_MESSAGE);
		} else {
			String[] tid = datoklokke();
			db.updateQuery("UPDATE erinviterttil SET kommer = false WHERE avtaleid = "+id+" AND brukernavn = '"+this.bruker+"'");
			db.updateQuery("INSERT INTO varsel VALUES (null, 'neiinvitevarsel "+this.bruker+"', '"+tid[0]+"', '"+tid[1]+"')");
			Component frame = null;
			JOptionPane.showMessageDialog(frame,"Du har nå svart nei på invitasjonen","Svar",JOptionPane.INFORMATION_MESSAGE);
		}
		ResultSet rs = db.readQuery("SELECT varselid FROM varsel ORDER by varselid DESC");
		ResultSet rs2 = db.readQuery("SELECT opprettetav FROM avtale WHERE avtaleid = "+id);
		try {
			rs.next();
			rs2.next();
			db.updateQuery("INSERT INTO haravtale VALUES ("+id+", '"+rs2.getString(1)+"', "+rs.getInt(1)+")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String[] datoklokke() {
		DateTime dtg = new DateTime();
		String time = Integer.toString(dtg.getHourOfDay());
		if (dtg.getHourOfDay()<10){
			time = "0"+time;
		}
		String minutt = Integer.toString(dtg.getMinuteOfHour());
		if (dtg.getMinuteOfHour() < 10){
			minutt = "0"+minutt;
		}
		String dag = Integer.toString(dtg.getDayOfMonth());
		if (dtg.getDayOfMonth()<10){
			dag = "0"+dag;
		}
		String maaned = Integer.toString(dtg.getMonthOfYear());
		if (dtg.getMonthOfYear() < 10){
			maaned = "0"+maaned;
		}
		String aar = Integer.toString(dtg.getYear());
		if (dtg.getYear() < 1000){
			aar = "0"+aar;
			if(dtg.getYear() < 100){
				aar = "0"+aar;
				if (dtg.getYear() < 10){
					aar = "0"+aar;
				}
			}
		}
		String[] klokkedato = {time+minutt, dag+maaned+aar};
		return klokkedato;
	}
}
