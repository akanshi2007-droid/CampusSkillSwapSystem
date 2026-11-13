package campusskillswap.model;

import campusskillswap.interfaces.Displayable;

/** A student offering a skill (table: student_skills). Shown in Explore Skills. */
public class SkillOffer implements Displayable {
    private int offerId;
    private int studentId;
    private String ownerName;
    private int skillId;
    private String skillName;
    private String category;
    private String level;
    private String description;
    private int experienceYears;
    private boolean weekendsOnly;

    public SkillOffer() {
    }

    @Override
    public String getDisplayText() {
        return skillName + " (" + level + ")";
    }

    @Override
    public String toString() {      // JList shows toString()
        return getDisplayText();
    }

    public int getOfferId() { return offerId; }
    public void setOfferId(int offerId) { this.offerId = offerId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }
    public boolean isWeekendsOnly() { return weekendsOnly; }
    public void setWeekendsOnly(boolean weekendsOnly) { this.weekendsOnly = weekendsOnly; }
}
