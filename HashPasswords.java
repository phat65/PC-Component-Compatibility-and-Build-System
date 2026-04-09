import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class HashPasswords {
    public static void main(String[] args) {
        BCryptPasswordEncoder e = new BCryptPasswordEncoder();
        System.out.println("pass123=" + e.encode("pass123"));
        System.out.println("admin123=" + e.encode("admin123"));
        System.out.println("staff123=" + e.encode("staff123"));
        System.out.println("cus123=" + e.encode("cus123"));
    }
}
