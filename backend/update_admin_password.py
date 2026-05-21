#!/usr/bin/env python3
"""
Update admin user password with proper BCrypt hash
"""
import pg8000.dbapi

# Database connection parameters
DB_HOST = "192.168.1.160"
DB_PORT = 5432
DB_NAME = "crm"
DB_USER = "postgres"
DB_PASSWORD = "Afa123456@"

# Proper BCrypt hash for password "Afa123456@"
BCRYPT_PASSWORD = "$2b$10$VvsF4ejXuoBNikm5kWZPS.IkAsH/6udDnjVEpDTa851iRReJRpDa6"

def main():
    try:
        # Connect to database
        conn = pg8000.dbapi.connect(
            host=DB_HOST,
            port=DB_PORT,
            database=DB_NAME,
            user=DB_USER,
            password=DB_PASSWORD
        )
        print("✓ Connected to PostgreSQL database")
        
        cursor = conn.cursor()
        
        # Update admin user's password
        update_sql = """
        UPDATE users 
        SET password = %s
        WHERE username = %s
        """
        cursor.execute(update_sql, [BCRYPT_PASSWORD, 'admin'])
        conn.commit()
        
        print("✓ Admin password updated successfully")
        print("  Username: admin")
        print("  Password: Afa123456@")
        
        # Verify the update
        cursor.execute("SELECT username, password, role FROM users WHERE username = 'admin'")
        result = cursor.fetchone()
        if result:
            print(f"\n✓ Verification:")
            print(f"  Username: {result[0]}")
            print(f"  Role: {result[2]}")
            print(f"  Password hash starts with: {result[1][:20]}...")
        
        cursor.close()
        conn.close()
        
    except Exception as e:
        print(f"Error: {e}")
        import traceback
        traceback.print_exc()
        raise

if __name__ == "__main__":
    main()
