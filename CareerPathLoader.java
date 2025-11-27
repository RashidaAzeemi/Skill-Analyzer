import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CareerPathLoader {

    // Define the file name as a constant
    private static final String CAREER_PATH_FILE = "career_paths.txt";

    public static Map<String, String[]> loadCareerPaths() {
        Map<String, String[]> paths = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(CAREER_PATH_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String pathNameCombined = parts[0].trim();
                    String[] requiredSkills = parts[1].split(";");

                    // Ensure both technical and soft skills are present
                    if (requiredSkills.length == 2) {

                        // Split the combined path name by '/'
                        String[] pathNames = pathNameCombined.split("/");

                        for (String pathName : pathNames) {
                            String individualPath = pathName.trim();

                            // Ensure the path name is not empty
                            if (!individualPath.isEmpty()) {
                                // Put the individual path name into the map (for the drop-down)
                                paths.put(individualPath, requiredSkills); // [0]=Tech, [1]=Soft
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            // Error handling if the file is not found or cannot be read
            System.err.println("Error reading " + CAREER_PATH_FILE + ". Please ensure the file is in the project root directory.");
        }
        return paths;
    }
}