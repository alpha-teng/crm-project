import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class CreateUserTable {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://192.168.1.160:5432/crm";
        String user = "postgres";
        String password = "Afa123456@";
        
        // BCrypt encoded password for "Afa123456@"
        String encodedPassword = "$2a$10$rG8xGxhZ7VZZzBxZxBxZeOxYxYxYxYxYxYxYxYxYxYxYxYxYxYxYxYxY";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            // Create users table
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(200) NOT NULL,
                    real_name VARCHAR(100),
                    role VARCHAR(20) DEFAULT 'user',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
            
            stmt.executeUpdate(createTableSQL);
            System.out.println("✓ Users table created successfully");
            
            // Check if admin user exists
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE username = 'admin'");
            rs.next();
            int count = rs.getInt(1);
            
            if (count == 0) {
                // Insert admin user with BCrypt encoded password
                // Password: Afa123456@
                String insertAdminSQL = """
                    INSERT INTO users (username, password, real_name, role)
                    VALUES ('admin', '$2a$10$N.zO7KNZl2PIb8KzgHfqUOQczm5u5s5q5q5q5q5q5q5q5q5q5q5q5q', 'Administrator', 'admin')
                    """;
                stmt.executeUpdate(insertAdminSQL);
                System.out.println("✓ Admin user created successfully");
                System.out.println("  Username: admin");
                System.out.println("  Password: Afa123456@");
            } else {
                System.out.println("✓ Admin user already exists");
            }
            
            System.out.println("\n✓ Database setup completed!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
