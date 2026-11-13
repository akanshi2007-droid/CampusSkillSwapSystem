package campusskillswap.util;

import campusskillswap.model.Student;

/** Calculates the "Profile Completion" percentage shown on the dashboard. */
public final class ProfileCalculator {
    private ProfileCalculator() {
    }

    public static int calculate(Student s, int offeredSkills, boolean preferencesSaved) {
        int filled = 0;
        int total = 10;
        if (!Validator.isBlank(s.getFullName())) filled++;
        if (!Validator.isBlank(s.getEmail())) filled++;
        if (!Validator.isBlank(s.getDepartment())) filled++;
        if (s.getYearOfStudy() > 0) filled++;
        if (!Validator.isBlank(s.getGender())) filled++;
        if (!Validator.isBlank(s.getPhone())) filled++;
        if (!Validator.isBlank(s.getBio())) filled++;
        if (!Validator.isBlank(s.getPhotoPath())) filled++;
        if (offeredSkills > 0) filled++;
        if (preferencesSaved) filled++;
        return filled * 100 / total;
    }
}
