public class Learner implements Assessments {

    private String name;
    private Course course;
    private int assignmentsMarks;
    private int quizMarks;

    Learner(String name, Course course) {
        this.name = name;
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getAssignmentsMarks() {
        return assignmentsMarks;
    }

    public int getQuizMarks() {
        return quizMarks;
    }

    @Override
    public void assignmentsScore(int marks) {
        this.assignmentsMarks = marks;
    }

    @Override
    public void quizScore(int marks) {
        this.quizMarks = marks;
    }

    public double calculateGrade() {
        double assignmentGrade = ((double) assignmentsMarks * 10) / course.getMaxAssignmentMarks();
        double quizGrade = ((double) quizMarks * 10) / course.getMaxQuizMarks();
        return (assignmentGrade + quizGrade) / 2;
    }

    @Override
    public String toString() {
        return "Name: " + name + " Course: " + course.getSubject().getTitle();
    }
}
