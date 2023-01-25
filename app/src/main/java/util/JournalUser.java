package util;
import android.app.Application;

public class JournalUser extends Application {
    private String username;
    private String userId;

    private static JournalUser instance;
//  Singleton
    public static JournalUser getInstance() {
        if(instance == null) {
            instance = new JournalUser();
        }
        return instance;
    }

// Empty constructor
    public JournalUser() {
    }

// Getters
    public String getUsername() {
        return username;
    }
    public String getUserId() {
        return userId;
    }

// Setters
    public void setUsername(String username) {
        this.username = username;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
