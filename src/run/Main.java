package run;

import java.util.List;
import java.util.Scanner;

import exceptions.WrongMenuInputException;
import managers.DatabaseManager;
import models.data.User;
import util.ORM;
import util.Sha1Hash;

public class Main {
	private static Scanner sc = new Scanner(System.in);
	private static DatabaseManager databaseManager;

	public static void main(String[] args) {
		databaseManager = DatabaseManager.getInstance();
		
		chooseMenuOption();

	}

	// listed in issue #1
	public static void chooseMenuOption() {
		System.out.println("To create user, press 0: ");
		System.out.println("To delete user, press 1: ");
		System.out.println("To list all users, press 2: ");
		System.out.println("To exit, press 3: ");
		
		int inputOption = Integer.parseInt(sc.nextLine());
		
		try {
			manageUserInput(inputOption);
		} catch (WrongMenuInputException e) {
			System.out.println(e.getMessage());
		}
		
		chooseMenuOption();
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
		sc.close();
		databaseManager.closeConnection();
		System.exit(0);
	}

	private static void listAllUsers() {
		List<User> users = ORM.selectAllUsers();
		for (User user : users) {
			System.out.println("User: " + user.getName() + " email: " + user.getEmail());
		}

	}

	private static void deleteUser() {
		System.out.println("Enter username or email to be deleted");
		String keyword = sc.nextLine();

		User user = ORM.selectUser(keyword, keyword);
		if (user != null) {
			if (ORM.deleteUser(user)) {
				System.out.println("User successfully deleted");
			}
		} else {
			System.out.println("No such user!");
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
			System.out.println("User created");
		} else {
			System.out.println("Username or email already exists");
			createUser();
		}
	}

}
