package models.data;

import java.util.ArrayList;
import java.util.List;

public class ChatGroup {
	private String id;

	private String name;

	private List<String> users = new ArrayList<String>();

	private String host;

	private Boolean isPrivate;

	private enum type {
		STANDARD, GLOBAL, LOBBY
	}
}
