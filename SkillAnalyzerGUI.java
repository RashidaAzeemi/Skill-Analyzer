import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// Inheritance: SkillAnalyzerGUI extends JFrame
public class SkillAnalyzerGUI extends JFrame {

    // Color Palette
    private static final Color PRIMARY_COLOR = new Color(52, 73, 94); // Dark Blue/Gray
    private static final Color SECONDARY_COLOR = new Color(44, 62, 80); // Even Darker Blue/Gray (Background)
    private static final Color ACCENT_COLOR = new Color(46, 204, 113); // Emerald Green (Success/Primary Action)
    private static final Color TEXT_COLOR = new Color(236, 240, 241); // Light Text

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    // Current user's profile once logged in
    private UserProfile currentUserProfile;

    private Map<String, String[]> careerPaths;

    // Key: userId, Value: UserProfile object
    private Map<String, UserProfile> registeredUsers;

    // UI Elements for Profile Page
    private JTextField fieldField;
    private JTextField skillNameField;
    private JComboBox<String> skillTypeCombo;
    private JTextField proficiencyField;
    private DefaultListModel<Skill> skillListModel = new DefaultListModel<>();

    // UI Elements for Analyze Page
    private JComboBox<String> pathCombo;
    private JTextArea reportArea;

    public SkillAnalyzerGUI() {
        // Frame Setup
        setTitle("🌟 Skill Analyzer");
        setSize(550, 700);
        // Ensure data is saved on exit
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                UserPersistence.saveUsers(registeredUsers);
                System.exit(0);
            }
        });
        setLocationRelativeTo(null);

        // Load data when the app starts
        careerPaths = CareerPathLoader.loadCareerPaths();
        registeredUsers = UserPersistence.loadUsers(); // Load all existing users

        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("TextField.background", PRIMARY_COLOR);
        UIManager.put("TextField.foreground", TEXT_COLOR);
        UIManager.put("TextField.caretColor", TEXT_COLOR);
        UIManager.put("ComboBox.background", PRIMARY_COLOR);
        UIManager.put("ComboBox.foreground", TEXT_COLOR);
        UIManager.put("List.background", PRIMARY_COLOR);
        UIManager.put("List.foreground", TEXT_COLOR);
        UIManager.put("PasswordField.background", PRIMARY_COLOR);
        UIManager.put("PasswordField.foreground", TEXT_COLOR);


        // Add Panels to CardLayout
        mainPanel.add(createRegistrationPage(), "Registration");
        mainPanel.add(createLoginPage(), "Login");
        mainPanel.add(createDefineProfilePage(), "DefineProfile");
        mainPanel.add(createAnalyzePage(), "Analyze");

        add(mainPanel);
        cardLayout.show(mainPanel, "Registration");
        setVisible(true);
    }

    private String generateUserId(String fullName) {
        String cleanName = fullName.replaceAll("\\s+", "");
        String prefix = cleanName.isEmpty() ? "" : cleanName.substring(0, 1).toUpperCase();
        // Append a random 4-digit number
        int randomSuffix = new Random().nextInt(9000) + 1000;
        String baseId = prefix + cleanName + randomSuffix;

        // Ensure uniqueness (loop until a unique ID is generated)
        while (registeredUsers.containsKey(baseId)) {
            randomSuffix = new Random().nextInt(9000) + 1000;
            baseId = prefix + cleanName + randomSuffix;
        }
        return baseId;
    }

    private JPanel createRegistrationPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SECONDARY_COLOR);

        JPanel inputContainer = new JPanel(new GridLayout(4, 2, 15, 15));
        inputContainer.setBackground(PRIMARY_COLOR);
        inputContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 3),
                new EmptyBorder(40, 40, 40, 40)
        ));

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(SECONDARY_COLOR);
        centerWrapper.add(inputContainer);

        JLabel header = new JLabel("<html><h1 style='color: " +
                String.format("#%06x", ACCENT_COLOR.getRGB() & 0xFFFFFF) +
                "; font-size: 24pt;'>Skill Analyzer</h1><p>New User Registration</p></html>", SwingConstants.CENTER);
        header.setBorder(new EmptyBorder(50, 0, 50, 0));

        JTextField regFullNameField = new JTextField(15);
        regFullNameField.setToolTipText("Enter your full name");
        JPasswordField regPasswordField = new JPasswordField(15);
        regPasswordField.setToolTipText("Minimum 8 characters required");
        JButton registerButton = new JButton("REGISTER");
        JButton goToLoginButton = new JButton("Already Registered? Login");

        // Styling
        registerButton.setBackground(ACCENT_COLOR);
        registerButton.setForeground(PRIMARY_COLOR);
        goToLoginButton.setBackground(PRIMARY_COLOR);
        goToLoginButton.setForeground(TEXT_COLOR);

        inputContainer.add(new JLabel("Full Name:"));
        inputContainer.add(regFullNameField);
        inputContainer.add(new JLabel("Create Password:"));
        inputContainer.add(regPasswordField);
        inputContainer.add(registerButton);
        inputContainer.add(goToLoginButton);

        panel.add(header, BorderLayout.NORTH);
        panel.add(centerWrapper, BorderLayout.CENTER);

        registerButton.addActionListener(e -> {
            String fullName = regFullNameField.getText().trim();
            String password = new String(regPasswordField.getPassword());

            if (fullName.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Full Name and Password cannot be empty.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            } else if (password.length() < 8) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else {
                // Generate User ID and Register
                String userId = generateUserId(fullName);
                UserProfile newUser = new UserProfile(fullName, password, userId);
                registeredUsers.put(userId, newUser);
                UserPersistence.saveUsers(registeredUsers); // File Handling: Save data

                JOptionPane.showMessageDialog(this,
                        "Registration Successful! Your User ID is: " + userId + ". Please use this to log in.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Clear fields and switch to login
                regFullNameField.setText("");
                regPasswordField.setText("");
                cardLayout.show(mainPanel, "Login");
            }
        });

        goToLoginButton.addActionListener(e -> cardLayout.show(mainPanel, "Login"));

        return panel;
    }

    private JPanel createLoginPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SECONDARY_COLOR);

        JPanel inputContainer = new JPanel(new GridLayout(4, 2, 15, 15));
        inputContainer.setBackground(PRIMARY_COLOR);
        inputContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 3),
                new EmptyBorder(40, 40, 40, 40)
        ));

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(SECONDARY_COLOR);
        centerWrapper.add(inputContainer);

        JLabel header = new JLabel("<html><h1 style='color: " +
                String.format("#%06x", ACCENT_COLOR.getRGB() & 0xFFFFFF) +
                "; font-size: 24pt;'>Skill Analyzer</h1><p>User Login</p></html>", SwingConstants.CENTER);
        header.setBorder(new EmptyBorder(50, 0, 50, 0));

        JTextField userIdField = new JTextField(15);
        userIdField.setToolTipText("Enter your generated User ID");
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setToolTipText("Enter your password");
        JButton loginButton = new JButton("LOGIN");
        JButton goToRegisterButton = new JButton("New User? Register");
        // Removed original logoutButton which was redundant here

        // Styling
        loginButton.setBackground(ACCENT_COLOR);
        loginButton.setForeground(PRIMARY_COLOR);
        goToRegisterButton.setBackground(PRIMARY_COLOR);
        goToRegisterButton.setForeground(TEXT_COLOR);


        inputContainer.add(new JLabel("User ID:"));
        inputContainer.add(userIdField);
        inputContainer.add(new JLabel("Password:"));
        inputContainer.add(passwordField);
        inputContainer.add(loginButton);
        inputContainer.add(goToRegisterButton);


        panel.add(header, BorderLayout.NORTH);
        panel.add(centerWrapper, BorderLayout.CENTER);
        // Removed redundant southPanel with logoutButton

        loginButton.addActionListener(e -> {
            String userId = userIdField.getText().trim();
            String password = new String(passwordField.getPassword());

            // Check credentials against the loaded map
            if (!registeredUsers.containsKey(userId)) {
                JOptionPane.showMessageDialog(this, "User ID not found.", "Login Error", JOptionPane.ERROR_MESSAGE);
            } else {
                UserProfile user = registeredUsers.get(userId);
                if (!user.getPassword().equals(password)) {
                    JOptionPane.showMessageDialog(this, "Invalid Password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    currentUserProfile = user; // Set the current user
                    JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + user.getFullName() + ".", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Clear fields
                    userIdField.setText("");
                    passwordField.setText("");

                    loadUserProfileData();

                    cardLayout.show(mainPanel, "DefineProfile");
                }
            }
        });

        goToRegisterButton.addActionListener(e -> cardLayout.show(mainPanel, "Registration"));

        return panel;
    }

    private void loadUserProfileData() {
        if (currentUserProfile.getField() != null) {
            fieldField.setText(currentUserProfile.getField());
        } else {
            fieldField.setText("");
        }

        // Clear old list and add current skills
        skillListModel.clear();
        for (Skill skill : currentUserProfile.getSkills()) {
            skillListModel.addElement(skill);
        }
    }

    private void performLogout() {
        // Save current user data before logging out
        if (currentUserProfile != null) {
            UserPersistence.saveUsers(registeredUsers);
        }
        currentUserProfile = null;
        skillListModel.clear();
        // Optionally clear analyze report
        if (reportArea != null) {
            reportArea.setText("");
        }
        JOptionPane.showMessageDialog(this, "You have been logged out.", "Logout", JOptionPane.INFORMATION_MESSAGE);
        cardLayout.show(mainPanel, "Login");
    }

    private JPanel createDefineProfilePage() {
        JPanel profilePanel = new JPanel(new BorderLayout(10, 10));
        profilePanel.setBackground(SECONDARY_COLOR);
        profilePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("<html><h2 style='color: " +
                String.format("#%06x", ACCENT_COLOR.getRGB() & 0xFFFFFF) +
                ";'>1. Define Your Profile</h2></html>", SwingConstants.CENTER);
        profilePanel.add(header, BorderLayout.NORTH);

        //Center: Input Form
        JPanel inputForm = new JPanel(new GridLayout(6, 2, 10, 10));
        inputForm.setBackground(SECONDARY_COLOR);

        fieldField = new JTextField();
        fieldField.setToolTipText("e.g., Computer Science, Electrical Engineering");
        skillNameField = new JTextField();
        skillNameField.setToolTipText("e.g., Python, Negotiation, Leadership");
        skillTypeCombo = new JComboBox<>(new String[]{"Technical", "Soft"});
        proficiencyField = new JTextField();
        proficiencyField.setToolTipText("Enter a number between 1 and 10");

        JButton addSkillButton = new JButton("Add Skill");
        addSkillButton.setBackground(ACCENT_COLOR);
        addSkillButton.setForeground(PRIMARY_COLOR);

        inputForm.add(new JLabel("Your Field:"));
        inputForm.add(fieldField);
        inputForm.add(new JLabel("Enter Skill:"));
        inputForm.add(skillNameField);
        inputForm.add(new JLabel("Skill Type:"));
        inputForm.add(skillTypeCombo);
        inputForm.add(new JLabel("Proficiency (1-10):"));
        inputForm.add(proficiencyField);
        inputForm.add(new JLabel(""));
        inputForm.add(addSkillButton);

        JList<Skill> skillJList = new JList<>(skillListModel);
        skillJList.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 5));

        JButton removeSkillButton = new JButton("Remove Selected Skill");
        removeSkillButton.setBackground(new Color(231, 76, 60)); // Red color
        removeSkillButton.setForeground(TEXT_COLOR);


        JButton nextButton = new JButton("2. Choose Career Path & Analyze");
        nextButton.setBackground(PRIMARY_COLOR);
        nextButton.setForeground(TEXT_COLOR);

        JButton backButton = new JButton("LOGOUT / Back to Login");
        backButton.setBackground(PRIMARY_COLOR.darker());
        backButton.setForeground(TEXT_COLOR);


        JPanel actionButtonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        actionButtonPanel.add(removeSkillButton);
        actionButtonPanel.add(nextButton);


        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(actionButtonPanel, BorderLayout.NORTH);
        southPanel.add(backButton, BorderLayout.SOUTH); // Place back button at the very bottom


        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(new JScrollPane(skillJList), BorderLayout.CENTER);
        listPanel.add(southPanel, BorderLayout.SOUTH); // Use the new south panel


        profilePanel.add(inputForm, BorderLayout.NORTH);
        profilePanel.add(listPanel, BorderLayout.CENTER);

        addSkillButton.addActionListener(e -> addSkill());
        removeSkillButton.addActionListener(e -> {
            int selectedIndex = skillJList.getSelectedIndex();
            if (selectedIndex != -1) {
                Skill skillToRemove = skillListModel.getElementAt(selectedIndex);
                skillListModel.remove(selectedIndex);
                // Encapsulation: Remove from the UserProfile object as well
                currentUserProfile.getSkills().removeIf(s -> s.getName().equals(skillToRemove.getName()) && s.getType().equals(skillToRemove.getType()));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a skill to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        nextButton.addActionListener(e -> {
            if (skillListModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add at least one skill before analyzing.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Encapsulation: Update the UserProfile before switching
            currentUserProfile.setField(fieldField.getText());
            // File Handling: Save data immediately after profile definition
            UserPersistence.saveUsers(registeredUsers);
            cardLayout.show(mainPanel, "Analyze");
        });

        // Back/Logout Action
        backButton.addActionListener(e -> performLogout());

        return profilePanel;
    }

    private void addSkill() {
        try {
            String name = skillNameField.getText().trim();
            String type = (String) skillTypeCombo.getSelectedItem();
            int proficiency = Integer.parseInt(proficiencyField.getText().trim());

            if (name.isEmpty() || proficiency < 1 || proficiency > 10) {
                JOptionPane.showMessageDialog(this, "Please enter a valid skill name and proficiency (1-10).", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if skill already exists to avoid duplicates
            boolean exists = currentUserProfile.getSkills().stream()
                    .anyMatch(s -> s.getName().equalsIgnoreCase(name) && s.getType().equals(type));

            if (exists) {
                JOptionPane.showMessageDialog(this, "This skill and type combination is already added.", "Duplicate Skill", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Skill newSkill = new Skill(name, type, proficiency);
            // Encapsulation: Add to the UserProfile object
            currentUserProfile.addSkill(name, type, proficiency);
            skillListModel.addElement(newSkill);

            // Clear input fields
            skillNameField.setText("");
            proficiencyField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Proficiency must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createAnalyzePage() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel selectionPanel = new JPanel(new FlowLayout());
        selectionPanel.setBackground(PRIMARY_COLOR);
        selectionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        pathCombo = new JComboBox<>(careerPaths.keySet().toArray(new String[0]));
        pathCombo.setToolTipText("Select the career path you want to analyze your skills against");

        // Polymorphism: Custom renderer for ComboBox items
        pathCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(ACCENT_COLOR.darker());
                    setForeground(Color.WHITE);
                } else {
                    setBackground(PRIMARY_COLOR);
                    setForeground(TEXT_COLOR);
                }
                return this;
            }
        });

        JButton analyzeButton = new JButton("Run Skill Analysis");
        analyzeButton.setBackground(ACCENT_COLOR);
        analyzeButton.setForeground(PRIMARY_COLOR);

        selectionPanel.add(new JLabel("2. Choose Career Path:"));
        selectionPanel.add(pathCombo);
        selectionPanel.add(analyzeButton);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setBackground(PRIMARY_COLOR);
        reportArea.setForeground(TEXT_COLOR);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR.darker(), 2));

        JLabel header = new JLabel("<html><h2 style='color: " +
                String.format("#%06x", ACCENT_COLOR.getRGB() & 0xFFFFFF) +
                ";'>3. Analysis Report</h2></html>", SwingConstants.CENTER);

        JButton backButton = new JButton("LOGOUT / Back to Login");
        backButton.setBackground(PRIMARY_COLOR.darker());
        backButton.setForeground(TEXT_COLOR);
        backButton.addActionListener(e -> performLogout());


        JPanel reportContainer = new JPanel(new BorderLayout());
        reportContainer.add(scrollPane, BorderLayout.CENTER);
        reportContainer.add(backButton, BorderLayout.SOUTH); // Place back button below the report

        scrollPane.setPreferredSize(new Dimension(500, 350)); // Adjusted size

        panel.add(header, BorderLayout.NORTH);
        panel.add(selectionPanel, BorderLayout.CENTER);
        panel.add(reportContainer, BorderLayout.SOUTH);

        analyzeButton.addActionListener(e -> runAnalysis());

        return panel;
    }

    private void runAnalysis() {
        String selectedPath = (String) pathCombo.getSelectedItem();
        if (selectedPath == null || currentUserProfile.getSkills().isEmpty()) {
            reportArea.setText("Please select a path and add skills first.");
            return;
        }

        String[] requiredSkills = careerPaths.get(selectedPath);
        String requiredTechSkill = requiredSkills[0].trim();
        String requiredSoftSkill = requiredSkills[1].trim();

        int matchScore = 0;
        StringBuilder report = new StringBuilder();

        report.append("Career Match Score:\n");
        report.append("--------------------------------------------------\n");

        int techProficiency = findSkillProficiency(requiredTechSkill, "Technical");
        String techStatus = techProficiency >= 7 ? "Met" : "Major Gap";
        if (techProficiency >= 7) matchScore += 50;
        report.append(String.format("Technical Skill (%s): Status: %s (Prof: %d/10)\n", requiredTechSkill, techStatus, techProficiency));

        int softProficiency = findSkillProficiency(requiredSoftSkill, "Soft");
        String softStatus = softProficiency >= 7 ? "Met" : "Major Gap";
        if (softProficiency >= 7) matchScore += 50;
        report.append(String.format("Soft Skill (%s): Status: %s (Prof: %d/10)\n", requiredSoftSkill, softStatus, softProficiency));

        report.insert(0, String.format("Overall Match Score: %d%%\n\n", matchScore));

        report.append("\nPersonalized Roadmap:\n");
        report.append("--------------------------------------------------\n");

        if (techStatus.equals("Major Gap")) {
            report.append(String.format("-> GAP: %s. Start here: Begin with foundational courses to build a solid understanding.\n", requiredTechSkill));
        } else {
            report.append(String.format("-> STRENGTH: %s. Great job! Focus on advanced application and mentoring.\n", requiredTechSkill));
        }

        if (softStatus.equals("Major Gap")) {
            report.append(String.format("-> GAP: %s. Practice and apply these skills in team projects or leadership roles.\n", requiredSoftSkill));
        } else {
            report.append(String.format("-> STRENGTH: %s. Maintain and leverage this skill in professional settings.\n", requiredSoftSkill));
        }

        reportArea.setText(report.toString());
    }

    private int findSkillProficiency(String name, String type) {
        // Encapsulation: Accessing the skills list via the getter
        for (Skill skill : currentUserProfile.getSkills()) {
            if (skill.getName().equalsIgnoreCase(name) && skill.getType().equals(type)) {
                return skill.getProficiency();
            }
        }
        return 0; // Skill not found
    }

    // MAIN METHOD
    public static void main(String[] args) {
        // The main thread is responsible for initializing the GUI components
        SwingUtilities.invokeLater(SkillAnalyzerGUI::new);
    }

    // Placeholder classes (assuming they exist in other files)
    private static class UserProfile {
        private String fullName;
        private String password;
        private String userId;
        private String field;
        private java.util.List<Skill> skills = new java.util.ArrayList<>();

        public UserProfile(String fullName, String password, String userId) {
            this.fullName = fullName;
            this.password = password;
            this.userId = userId;
        }

        public String getFullName() { return fullName; }
        public String getPassword() { return password; }
        public String getUserId() { return userId; }
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public java.util.List<Skill> getSkills() { return skills; }
        public void addSkill(String name, String type, int proficiency) {
            this.skills.add(new Skill(name, type, proficiency));
        }
    }

    private static class Skill {
        private String name;
        private String type;
        private int proficiency;

        public Skill(String name, String type, int proficiency) {
            this.name = name;
            this.type = type;
            this.proficiency = proficiency;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public int getProficiency() { return proficiency; }

        @Override
        public String toString() {
            return String.format("%s (%s): %d/10", name, type, proficiency);
        }
    }

    private static class UserPersistence {
        // Placeholder for file saving/loading logic
        public static void saveUsers(Map<String, UserProfile> users) {
            // Simulate saving to file
            System.out.println("Saving " + users.size() + " users...");
        }
        public static Map<String, UserProfile> loadUsers() {
            // Simulate loading from file
            System.out.println("Loading users...");
            return new HashMap<>();
        }
    }

    private static class CareerPathLoader {
        public static Map<String, String[]> loadCareerPaths() {
            Map<String, String[]> paths = new HashMap<>();
            paths.put("Data Scientist", new String[]{"Python", "Communication"});
            paths.put("Software Engineer", new String[]{"Java", "Problem Solving"});
            paths.put("Robotics Engineer", new String[]{"Control Systems", "Teamwork"});
            paths.put("Cybersecurity Analyst", new String[]{"Networking", "Critical Thinking"});
            paths.put("AI Engineer", new String[]{"Deep Learning", "Communication"});
            paths.put("Machine Learning Engineer", new String[]{"Deep Learning", "Communication"});
            paths.put("Application Developer", new String[]{"Java/HTML/CSS", "problem Solving"});
            paths.put("Full Stack Web Developer", new String[]{"JavaScript", "Teamwork"});
            paths.put("Automation Engineer", new String[]{"Control Systems", "Teamwork"});
            paths.put("IOT (Internet of Things) Engineer", new String[]{"Embedded Systems", "Critical Thinking"});
            paths.put("UX Designer", new String[]{"Figma", "Empathy"});
            paths.put("Marketing Specialist", new String[]{"SEO", "Creativity"});
            return paths;
        }
    }
}