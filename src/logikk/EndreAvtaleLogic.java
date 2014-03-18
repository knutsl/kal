package logikk;

import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.joda.time.DateTime;

import database.Database;

public class EndreAvtaleLogic {

	Database db = new Database();
	AvtaleLogic al;
	SendEmail se;

	public boolean endreAvtale(int avtaleid, String dato, String start,
			String slutt, String beskrivelse, Object romid, String sted,
			String ansatt, String[] deltakere, String[] eksterne){

		al = new AvtaleLogic(ansatt, db);
		
		try {
			ResultSet rs = db.readQuery("SELECT opprettetav FROM avtale WHERE avtaleid =" + avtaleid);
			rs.next();
			String bruker = rs.getString(1);
			
			String[] tid = al.datoklokke();
			
			String varseldato = tid[1];
			String varselstring = tid[0];
			int alarmtid = Integer.parseInt(start)-100;
			String alarmstring = Integer.toString(alarmtid);
			while (alarmstring.length() < 4){
				alarmstring = "0" + alarmstring;
			}
			while (varselstring.length() < 4) {
				varselstring = "0" + varselstring;
			}
			
			if(bruker.equals(ansatt)){ 
// oppdaterer avtalen i databasen dersom bruker har lov. Sletter derette midlertidig alle inviterte fra erinviterttil.
				db.updateQuery("UPDATE avtale "
						+ "SET dato = '" + dato + "', starttid = '" + start + "', sluttid = '" 
						+ slutt + "', beskrivelse = '" + beskrivelse + "', romid = " + romid + ", sted = '" + sted + "'"
						+ " WHERE avtaleid =" + avtaleid );
				db.updateQuery("DELETE FROM erinviterttil WHERE avtaleid = "+avtaleid);
				
// finner alle alarm-varsel-ideer for å kunne fjerne varsel og ansatt fra varsel- og haravtale-tabellene.
				ResultSet rs2 = db.readQuery("SELECT varselid, brukernavn, avtaleid "
						+ "FROM haravtale NATURAL JOIN varsel WHERE avtaleid = "+avtaleid+" AND beskjed = 'alarm'");
				
// alle varsler som slettes vil også slette sin rad i haravtale pga on delete cascade. Tar vare på id'ene som slettes for å
//	bruke de igjen.
				ArrayList<Integer> alarmid = new ArrayList<Integer>();
				while(rs2.next()){
					db.updateQuery("DELETE FROM varsel WHERE varselid = "+rs2.getInt(1));
					alarmid.add(rs2.getInt(1));
				}
				for (int i= 0; i < deltakere.length; i++){
					db.updateQuery("INSERT INTO erinviterttil VALUES ("+avtaleid+", '"+deltakere[i]+"', null, true)");
					if (alarmid.size() > 0){
						db.updateQuery("INSERT INTO varsel VALUES ("+alarmid.get(0)+", 'alarm', '"+alarmstring+"', '"+dato+"')");
						db.updateQuery("INSERT INTO haravtale VALUES ("+avtaleid+", '"+ deltakere[i] +"', "+alarmid.get(0)+")");
						alarmid.remove(0);
					} else {
						db.updateQuery("INSERT INTO varsel VALUES (null, 'alarm', '"+alarmstring+"', '"+dato+"')");
						ResultSet rs3 = db.readQuery("SELECT varselid FROM varsel ORDER BY varselid DESC");
						rs3.next();
						int varselid = rs3.getInt(1);
						db.updateQuery("INSERT INTO haravtale VALUES ("+avtaleid+", '"+ deltakere[i] +"', "+varselid+")");
					}
					
					if (alarmid.size() > 0){
						db.updateQuery("INSERT INTO varsel VALUES ("+alarmid.get(0)+", 'Ending i avtaleID "+avtaleid+"', '"+varselstring+"', '"+varseldato+"')");
						db.updateQuery("INSERT INTO haravtale VALUES ("+avtaleid+", '"+ deltakere[i] +"', "+alarmid.get(0)+")");
						alarmid.remove(0);
					} else {
						db.updateQuery("INSERT INTO varsel VALUES (null, 'Ending i avtaleID "+avtaleid+"', '"+varselstring+"', '"+varseldato+"')");
						ResultSet rs3 = db.readQuery("SELECT varselid FROM varsel ORDER BY varselid DESC");
						rs3.next();
						int varselid = rs3.getInt(1);
						db.updateQuery("INSERT INTO haravtale VALUES ("+avtaleid+", '"+ deltakere[i] +"', "+varselid+")");
					}
				}
				ResultSet rs3 = db.readQuery("SELECT email FROM eksternbruker WHERE inviterttil = "+avtaleid);

				ArrayList<String> e = new ArrayList<String>();
				try {
					se = new SendEmail(this.db);
					ArrayList<String> em = new ArrayList<String>();
					for (String mail : eksterne) {
						e.add(mail);
					}
					while (rs3.next()){
							em.add(rs3.getString(1));
							for (int i = 0; i < e.size(); i++){
								if (e.get(i).equals(rs3.getString(1))){
									e.remove(i);
								}
							}
					}
					String[] eksm = new String[em.size()];
					se.varselEkstern(em.toArray(eksm), avtaleid);
				} catch (Exception ex){
					ex.printStackTrace();
				}
				try {
					String[] eksternliste = new String[e.size()];
					e.toArray(eksternliste);
					if (eksternliste.length > 0 && !eksternliste[0].equals("")) {
						se = new SendEmail(this.db);
						for (int i = 0; i < eksternliste.length; i++){
							db.updateQuery("INSERT INTO eksternbruker VALUES ('"+eksternliste[i]+"', "+avtaleid+")");
						}
						se.inviterEkstern(eksternliste, avtaleid);
					}
				return true;
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
			} catch (Exception e) {
				return false;
			}
		return false;
		
	}

	public void slettAvtale(int avtaleid, String bruker, String[] deltakere, AvtaleLogic al) {
		try {
			ResultSet rs = db.readQuery("SELECT opprettetav, dato, starttid, beskrivelse, avtaleid FROM avtale WHERE avtaleid =" + avtaleid);
			rs.next();
			String bruker2 = rs.getString(1);
			String dato = rs.getString(2).substring(0, 2) + "." + rs.getString(2).substring(2, 4)+"."+rs.getString(2).substring(4);
			

			if(bruker2.equals(bruker)){ 
				ResultSet rs2 = db.readQuery("SELECT varselid, avtaleid FROM varsel NATURAL JOIN haravtale WHERE avtaleid = "+avtaleid);
				ArrayList<Integer> temp = new ArrayList<Integer>();
				while (rs2.next()) {
					temp.add(rs2.getInt(1));
					db.updateQuery("DELETE FROM varsel WHERE varselid = "+rs2.getInt(1));
				}
				db.updateQuery("DELETE FROM avtale WHERE avtaleid = " + avtaleid);
				try{
					if (temp.size() > 0){
						db.updateQuery("INSERT INTO varsel VALUES ("+temp.get(0)+", 'AvtaleID "+avtaleid+" "+rs.getString(4)+", som skulle"
								+ " være "+dato+" klokken "+rs.getString(3)+" er slettet.', '"+al.datoklokke()[0]+"',"
								+ " '"+al.datoklokke()[1]+"')");
					} else {
						db.updateQuery("INSERT INTO varsel VALUES (null, 'AvtaleID "+avtaleid+" "+rs.getString(4)+", som skulle"
								+ " være "+dato+" klokken "+rs.getString(3)+" er slettet.', '"+al.datoklokke()[0]+"',"
								+ " '"+al.datoklokke()[1]+"')");
					}
					ResultSet rs3 = db.readQuery("SELECT varselid FROM varsel ORDER BY varselid DESC");
					rs3.next();
						for (String user : deltakere){
							if (temp.size() > 0){
								db.updateQuery("INSERT INTO haravtale VALUES (1, '"+user+"', "+temp.get(0)+")");
								temp.clear();
							} else {
								db.updateQuery("INSERT INTO haravtale VALUES (1, '"+user+"', "+rs3.getInt(1)+")");
							}
						}
						ResultSet rs4 = db.readQuery("SELECT email FROM eksternbruker WHERE inviterttil = "+avtaleid);
						ArrayList<String> e = new ArrayList<String>();
						while (rs4.next()){
							e.add(rs4.getString(1));
						}
						if (e.size() > 0){
							String[] eksterne = new String[e.size()];
							e.toArray(eksterne);
							se.slettEkstern(eksterne, avtaleid);
						}
						Component frame = null;
						JOptionPane.showMessageDialog(frame,"Avtale slettet!","Suksess",JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
					Component frame = null;
					JOptionPane.showMessageDialog(frame,"Avtalen ble slettet, \n men varsel kom ikke fra til alle deltakere.","Feil",JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				Component frame = null;
			JOptionPane.showMessageDialog(frame,"Avtale ikke slettet! Avtalen ble opprettet av noen andre.","Feil",JOptionPane.WARNING_MESSAGE);
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("HEI");
			Component frame = null;
			JOptionPane.showMessageDialog(frame,"Avtale ikke slettet! Feil avtaleid.","Feil",JOptionPane.WARNING_MESSAGE);
		}
	}

	public void printAvtale(boolean success) {
		if (success) {
			Component frame = null;
			JOptionPane.showMessageDialog(frame,"Avtale endret!","Suksess",JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			Component frame = null;
			JOptionPane.showMessageDialog(frame,"Avtale ikke endret!","Feil",JOptionPane.WARNING_MESSAGE);
		}
	}

	public String[] getAvtaler(String bruker) {
		ResultSet rs = db.readQuery("SELECT * FROM avtale WHERE opprettetav = '"+bruker+"' AND avtaleid != 1");
		ArrayList<String> tempd = new ArrayList<String>();
		try {
			while (rs.next()){
				String ID = Integer.toString(rs.getInt(1));
				while (ID.length()<4){
					ID = "0"+ID;
				} 
				tempd.add("ID:"+ID+" dato:"+rs.getString(2)+" tid:"+rs.getString(3)+" beskrivelse: '"+rs.getString(5)+"'\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] avtaler = new String[tempd.size()];
		return tempd.toArray(avtaler);
	}

	public ArrayList<String> getInfo(String id) {
		ResultSet rs = db.readQuery("SELECT * FROM avtale NATURAL JOIN erinviterttil WHERE avtaleid = "+id);
		ArrayList<String> info = new ArrayList<String>();
		
		if (id != "1"){
			try {
				rs.next();
				for (int i = 2; i<8; i++){
					if (i==6){
						if (rs.getObject(i) == null){
							info.add(null);
						} else {
							info.add(Integer.toString(rs.getInt(i)));
						}
					} else {
						info.add(rs.getString(i));
					}
				}
				info.add(rs.getString(9));
				info.add(rs.getString(8));
				while (rs.next()){
					info.set(6, info.get(6)+", "+rs.getString(9));
				}


			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return info;
	}



}
