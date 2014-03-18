package logikk;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import database.Database;


public class SendEmail {

	private Database db;
	private static String USER_NAME = "fellesprosjekt44@gmail.com";  // GMail user name (just the part before "@gmail.com")
	private static String PASSWORD = "gruppe44"; // GMail password

	String from;
	String pass;

	public SendEmail(Database db) {
		this.db = db;
		from = USER_NAME;
		pass = PASSWORD;

	}

	protected void inviterEkstern(String[] participants, int avtaleID) {
		String[] to = participants; // list of recipient email addresses
		String subject = "Invitasjon til avtale: " + avtaleID;
		String body = "ERROR: Feil ved henting av avtale informasjon:";

		ResultSet rs = db.readQuery("SELECT dato, starttid, sluttid, sted, romid, beskrivelse "
				+ "FROM avtale "
				+ "WHERE avtaleid = "+avtaleID);
		try {
			rs.next();

			body = "Dato: " + rs.getString(1) + "\n Sted: " +rs.getString(4) +  "/romid: "
					+ rs.getString(5) + ", \n Start tid:  " + rs.getString(2) + "    Slutt tid: " + rs.getString(3) + " \n Beskrivelse: " + rs.getString(6);


		} catch (SQLException e) {
			e.printStackTrace();
		}

		sendFromGMail(from, pass, to, subject, body);
	}

	protected void varselEkstern(String[] participants, int avtaleID) {
		String[] to = participants; // list of recipient email addresses
		String subject = "Forandring i avtale: " + avtaleID;
		String body = "ERROR: Feil ved henting av avtale informasjon:";

		ResultSet rs = db.readQuery("SELECT dato, starttid, sluttid, sted, romid, beskrivelse "
				+ "FROM avtale "
				+ "WHERE avtaleid = "+avtaleID);
		try {
			rs.next();

			body = "Dato: " + rs.getString(1) + "\n Sted: " +rs.getString(4) +  "/romid: "
					+ rs.getString(5) + ", \n Start tid:  " + rs.getString(2) + "    Slutt tid: " + rs.getString(3) + " \n Beskrivelse: " + rs.getString(6);


		} catch (SQLException e) {
			e.printStackTrace();
		}

		sendFromGMail(from, pass, to, subject, body);
	}

	protected void slettEkstern(String[] participants, int avtaleID) {
		String[] to = participants; // list of recipient email addresses
		String subject = "Avtalen: " + avtaleID + "er kansellert";
		String body = "ERROR: Feil ved henting av avtale informasjon:";

		ResultSet rs = db.readQuery("SELECT dato, starttid, sluttid, sted, romid, beskrivelse "
				+ "FROM avtale "
				+ "WHERE avtaleID = '"+avtaleID+"'");
		try {
			rs.next();

			body = "Dato: " + rs.getString(1) + "\n Sted: " +rs.getString(4) +  "/romid: "
					+ rs.getString(5) + ", \n Start tid:  " + rs.getString(2) + "    Slutt tid: " + rs.getString(3) + " \n Beskrivelse: " + rs.getString(6);


		} catch (SQLException e) {
			e.printStackTrace();
		}

		sendFromGMail(from, pass, to, subject, body);
	}

	protected static void sendFromGMail(String from, String pass, String[] to, String subject, String body) {
		Properties props = System.getProperties();
		String host = "smtp.gmail.com";
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);

		try {
			ArrayList<String> something = new ArrayList<String>();
			for( int i = 0; i < to.length; i++ ) {
				if(to[i].contains("@")) {
					something.add(to[i]);
				}
			}

			if(something.size() > 0) {
				message.setFrom(new InternetAddress(from));
				InternetAddress[] toAddress = new InternetAddress[something.size()];

				// To get the array of addresses
				for( int i = 0; i < something.size(); i++ ) {
					toAddress[i] = new InternetAddress(something.get(i));
				}


				for( int i = 0; i < toAddress.length; i++) {
					message.addRecipient(Message.RecipientType.TO, toAddress[i]);
				}

				message.setSubject(subject);
				message.setText(body);
				Transport transport = session.getTransport("smtp");
				transport.connect(host, from, pass);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			}
		}
		catch (AddressException ae) {
			ae.printStackTrace();
		}
		catch (MessagingException me) {
			me.printStackTrace();
		}
	}
}
