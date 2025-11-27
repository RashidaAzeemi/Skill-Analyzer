import java.io.Serializable;

// Encapsulation: Fields are private, access is controlled by public methods.
public class Skill implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String type; // Technical or Soft
    private int proficiency; // 1-10

    public Skill(String name, String type, int proficiency) {
        this.name = name;
        this.type = type;
        this.proficiency = proficiency;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getProficiency() {
        return proficiency;
    }

    // Polymorphism (Method Overriding): Custom representation for JList
    @Override
    public String toString() {
        String skillType = type.equals("Technical") ? "Tech" : "Soft";
        return name + " (" + skillType + " | Prof: " + proficiency + "/10)";
    }
}