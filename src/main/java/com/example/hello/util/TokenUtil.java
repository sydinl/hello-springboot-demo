package com.example.hello.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

/**
 * Token工具类 - 用于从JWT token中提取用户信息
 */
@Component
@Slf4j
public class TokenUtil {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 从Authorization头中提取token
     */
    public String extractTokenFromHeader(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring(7);
    }
    
    /**
     * 验证token并获取用户ID
     */
    public String getUserIdFromToken(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return null;
            }
            return jwtUtil.getClaimFromToken(token, "userId");
        } catch (Exception e) {
            log.error("从token中获取用户ID失败", e);
            return null;
        }
    }
    
    /**
     * 验证token并获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return null;
            }
            return jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            log.error("从token中获取用户名失败", e);
            return null;
        }
    }
    
    /**
     * 验证token并获取用户角色
     */
    public String getUserRoleFromToken(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return null;
            }
            return jwtUtil.getClaimFromToken(token, "role");
        } catch (Exception e) {
            log.error("从token中获取用户角色失败", e);
            return null;
        }
    }
    
    /**
     * 验证token并获取OpenID
     */
    public String getOpenIdFromToken(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return null;
            }
            return jwtUtil.getClaimFromToken(token, "openId");
        } catch (Exception e) {
            log.error("从token中获取OpenID失败", e);
            return null;
        }
    }
    
    /**
     * 验证token是否有效
     */
    public boolean isTokenValid(String token) {
        return jwtUtil.validateToken(token);
    }
    
    /**
     * 从Authorization头中直接获取用户ID
     */
    public String getUserIdFromHeader(String authorization) {
        String token = extractTokenFromHeader(authorization);
        if (token == null) {
            return null;
        }
        return getUserIdFromToken(token);
    }
    
    /**
     * 从Authorization头中直接获取用户名
     */
    public String getUsernameFromHeader(String authorization) {
        String token = extractTokenFromHeader(authorization);
        if (token == null) {
            return null;
        }
        return getUsernameFromToken(token);
    }
    
    /**
     * 从Authorization头中直接获取用户角色
     */
    public String getUserRoleFromHeader(String authorization) {
        String token = extractTokenFromHeader(authorization);
        if (token == null) {
            return null;
        }
        return getUserRoleFromToken(token);
    }
    
    /**
     * 从Authorization头中直接获取OpenID
     */
    public String getOpenIdFromHeader(String authorization) {
        String token = extractTokenFromHeader(authorization);
        if (token == null) {
            return null;
        }
        return getOpenIdFromToken(token);
    }
}
