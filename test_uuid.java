import java.util.UUID;

public class test_uuid {
    public static void main(String[] args) {
        // 测试UUID生成
        String id = UUID.randomUUID().toString();
        System.out.println("Generated UUID: " + id);
        System.out.println("UUID length: " + id.length());
        
        // 测试User实体构造
        User user = new User();
        System.out.println("User ID after construction: " + user.getId());
        System.out.println("User ID length: " + (user.getId() != null ? user.getId().length() : "null"));
    }
}

class User {
    private String id;
    
    public User() {
        this.id = UUID.randomUUID().toString();
    }
    
    public String getId() {
        return id;
    }
}

