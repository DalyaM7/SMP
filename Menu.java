import java.io.IOException;

public class Menu {
	
	// Display the menu and then get a letter representing the menu choice, depending on the menu type
	public static String getMenuChoice(String menuType) throws IOException {
		// Create strings defining which letters are allowed for each menu type.
		String allowedChoicesMain = ("LEPMDRQ"), allowedChoicesEdit = ("SHQ"), allowedChoicesYesNo = ("YN");
		// Depending on the menu type, only one of the above strings will be set to allowedChoices. choice represents the menu option chosen by the user.
		String allowedChoices = "", choice = "";

		// Initiate the boolean for the do-while loop. It will indicate if the choice is valid.
		boolean valid = true;
		do {
			// Set valid to be true by default
			valid = true;
			
			// Depending on the menu choice, allowedChoices will take on different strings. Additionally, different menus will be displayed
			if (menuType.equals("main")) {
				allowedChoices = allowedChoicesMain;
				displayMenu();
			}
			else if (menuType.equals("edit")) {
				allowedChoices = allowedChoicesEdit;
				displayEditMenu();
			}
			else {
				allowedChoices = allowedChoicesYesNo;
				displayEditRankingsMenu();
			}

			choice = BasicFunctions.getString("");
			// convert the choice to upper-case in order to compare it with allowedChoicesMain, which just contains upper-case letters.
			choice = choice.toUpperCase();

			// The choice is not valid if : (1) it is longer than one character (2) It is not contained in the allowedChoicesMain string
			if (choice.length() != 1 || !allowedChoices.contains(choice)) {
				valid = false;
				if (!menuType.equals("yesno")) {
					System.out.println("\nERROR: Invalid menu choice!\n");
				}
				else {
					System.out.println("ERROR: Choice must be 'y' or 'n'!");
				}
			}
		//Keep looping until user provides a valid menu choice input
		} while (!valid);
		return choice;
	}	// End of getMenuChoice
	
	// Print out the main menu
    public static void displayMenu() {
    	System.out.println("JAVA STABLE MARRIAGE PROBLEM v2\n");
    	System.out.println("L - Load students and schools from file");
    	System.out.println("E - Edit students and schools");
    	System.out.println("P - Print students and schools");
    	System.out.println("M - Match students and schools using Gale-Shapley algorithm");
    	System.out.println("D - Display matches and statistics");
    	System.out.println("R - Reset database");
    	System.out.println("Q - Quit\n");
    	System.out.print("Enter choice: ");
    } // end of displayMenu() function
    
    // Print out the edit students/schools menu
	public static void displayEditMenu() {
    	System.out.println("Edit data");
    	System.out.println("---------");
    	System.out.println("S - Edit students");
    	System.out.println("H - Edit high schools");
    	System.out.println("Q - Quit\n");
    	System.out.print("Enter choice: ");
	} // end of displayEditMenu() function
	
	// Print out the edit rankings menu
	public static void displayEditRankingsMenu() {
		System.out.print("Edit rankings (y/n): ");
	}	// End of displayEditRankingsMenu

}
