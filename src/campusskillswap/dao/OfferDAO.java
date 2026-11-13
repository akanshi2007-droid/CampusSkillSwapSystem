
package campusskillswap.dao;

import campusskillswap.exceptions.DuplicateEntryException;
import campusskillswap.interfaces.CrudDAO;
import campusskillswap.model.SkillOffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Database operations for skills offered by students. */
public class OfferDAO implements CrudDAO<SkillOffer> {

    private static final String BASE_SQL =
        "SELECT o.user_skill_id AS offer_id, o.user_id AS student_id, " +
        "u.name AS owner_name, o.skill_id, sk.skill_name, sk.category, " +
        "COALESCE(sk.description, '') AS offer_description " +
        "FROM user_skills o " +
        "JOIN users u ON u.user_id = o.user_id " +
        "JOIN skills sk ON sk.skill_id = o.skill_id " +
        "WHERE UPPER(o.skill_type) = 'OFFER' ";

    private SkillOffer mapRow(ResultSet rs) throws SQLException {
        SkillOffer offer = new SkillOffer();

        offer.setOfferId(rs.getInt("offer_id"));
        offer.setStudentId(rs.getInt("student_id"));
        offer.setOwnerName(rs.getString("owner_name"));
        offer.setSkillId(rs.getInt("skill_id"));
        offer.setSkillName(rs.getString("skill_name"));
        offer.setCategory(rs.getString("category"));

        // These details do not have columns in the current database.
        offer.setLevel("Not specified");
        offer.setDescription(rs.getString("offer_description"));
        offer.setExperienceYears(0);
        offer.setWeekendsOnly(false);

        return offer;
    }

    public int addOffer(SkillOffer offer)
            throws SQLException, DuplicateEntryException {

        if (offerExists(offer.getStudentId(), offer.getSkillId())) {
            throw new DuplicateEntryException(
                "You are already offering this skill."
            );
        }

        return insert(offer);
    }

    private boolean offerExists(int studentId, int skillId)
            throws SQLException {

        String sql =
            "SELECT 1 FROM user_skills " +
            "WHERE user_id = ? AND skill_id = ? " +
            "AND UPPER(skill_type) = 'OFFER'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, skillId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<SkillOffer> search(String category, String keyword)
            throws SQLException {

        List<SkillOffer> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(BASE_SQL);
        boolean filterCategory =
            category != null && !category.equalsIgnoreCase("All");

        boolean filterKeyword =
            keyword != null && !keyword.trim().isEmpty();

        if (filterCategory) {
            sql.append("AND sk.category = ? ");
        }

        if (filterKeyword) {
            sql.append(
                "AND (LOWER(sk.skill_name) LIKE ? " +
                "OR LOWER(u.name) LIKE ? " +
                "OR LOWER(sk.category) LIKE ?) "
            );
        }

        sql.append("ORDER BY sk.skill_name");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int index = 1;

            if (filterCategory) {
                ps.setString(index++, category);
            }

            if (filterKeyword) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(index++, pattern);
                ps.setString(index++, pattern);
                ps.setString(index, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    public List<SkillOffer> findByStudent(int studentId)
            throws SQLException {

        List<SkillOffer> list = new ArrayList<>();

        String sql = BASE_SQL +
            "AND o.user_id = ? ORDER BY sk.skill_name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    public int countByStudent(int studentId) throws SQLException {

        String sql =
            "SELECT COUNT(*) FROM user_skills " +
            "WHERE user_id = ? AND UPPER(skill_type) = 'OFFER'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public int insert(SkillOffer offer) throws SQLException {

        String sql =
            "INSERT INTO user_skills (user_id, skill_id, skill_type) " +
            "VALUES (?, ?, 'OFFER')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, offer.getStudentId());
            ps.setInt(2, offer.getSkillId());

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
    public SkillOffer findById(int id) throws SQLException {

        String sql = BASE_SQL + "AND o.user_skill_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    @Override
    public List<SkillOffer> findAll() throws SQLException {
        return search("All", "");
    }

    @Override
    public boolean update(SkillOffer offer) throws SQLException {

        String sql =
            "UPDATE user_skills SET user_id = ?, skill_id = ?, " +
            "skill_type = 'OFFER' WHERE user_skill_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, offer.getStudentId());
            ps.setInt(2, offer.getSkillId());
            ps.setInt(3, offer.getOfferId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int offerId) throws SQLException {

        String sql =
            "DELETE FROM user_skills WHERE user_skill_id = ? " +
            "AND UPPER(skill_type) = 'OFFER'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, offerId);
            return ps.executeUpdate() > 0;
        }
    }
}
