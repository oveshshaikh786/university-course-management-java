class ClassroomCourse extends Course {

    String school;
    String session;

    ClassroomCourse (Subject subject, String instructor, int fee, String school, String session) {
        super(subject, instructor, fee);
        this.school = school;
        this.session = session;
    }

//    public void setSchool(String school) {
//        this.school = school;
//    }
//
//    public String getSchool() {
//        return school;
//    }
//
//    public void setSession(String session) {
//        this.session = session;
//    }
//
//    public String getSession() {
//        return session;
//    }
}