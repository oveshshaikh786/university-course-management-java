class OnlineCourse extends Course {

    int weeks;
    int videoLessons;

    OnlineCourse (Subject subject, String instructor, int fee, int weeks, int videoLessons) {
        super(subject, instructor, fee);
        this.weeks = weeks;
        this.videoLessons = videoLessons;
    }

//    public void setWeeks(int weeks) {
//        this.weeks = weeks;
//    }
//
//    public int getWeeks() {
//        return weeks;
//    }
//
//    public void setVideoLessons(int videoLessons) {
//        this.videoLessons = videoLessons;
//    }
//
//    public int getVideoLessons() {
//        return videoLessons;
//    }
}