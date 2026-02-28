# 微信小程序登录API文档

## 概述

本文档描述了微信小程序登录相关的API接口，包括用户登录、令牌刷新、令牌验证等功能。

## 基础信息

- **基础URL**: `https://your-domain.com/api/wechat/miniprogram`
- **认证方式**: JWT Bearer Token
- **内容类型**: `application/json`

## 接口列表

### 1. 微信小程序登录

**接口地址**: `POST /api/wechat/miniprogram/login`

**功能描述**: 通过微信小程序登录凭证code进行用户登录

**请求参数**:
```json
{
  "code": "string",           // 必填，微信小程序登录凭证
  "nickname": "string",       // 可选，用户昵称
  "avatarUrl": "string",      // 可选，用户头像URL
  "gender": 1,                // 可选，用户性别（0-未知，1-男，2-女）
  "city": "string",           // 可选，用户所在城市
  "province": "string",       // 可选，用户所在省份
  "country": "string",        // 可选，用户所在国家
  "language": "string"        // 可选，用户语言
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "userId": "user-uuid-here",
    "nickname": "微信用户",
    "avatarUrl": "https://example.com/avatar.jpg",
    "isNewUser": false,
    "openId": "wx_openid_here",
    "unionId": "wx_unionid_here",
    "role": "USER",
    "enabled": true
  }
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "登录失败：获取微信用户信息失败",
  "data": null
}
```

### 2. 刷新访问令牌

**接口地址**: `POST /api/wechat/miniprogram/refresh-token`

**功能描述**: 使用刷新令牌获取新的访问令牌

**请求参数**:
```json
{
  "refreshToken": "string"    // 必填，刷新令牌
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200
  }
}
```

### 3. 验证访问令牌

**接口地址**: `POST /api/wechat/miniprogram/validate-token`

**功能描述**: 验证访问令牌是否有效

**请求参数**:
```json
{
  "accessToken": "string"     // 必填，访问令牌
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "valid": true,
    "message": "令牌有效"
  }
}
```

### 4. 获取用户信息

**接口地址**: `GET /api/wechat/miniprogram/userinfo`

**功能描述**: 获取当前登录用户的详细信息

**请求头**:
```
Authorization: Bearer <access_token>
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "message": "用户信息获取成功"
  }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权或令牌无效 |
| 403 | 禁止访问 |
| 500 | 服务器内部错误 |

## 使用示例

### JavaScript (微信小程序)

```javascript
// 微信小程序登录
wx.login({
  success: function(res) {
    if (res.code) {
      // 调用后端登录接口
      wx.request({
        url: 'https://your-domain.com/api/wechat/miniprogram/login',
        method: 'POST',
        data: {
          code: res.code,
          nickname: '用户昵称',
          avatarUrl: 'https://example.com/avatar.jpg',
          gender: 1,
          city: '北京',
          province: '北京',
          country: '中国',
          language: 'zh_CN'
        },
        success: function(response) {
          if (response.data.code === 200) {
            // 登录成功，保存令牌
            wx.setStorageSync('accessToken', response.data.data.accessToken);
            console.log('登录成功', response.data.data);
          } else {
            console.error('登录失败', response.data.message);
          }
        },
        fail: function(error) {
          console.error('登录请求失败', error);
        }
      });
    } else {
      console.error('获取微信登录凭证失败', res.errMsg);
    }
  }
});

// 验证令牌
function validateToken() {
  const token = wx.getStorageSync('accessToken');
  if (!token) {
    console.log('未找到访问令牌');
    return;
  }
  
  wx.request({
    url: 'https://your-domain.com/api/wechat/miniprogram/validate-token',
    method: 'POST',
    data: {
      accessToken: token
    },
    success: function(response) {
      if (response.data.code === 200) {
        console.log('令牌有效', response.data.data);
      } else {
        console.log('令牌无效', response.data.message);
        // 重新登录
        wx.removeStorageSync('accessToken');
      }
    }
  });
}
```

### Java (后端调用示例)

```java
@RestController
public class TestController {
    
    @Autowired
    private WechatMiniprogramService wechatService;
    
    @PostMapping("/test-login")
    public ApiResponse<String> testLogin(@RequestBody WechatMiniprogramLoginRequest request) {
        try {
            WechatMiniprogramLoginResponse response = wechatService.login(request);
            return ApiResponse.success("登录成功: " + response.getUserId());
        } catch (Exception e) {
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }
}
```

## 配置说明

### 后端配置

在 `application.yml` 中配置微信小程序参数：

```yaml
# 微信小程序配置
wechat:
  miniprogram:
    appid: wx1234567890abcdef          # 微信小程序AppID
    secret: your-wechat-miniprogram-secret-here  # 微信小程序Secret

# JWT配置
jwt:
  secret: your-secret-key-here         # JWT签名密钥
  expiration: 7200                     # 令牌过期时间（秒）
```

### 数据库字段

用户表需要添加以下微信相关字段：

```sql
ALTER TABLE users ADD COLUMN open_id VARCHAR(255) UNIQUE;
ALTER TABLE users ADD COLUMN union_id VARCHAR(255);
ALTER TABLE users ADD COLUMN city VARCHAR(100);
ALTER TABLE users ADD COLUMN province VARCHAR(100);
ALTER TABLE users ADD COLUMN country VARCHAR(100);
ALTER TABLE users ADD COLUMN language VARCHAR(50);
ALTER TABLE users ADD COLUMN last_login_time DATETIME;
```

## 安全注意事项

1. **令牌安全**: 访问令牌应安全存储，避免在URL中传递
2. **HTTPS**: 所有API调用都应使用HTTPS协议
3. **令牌过期**: 定期检查令牌有效性，及时刷新
4. **敏感信息**: 不要在客户端存储敏感信息
5. **API限流**: 已配置API限流，防止恶意调用

## 常见问题

### Q: 登录时提示"获取微信用户信息失败"
A: 检查微信小程序AppID和Secret配置是否正确，确保网络连接正常。

### Q: 令牌验证失败
A: 检查令牌是否过期，或重新获取新的访问令牌。

### Q: 用户信息更新不及时
A: 每次登录时会自动更新用户信息，包括昵称、头像等。

### Q: 如何处理新用户
A: 系统会自动创建新用户，`isNewUser`字段标识是否为新用户。

## 更新日志

- **v1.0.0** (2025-09-13): 初始版本，支持微信小程序登录功能
