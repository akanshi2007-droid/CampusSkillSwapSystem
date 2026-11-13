package campusskillswap.dao;

import campusskillswap.interfaces.CrudDAO;
import campusskillswap.model.Skill;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Database operations for the skills catalog table. */
public class SkillDAO implements CrudDAO<Skill> {

    private Skill mapRow(ResultSet rs) throws SQLException {
        return new Skill(rs.getInt("skill_id"), rs.getString("skill_name"), rs.getString("category"));
    }

    @Override
    public int insert(Skill skill) throws SQLException {
        String sql = "INSERT INTO skills (skill_name, category) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, skill.getName());
            ps.setString(2, skill.getCategory());
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
    public Skill findById(int id) throws SQLException {
        String sql = "SELECT * FROM skills WHERE skill_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public Skill findByName(String name) throws SQLException {
        String sql = "SELECT * FROM skills WHERE LOWER(skill_name) = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    @Override
    public List<Skill> findAll() throws SQLException {
        List<Skill> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM skills")) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        Collections.sort(list);         // uses Skill.compareTo()
        return list;
    }

    public List<Skill> findByCategory(String category) throws SQLException {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT * FROM skills WHERE category = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        Collections.sort(list);
        return list;
    }

    @Override
    public boolean update(Skill skill) throws SQLException {
        String sql = "UPDATE skills SET skill_name=?, category=? WHERE skill_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, skill.getName());
            ps.setString(2, skill.getCategory());
            ps.setInt(3, skill.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM skills WHERE skill_id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
