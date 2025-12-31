class Subject {

    String title;
    double credits;

    Subject(String title, double credits) {
        this.title = title;
        this.credits = credits;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "title='" + title + '\'' +
                ", credits=" + credits +
                '}';
    }

//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setCredits(double credits) {
//        this.credits = credits;
//    }
//
//    public double getCredits() {
//        return credits;
//    }
}
