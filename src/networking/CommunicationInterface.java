package networking;

import models.network.NetworkMessage;

public interface CommunicationInterface {

	public void addMessageToQueue(NetworkMessage message);
	
	public void closeCommunication();
}
