package models.data;

import java.util.ArrayList;
import java.util.List;

public class ChatGroup {
	
	//TODO The id should be the server-provided and database-generated index (Long).
	private String id;

	private String name;

	//TODO Should all users be referenced by their ID instead of name?
	private List<String> users = new ArrayList<String>();
	private String host;

	private Boolean isPrivate;

	//TODO Change the enum name as per the Java standard and add a field for the type itself.
	private enum type {
		STANDARD, GLOBAL, LOBBY
	}
	
	//TODO Field accessors.
}
