import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Encapsulation: Fields are private, access is controlled by public methods.
public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String fullName;
    private String field;
    private String password;
    private List<Skill> skills = new ArrayList<>();

    // Constructor is required for user creation/registration
    public UserProfile(String fullName, String password, String userId) {
        this.fullName = fullName;
        this.password = password;
        this.userId = userId;
    }

    public void addSkill(String name, String type, int proficiency) {
        // Encapsulation: The list is manipulated internally
        skills.add(new Skill(name, type, proficiency));
    }

    // Encapsulation: Provides controlled access to the skills list
    public List<Skill> getSkills() {
        return skills;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}