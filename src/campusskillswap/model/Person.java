package campusskillswap.model;

/**
 * ABSTRACT CLASS - base class for every user of the system.
 * Student extends Person (INHERITANCE). getRole() is abstract, so each
 * child class must give its own version (POLYMORPHISM).
 */
public abstract class Person {
    protected int id;
    protected String fullName;
    protected String email;
    protected String phone;

    public Person() {
    }

    public Person(int id, String fullName, String email, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public abstract String getRole();

    /** Uses String methods: trim, split, charAt, StringBuilder. Example: "Aakanksha Singh" -> "AS" */
    public String getInitials() {
        StringBuilder sb = new StringBuilder();
        if (fullName != null) {
            for (String part : fullName.trim().split("\\s+")) {
                if (!part.isEmpty()) {
                    sb.append(Character.toUpperCase(part.charAt(0)));
                }
            }
        }
        return sb.toString();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
