package managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import library.networking.CommunicationInterface;
import library.networking.Communication;
import library.util.MessagingLogger;

public class MessagingManager {

	private Map<String, Communication> allConnectionsMap = new HashMap<>();
	private Map<Long, CommunicationInterface> loggedUserMap = new ConcurrentHashMap<>();
	private static final MessagingManager messagingManager = new MessagingManager();

	private MessagingManager() {
	}

	public static MessagingManager getInstance() {
		return messagingManager;

	}

	public void addLoggedUserInMap(Long userId, CommunicationInterface communication) {
		loggedUserMap.put(userId, communication);
	}

	public void removeLoggedUserFromMap(Long userId) {
		if (loggedUserMap.containsKey(userId)) {
			loggedUserMap.remove(userId);
		} else {
			MessagingLogger.getLogger()
					.info("Attempting to remove non-existing communication for user " + userId + ".");
		}
	}

	public void addConnection(Communication communication) {
		allConnectionsMap.put(communication.getSessionID(), communication);
	}

	public Communication getConnection(String sessionID) {
		return allConnectionsMap.get(sessionID);
	}

	public void removeCommunication(Communication communication) {
		if (allConnectionsMap.containsKey(communication.getSessionID())) {
			allConnectionsMap.remove(communication.getSessionID());
		}
	}

	public void closeAllCommunication() {
		for (CommunicationInterface communication : allConnectionsMap.values()) {
			communication.closeCommunication();
		}
	}

	// TODO this should be moved to an utility class.
	public static String generateSessionUUID() {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		return randomUUIDString;
	}
}
