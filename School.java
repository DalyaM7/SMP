import java.io.IOException;
import java.util.ArrayList;

public class School {
	
	// Member fields
	private String name; 						// name
	private double alpha; 						// GPA weight
	private ArrayList<Integer> rankings;		// school's rankings of students
	private int student;						// index of matched student
	private int regret; 						// regret

	private void initSchool(String name, double alpha) {
		this.name = name;
		this.alpha = alpha;
		this.rankings = new ArrayList<Integer>();
		this.student = -1;
		this.regret = 0;
	}
	
	// constructors
	public School() {
		initSchool("",0);
	}
	
	public School(String name, double alpha) {
		initSchool(name, alpha);
	}
	
	
	// getters
	public String getName() {return this.name;}
	public double getAlpha () {return this.alpha;}
	public int getRanking (int i) {return this.rankings.get(i);}
	public int getStudent () {return this.student;}
	public int getRegret () {return this.regret;}
	public int getNumRankings() {return this.rankings.size();}	// get the rankings array size
	
	// setters
	public void setName (String name) {this.name = name;}
	public void setAlpha (double alpha) {this.alpha = alpha;}
	public void setRanking (int i , int r) {this.rankings.set(i, r); }
	public void setStudent (int i) {this.student = i;}
	public void setRegret (int r) {this.regret = r;}
	
	//find student ranking based on student ID
	public int findRankingByID(int ind) {return this.rankings.indexOf(ind);}
	
	// add an element to the rankings list
	public void addToRankings(int ranking) {this.rankings.add(ranking);}
	// check if a ranking number the user entered has not already been assigned for a different school. All rankings must be unique.
	public boolean rankingUsed(int ranking) {return this.rankings.contains(ranking);}
	// Clear the rankings array
	public void clearRankings() {this.rankings.clear();}
	
	// get new info from the user
	public void editInfo(ArrayList<Student> S) throws IOException {
		this.setName(BasicFunctions.getString("Name: "));
		this.setAlpha(BasicFunctions.getDouble("GPA weight: ", 0, 1));
		// Edit the school's rankings and if matching has been performed, also edit the regret.
		this.calcRankings(S);
		if (this.getStudent() != -1) {
			this.calcAndSetRegret();
		}
	}

	// calculate rankings based on weight alpha
	public void calcRankings(ArrayList<Student> S) {
		this.clearRankings();
		ArrayList<Double> scores = new ArrayList<Double>();	// The scores of each student will be stored in an array list
		// Initialize a rankings array list where the indices will represent students and the values themselves will represent rankings. The ranking data in this arraylist will be reformatted and transferred to the official rankings array at the end.
		ArrayList<Integer> preRankings = new ArrayList<Integer>();
		double alpha = this.getAlpha();
		int nStudents = S.size();
		
		// Loop through each student to attain their GPAs and extra-curricular scores so that the composite score for each student can be calculated and added to the scores array
		for (int i = 0; i < nStudents; i++) {
			double G = S.get(i).getGPA();
			int E = S.get(i).getES();
			double compositeScore = alpha * G + (1 - alpha) * E;
			scores.add(compositeScore);
		}
		
		// Loop through all the scores just attained in order to translate it to rankings. Example: if scores = [4,1,3,5], rankings = [2,4,3,1]
		// Algorithm explained: Since higher relative scores result in lower rankings, if one score is bigger than another score, increase the counter by 1.
		int numScores = scores.size();	// This is the same as nStudents but since the code in the loop is doing things to each respective score, for clarity of code, the number of scores is looped through instead.
		for (int i = 0; i < numScores; i++) {
			int count = 1;	// This will keep track of how many scores (i.e. the ones that will be attained in the following loop) are bigger than the one at hand (i.e. the one being analyzed in the outer loop). Note that since a rank cannot be less than 1, the count begins at 1.
			
			for (int j = 0; j < numScores; j++ ) {
				if (scores.get(j) > scores.get(i)) {
					count++;
				}
			}
			int rank = count; // save the count after each iteration. The count is in fact the rank of the student.
			// Account for the case where there are ties in rankings
			int numRankings = preRankings.size();
			if  (numRankings != 0) {	// It would only be equal to zero if this were the first iteration of adding a ranking
				do {
					// Couldn't join this nested if statement with the outer one with an && operator because an empty list cannot contain anything and will result in error
					// Every single time rank is found to be equal to another rank that was already added to the ranking array, increase the rank by 1. Keep looping until there are no more ranks that are equal
					if (preRankings.contains(rank)) {
						rank++;
					}
				} while (preRankings.contains(rank));
			}
			preRankings.add(rank);
		}
		// Now that the preRankings have been achieved, create the final rankings array
		int numRankings = preRankings.size();
		for (int i = 0; i < numRankings; i++) {
			this.rankings.add(0);	// ensure that all indices where rankings will be added into the final array actually exist.
		}
		// Loop through the number of rankings and transfer the data from the preRankings array into the rankings array attribute. The only difference is the format. One is the inverse of the other.
		for (int i = 0; i < numRankings; i++) {
			this.rankings.set(preRankings.get(i) - 1, i + 1);	// Example: If preRankings = [2, 4, 3, 1] --> rankings = [4, 1, 3, 2]. The former has indices that represent students and the latter has indices that represent rankings.
		}
	}

	// print school info and assigned student in tabular format
	public void print(ArrayList<Student> S) {
		String name = this.getName();
		double alpha = this.getAlpha();
		String assignedStudent;
		if (this.getStudent() == -1) {
			assignedStudent = "-";
		}
		else {
			assignedStudent = S.get(this.getStudent()).getName();
		}
		String spaces1 = BasicFunctions.getSpaces(name.length());
		String spaces2 = BasicFunctions.getSpaces(assignedStudent.length());
		// include 5 spaces before alpha so that the getSpaces function in BasicFunctions can consistently use 26 - nameLength
    	System.out.format("%s%s     %.2f  %s%s", name, spaces1, alpha, assignedStudent, spaces2);
    	if (this.getNumRankings() == 0) {
    		System.out.println("-");
    	}
    	else {
    		this.printRankings(S);
    	}
	}
	
	// print the school's rankings of students separated by a comma
	public void printRankings(ArrayList<Student> S) {
		//  this string will contain students' names in the order of the school's preferences separated by commas
		String prefferedOrder = "";
		// Loop through the number of rankings.
		int numRankings = this.getNumRankings();
		for (int i = 0; i < numRankings; i++) {
			String name = S.get(this.getRanking(i) - 1).getName();
			// if this is the last iteration of the loop, it means that this student is ranked the last and therefore does not need any commas after it.
			if (i + 1 == numRankings) {
				prefferedOrder += name;
			}
			else {
				prefferedOrder += name + ", ";
			}
		}
		System.out.println(prefferedOrder);
	}
	
	// Calculate the regret of the school and then set it to the regret attribute
	public void calcAndSetRegret() {
		int studentRanking = this.findRankingByID(this.getStudent() + 1) + 1;	// add 1 after getting the index because the indices represent rankings but indices start from zero.
		int regret = studentRanking - 1;
		this.setRegret(regret);
	}
	
	public boolean isValid() {	// check if this school has valid info
		if (this.alpha >= 0 && this.alpha <= 1) {
			return true;
		}
		else {
			return false;
		}
	}
}