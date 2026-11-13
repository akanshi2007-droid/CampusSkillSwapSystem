package campusskillswap.model;

/** Learning preferences of a student (table: preferences). */
public class StudentPreference {
    private int studentId;
    private String mode = "Both";          // Online / Offline / Both
    private boolean weekends;
    private boolean emailAlerts = true;
    private int hoursPerWeek = 5;
    private String interestCategory = "Programming";
    private boolean availableNow = true;
    private String notes = "";

    public StudentPreference() {
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public boolean isWeekends() { return weekends; }
    public void setWeekends(boolean weekends) { this.weekends = weekends; }
    public boolean isEmailAlerts() { return emailAlerts; }
    public void setEmailAlerts(boolean emailAlerts) { this.emailAlerts = emailAlerts; }
    public int getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(int hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }
    public String getInterestCategory() { return interestCategory; }
    public void setInterestCategory(String interestCategory) { this.interestCategory = interestCategory; }
    public boolean isAvailableNow() { return availableNow; }
    public void setAvailableNow(boolean availableNow) { this.availableNow = availableNow; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
