package logikk;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.Database;


public class LoginLogic {
	
	private String pw;
	private String user;
	Database db;
	
	
	
	public LoginLogic(String username){
		db = new Database();
		setUser(username);
		setPw();
	}
	
	public boolean isTrue(String pw){
		if (pw.equals(this.pw)){
			return true;
		} else {
			return false;
		}
	}
	
	public void setUser(String username){
		ResultSet rs = db.readQuery("SELECT brukernavn FROM ansatt WHERE (brukernavn = '"+username+"')");
		try {
			if (rs.next()){
				this.user = username;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getUser(){
		return this.user;
	}
	
	public void setPw(){
		try {
			ResultSet rs = db.readQuery("SELECT passord FROM ansatt WHERE (brukernavn = '"+this.user+"')");
			if ( rs.next()){
				this.pw = rs.getString(1);
			} else {
				this.pw = "";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getPw(){
		return this.pw;
	}

	public boolean registrer(String bruker, String passord) {
		try {
			ResultSet test = db.readQuery("SELECT brukernavn FROM ansatt WHERE (brukernavn = '"+bruker+"')");
			if (test.next()){
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.updateQuery("INSERT INTO ansatt VALUES ('" + bruker + "','" + passord + "')");
		return true;
	}

	public void createNullAvtale() {
		ResultSet rs = db.readQuery("SELECT avtaleid FROM avtale WHERE avtaleid = 1");
		try {
			if (rs != null){
				rs.next();
				if (rs.getInt(1) != 1){
					db.updateQuery("INSERT INTO avtale VALUES (1, '11111111', '1111', '2222', 'Tom avtale for å binde slettevarsel', null, 'a', '"+this.user+"')");	
				}
			}
		} catch (SQLException e) {
			db.updateQuery("INSERT INTO avtale VALUES (1, '11111111', '1111', '2222', 'Tom avtale for å binde slettevarsel', null, 'a', '"+this.user+"')");
		}
	}
	
	public Database getDB() {
		return this.db;
	}
}
