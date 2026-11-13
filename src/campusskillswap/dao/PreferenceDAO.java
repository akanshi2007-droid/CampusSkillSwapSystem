package campusskillswap.dao;

import campusskillswap.model.StudentPreference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Database operations for the preferences table (one row per student). */
public class PreferenceDAO {

    /** Returns null if the student has not saved preferences yet. */
    public StudentPreference find(int studentId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM preferences WHERE student_id = ?")) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StudentPreference p = new StudentPreference();
                    p.setStudentId(studentId);
                    p.setMode(rs.getString("mode"));
                    p.setWeekends(rs.getBoolean("weekends"));
                    p.setEmailAlerts(rs.getBoolean("email_alerts"));
                    p.setHoursPerWeek(rs.getInt("hours_per_week"));
                    p.setInterestCategory(rs.getString("interest_category"));
                    p.setAvailableNow(rs.getBoolean("available_now"));
                    p.setNotes(rs.getString("notes"));
                    return p;
                }
            }
        }
        return null;
    }

    public boolean exists(int studentId) throws SQLException {
        return find(studentId) != null;
    }

    /** Insert the row, or update it if it already exists (MySQL: ON DUPLICATE KEY UPDATE). */
    public void save(StudentPreference p) throws SQLException {
        String sql = "INSERT INTO preferences (student_id, mode, weekends, email_alerts, hours_per_week, interest_category, available_now, notes) "
                + "VALUES (?,?,?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE mode=VALUES(mode), weekends=VALUES(weekends), email_alerts=VALUES(email_alerts), "
                + "hours_per_week=VALUES(hours_per_week), interest_category=VALUES(interest_category), "
                + "available_now=VALUES(available_now), notes=VALUES(notes)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getStudentId());
            ps.setString(2, p.getMode());
            ps.setBoolean(3, p.isWeekends());
            ps.setBoolean(4, p.isEmailAlerts());
            ps.setInt(5, p.getHoursPerWeek());
            ps.setString(6, p.getInterestCategory());
            ps.setBoolean(7, p.isAvailableNow());
            ps.setString(8, p.getNotes());
            ps.executeUpdate();
        }
    }
}
