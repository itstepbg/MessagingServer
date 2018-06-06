package models.data;

import java.util.Date;

public class Message {

	// Placed enum before all field declarations.
	// Changed the enum name as per the Java standard and added a field for the
	// status itself.
	private enum Status {
		RECEIVED, SEEN, DELETED
	}

	private Status status;

	private Date timestamp;

	private String text;

	// TODO Should all users be referenced by their ID instead of name?
	private String author;
	private String recipient;

	// Created initialization constructor.
	public Message(Date timestamp, String text, String author, String recipient) {
		super();
		this.timestamp = timestamp;
		this.text = text;
		this.author = author;
		this.recipient = recipient;
	}

	// Created field accessors
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
}
