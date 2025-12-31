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
        System.out.println("1. Java Class-Room\n");
        System.out.println("2. Java Online \n");
        System.out.println("3. JavaScript Class-Room\n");
        System.out.println("4. JavaScript Online ");
        System.out.println("--------------------------");

        System.out.print("Enter course code: ");
        int ch = in.nextInt();

        Course course1 = null;
        if (ch == 1)
            course1 = new ClassroomCourse(subject1, "Mark", 1000, "Cambridge", "Winter");
        else if (ch == 2)
            course1 = new OnlineCourse(subject2, "Mark", 1000, 6, 12);
        else if (ch == 3)
            course1 = new ClassroomCourse(subject3, "Mark", 1200, "Oxford", "Spring");
        else if (ch == 4)
            course1 = new OnlineCourse(subject4, "Mark", 1200, 8, 16);

        in.nextLine(); // consume newline
        System.out.print("Enter name: ");
        String name = in.nextLine();

        Learner learner = new Learner(name, course1);

        System.out.println("Assignment marks: ");
        int mark1 = in.nextInt();
        System.out.println("Quiz marks: ");
        int mark2 = in.nextInt();

        learner.assignmentsScore(mark1);
        learner.quizScore(mark2);

        double grade = learner.calculateGrade();
        System.out.println("Grade score: " + grade);

        if (grade >= 5)
            System.out.println("Congratulations " + learner.name
                    + ", you have successfully passed the " + learner.course.subject.title + " course.");
        else
            System.out.println("Congratulations " + learner.name
                    + ", you have successfully completed the " + learner.course.subject.title + " course.");
    }
}