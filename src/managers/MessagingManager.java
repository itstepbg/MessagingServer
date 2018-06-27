package managers;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import networking.CommunicationThread;
import networking.CommunicationThreadFactory;

public class MessagingManager {

	private Map<Long, CommunicationThread> userCommunicationMap = new ConcurrentHashMap<>();
	private static final MessagingManager messagingManager = new MessagingManager();

	private MessagingManager() {
	}

	public static MessagingManager getInstance() {
		return messagingManager;

	}

	public void initCommunication(Long userId, CommunicationThread communicationThread) {
		userCommunicationMap.put(userId, communicationThread);
	}

	public void closeCommunication(Long userId) {
		if (userCommunicationMap.containsKey(userId)) {
			userCommunicationMap.remove(userId);
		} else {
			System.out.println("Attempting to remove non-existing communication thread.");
		}
	}

}
