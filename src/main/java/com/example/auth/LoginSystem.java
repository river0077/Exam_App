package main.java.com.example.auth;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader; // Import for file reading
import java.io.BufferedWriter; // Import for file writing
import java.io.FileReader;    // Import for file reading
import java.io.FileWriter;    // Import for file writing
import java.io.IOException;   // Import for I/O exceptions
import java.io.File;          // Import for File class to create directories

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class LoginSystem extends JFrame {
	// Attributes
	private static final long serialVersionUID = 1L;
	private List<User<String, String>> list;
	private LocalDateTime dt;
	private static final String DATA_DIRECTORY = "data";
	private static final String USER_DATA_FILE = DATA_DIRECTORY + File.separator + "users.txt";

	// Constructor
	public LoginSystem() {
		list = new ArrayList<>();
		loadUsersFromFile();
	}

	// Methods
	public void addUser(User<String, String> userAdd, boolean showMes) {
        for (int i = 0; i < list.size(); i++) {
            User<String, String> user = list.get(i);
            if (user.getUser().equals(userAdd.getUser())) {
                list.remove(i);
                break;
            }
        }

		list.add(userAdd);
		saveUsersToFile();
		if (showMes == true) {
			JOptionPane.showMessageDialog(this, "ADDED ACCOUNT!");
			System.out.println(upTime() + " Add User - User: " + userAdd.getUser() + " (Added user successfully)");
		}
	}

	public boolean authenticate(String username, String password) {
		for (User<String, String> user : list) {
			if (user.getUser().equals(username) && user.getPass().equals(password)) {
				return true;
			}
		}
		return false;
	}

	public boolean wrongPass(String username, String password) {
		for (User<String, String> user : list) {
			if (user.getUser().equals(username) && user.getPass().equals(password) == false) {
				return true;
			}
		}
		return false;
	}

	public void printList() {
		System.out.println("user          password");
		for (User<String, String> user : list) {
			user.printList();
		}
	}

	public List<User<String, String>> getUserList() {
		return list;
	}

	public void deleteUser(String username) {
	    boolean found = false;
	    for (int i = 0; i < list.size(); i++) {
	        User<String, String> user = list.get(i);
	        if (user.getUser().equals(username)) {
	            if (user.getUser().equals("root")) {
	                JOptionPane.showMessageDialog(this, "YOU CAN'T DELETE ROOT ACCOUNT!");
	                return;
	            } else {
	                list.remove(i);
	                saveUsersToFile();
	                JOptionPane.showMessageDialog(this, "DELETED ACCOUNT!");
	                System.out.println(upTime() + " Delete User - User: " + username + " (deleted successfully)");
	                found = true;
	                break;
	            }
	        }
	    }
	    if (!found) {
	        JOptionPane.showMessageDialog(this, "NOT FOUND ACCOUNT!");
	    }
	}


	public boolean lockUser(String username) {
		for (User<String, String> user : list) {
			if (user.getUser().equals(username)) {
				return true;
			}
		}
		return false;
	}

	public String upTime() {
		dt = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String dtfor = dt.format(format);
		return dtfor;
	}

	private void saveUsersToFile() {
		File dataDir = new File(DATA_DIRECTORY);
		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE))) {
			for (User<String, String> user : list) {
				writer.write(user.getUser() + "," + user.getPass() + "," + user.getScore() + "," + (user.getStatus() != null ? user.getStatus() : "null"));
				writer.newLine();
			}
			System.out.println(upTime() + " User data saved to " + USER_DATA_FILE);
		} catch (IOException e) {
			System.err.println(upTime() + " Error saving user data to file: " + USER_DATA_FILE + " (" + e.getMessage() + ")");
		}
	}

	private void loadUsersFromFile() {
		list.clear();
		try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length == 4) {
					String username = parts[0];
					String password = parts[1];
					double score = Double.parseDouble(parts[2]);
					String status = parts[3].equals("null") ? null : parts[3];
					list.add(new User<>(username, password, score, status));
				}
			}
			System.out.println(upTime() + " User data loaded from " + USER_DATA_FILE);
		} catch (IOException e) {
			System.err.println(upTime() + " No existing user data file found or error loading: " + USER_DATA_FILE + " (" + e.getMessage() + ")");
			addInitialUsers();
		} catch (NumberFormatException e) {
			System.err.println(upTime() + " Error parsing user data from file: " + USER_DATA_FILE + " (" + e.getMessage() + ")");
			addInitialUsers();
		}
	}

	private void addInitialUsers() {
		list.clear();
		addUser(new User<String, String>("root", "admin", 0.0, null), false);
	}
}