package campusskillswap.model;

import campusskillswap.exceptions.InvalidInputException;
import campusskillswap.interfaces.Displayable;
import campusskillswap.interfaces.Validatable;
import campusskillswap.util.Validator;

/**
 * Student model (table: students).
 * extends Person  -> inheritance
 * implements Validatable, Displayable -> interfaces
 */
public class Student extends Person implements Validatable, Displayable {
    private String password;      // plain text only while registering / changing password
    private String department;
    private int yearOfStudy;
    private String gender;
    private String bio;
    private String photoPath;

    public Student() {
        super();
    }

    public Student(int id, String fullName, String email, String department, int yearOfStudy) {
        super(id, fullName, email, "");
        this.department = department;
        this.yearOfStudy = yearOfStudy;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public void validate() throws InvalidInputException {
        Validator.validateName(fullName);
        Validator.validateEmail(email);
        Validator.validatePhone(phone);
        if (department == null || department.trim().isEmpty()) {
            throw new InvalidInputException("Please select a department.");
        }
        if (yearOfStudy < 1 || yearOfStudy > 4) {
            throw new InvalidInputException("Year of study must be between 1 and 4.");
        }
        if (gender == null || gender.trim().isEmpty()) {
            throw new InvalidInputException("Please select a gender.");
        }
        if (bio != null && bio.length() > 300) {
            throw new InvalidInputException("Bio must be within 300 characters.");
        }
    }

    @Override
    public String getDisplayText() {
        return fullName + " (" + department + ", Year " + yearOfStudy + ")";
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}
