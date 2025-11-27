import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserPersistence {
    private static final String DATA_FILE = "user_data.ser";

    // Encapsulation: Utility class for file handling, methods are static

    public static Map<String, UserProfile> loadUsers() {
        // File Handling: Reading objects from a serialized file
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            // Unchecked cast is safe as we control what's written.
            return (Map<String, UserProfile>) ois.readObject();
        } catch (FileNotFoundException e) {
            // First run, file doesn't exist yet. Return an empty map.
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading user data: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }


    public static void saveUsers(Map<String, UserProfile> users) {
        // File Handling: Writing objects to a serialized file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}