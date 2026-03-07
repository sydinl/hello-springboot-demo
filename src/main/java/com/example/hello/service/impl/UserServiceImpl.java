package com.example.hello.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.hello.entity.User;
import com.example.hello.entity.UserInfo;
import com.example.hello.repository.UserRepository;
import com.example.hello.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public User createUser(User user) {
        try {
            // 加密密码
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // 设置默认角色
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }
            
            User savedUser = userRepository.save(user);
            log.info("创建用户成功，用户名：{}", savedUser.getUsername());
            return savedUser;
            
        } catch (Exception e) {
            log.error("创建用户失败", e);
            throw new RuntimeException("创建用户失败", e);
        }
    }
    
    @Override
    @Transactional
    public User updateUser(User user) {
        try {
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("用户不存在，用户ID：" + user.getId()));
            
            // 更新用户信息
            if (user.getUsername() != null) {
                existingUser.setUsername(user.getUsername());
            }
            if (user.getFullName() != null) {
                existingUser.setFullName(user.getFullName());
            }
            if (user.getPhone() != null) {
                existingUser.setPhone(user.getPhone());
            }
            if (user.getRole() != null) {
                existingUser.setRole(user.getRole());
            }
            if (user.getGender() != null) {
                existingUser.setGender(user.getGender());
            }
            if (user.getBirthdate() != null) {
                existingUser.setBirthdate(user.getBirthdate());
            }
            if (user.getPoints() != null) {
                existingUser.setPoints(user.getPoints());
            }
            if (user.getBalance() != null) {
                existingUser.setBalance(user.getBalance());
            }
            if (user.getMemberLevel() != null) {
                existingUser.setMemberLevel(user.getMemberLevel());
            }
            if (user.getAvatar() != null) {
                existingUser.setAvatar(user.getAvatar());
            }
            if (user.getEnabled() != null) {
                existingUser.setEnabled(user.getEnabled());
            }
            
            User updatedUser = userRepository.save(existingUser);
            log.info("更新用户成功，用户ID：{}", user.getId());
            return updatedUser;
            
        } catch (Exception e) {
            log.error("更新用户失败，用户ID：{}", user.getId(), e);
            throw new RuntimeException("更新用户失败", e);
        }
    }
    
    @Override
    @Transactional
    public void deleteUser(String userId) {
        try {
            if (!userRepository.existsById(userId)) {
                throw new RuntimeException("用户不存在，用户ID：" + userId);
            }
            
            userRepository.deleteById(userId);
            log.info("删除用户成功，用户ID：{}", userId);
            
        } catch (Exception e) {
            log.error("删除用户失败，用户ID：{}", userId, e);
            throw new RuntimeException("删除用户失败", e);
        }
    }
    
    @Override
    public User getUserById(String userId) {
        try {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在，用户ID：" + userId));
        } catch (Exception e) {
            log.error("查询用户失败，用户ID：{}", userId, e);
            throw new RuntimeException("查询用户失败", e);
        }
    }
    
    @Override
    public User getUserByUsername(String username) {
        try {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在，用户名：" + username));
        } catch (Exception e) {
            log.error("查询用户失败，用户名：{}", username, e);
            throw new RuntimeException("查询用户失败", e);
        }
    }
    
    @Override
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            log.error("查询所有用户失败", e);
            throw new RuntimeException("查询用户失败", e);
        }
    }
    
    @Override
    @Transactional
    public User createAdminUser(String username, String password, String email, String fullName) {
        try {
            // 检查用户是否已存在
            if (userRepository.existsByUsername(username)) {
                throw new RuntimeException("用户名已存在：" + username);
            }
            
            
            
            // 创建管理员用户
            User adminUser = new User();
            adminUser.setUsername(username);
            adminUser.setPassword(passwordEncoder.encode(password));
            adminUser.setPhone(email); // 使用email作为phone字段
            adminUser.setFullName(fullName);
            adminUser.setRole("ADMIN");
            adminUser.setEnabled(true);
            
            User savedUser = userRepository.save(adminUser);
            log.info("创建管理员用户成功，用户名：{}", username);
            return savedUser;
            
        } catch (Exception e) {
            log.error("创建管理员用户失败", e);
            throw new RuntimeException("创建管理员用户失败", e);
        }
    }
    
    @Override
    public boolean isAdmin(String username) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                return false;
            }
            
            return "ADMIN".equals(user.getRole());
        } catch (Exception e) {
            log.error("检查管理员权限失败，用户名：{}", username, e);
            return false;
        }
    }
    
    // ========== 原有UserController需要的方法 ==========
    
    @Override
    public Map<String, Object> login(String phone, String password) {
        try {
            // 这里应该实现实际的登录逻辑
            // 为了演示，返回一个模拟的登录结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("token", "mock_token_" + System.currentTimeMillis());
            
            // 模拟用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", UUID.randomUUID().toString());
            userInfo.put("phone", phone);
            userInfo.put("nickname", "用户" + phone.substring(phone.length() - 4));
            result.put("userInfo", userInfo);
            
            log.info("用户登录成功，手机号：{}", phone);
            return result;
            
        } catch (Exception e) {
            log.error("用户登录失败，手机号：{}", phone, e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "登录失败");
            return result;
        }
    }
    
    @Override
    public UserInfo getUserInfo(UUID userId) {
        try {
            // 这里应该从数据库查询用户信息
            // 为了演示，返回一个模拟的用户信息
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId.toString());
            userInfo.setNickname("用户" + userId.toString().substring(0, 8));
            userInfo.setRealName("真实姓名");
            userInfo.setAvatar("https://via.placeholder.com/100");
            userInfo.setGender("男");
            userInfo.setBirthdate("1990-01-01");
            userInfo.setPhone("138****8888");
            userInfo.setPoints(1000);
            userInfo.setBalance(500.0);
            userInfo.setMemberLevel("VIP");
            userInfo.setAddressCount(3);
            userInfo.setFavoriteCount(10);
            userInfo.setCouponCount(5);
            userInfo.setCardCount(2);
            
            return userInfo;
            
        } catch (Exception e) {
            log.error("获取用户信息失败，用户ID：{}", userId, e);
            throw new RuntimeException("获取用户信息失败", e);
        }
    }
    
    @Override
    @Transactional
    public UserInfo updateUserInfo(UUID userId, UserInfo userInfo) {
        try {
            // 这里应该更新数据库中的用户信息
            // 为了演示，直接返回传入的用户信息
            userInfo.setUserId(userId.toString());
            
            log.info("更新用户信息成功，用户ID：{}", userId);
            return userInfo;
            
        } catch (Exception e) {
            log.error("更新用户信息失败，用户ID：{}", userId, e);
            throw new RuntimeException("更新用户信息失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean changePassword(UUID userId, String oldPassword, String newPassword) {
        try {
            // 这里应该验证旧密码并更新新密码
            // 为了演示，简单验证密码长度
            if (newPassword == null || newPassword.length() < 6) {
                log.warn("密码修改失败，新密码长度不足，用户ID：{}", userId);
                return false;
            }
            
            log.info("修改密码成功，用户ID：{}", userId);
            return true;
            
        } catch (Exception e) {
            log.error("修改密码失败，用户ID：{}", userId, e);
            return false;
        }
    }
    
    // ==================== 用户管理扩展方法实现 ====================
    
    @Override
    public List<User> searchUsers(String keyword, String role, Boolean enabled) {
        try {
            List<User> allUsers = userRepository.findAll();
            
            return allUsers.stream()
                .filter(user -> keyword == null || keyword.isEmpty() || 
                    user.getUsername().contains(keyword) || 
                    (user.getFullName() != null && user.getFullName().contains(keyword)) ||
                    user.getPhone().contains(keyword))
                .filter(user -> role == null || role.isEmpty() || role.equals(user.getRole()))
                .filter(user -> enabled == null || enabled.equals(user.getEnabled()))
                .toList();
                
        } catch (Exception e) {
            log.error("搜索用户失败", e);
            throw new RuntimeException("搜索用户失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getUserStatistics() {
        try {
            List<User> allUsers = userRepository.findAll();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", allUsers.size());
            stats.put("activeUsers", allUsers.stream().mapToInt(user -> user.getEnabled() ? 1 : 0).sum());
            stats.put("inactiveUsers", allUsers.stream().mapToInt(user -> !user.getEnabled() ? 1 : 0).sum());
            stats.put("adminUsers", allUsers.stream().mapToInt(user -> "ADMIN".equals(user.getRole()) ? 1 : 0).sum());
            stats.put("regularUsers", allUsers.stream().mapToInt(user -> "USER".equals(user.getRole()) ? 1 : 0).sum());
            
            return stats;
            
        } catch (Exception e) {
            log.error("获取用户统计失败", e);
            throw new RuntimeException("获取用户统计失败", e);
        }
    }
    
    @Override
    @Transactional
    public int batchUpdateUserStatus(List<String> userIds, Boolean enabled) {
        try {
            int updatedCount = 0;
            for (String userId : userIds) {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    user.setEnabled(enabled);
                    userRepository.save(user);
                    updatedCount++;
                }
            }
            
            log.info("批量更新用户状态成功，更新数量：{}", updatedCount);
            return updatedCount;
            
        } catch (Exception e) {
            log.error("批量更新用户状态失败", e);
            throw new RuntimeException("批量更新用户状态失败", e);
        }
    }
    
    @Override
    public boolean isUsernameAvailable(String username, String excludeUserId) {
        try {
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent()) {
                // 如果是编辑用户且用户名没有改变，则可用
                return excludeUserId != null && excludeUserId.equals(existingUser.get().getId());
            }
            return true;
            
        } catch (Exception e) {
            log.error("检查用户名可用性失败，用户名：{}", username, e);
            return false;
        }
    }
    
    @Override
    public boolean isPhoneAvailable(String phone, String excludeUserId) {
        try {
            // 这里需要添加根据手机号查询的方法到UserRepository
            List<User> users = userRepository.findAll();
            Optional<User> existingUser = users.stream()
                .filter(user -> phone.equals(user.getPhone()))
                .findFirst();
                
            if (existingUser.isPresent()) {
                // 如果是编辑用户且手机号没有改变，则可用
                return excludeUserId != null && excludeUserId.equals(existingUser.get().getId());
            }
            return true;
            
        } catch (Exception e) {
            log.error("检查手机号可用性失败，手机号：{}", phone, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminSetPassword(String userId, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("密码长度至少6位");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("管理员已重置用户密码，用户ID：{}", userId);
    }
}