package managers;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import library.networking.CommunicationInterface;

public class MessagingManager {

	private ArrayList<CommunicationInterface> allCommunicationThreads = new ArrayList<>();
	private Map<Long, CommunicationInterface> loggedUserCommunicationMap = new ConcurrentHashMap<>();
	private static final MessagingManager messagingManager = new MessagingManager();
	
	
	private MessagingManager() {
	}

	public static MessagingManager getInstance() {
		return messagingManager;

	}

	public void addLoggedUserInMap(Long userId, CommunicationInterface communication) {
		loggedUserCommunicationMap.put(userId, communication);
	}

	public void removeLoggedUserFromMap(Long userId) {
		if (loggedUserCommunicationMap.containsKey(userId)) {
			loggedUserCommunicationMap.remove(userId);
		} else {
			System.out.println("Attempting to remove non-existing communication thread.");
		}
	}
	
	public void addCommunication(CommunicationInterface communication) {
		allCommunicationThreads.add(communication);
	}
	
	public void removeCommunication(CommunicationInterface communication) {
		if (allCommunicationThreads.contains(communication)) {
			allCommunicationThreads.remove(communication);
		}
	}
	
	public void closeAllCommunication() {
		for (CommunicationInterface communication : allCommunicationThreads) {
			communication.closeCommunication();
		}
	}
	
	

}
