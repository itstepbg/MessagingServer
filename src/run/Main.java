package run;

import java.util.Scanner;

import exceptions.WrongUserMenuInputException;

public class Main {
	
	public static void main(String[] args) {
		chooseMenuOption();
		Scanner sc = new Scanner(System.in);
		int inputOption = sc.nextInt(); 
		try {
			manageUserInput(inputOption);
		} catch (WrongUserMenuInputException e) {
			// TODO Replace with a more user-friendly message.
			e.printStackTrace();
		}
		
		sc.close();
	}
		
	public static void chooseMenuOption() {
		//TODO These are client-side functionalities. The server application needs to have the administrative ones
		//listed in issue #1
		System.out.println("For login, press 0: ");
		System.out.println("For creating account, press 1: ");
		System.out.println("For starting new chat conversation, press 2: ");
		System.out.println("To invite user in the chat group, press 3: ");
		System.out.println("To join existing chat group, press 4: ");
		System.out.println("To leave the chat group, press 5: ");
		System.out.println("For logout, press 6: ");
	}
	
	public static void manageUserInput(int inputOption) throws WrongUserMenuInputException {
		switch (inputOption) {
		case 0: 
			userLogin();
			break;
		case 1:
			createAccount();
			break;
		case 2:
			startConversation();
			break;
		case 3:
			inviteUser();
			break;
		case 4:
			joinGroupChat();
			break;
		case 5:
			leaveChatGroup();
			break;
		case 6:
			userLogout();
			break;
		default:
			throw new WrongUserMenuInputException("Choose a valid menu option!");
		}
	}

	private static void leaveChatGroup() {
		// TODO Auto-generated method stub
		
	}


	private static void userLogout() {
		// TODO Auto-generated method stub
		
	}

	private static void joinGroupChat() {
		// TODO Auto-generated method stub
		
	}

	private static void inviteUser() {
		// TODO Auto-generated method stub
		
	}

	private static void startConversation() {
		// TODO Auto-generated method stub
		
	}

	private static void createAccount() {
		// TODO Auto-generated method stub
		
	}

	private static void userLogin() {
		// TODO Auto-generated method stub
		
	}

}
