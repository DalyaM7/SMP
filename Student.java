import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Student {
	// member fields
	private String name;						// name
	private double GPA;							// GPA
	private int ES;								// extracurricular score
	private ArrayList<Integer> rankings;		// student's rankings of schools --> indices are rankings and values are schools
	private int school;							// index of matched school
	private int regret;							// regret
	
	// Used to create a student object
	private void initStudent(String name, double GPA, int ES) {
		this.name = name;
		this.GPA = GPA;
		this.ES = ES;
		this.rankings = new ArrayList<Integer>();
		this.school = -1;
		this.regret = 0;
	}
	
	// constructors
	public Student() {
		this.initStudent("", 0, 0);
	}

	public Student(String name, double GPA, int ES) {
		this.initStudent(name, GPA, ES);
	}
	
	//getters
	public String getName() {return this.name;}
	public double getGPA() {return this.GPA;}
	public int getES() {return this.ES;}
	public int getRanking(int i) {return this.rankings.get(i);}		// find the school based on the student's ranking of the school
	public int getSchool() {return this.school;}
	public int getRegret() {return this.regret;}
	public int getNumRankings() {return this.rankings.size();}		// get the rankings array size
	
	// setters
	public void setName(String name) {this.name = name;}
	public void setGPA(double GPA) {this.GPA = GPA;}
	public void setES(int ES) {this.ES = ES;}
	public void setRanking(int i, int r) {this.rankings.set(i,r);}
	public void setSchool(int i) {this.school = i;}
	public void setRegret(int r) {this.regret = r;}
	
	// find school ranking based on school ID
	public int findRankingByID(int ind) {return this.rankings.indexOf(ind);}	// Find the index of a certain number in the rankings array (in other words, student's ranking for school)
	
	// The following are methods pertaining to rankings
	public void addToRankings(int ranking) {this.rankings.add(ranking);}	// add an element to the rankings list
	public boolean rankingUsed(int ranking) {return this.rankings.contains(ranking);}	// check if a ranking number the user entered has not already been assigned for a different school. All rankings must be unique.
	public void clearRankings() {this.rankings.clear();}	// Clear the rankings array
	public void setValidRanking(String prompt, int nSchools) {	// Check if a ranking the user inputed is valid and if it is, add it to the rankings array.
		boolean valid = true;	// This is for do-while loop on next lines. It is to identify if a rank the user has inputed is valid.
		// Get the student's ranking of the school. If the ranking has already been inputed, try again. Keep looping until ranking is unique.
		do {
			int ranking = BasicFunctions.getInteger(prompt, 1, nSchools);
	
			// It must be ensured that the ranking number the user entered has not already been assigned for a different school. All rankings must be unique. This will work even if there are no rankings yet.
			if (!this.rankingUsed(ranking)) {
				this.addToRankings(ranking);
				valid = true;
			}
			// The only other situation is if the ranking the user inputed has already been used, which is not allowed.
			else {
				System.out.format("ERROR: Rank %d already used!\n\n", ranking);
				valid = false;
			}
		} while (!valid);
	}	// End of setValidRanking
	
	
	// get new info from the user
	public void editInfo(ArrayList<School> H) throws IOException {
		this.setName(BasicFunctions.getString("Name: "));
		this.setGPA(BasicFunctions.getDouble("GPA: ", 0, 4));
		this.setES(BasicFunctions.getInteger("Extracurricular score: ", 0, 5));
		String userConcession = Menu.getMenuChoice("yesno");	// Check if the user wants to edit rankings
		if (userConcession.equals("Y")) {
			System.out.println();
			this.editRankings(H);
			if (this.school != -1) {
				this.calcAndSetRegret();	// calculate and set the regret of the student since the GPA and/or ES and/or rankings could have been changed (this is of course meaningless if the matchings have not been set)
			}
		}
	} // End of editInfo
	
	// Edit rankings
	public void editRankings(ArrayList<School> H) throws IOException {
		this.clearRankings();
		System.out.format("Student %s's rankings:\n", this.getName());
		int nSchools = H.size();
		// loop through all the schools so the user can type the student's new ranking of each one and so the rankings array will be updated with each one
		for (int i = 0; i < nSchools; i++) {
			String prompt = "School " + H.get(i).getName() + ": ";
			this.setValidRanking(prompt, nSchools);
		}
		System.out.println();
	} // End of editRankings
	
	
	// Print student info and assigned school in tabular format
	public void print(ArrayList<School> H) {
		String name = this.getName();
		double GPA = this.getGPA();
		int ES = this.getES();
		String assignedSchool;
		if (this.getSchool() == -1) {
			assignedSchool = "-";
		}
		else {
			assignedSchool = H.get(this.getSchool()).getName();
		}
		String spaces1 = BasicFunctions.getSpaces(name.length());
		String spaces2 = BasicFunctions.getSpaces(assignedSchool.length());

    	System.out.format("%s%s    %.2f%4d  %s%s", name, spaces1, GPA, ES, assignedSchool, spaces2);

        this.printRankings(H);
	}
	
	// print the rankings separated by a comma
	public void printRankings(ArrayList<School> H) {
		// this string will contain schools' names in the order of the student's preferences separated by commas
		String prefferedOrder = "";
		// Loop through the number of rankings
		int numRankings = this.getNumRankings();
		for (int i = 0; i < numRankings; i++) {
			int schoolInd = this.getRanking(i) - 1;
			String name = H.get(schoolInd).getName();
		//	Rankings are formatted, for example, as follows: [2,5,1,4,3] = school 2, school 5, school 1, school 4, school 3
			// if this is the last iteration of the loop, it means that this school is ranked the last and therefore does not need any commas after it.
			if (i + 1 == this.getNumRankings()) {
				prefferedOrder += name;
			}
			// If this is not the last iteration of the loop, add the school name to the string with a comma after it.
			else {
				prefferedOrder += name + ", ";
			}
		}
		System.out.println(prefferedOrder);
	}
	
	public boolean isValid(int nSchools) {	// check if this student has valid info

		Set<Integer> rankingsSet = new HashSet<Integer>(this.rankings);	// this will be used when determining if all rankings are unique.
		if ((this.GPA < 0 || this.GPA > 4) || (this.ES < 0 || this.ES > 5)) {	// check if GPA and ES are valid numbers
			return false;
		}
		
		// Now do the three checks on rankings: (a) there must be 1 per school (b) all rankings must be unique. (c) All ranks must be integers in the range [1,n]
		int numRankings = this.getNumRankings();
		if (numRankings != nSchools) {	// (a) check if there are the same amount of rankings as there are schools
			return false;
		}
		else {	// checks (b) and (c) happen when the number of ranks are correct - i.e. there is in fact 1 per school, but might be invalid another way.
			if (this.getNumRankings() != rankingsSet.size()) {	// the rankings are unique if, when converted into a set, the number of rankings stays the same (since sets erase all duplicates)
				return false;
			}
			else {	// When the rankings do contain unique schools:
				// Loop through the students' rankings of the schools to see if each ranking is valid.
				for (int i = 0; i < numRankings; i++) {
					if (this.getRanking(i) > nSchools || this.getRanking(i) < 1) {	// each ranking must be from 1 to the number of schools.
						return false;
					}
					else {
						continue;
					}
				}
			}
		}
		return true;
	}
	
	public void calcAndSetRegret() {	// Calculate the regret of the student and then set it to the regret attribute
		int schoolRanking = this.findRankingByID(this.getSchool() + 1) + 1;	// get the ranking of the school the student is matched to. Add 1 to the matched school AND to the entire result in order to make up for lists starting from index 0 - schools in the rankings array start from 1 - not 0. 
		int regret = schoolRanking - 1;	// calculate the regret.
		this.setRegret(regret);	// set the regret
	} // end of calcRegret
}