abstract class Course {

    Subject subject;
    String instructor;
    int fee;
    int assignmentsMarks;
    int quizMarks;

    Course(Subject subject, String instructor, int fee) {
        this.subject = subject;
        this.instructor = instructor;
        this.fee = fee;
    }

//    public void setSubject(Subject subject) {
//        this.subject = subject;
//    }
//
//    public Subject getSubject() {
//        return subject;
//    }
//
//    public void setInstructor(String instructor) {
//        this.instructor = instructor;
//    }
//
//    public String getInstructor() {
//        return instructor;
//    }
//
//    public void setFee(int fee) {
//        this.fee = fee;
//    }
//
//    public int getFee() {
//        return fee;
//    }
//
//    public void setAssignmentsMarks(int assignmentsMarks) {
//        this.assignmentsMarks = assignmentsMarks;
//    }
//
//    public int getAssignmentsMarks() {
//        return assignmentsMarks;
//    }
//
//    public void setQuizMarks(int quizMarks) {
//        this.quizMarks = quizMarks;
//    }
//
//    public int getQuizMarks() {
//        return quizMarks;
//    }
}