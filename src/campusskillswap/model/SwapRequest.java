package campusskillswap.model;

import campusskillswap.interfaces.Displayable;
import java.sql.Timestamp;

/** A request from one student to learn a skill offered by another (table: swap_requests). */
public class SwapRequest implements Displayable {
    public static final String PENDING = "PENDING";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REJECTED = "REJECTED";

    private int requestId;
    private int requesterId;
    private String requesterName;
    private int offerId;
    private int ownerId;
    private String ownerName;
    private String skillName;
    private String message;
    private String status = PENDING;
    private Timestamp requestDate;

    public SwapRequest() {
    }

    @Override
    public String getDisplayText() {
        return requesterName + " -> " + ownerName + " : " + skillName + " [" + status + "]";
    }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public int getOfferId() { return offerId; }
    public void setOfferId(int offerId) { this.offerId = offerId; }
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getRequestDate() { return requestDate; }
    public void setRequestDate(Timestamp requestDate) { this.requestDate = requestDate; }
}
