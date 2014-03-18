package logikk;

import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import org.joda.time.DateTime;

import database.Database;

public class AvtaleLogic {

	Database db;
	SendEmail se;

	public AvtaleLogic(String bruker, Database db){
		this.db = db;
	}

	public boolean lagAvtale(String dato, String start, String slutt, String beskrivelse, Object romid, String sted, String[] deltakere, String ansatt, String[] eksternmail) {
		if (dato.length() != 8 || start.length() != 4){
			return false;
		} else if (Integer.parseInt(dato.substring(0, 2)) > 31 || Integer.parseInt(dato.substring(2, 4)) > 12){
			return false;
		} else if (Integer.parseInt(start.substring(2, 3)) > 5 || Integer.parseInt(start.substring(0, 2)) > 23){
			return false;
		}
		try {
			db.updateQuery("INSERT INTO avtale VALUES (null, '" + dato + "','" + start + "','" + slutt + "','" + beskrivelse + "'," + romid + ",'" + sted + "','" + ansatt + "')");

			int varseltid = Integer.parseInt(start)-100;
			String varselstring = Integer.toString(varseltid);
			while (varselstring.length() < 4){
				varselstring = "0" + varselstring;
			}

			ResultSet rs = db.readQuery("SELECT avtaleid FROM avtale ORDER BY avtaleid DESC");
			rs.next();
			int avtaleid = rs.getInt(1);
			int varselid;
			for(int i = 0; i < deltakere.length; i++) {
				db.updateQuery("INSERT INTO varsel VALUES (null, 'alarm', '"+varselstring+"', '"+dato+"')");
				ResultSet rs2 = db.readQuery("SELECT varselid FROM varsel ORDER BY varselid DESC");
				rs2.next();
				varselid = rs2.getInt(1);
				db.updateQuery("INSERT INTO erinviterttil VALUES ("+avtaleid+", '" + deltakere[i] + "', null, true)");
				db.updateQuery("INSERT INTO haravtale VALUES ("+avtaleid+", '"+ deltakere[i] +"', "+varselid+")");


				String[] datoklokke = datoklokke();
				String endring = "Du ble invitert til ny avtale av "+ansatt+".";
				db.updateQuery("INSERT INTO varsel VALUES (null, '"+endring+"', '"+datoklokke[0]+"', '"+datoklokke[1]+"')");
				ResultSet rs3 = db.readQuery("SELECT varselid FROM varsel ORDER BY varselid DESC");
				rs3.next();
				varselid = rs3.getInt(1);
				db.updateQuery("INSERT INTO haravtale VALUES ("+avtaleid+", '"+ deltakere[i] +"', "+varselid+")");
			}
			try {
				if (eksternmail.length > 0 && !eksternmail[0].equals("")) {
					se = new SendEmail(this.db);
					for (int i = 0; i < eksternmail.length; i++){
						if(eksternmail[i].contains("@")) {
							db.updateQuery("INSERT INTO eksternbruker VALUES ('"+eksternmail[i]+"', "+avtaleid+")");
						}
					}
					se.inviterEkstern(eksternmail, avtaleid);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}

	public String[] datoklokke(){
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

	public String[] getAnsatte(String bruker) {
		try {
			ResultSet rs = db.readQuery("SELECT brukernavn FROM ansatt WHERE brukernavn != '"+bruker+"'");
			ArrayList<String> ar = new ArrayList<String>();
			int counter = 0;

			while (rs.next()) {
				counter++;
				ar.add(rs.getString(1));
			}

			String[] sa = new String[ar.size()];
			return ar.toArray(sa);

		} catch (SQLException e) {
			return null;
		}

	}

	public String[] extractDeltakere (String bruker, String deltakere) {
		String[] temp = deltakere.split(", ");
		int count = 0;
		for (int i = 0; i<temp.length; i++){
			if (temp[i].equals(bruker)){
				count++;
			}
		}
		if (count == 0){
			deltakere = deltakere + bruker;
			String[] ansatte = deltakere.split(", ");
			return ansatte;
		} else{
			return temp;
		}
	}

	public void printAvtale(boolean success) {
		if (success) {
			Component frame = null;
			JOptionPane.showMessageDialog(frame,"Avtale opprettet!","Suksess",JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			Component frame = null;
			JOptionPane.showMessageDialog(frame,"Avtale ikke opprettet!","Feil",JOptionPane.WARNING_MESSAGE);
		}
	}

	private String bruker;
	private ArrayList<String> avtaler = new ArrayList<String>();


	public void getAvtaler(String bruker, String dato){
		this.bruker = bruker;

		ResultSet rs = db.readQuery("SELECT beskrivelse, starttid, sted, erinviterttil.brukernavn FROM avtale, haravtale WHERE erinviterttil.brukernavn = '"+this.bruker+"' and dato = '" + dato + "'" );
		try {
			while (rs.next()){
				avtaler.add("Avtaler for" +rs.getString(4) + ": Tid: " + rs.getString(2) + " Sted: " + rs.getString(3) + " Beskrivelse: " + rs.getString(1) + " skjedde ");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String[] getVarsel(){
		String[] avtalestring = new String[this.avtaler.size()];

		return this.avtaler.toArray(avtalestring);
	}

	public boolean textContains(String deltakere, String nybruker) {
		String[] deltakerliste = deltakere.split(", ");
		for (int i = 0; i < deltakerliste.length; i++){
			if(deltakerliste[i].equals(nybruker)){
				return true;
			}
		}
		return false;
	}

	public boolean sjekkPlass(String romid, int påmeldte) {
		try {
			ResultSet rs = db.readQuery("SELECT kapasitet FROM rom WHERE romid = " + romid);
			rs.next();
			return (rs.getInt(1) < påmeldte);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
