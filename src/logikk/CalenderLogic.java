package logikk;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ListModel;

import org.joda.time.DateTime;

import database.Database;

public class CalenderLogic {
	int startDay;
	Database db;
	String bruker;
	
	public CalenderLogic(Database db, String bruker) {
		this.bruker = bruker;
		this.db = db;
	}


	public int daysInMonth(int year, int month) {
		int days;
		switch (month) {
		case 1:
			days = 31;
			break;
		case 2:
			if (isLeapYear(year) == true)
				days = 29;
			else
				days = 28;
			break;
		case 3:
			days = 31;
			break;
		case 4:
			days = 30;
			break;
		case 5:
			days = 31;
			break;
		case 6:
			days = 30;
			break;
		case 7:
			days = 31;
			break;
		case 8:
			days = 31;
			break;
		case 9:
			days = 30;
			break;
		case 10:
			days = 31;
			break;
		case 11:
			days = 30;
			break;
		case 12:
			days = 31;
			break;

		default:
			days = 0;
		}
		return days;

	}

	public int getMonthStartDay(int year, int month) {
		if (month == 1)
			startDay = getYearStartDay(year);
		else if (month < 1 || month > 12)
			startDay = 0;
		else {
			startDay = getMonthStartDay(year,month-1) + (daysInMonth(year, month-1)%7);
			if (startDay > 7)
				startDay = startDay - 7;
		}
		return startDay;
	}

	public int getYearStartDay(int year) {
		int diff = (year - 1900);
		startDay = (diff%7) + 1;


		for (int i = 1900; i < year + 1; i++) {
			int x = i - 1;
			if (isLeapYear(x) == true) {
				startDay = startDay + 1;
			}
			startDay = (startDay%7);
			if (startDay == 0) {
				startDay = 7;
			}
		}
		return startDay;
	}

