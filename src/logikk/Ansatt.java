package logikk;

import java.util.Scanner;

public class Ansatt {

	Scanner scanner1 = new Scanner( System.in );
	public String brukernavn;
	public String passord;
	public String fornavn;
	public String etternavn;
	public boolean loggetInn;

	public Ansatt(String brukernavn, String passord, String fornavn,
			String etternavn) {
		this.brukernavn = brukernavn;
		this.passord = passord;
		this.fornavn = fornavn;
		this.etternavn = etternavn;
	}

	public String getFornavn() {
		return fornavn;
	}
	
	public void setFornavn(String fornavn) {
		this.fornavn = fornavn;
	}
	
	public String getEtternavn() {
		return etternavn;
	}
	
	public void setEtternavn(String etternavn) {
		this.etternavn = etternavn;
	}
	
	public String getBrukernavn() {
		return brukernavn;
	}
	public void setBrukernavn(String brukernavn) {
		this.brukernavn = brukernavn;
	}
	public String getPassord() {
		return passord;
	}
	public void setPassord(String passord) {
		this.passord = passord;
	}
	
	public boolean isLoggetInn() {
		return loggetInn;
	}

	public void setLoggetInn(boolean loggetInn) {
		this.loggetInn = loggetInn;
	}
	
}
