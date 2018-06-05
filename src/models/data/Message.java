package models.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {

	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();
	private String timestamp = dtf.format(now);

	private String text;

	private String author;

	private String recipient;

	private enum status {
		RECEIVED, SEEN, DELETED
	}

}
