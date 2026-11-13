
package campusskillswap.dao;

import campusskillswap.exceptions.AuthenticationException;
import campusskillswap.exceptions.DuplicateEntryException;
import campusskillswap.interfaces.CrudDAO;
import campusskillswap.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Database operations using the existing users table. */
public class StudentDAO implements CrudDAO<Student> {

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();

        s.setId(rs.getInt("user_id"));
        s.setFullName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setDepartment(rs.getString("department"));
        s.setYearOfStudy(rs.getInt("year"));

        // These fields are not present in the current database.
        s.setGender(null);
        s.setPhone(null);
        s.setBio(null);
        s.setPhotoPath(null);

        return s;
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int register(Student s)
            throws SQLException, DuplicateEntryException {

        if (emailExists(s.getEmail())) {
            throw new DuplicateEntryException(
                "This email is already registered. Please login."
            );
        }

        return insert(s);
    }

    public Student login(String email, String password)
            throws SQLException, AuthenticationException {

        String sql = "SELECT user_id, name, email, department, year " +
                     "FROM users WHERE email = ? AND password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        throw new AuthenticationException("Invalid email or password.");
    }

    @Override
    public int insert(Student s) throws SQLException {

        String sql = "INSERT INTO users " +
                     "(name, email, password, department, year) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getFullName().trim());
            ps.setString(2, s.getEmail().trim().toLowerCase());
            ps.setString(3, s.getPassword());
            ps.setString(4, s.getDepartment());
            ps.setInt(5, s.getYearOfStudy());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return -1;
    }

    @Override
    public Student findById(int id) throws SQLException {

        String sql = "SELECT user_id, name, email, department, year " +
                     "FROM users WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    @Override
    public List<Student> findAll() throws SQLException {

        List<Student> list = new ArrayList<>();

        String sql = "SELECT user_id, name, email, department, year " +
                     "FROM users ORDER BY name";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    @Override
    public boolean update(Student s) throws SQLException {

        String sql = "UPDATE users SET name=?, department=?, year=? " +
                     "WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getFullName().trim());
            ps.setString(2, s.getDepartment());
            ps.setInt(3, s.getYearOfStudy());
            ps.setInt(4, s.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(int userId, String newPassword)
            throws SQLException {

        String sql = "UPDATE users SET password=? WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {

        String sql = "DELETE FROM users WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}
