package com.example.hello.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.example.hello.entity.User;
import com.example.hello.entity.UserInfo;

@Service
public interface UserService {
    
    User createUser(User user);
    
    User updateUser(User user);
    
    void deleteUser(String userId);
    
    User getUserById(String userId);
    
    User getUserByUsername(String username);
    
    List<User> getAllUsers();
    
    User createAdminUser(String username, String password, String email, String fullName);
    
    boolean isAdmin(String username);
    
    // 原有UserController需要的方法
    Map<String, Object> login(String phone, String password);
    
    UserInfo getUserInfo(UUID userId);
    
    UserInfo updateUserInfo(UUID userId, UserInfo userInfo);
    
    boolean changePassword(UUID userId, String oldPassword, String newPassword);
    
    // ==================== 用户管理扩展方法 ====================
    
    /**
     * 根据条件搜索用户
     */
    List<User> searchUsers(String keyword, String role, Boolean enabled);
    
    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStatistics();
    
    /**
     * 批量更新用户状态
     */
    int batchUpdateUserStatus(List<String> userIds, Boolean enabled);
    
    /**
     * 检查用户名是否可用
     */
    boolean isUsernameAvailable(String username, String excludeUserId);
    
    /**
     * 检查手机号是否可用
     */
    boolean isPhoneAvailable(String phone, String excludeUserId);
}