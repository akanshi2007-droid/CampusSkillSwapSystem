
package campusskillswap.dao;

import campusskillswap.interfaces.CrudDAO;
import campusskillswap.model.SwapRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Database operations for skill swap requests. */
public class SwapRequestDAO implements CrudDAO<SwapRequest> {

    private static final String BASE_SQL =
        "SELECT r.request_id, r.sender_id AS requester_id, " +
        "sender.name AS requester_name, " +
        "COALESCE(o.user_skill_id, 0) AS offer_id, " +
        "r.receiver_id AS owner_id, receiver.name AS owner_name, " +
        "sk.skill_name, r.status, r.request_date " +
        "FROM swap_requests r " +
        "JOIN users sender ON sender.user_id = r.sender_id " +
        "JOIN users receiver ON receiver.user_id = r.receiver_id " +
        "JOIN skills sk ON sk.skill_id = r.skill_id " +
        "LEFT JOIN user_skills o ON o.user_id = r.receiver_id " +
        "AND o.skill_id = r.skill_id " +
        "AND UPPER(o.skill_type) = 'OFFER' ";

    private SwapRequest mapRow(ResultSet rs) throws SQLException {

        SwapRequest request = new SwapRequest();

        request.setRequestId(rs.getInt("request_id"));
        request.setRequesterId(rs.getInt("requester_id"));
        request.setRequesterName(rs.getString("requester_name"));
        request.setOfferId(rs.getInt("offer_id"));
        request.setOwnerId(rs.getInt("owner_id"));
        request.setOwnerName(rs.getString("owner_name"));
        request.setSkillName(rs.getString("skill_name"));

        // Normalize status so Java comparisons work correctly.
        String status = rs.getString("status");

        request.setStatus(
            status == null
                ? SwapRequest.PENDING
                : status.trim().toUpperCase()
        );

        request.setRequestDate(rs.getTimestamp("request_date"));

        // The current swap_requests table has no message column.
        request.setMessage(null);

        return request;
    }

    private List<SwapRequest> query(String condition, int value)
            throws SQLException {

        List<SwapRequest> list = new ArrayList<>();
        String sql = BASE_SQL + condition;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    /** Requests received by a student. */
    public List<SwapRequest> findReceived(int ownerId)
            throws SQLException {

        return query(
            "WHERE r.receiver_id = ? ORDER BY r.request_date DESC",
            ownerId
        );
    }

    /** Requests sent by a student. */
    public List<SwapRequest> findSent(int requesterId)
            throws SQLException {

        return query(
            "WHERE r.sender_id = ? ORDER BY r.request_date DESC",
            requesterId
        );
    }

    /** Counts pending requests received by a student. */
    public int countPendingReceived(int ownerId) throws SQLException {

        String sql =
            "SELECT COUNT(*) FROM swap_requests " +
            "WHERE receiver_id = ? AND UPPER(status) = 'PENDING'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** Checks whether a pending request already exists for an offer. */
    public boolean alreadyRequested(int requesterId, int offerId)
            throws SQLException {

        String sql =
            "SELECT 1 FROM swap_requests r " +
            "JOIN user_skills o ON o.user_id = r.receiver_id " +
            "AND o.skill_id = r.skill_id " +
            "WHERE r.sender_id = ? AND o.user_skill_id = ? " +
            "AND UPPER(r.status) = 'PENDING'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requesterId);
            ps.setInt(2, offerId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Updates a request to ACCEPTED or REJECTED. */
    public boolean updateStatus(int requestId, String status)
            throws SQLException {

        String sql =
            "UPDATE swap_requests SET status = ? WHERE request_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status.trim().toUpperCase());
            ps.setInt(2, requestId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public int insert(SwapRequest request) throws SQLException {

        // Resolve the selected offer to its owner and skill.
        String lookupSql =
            "SELECT user_id, skill_id FROM user_skills " +
            "WHERE user_skill_id = ? AND UPPER(skill_type) = 'OFFER'";

        String insertSql =
            "INSERT INTO swap_requests " +
            "(sender_id, receiver_id, skill_id) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            int ownerId;
            int skillId;

            try (PreparedStatement ps = con.prepareStatement(lookupSql)) {

                ps.setInt(1, request.getOfferId());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException(
                            "The selected skill offer no longer exists."
                        );
                    }

                    ownerId = rs.getInt("user_id");
                    skillId = rs.getInt("skill_id");
                }
            }

            if (ownerId == request.getRequesterId()) {
                throw new SQLException(
                    "You cannot request your own skill."
                );
            }

            try (PreparedStatement ps = con.prepareStatement(
                insertSql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, request.getRequesterId());
                ps.setInt(2, ownerId);
                ps.setInt(3, skillId);

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        }

        return -1;
    }

    @Override
    public SwapRequest findById(int id) throws SQLException {

        String sql = BASE_SQL + "WHERE r.request_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    @Override
    public List<SwapRequest> findAll() throws SQLException {

        List<SwapRequest> list = new ArrayList<>();
        String sql = BASE_SQL + "ORDER BY r.request_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    @Override
    public boolean update(SwapRequest request) throws SQLException {
        return updateStatus(
            request.getRequestId(),
            request.getStatus()
        );
    }

    @Override
    public boolean delete(int id) throws SQLException {

        String sql =
            "DELETE FROM swap_requests WHERE request_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}
