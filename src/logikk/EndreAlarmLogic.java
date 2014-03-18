package logikk;

import gui.EndreAlarm;

import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import database.Database;

public class EndreAlarmLogic {

	Database db;
	EndreAlarm ea;
	
	
	public EndreAlarmLogic(Database db){
		this.db = db;
	}

	public String[] getAlarmer(String[] varselid) {
		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < varselid.length; i++){
			String[] info = getInfo(varselid[i]);
			temp.add(info[5] + ": " + info[0].substring(0, 2)+"."+info[0].substring(2, 4)+"."+info[0].substring(4, 8)+", klokka "+info[1]
					+": "+info[2]+", "+ info[3] +". Alarm starter "+info[4]);
		}
		String[] alarmer = new String[temp.size()];
		temp.toArray(alarmer);
		return alarmer;
	}

	public String[] getInfo(String varselid) {
		ResultSet rs = db.readQuery("SELECT avtale.dato, starttid, beskrivelse, romid, sted, varsel.tidspunkt, varselid, varsel.dato "
				+ "FROM varsel NATURAL JOIN haravtale NATURAL JOIN avtale "
				+ "WHERE varselid = "+Integer.parseInt(varselid));
		String[] info = new String[7];
		try {
			rs.next();
			info[0] = rs.getString(1);
			info[1] = rs.getString(2);
			info[2] = rs.getString(3);
			if (rs.getObject(4)==null){
				info[3] = rs.getString(5);
			} else {
				info[3] = rs.getString(4);
			}
			info[4] = rs.getString(6);
			info[5] = rs.getString(7);
			while (info[5].length()<4) {
				info[5] = "0"+info[5];
			}
			info[6] = rs.getString(8);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return info;
	}

	public void updateAlarm(String alarm, String id, String alarmdato) {
		ResultSet rs = db.readQuery("SELECT avtaleid, brukernavn FROM haravtale WHERE varselid = "+id);
		int avtaleid = 0;
		String bruker = "";
		try {
			rs.next();
			avtaleid = avtaleid + rs.getInt(1);
			bruker = bruker + rs.getString(2);
		} catch (SQLException e) {
			Component frame = null;
			JOptionPane.showMessageDialog(frame, "Du må velge en avtale.", "Advarsel!", JOptionPane.INFORMATION_MESSAGE);
		}
		db.updateQuery("UPDATE varsel SET tidspunkt = '"+alarm+"', dato = '"+alarmdato+"' WHERE varselid = "+id);
	}
	
}
