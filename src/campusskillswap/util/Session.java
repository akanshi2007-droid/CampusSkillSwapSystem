package campusskillswap.util;

import campusskillswap.model.Student;

/** SINGLETON - only one Session object exists; it remembers who is logged in. */
public class Session {
    private static Session instance;
    private Student currentStudent;

    private Session() {
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public Student getCurrentStudent() { return currentStudent; }
    public void setCurrentStudent(Student s) { this.currentStudent = s; }
    public boolean isLoggedIn() { return currentStudent != null; }
    public void clear() { currentStudent = null; }
}
