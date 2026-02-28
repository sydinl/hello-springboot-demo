# Token认证更新说明

## 概述

集成了微信登录后，将普通用户接口从使用用户ID参数改为使用JWT token认证，提高安全性和一致性。

## 修改内容

### 1. 新增工具类

#### TokenUtil.java
- 位置：`src/main/java/com/example/hello/util/TokenUtil.java`
- 功能：从JWT token中提取用户信息
- 主要方法：
  - `getUserIdFromHeader()`: 从Authorization头获取用户ID
  - `getUsernameFromHeader()`: 从Authorization头获取用户名
  - `getUserRoleFromHeader()`: 从Authorization头获取用户角色
  - `getOpenIdFromHeader()`: 从Authorization头获取OpenID

### 2. 修改的控制器

#### UserController.java
**修改的接口**：
- `GET /api/user/info` - 获取用户信息
- `PUT /api/user/info` - 更新用户信息  
- `PUT /api/user/password` - 修改密码

**变化**：
- 移除 `@RequestParam UUID userId` 参数
- 添加 `@RequestHeader("Authorization") String authorization` 参数
- 使用 `tokenUtil.getUserIdFromHeader(authorization)` 获取用户ID
- 统一返回 `ApiResponse` 格式

#### OrderController.java
**修改的接口**：
- `GET /api/order/list` - 获取用户订单列表
- `GET /api/order/listByStatus` - 按状态获取订单列表
- `POST /api/order/coupon/validate` - 验证优惠券
- `GET /api/order/coupon/available` - 获取可用优惠券
- `POST /api/order/coupon/calculate` - 计算最终金额

**变化**：
- 移除 `@RequestParam String userId` 参数
- 添加 `@RequestHeader("Authorization") String authorization` 参数
- 使用token认证获取用户ID

#### CouponController.java
**修改的接口**：
- `POST /admin/api/coupons/user/claim` - 用户领取优惠券
- `GET /admin/api/coupons/user/list` - 获取用户优惠券列表
- `GET /admin/api/coupons/user/available` - 获取用户可用优惠券
- `POST /admin/api/coupons/user/use` - 使用用户优惠券
- `GET /admin/api/coupons/user/statistics` - 获取用户优惠券统计

**变化**：
- 移除 `@RequestParam String userId` 参数
- 添加 `@RequestHeader("Authorization") String authorization` 参数
- 使用token认证获取用户ID

### 3. 更新的文档

#### ORDER_API_FRONTEND.md
- 添加JWT Token认证说明
- 更新所有接口文档，移除userId参数
- 添加Authorization请求头说明
- 更新请求示例

## 认证方式区分

### 普通用户接口（微信登录）
- **认证方式**: JWT Bearer Token
- **获取方式**: 通过微信小程序登录接口
- **使用方式**: 在请求头中携带 `Authorization: Bearer <token>`
- **适用接口**: `/api/user/*`, `/api/order/*`, `/admin/api/coupons/user/*`

### 管理员接口（传统登录）
- **认证方式**: 保持原有方式不变
- **适用接口**: `/admin/api/*` (除用户优惠券接口外)

## 前端集成变化

### 1. 请求头变化
所有用户相关接口都需要添加Authorization头：

```javascript
// 之前
fetch('/api/order/list?userId=123&page=0&size=10')

// 现在
fetch('/api/order/list?page=0&size=10', {
  headers: {
    'Authorization': 'Bearer ' + token
  }
})
```

### 2. 请求参数变化
移除userId参数，从token中自动获取：

```javascript
// 之前
{
  "couponCode": "DISCOUNT10",
  "userId": "user-uuid",
  "orderAmount": 100.00
}

// 现在
{
  "couponCode": "DISCOUNT10", 
  "orderAmount": 100.00
}
```

### 3. 错误处理
新增认证失败错误码：

```json
{
  "code": 2001,
  "message": "认证失败，请重新登录",
  "data": null
}
```

## 安全性提升

1. **防止用户ID篡改**: 用户无法通过修改参数来访问其他用户的数据
2. **统一认证**: 所有接口都通过token进行身份验证
3. **自动过期**: JWT token有过期时间，提高安全性
4. **简化前端**: 前端只需要管理token，不需要管理用户ID

## 向后兼容性

- **管理员接口**: 完全保持向后兼容
- **用户接口**: 需要前端更新以使用新的认证方式
- **API文档**: 已更新为新的接口格式

## 测试建议

1. **Token获取测试**: 验证微信登录接口返回正确的token
2. **Token验证测试**: 验证token过期和无效时的错误处理
3. **接口调用测试**: 验证所有修改的接口都能正确从token获取用户信息
4. **权限测试**: 验证用户只能访问自己的数据

---

**更新时间**: 2024-01-01  
**影响范围**: 用户相关接口  
**向后兼容**: 管理员接口不受影响
