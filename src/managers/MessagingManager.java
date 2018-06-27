package managers;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import networking.CommunicationThread;
import networking.CommunicationThreadFactory;

public class MessagingManager {

	private ArrayList<CommunicationThread> allCommunicationThreads = new ArrayList<>();
	private Map<Long, CommunicationThread> loggedUserCommunicationMap = new ConcurrentHashMap<>();
	private static final MessagingManager messagingManager = new MessagingManager();

	private MessagingManager() {
	}

	public static MessagingManager getInstance() {
		return messagingManager;

	}

	public void addLoggedUserInMap(Long userId, CommunicationThread communicationThread) {
		loggedUserCommunicationMap.put(userId, communicationThread);
	}

	public void removeLoggedUserFromMap(Long userId) {
		if (loggedUserCommunicationMap.containsKey(userId)) {
			loggedUserCommunicationMap.remove(userId);
		} else {
			System.out.println("Attempting to remove non-existing communication thread.");
		}
	}
	
	public void addCommunicationThread(CommunicationThread communicationThread) {
		allCommunicationThreads.add(communicationThread);
	}
	
	public void removeCommunicationThread(CommunicationThread communicationThread) {
		if (allCommunicationThreads.contains(communicationThread)) {
			allCommunicationThreads.remove(communicationThread);
		}
	}
	
	public void closeAllCommunication() {
		for (CommunicationThread communicationThread : allCommunicationThreads) {
			communicationThread.interrupt();
		}
	}
	
	

}
