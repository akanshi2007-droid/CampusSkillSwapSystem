package campusskillswap.model;

import campusskillswap.interfaces.Displayable;

/**
 * Skill model (table: skills) - the catalog of skills.
 * implements Comparable -> lets Collections.sort() sort skills by name.
 */
public class Skill implements Comparable<Skill>, Displayable {
    private int id;
    private String name;
    private String category;

    public Skill() {
    }

    public Skill(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    @Override
    public int compareTo(Skill other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String getDisplayText() {
        return name + " [" + category + "]";
    }

    @Override
    public String toString() {      // JComboBox shows toString()
        return name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
