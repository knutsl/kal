package logikk;

import gui.EndreAlarm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

import database.Database;

public class InboxLogic {

	private String bruker;
	private ArrayList<String> varsel = new ArrayList<String>();
	private ArrayList<String> alarm = new ArrayList<String>();
	Database db;
	EndreAlarm endrealarmbox;

	public InboxLogic(String bruker, Database db){
		this.bruker = bruker;
		this.db = db;
		findVarsel();
		findAlarm();
	}

	public void findVarsel(){
		ResultSet rs = db.readQuery("SELECT beskjed, avtaleid, tidspunkt, dato, brukernavn, varselid"
				+ " FROM ansatt NATURAL JOIN haravtale NATURAL JOIN varsel WHERE brukernavn = '"+this.bruker+"' "
						+ "ORDER BY varselid DESC");
		try {
			while (rs.next()){
				if (!(rs.getString(1).equals("alarm"))) {
					String varselid = Integer.toString(rs.getInt(6));
					while (varselid.length() < 4){
						varselid = "0"+varselid;
					}
					if (rs.getString(1).contains("Du ble invitert til ny avtale")){
						varsel.add(varselid+": Du ble invitert til avtale " +rs.getString(2) + ": Invitasjonen kom " + rs.getString(3)+" "+rs.getString(4).substring(0,2)+"."+rs.getString(4).substring(2,4)+"."+rs.getString(4).substring(4));
					} else if (rs.getString(1).contains("er slettet.")) {
						varsel.add(varselid+": "+rs.getString(1));
					} else if (rs.getString(1).contains("jainvitevarsel")) {
						varsel.add(varselid+": "+rs.getString(1).split(" ")[1]+" har svart ja til avtale "+rs.getInt(2));
					} else if (rs.getString(1).contains("neiinvitevarsel")) {
						varsel.add(varselid+": "+rs.getString(1).split(" ")[1]+" har svart nei til avtale "+rs.getInt(2));
					} else {
						varsel.add(varselid+": Endring i avtale" +rs.getString(2) + ": " + rs.getString(1) + " Endringen skjedde " + rs.getString(3)+" "+rs.getString(4).substring(0,2)+"."+rs.getString(4).substring(2,4)+"."+rs.getString(4).substring(4));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String[] getVarsel(){
		String[] varselstring = new String[this.varsel.size()];
		
		return this.varsel.toArray(varselstring);
	}

	public void findAlarm() {
		ResultSet rs = db.readQuery("SELECT * FROM haravtale WHERE brukernavn = '"+this.bruker+"'");
		try {
			int count = 0;
			while (rs.next()){
				int varselid = rs.getInt(3);
				int avtaleid = rs.getInt(1);
				ResultSet rsvarsel = db.readQuery("SELECT * FROM varsel WHERE varselid = "+varselid);
				ResultSet rsavtale = db.readQuery("SELECT * FROM avtale WHERE avtaleid = "+avtaleid);
				rsvarsel.next();
				rsavtale.next();
				String varselids = Integer.toString(varselid);
				while (varselids.length() < 4){
					varselids = "0"+varselids;
				}
				if (hasBeen(rsvarsel.getString(3), rsvarsel.getString(4)) && (rsvarsel.getString(2).equals("alarm"))){
					String dato = rsavtale.getString(2);
					String klokke = rsavtale.getString(3);
					alarm.add(varselids+": ALARM: AvtaleID "+avtaleid+" starter klokken "+klokke.substring(0, 2) + ":" + klokke.substring(2) +" ved dato "+ dato.substring(0,2) + "." + dato.substring(2,4) + "." + dato.substring(4));
					if (hasBeen(rsvarsel.getString(3), rsvarsel.getString(4)) && rsavtale.getObject(6) == null){
						alarm.set(count, alarm.get(count)+ " i "+ rsavtale.getString(7) + "\n");
					} else {
						alarm.set(count, alarm.get(count)+ " i rom nr"+ rsavtale.getString(6)+ "\n");
					}
					count++;
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean hasBeen(String tidspunkt, String dato) {
		DateTime tidno = new DateTime();
		int time = Integer.parseInt(tidspunkt.substring(0, 2));
		int minutt = Integer.parseInt(tidspunkt.substring(2, 4));
		int dag = Integer.parseInt(dato.substring(0, 2));
		int maaned = Integer.parseInt(dato.substring(2, 4));
		int aar = Integer.parseInt(dato.substring(4, 8));

		if (aar < (tidno.year().get())){
			return true;
		} else if (aar == (tidno.year().get())){
			if (maaned < tidno.monthOfYear().get()) {
				return true;
			} else if (maaned == tidno.monthOfYear().get()) {
				if (dag < tidno.dayOfMonth().get()) {
					return true;
				} else if (dag == tidno.dayOfMonth().get()){
					if (time < tidno.hourOfDay().get()) {
						return true;
					} else if (time == tidno.hourOfDay().get()) {
						if (minutt <= tidno.minuteOfHour().get()){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public String[] getAlarm(){
		String[] alarmstring = new String[this.alarm.size()];
		return this.alarm.toArray(alarmstring);
	}

	public void slettVarsel(String varselid) {
		db.updateQuery("DELETE FROM haravtale WHERE varselid = "+Integer.parseInt(varselid)+" AND brukernavn = '"+bruker+"'");
	}

	public String[] endreAlarm() {
		ArrayList<String> temp = new ArrayList<String>();
		ResultSet rs = db.readQuery("SELECT varselid, avtale.dato, starttid, brukernavn, beskjed FROM haravtale "
				+ "NATURAL JOIN avtale NATURAL JOIN varsel WHERE (brukernavn = '"+this.bruker+"' AND beskjed = 'alarm')");
		try {
			while (rs.next()){
				if ((rs.getString(5).equals("alarm")) && (!hasBeen(rs.getString(3), rs.getString(2)))){
					temp.add(rs.getString(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] AvtalerSomHarVaert = new String[temp.size()];
		temp.toArray(AvtalerSomHarVaert);
		return AvtalerSomHarVaert;
	}
}
