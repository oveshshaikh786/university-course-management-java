import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        Subject subject1 = new Subject("Java", 4);
        Subject subject2 = new Subject("Java Online", 4);
        Subject subject3 = new Subject("JavaScript", 6);
        Subject subject4 = new Subject("JavaScript Online", 6);

        System.out.println("--------------------------");
        System.out.println("List of Available Courses:");
        System.out.println("1. Java Class-Room");
        System.out.println("2. Java Online");
        System.out.println("3. JavaScript Class-Room");
        System.out.println("4. JavaScript Online");
        System.out.println("--------------------------");

        System.out.print("Enter course code: ");
        int ch = in.nextInt();

        Course course;
        if (ch == 1)
            course = new ClassroomCourse(subject1, "Mark", 1000, "Cambridge", "Winter");
        else if (ch == 2)
            course = new OnlineCourse(subject2, "Mark", 1000, 6, 12);
        else if (ch == 3)
            course = new ClassroomCourse(subject3, "Mark", 1200, "Oxford", "Spring");
        else if (ch == 4)
            course = new OnlineCourse(subject4, "Mark", 1200, 8, 16);
        else {
            System.out.println("Invalid course code. Please enter a number between 1 and 4.");
            in.close();
            return;
        }

        in.nextLine(); // consume newline
        System.out.print("Enter name: ");
        String name = in.nextLine();

        Learner learner = new Learner(name, course);

        System.out.print("Assignment marks (0 - " + course.getMaxAssignmentMarks() + "): ");
        int mark1 = in.nextInt();
        if (mark1 < 0 || mark1 > course.getMaxAssignmentMarks()) {
            System.out.println("Invalid assignment marks. Must be between 0 and " + course.getMaxAssignmentMarks() + ".");
            in.close();
            return;
        }

        System.out.print("Quiz marks (0 - " + course.getMaxQuizMarks() + "): ");
        int mark2 = in.nextInt();
        if (mark2 < 0 || mark2 > course.getMaxQuizMarks()) {
            System.out.println("Invalid quiz marks. Must be between 0 and " + course.getMaxQuizMarks() + ".");
            in.close();
            return;
        }

        learner.assignmentsScore(mark1);
        learner.quizScore(mark2);

        double grade = learner.calculateGrade();
        System.out.println("Grade score: " + grade);

        if (grade >= 5)
            System.out.println("Congratulations " + learner.getName()
                    + ", you have successfully passed the " + learner.getCourse().getSubject().getTitle() + " course.");
        else
            System.out.println("Unfortunately " + learner.getName()
                    + ", you did not pass the " + learner.getCourse().getSubject().getTitle() + " course. Better luck next time!");

        in.close();
    }
}