	public int[][] getCalender(int month, int year) {
		startDay = getMonthStartDay(year, month);
		startDay = startDay - 1;
		int numDays = daysInMonth(year, month);

		int kalender[][] = new int[6][7];
		int date = 0;

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				if (date == 0) {
					if (i < 1 && j == startDay) {
						date = 1;
					}
				}
				else {
					if (date < numDays)
						date = date + 1;
					else
						date = 0;
				}
				kalender[i][j] = date;
			}
		}
		return kalender;            

	}
	
	public int getMonth(){
		DateTime tid = new DateTime();
		return tid.getMonthOfYear();
	}
	
	public int getYear() {
		DateTime tid = new DateTime();
		return tid.getYear();
	}

	
	// er ikke benyttet for øyeblikket...!!!
	public String getMonthName(int month) {	
		String monthString;

		switch (month) {
		case 1:  monthString = "January";
		break;
		case 2:  monthString = "February";
		break;
		case 3:  monthString = "March";
		break;
		case 4:  monthString = "April";
		break;
		case 5:  monthString = "May";
		break;
		case 6:  monthString = "June";
		break;
		case 7:  monthString = "July";
		break;
		case 8:  monthString = "August";
		break;
		case 9:  monthString = "September";
		break;
		case 10: monthString = "October";
		break;
		case 11: monthString = "November";
		break;
		case 12: monthString = "December";
		break;
		default: monthString = "Invalid month";
		break;
		}
		return monthString;
	}

	public boolean isLeapYear(int year) {
		boolean skuddaar = false;
		if ((year%4) != 0) {
			skuddaar = false;
		}
		else if ((year%400) == 0) {
			skuddaar = true;
		}
		else if((year%4) == 0) {

			if ((year%100) == 0){
				skuddaar = false;
			}
			else {
				skuddaar = true;
			}
		}
		return skuddaar;
	}

	public String[] getDineAvtaler(int d, int m, int a) {
		String dato = datoAsString(d, m, a);
		ArrayList<String> temp = new ArrayList<String>();
		ResultSet rs = db.readQuery("SELECT avtaleid, dato, starttid, sluttid, beskrivelse, romid, sted, kommer "
				+ "FROM avtale NATURAL JOIN erinviterttil "
				+ "WHERE synlig = 1 AND dato = '"+dato+"' AND brukernavn = '"+ bruker + "'");
		try {
			int count = 0;
			while (rs.next()){
				String avtaleid = Integer.toString(rs.getInt(1));
				while (avtaleid.length() < 4){
					avtaleid = "0"+avtaleid;
				}
				temp.add(avtaleid + ": " + rs.getString(2).substring(0, 2)+"."+rs.getString(2).substring(2, 4)+ "."
						+ rs.getString(2).substring(4)+ " " + rs.getString(3)+" - "+rs.getString(4)+": "+rs.getString(5)+ " i ");
				if (rs.getString(6) == null){
					temp.set(count, temp.get(count) + rs.getString(7) + ".");
				} else {
					temp.set(count, temp.get(count) + "romnr " + rs.getInt(6) + ".");
				}
				if (rs.getString(8) == null){
					temp.set(count, temp.get(count) + " Du har ikke svart på invitasjonen.");
				} else if (rs.getBoolean(8)) {
					temp.set(count, temp.get(count) + " Du har svart ja.");
				} else {
					temp.set(count, temp.get(count) + " Du har svart nei.");
				}
				count++;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] dineavtaler = new String[temp.size()];
		temp.toArray(dineavtaler);
		return dineavtaler;
	}

	public String[] getAlleAvtaler(int d, int m, int a) {
		String dato = datoAsString(d, m, a);
		ArrayList<String> temp = new ArrayList<String>();
		ArrayList<Integer> ja = new ArrayList<Integer>();
		ArrayList<Integer> nei = new ArrayList<Integer>();
		ArrayList<Integer> ikkesvart = new ArrayList<Integer>();
		ResultSet rs = db.readQuery("SELECT avtaleid, dato, starttid, sluttid, beskrivelse, romid, sted, kommer "
				+ "FROM avtale NATURAL JOIN erinviterttil "
				+ "WHERE dato = '"+dato+"' ORDER BY avtaleid");
		try {
			int count = -1;
			while (rs.next()){
				String avtaleid = Integer.toString(rs.getInt(1));
				while (avtaleid.length() < 4){
					avtaleid = "0"+avtaleid;
				}
				if (temp.size() == 0 || !(avtaleid.equals(temp.get(count).substring(0, 4)))){
					count++;
					temp.add(avtaleid + ": " + rs.getString(2).substring(0, 2)+"."+rs.getString(2).substring(2, 4)+ "."
							+ rs.getString(2).substring(4)+ " " + rs.getString(3)+" - "+rs.getString(4)+": "+rs.getString(5)+ " i ");
					ikkesvart.add(0);
					ja.add(0);
					nei.add(0);
					if (rs.getString(6) == null){
						temp.set(count, temp.get(count) + rs.getString(7) + ". ");
					} else {
						temp.set(count, temp.get(count) +"romnr "+ rs.getInt(6) + ". ");
					}
				}
				if (rs.getString(8) == null){
					ikkesvart.set(count, ikkesvart.get(count)+1);
				} else if (rs.getBoolean(8)) {
					ja.set(count, ja.get(count)+1);
				} else {
					nei.set(count, nei.get(count)+1);
				}
			}
			for (int i = 0; i < temp.size(); i++){
				temp.set(i, temp.get(i) + ja.get(i) +" har svart ja, "+nei.get(i)+" har svart nei, "+ ikkesvart.get(i)+"har ikke svart.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] alleavtaler = new String[temp.size()];
		temp.toArray(alleavtaler);
		return alleavtaler;
	}
	
	public String datoAsString(int d, int m, int a) {
		String dag = Integer.toString(d);
		String maaned = Integer.toString(m);
		String aar = Integer.toString(a);
		if (dag.length() < 2){
			dag = "0"+dag;
		}
		if (maaned.length() < 2){
			maaned = "0"+maaned;
		}
		while (aar.length() < 4){
			aar = "0"+aar;
		}
		return dag + maaned + aar;
	}


	public boolean harAvtale(int day, int maaned, int aar) {
		ResultSet rs = db.readQuery("SELECT count(avtaleid) FROM avtale NATURAL JOIN erinviterttil"
				+ " WHERE dato = '"+datoAsString(day, maaned, aar)+"' AND brukernavn = '"+this.bruker+"' "
						+ "AND synlig = 1");
		int count = 0;
		try {
			rs.next();
			count = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			return true;
		}
		return false;
	}


	public boolean harAnsattAvtale(int day, int maaned, int aar) {
		ResultSet rs = db.readQuery("SELECT count(avtaleid) FROM avtale WHERE dato = '"+datoAsString(day, maaned, aar)+"'");
		int count = 0;
		try {
			rs.next();
			count = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			return true;
		}
		return false;
	}
}
