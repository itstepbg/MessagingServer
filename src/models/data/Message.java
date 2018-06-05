package models.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {

	//TODO This is not a behaviour that should be governed by the Message class.
	//The time stamp should be in the standard java Date format.
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();
	private DateTime timestamp = dtf.format(now);

	private String text;

	//TODO Should all users be referenced by their ID instead of name?
	private String author;
	private String recipient;

	//TODO Change the enum name as per the Java standard and add a field for the status itself.
	private enum status {
		RECEIVED, SEEN, DELETED
	}

	//TODO Field accessors.
}
