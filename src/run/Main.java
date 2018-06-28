package run;

import java.util.List;
import java.util.Scanner;

import exceptions.WrongMenuInputException;
import managers.DatabaseManager;
import managers.MessagingManager;
import managers.NetworkManager;
import models.data.User;
import storage.ORM;
import util.Logger;
import util.Sha1Hash;

public class Main {
	private static Scanner sc = new Scanner(System.in);
	private static DatabaseManager databaseManager;
	private static NetworkManager networkManager;

	private static boolean running = true;

	public static void main(String[] args) {
		databaseManager = DatabaseManager.getInstance();
		networkManager = new NetworkManager();
		networkManager.startConnectionThread(3000);

		while (running) {
			chooseMenuOption();
		}
	}

	// listed in issue #1
	public static void chooseMenuOption() {
		System.out.println();
		System.out.println("0. Create User");
		System.out.println("1. Delete User");
		System.out.println("2. List Users");
		System.out.println("3. Exit");
		System.out.println();

		int inputOption = Integer.parseInt(sc.nextLine());

		try {
			manageUserInput(inputOption);
		} catch (WrongMenuInputException e) {
			System.out.println(e.getMessage());
		}
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
		running = false;
		sc.close();
		databaseManager.closeConnection();
		networkManager.stopConnectionThread();
		MessagingManager.getInstance().closeAllCommunication();
		System.exit(0);
	}

	private static void listAllUsers() {
		List<User> users = ORM.selectAllUsers();
		for (User user : users) {
			if (user != null) {
				System.out.println("User: " + user.getName() + " Email: " + user.getEmail());
			}
		}
	}

	private static void deleteUser() {
		System.out.println("Enter username or email to be deleted");
		String keyword = sc.nextLine();

		User user = ORM.selectUser(keyword, keyword);
		if (user != null) {
			if (ORM.deleteUser(user)) {
				Logger.logInfo("User successfully deleted");
			}
		} else {
			Logger.logInfo("No such user!");
		}
	}

	private static void createUser() {
		System.out.println("Please enter username:");

		String name = sc.nextLine();

		// TODO please make user validation.
		// String regex = "[a-zA-Z._0-9-]+";
		// if(name.length()<6) {
		// System.out.println("Please enter at least 6 characters:");
		// name = sc.nextLine();
		// }

		System.out.println("Please enter password:");
		String password = sc.nextLine();

		String passwrodHash = Sha1Hash.generateHash(password);

		System.out.println("Please enter email:");
		String email = sc.nextLine();

		User user = new User(name, passwrodHash, email);
		if (ORM.insertUser(user)) {
			Logger.logInfo("User created");
		} else {
			Logger.logError("Username or email already exists");
			createUser();
		}
	}

}
