package network;

public class NetworkMessage {
	private MessageType type;
	private String actor;
	private String user;
	private Long groupId;
	private String text;
	private String passwordHash;
	private String email;
	private int status;
	
	public NetworkMessage(String type,String actor, String user, Long groupId, 
			String text, String passwordHash, String email, int status) {
		
		 this.type = MessageType.valueOf(type.toUpperCase());
		 this.actor = actor;
		 this.user = user;
		 this.groupId = groupId;
		 this.text = text;
		 this.passwordHash = passwordHash;
		 this.email = email;
		 this.status = status;
	}
}