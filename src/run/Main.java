package run;

import java.util.Scanner;

import exceptions.WrongMenuInputException;
import managers.DatabaseManager;

public class Main {
	
	private static DatabaseManager databaseManager ;
	
	public static void main(String[] args) {
		databaseManager = DatabaseManager.getInstance();
		chooseMenuOption();
		
		
		Scanner sc = new Scanner(System.in);
		int inputOption = sc.nextInt();
		try {
			manageUserInput(inputOption);
		} catch (WrongMenuInputException e) {
			System.out.println(e.getMessage());
		}
		
		sc.close();
	}
		
		//listed in issue #1
		public static void chooseMenuOption() {
			System.out.println("To create user, press 0: ");
			System.out.println("To delete user, press 1: ");
			System.out.println("To list all users, press 2: ");
			System.out.println("To exit, press 3: ");
		}
	
	public static void manageUserInput(int inputOption) throws WrongMenuInputException {
		switch (inputOption) {
		case 0:
			createUser();
			break;
		case 1:
			deleteUser();
			break;
		case 2:
			listAllUsers();
			break;
		case 3:
			exit();
			break;
		default:
			throw new WrongMenuInputException("Choose a valid menu option!");
		}
	}

	private static void exit() {
		// TODO Auto-generated method stub
		
	}

	private static void listAllUsers() {
		// TODO Auto-generated method stub
		
	}

	private static void deleteUser() {
		// TODO Auto-generated method stub
		
	}

	private static void createUser() {
		// TODO Auto-generated method stub
		
	}

}
