package com.example.hello.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.hello.common.ApiResponse;
import com.example.hello.dto.UserCreateRequest;
import com.example.hello.dto.UserUpdateRequest;
import com.example.hello.entity.User;
import com.example.hello.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }
    
    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "admin/dashboard";
    }
    
    @GetMapping("/")
    public String adminHome() {
        return "redirect:/admin/dashboard";
    }
    
    // ==================== 用户管理 API ====================
    
    /**
     * 获取用户列表（分页）
     */
    @GetMapping("/api/users")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean enabled) {
        
        try {
            // 使用UserService的搜索方法
            List<User> filteredUsers = userService.searchUsers(keyword, role, enabled);
            
            // 简单的内存分页（实际项目中应该在数据库层面实现）
            int start = page * size;
            int end = Math.min(start + size, filteredUsers.size());
            List<User> pageContent = filteredUsers.subList(start, end);
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", pageContent);
            result.put("totalElements", filteredUsers.size());
            result.put("totalPages", (int) Math.ceil((double) filteredUsers.size() / size));
            result.put("currentPage", page);
            result.put("size", size);
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取用户列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/api/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable String userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取用户详情失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建用户
     */
    @PostMapping("/api/users")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody UserCreateRequest request, BindingResult bindingResult) {
        try {
            // 验证数据
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("数据验证失败");
                return ResponseEntity.ok(ApiResponse.error(errorMessage));
            }
            
            // 检查用户名和手机号是否已存在
            if (!userService.isUsernameAvailable(request.getUsername(), null)) {
                return ResponseEntity.ok(ApiResponse.error("用户名已存在"));
            }
            
            if (!userService.isPhoneAvailable(request.getPhone(), null)) {
                return ResponseEntity.ok(ApiResponse.error("手机号已存在"));
            }
            
            // 创建用户对象
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setPhone(request.getPhone());
            user.setFullName(request.getFullName());
            user.setRole(request.getRole());
            user.setGender(request.getGender());
            // 暂时跳过birthdate设置，避免类型转换问题
            user.setPoints(request.getPoints());
            user.setBalance(request.getBalance());
            user.setMemberLevel(request.getMemberLevel());
            user.setAvatar(request.getAvatar());
            user.setEnabled(request.getEnabled());
            
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(ApiResponse.success(createdUser));
            
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/api/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable String userId, @Valid @RequestBody UserUpdateRequest request, BindingResult bindingResult) {
        try {
            // 验证数据
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("数据验证失败");
                return ResponseEntity.ok(ApiResponse.error(errorMessage));
            }
            
            // 检查用户名和手机号是否已被其他用户使用
            if (!userService.isUsernameAvailable(request.getUsername(), userId)) {
                return ResponseEntity.ok(ApiResponse.error("用户名已被其他用户使用"));
            }
            
            if (!userService.isPhoneAvailable(request.getPhone(), userId)) {
                return ResponseEntity.ok(ApiResponse.error("手机号已被其他用户使用"));
            }
            
            // 创建用户对象
            User user = new User();
            user.setId(userId);
            user.setUsername(request.getUsername());
            user.setPhone(request.getPhone());
            user.setFullName(request.getFullName());
            user.setRole(request.getRole());
            user.setGender(request.getGender());
            // 暂时跳过birthdate设置，避免类型转换问题
            user.setPoints(request.getPoints());
            user.setBalance(request.getBalance());
            user.setMemberLevel(request.getMemberLevel());
            user.setAvatar(request.getAvatar());
            user.setEnabled(request.getEnabled());
            
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(ApiResponse.success(updatedUser));
            
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/api/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.success("删除用户成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 启用/禁用用户
     */
    @PutMapping("/api/users/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<ApiResponse<User>> toggleUserStatus(@PathVariable String userId) {
        try {
            User user = userService.getUserById(userId);
            user.setEnabled(!user.getEnabled());
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(ApiResponse.success(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新用户状态失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户统计信息
     */
    @GetMapping("/api/users/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStatistics() {
        try {
            Map<String, Object> stats = userService.getUserStatistics();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取用户统计失败: " + e.getMessage()));
        }
    }
}