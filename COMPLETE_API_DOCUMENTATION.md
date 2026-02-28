# 完整API接口文档

## 概述

本文档整理了用户管理、优惠券管理、订单管理的所有API接口，供前端对接使用。

## 基础信息

- **基础URL**: `http://your-domain.com`
- **认证方式**: 根据接口要求使用相应权限
- **响应格式**: 统一使用 `ApiResponse` 格式
- **字符编码**: `UTF-8`

## 通用响应格式

```json
{
  "code": 200,           // 状态码：200-成功，其他-失败
  "message": "success",  // 响应消息
  "data": {},            // 响应数据
  "timestamp": 1640995200000 // 时间戳
}
```

## 1. 用户管理接口

### 1.1 普通用户接口 (`/api/user`)

| 接口 | 方法 | 功能 | 权限 | 限流 |
|------|------|------|------|------|
| `/login` | POST | 用户登录 | 无 | 每用户每分钟10次 |
| `/info` | GET | 获取用户信息 | 无 | 每分钟200次 |
| `/info` | PUT | 更新用户信息 | 无 | 每分钟200次 |
| `/password` | PUT | 修改密码 | 无 | 每用户每分钟5次 |

#### 1.1.1 用户登录
**接口地址**: `POST /api/user/login`

**请求参数**:
```json
{
  "phone": "13800138000",
  "password": "password123"
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": "user-uuid",
    "username": "testuser",
    "token": "jwt-token",
    "role": "USER",
    "enabled": true
  }
}
```

#### 1.1.2 获取用户信息
**接口地址**: `GET /api/user/info?userId={userId}`

**响应格式**:
```json
{
  "userId": "user-uuid",
  "nickname": "用户昵称",
  "realName": "真实姓名",
  "avatar": "头像URL",
  "gender": "男",
  "birthdate": "1990-01-01",
  "phone": "13800138000",
  "points": 100,
  "balance": 50.0,
  "memberLevel": "普通会员",
  "addressCount": 2,
  "favoriteCount": 5,
  "couponCount": 3,
  "cardCount": 1
}
```

#### 1.1.3 更新用户信息
**接口地址**: `PUT /api/user/info?userId={userId}`

**请求参数**:
```json
{
  "nickname": "新昵称",
  "realName": "新真实姓名",
  "avatar": "新头像URL",
  "gender": "女",
  "birthdate": "1990-01-01"
}
```

#### 1.1.4 修改密码
**接口地址**: `PUT /api/user/password?userId={userId}`

**请求参数**:
```json
{
  "oldPassword": "旧密码",
  "newPassword": "新密码"
}
```

### 1.2 管理员用户接口 (`/admin/api/users`)

| 接口 | 方法 | 功能 | 权限 | 限流 |
|------|------|------|------|------|
| `/` | GET | 获取用户列表 | ADMIN | 每分钟200次 |
| `/{userId}` | GET | 获取用户详情 | ADMIN | 每分钟200次 |
| `/` | POST | 创建用户 | ADMIN | 每分钟200次 |
| `/{userId}` | PUT | 更新用户 | ADMIN | 每分钟200次 |
| `/{userId}` | DELETE | 删除用户 | ADMIN | 每分钟200次 |
| `/{userId}/toggle-status` | PUT | 切换用户状态 | ADMIN | 每分钟200次 |
| `/statistics` | GET | 获取用户统计 | ADMIN | 每分钟200次 |

#### 1.2.1 获取用户列表
**接口地址**: `GET /admin/api/users?page=1&size=10&keyword=搜索词&role=USER&enabled=true&sortBy=createTime&sortOrder=desc`

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [
      {
        "id": "user-uuid",
        "username": "testuser",
        "fullName": "测试用户",
        "phone": "13800138000",
        "role": "USER",
        "gender": "男",
        "points": 100,
        "balance": 50.0,
        "memberLevel": "普通会员",
        "avatar": "头像URL",
        "enabled": true,
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "page": 1,
    "size": 10,
    "totalPages": 10
  }
}
```

#### 1.2.2 创建用户
**接口地址**: `POST /admin/api/users`

**请求参数**:
```json
{
  "username": "testuser",
  "password": "password123",
  "phone": "13800138000",
  "fullName": "测试用户",
  "role": "USER",
  "gender": "男",
  "birthdate": "1990-01-01",
  "points": 100,
  "balance": 50.0,
  "memberLevel": "普通会员",
  "avatar": "头像URL",
  "enabled": true
}
```

#### 1.2.3 更新用户
**接口地址**: `PUT /admin/api/users/{userId}`

**请求参数**: 同创建用户，但password可选

#### 1.2.4 获取用户统计
**接口地址**: `GET /admin/api/users/statistics`

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalUsers": 100,
    "activeUsers": 80,
    "disabledUsers": 20,
    "adminUsers": 5,
    "todayNewUsers": 10
  }
}
```

## 2. 优惠券管理接口

### 2.1 管理员优惠券接口 (`/admin/api/coupons`)

| 接口 | 方法 | 功能 | 权限 | 限流 |
|------|------|------|------|------|
| `/` | GET | 获取优惠券列表 | ADMIN | 每分钟200次 |
| `/{couponId}` | GET | 获取优惠券详情 | ADMIN | 每分钟200次 |
| `/` | POST | 创建优惠券 | ADMIN | 每分钟200次 |
| `/{couponId}` | PUT | 更新优惠券 | ADMIN | 每分钟200次 |
| `/{couponId}` | DELETE | 删除优惠券 | ADMIN | 每分钟200次 |
| `/{couponId}/toggle-status` | PUT | 切换优惠券状态 | ADMIN | 每分钟200次 |
| `/statistics` | GET | 获取优惠券统计 | ADMIN | 每分钟200次 |
| `/validate` | POST | 验证优惠券 | 无 | 每分钟200次 |

#### 2.1.1 获取优惠券列表
**接口地址**: `GET /admin/api/coupons?page=1&pageSize=10&status=true&type=PERCENTAGE&keyword=搜索词`

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "list": [
      {
        "id": "coupon-uuid",
        "couponCode": "DISCOUNT10",
        "couponName": "新用户优惠券",
        "description": "新用户专享优惠券",
        "couponType": "PERCENTAGE",
        "discountValue": 10.00,
        "minOrderAmount": 50.00,
        "maxDiscountAmount": 20.00,
        "totalQuantity": 1000,
        "usedQuantity": 100,
        "remainingQuantity": 900,
        "validFrom": "2024-01-01T00:00:00",
        "validUntil": "2024-12-31T23:59:59",
        "isActive": true,
        "isPublic": true,
        "usageLimitPerUser": 1,
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "page": 1,
    "pageSize": 10,
    "totalPages": 5
  }
}
```

#### 2.1.2 创建优惠券
**接口地址**: `POST /admin/api/coupons`

**请求参数**:
```json
{
  "couponCode": "DISCOUNT10",
  "couponName": "新用户优惠券",
  "description": "新用户专享优惠券",
  "couponType": "PERCENTAGE",
  "discountValue": 10.00,
  "minOrderAmount": 50.00,
  "maxDiscountAmount": 20.00,
  "totalQuantity": 1000,
  "validFrom": "2024-01-01T00:00:00",
  "validUntil": "2024-12-31T23:59:59",
  "isActive": true,
  "isPublic": true,
  "usageLimitPerUser": 1,
  "applicableCategories": "cat1,cat2",
  "applicableProjects": "proj1,proj2"
}
```

#### 2.1.3 获取优惠券统计
**接口地址**: `GET /admin/api/coupons/statistics`

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCoupons": 50,
    "activeCoupons": 40,
    "inactiveCoupons": 10,
    "totalUsed": 1000,
    "totalRemaining": 9000,
    "expiringCoupons": 5
  }
}
```

### 2.2 用户优惠券接口 (`/admin/api/coupons/user`)

| 接口 | 方法 | 功能 | 权限 | 限流 |
|------|------|------|------|------|
| `/claim` | POST | 用户领取优惠券 | USER | 每分钟200次 |
| `/list` | GET | 获取用户优惠券列表 | USER | 每分钟200次 |
| `/available` | GET | 获取用户可用优惠券 | USER | 每分钟200次 |
| `/use` | POST | 使用用户优惠券 | USER | 每分钟200次 |
| `/statistics` | GET | 获取用户优惠券统计 | USER | 每分钟200次 |

#### 2.2.1 用户领取优惠券
**接口地址**: `POST /admin/api/coupons/user/claim`

**请求参数**:
```json
{
  "couponCode": "DISCOUNT10",
  "userId": "user-uuid"
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "user-coupon-uuid",
    "userId": "user-uuid",
    "couponId": "coupon-uuid",
    "couponCode": "DISCOUNT10",
    "couponName": "新用户优惠券",
    "couponType": "PERCENTAGE",
    "discountValue": 10.00,
    "minOrderAmount": 50.00,
    "maxDiscountAmount": 20.00,
    "validFrom": "2024-01-01T00:00:00",
    "validUntil": "2024-12-31T23:59:59",
    "status": "UNUSED",
    "claimedAt": "2024-01-01T10:00:00"
  }
}
```

#### 2.2.2 获取用户优惠券列表
**接口地址**: `GET /admin/api/coupons/user/list?userId={userId}&status=UNUSED&page=1&size=10`

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 10,
    "list": [
      {
        "id": "user-coupon-uuid",
        "couponCode": "DISCOUNT10",
        "couponName": "新用户优惠券",
        "couponType": "PERCENTAGE",
        "discountValue": 10.00,
        "minOrderAmount": 50.00,
        "maxDiscountAmount": 20.00,
        "validFrom": "2024-01-01T00:00:00",
        "validUntil": "2024-12-31T23:59:59",
        "status": "UNUSED",
        "claimedAt": "2024-01-01T10:00:00"
      }
    ],
    "page": 1,
    "size": 10,
    "totalPages": 1
  }
}
```

#### 2.2.3 获取用户可用优惠券
**接口地址**: `GET /admin/api/coupons/user/available?userId={userId}`

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "user-coupon-uuid",
      "couponCode": "DISCOUNT10",
      "couponName": "新用户优惠券",
      "couponType": "PERCENTAGE",
      "discountValue": 10.00,
      "minOrderAmount": 50.00,
      "maxDiscountAmount": 20.00,
      "validFrom": "2024-01-01T00:00:00",
      "validUntil": "2024-12-31T23:59:59",
      "status": "UNUSED",
      "claimedAt": "2024-01-01T10:00:00"
    }
  ]
}
```

#### 2.2.4 使用用户优惠券
**接口地址**: `POST /admin/api/coupons/user/use`

**请求参数**:
```json
{
  "userCouponId": "user-coupon-uuid",
  "orderId": "order-uuid"
}
```

#### 2.2.5 获取用户优惠券统计
**接口地址**: `GET /admin/api/coupons/user/statistics?userId={userId}`

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCoupons": 10,
    "unusedCoupons": 8,
    "usedCoupons": 2,
    "expiredCoupons": 0,
    "expiringCoupons": 1
  }
}
```

## 3. 订单管理接口

### 3.1 订单基础接口 (`/api/order`)

| 接口 | 方法 | 功能 | 权限 | 限流 |
|------|------|------|------|------|
| `/` | POST | 创建订单（简单） | 无 | 每用户每分钟20次 |
| `/create` | POST | 创建订单（支付） | 无 | 每用户每分钟20次 |
| `/detail` | GET | 获取订单详情 | 无 | 每分钟100次 |
| `/list` | GET | 获取用户订单列表 | 无 | 每分钟100次 |
| `/listByStatus` | GET | 按状态获取订单列表 | 无 | 每分钟100次 |
| `/status` | PUT | 更新订单状态 | 无 | 每分钟100次 |

#### 3.1.1 创建订单（支付方式）
**接口地址**: `POST /api/order/create`

**请求参数**:
```json
{
  "items": [
    {
      "projectId": "project-uuid",
      "projectName": "项目名称",
      "price": 100.00,
      "quantity": 1,
      "duration": "60分钟",
      "technicianId": "technician-uuid",
      "timeSlot": "10:00-11:00"
    }
  ],
  "totalAmount": 100.00,
  "paymentMethod": "wechat",
  "source": "cart",
  "couponCode": "DISCOUNT10",
  "discountAmount": 10.00,
  "finalAmount": 90.00
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": "order-uuid",
    "orderNo": "ORDER16409952000001234",
    "status": "pending",
    "totalAmount": 100.00,
    "discountAmount": 10.00,
    "finalAmount": 90.00,
    "couponCode": "DISCOUNT10",
    "paymentMethod": "wechat",
    "createTime": "2024-01-01T10:00:00",
    "expireTime": "2024-01-01T10:30:00"
  }
}
```

#### 3.1.2 获取订单详情
**接口地址**: `GET /api/order/detail?orderId={orderId}`

**响应格式**:
```json
{
  "orderId": "order-uuid",
  "userId": "user-uuid",
  "status": "pending",
  "totalPrice": 100.00,
  "discountAmount": 10.00,
  "finalAmount": 90.00,
  "couponCode": "DISCOUNT10",
  "paymentMethod": "wechat",
  "orderNo": "ORDER16409952000001234",
  "source": "cart",
  "createTime": "2024-01-01T10:00:00",
  "payTime": null,
  "serviceTime": null,
  "expireTime": "2024-01-01T10:30:00",
  "remarks": "订单备注",
  "items": [
    {
      "id": "item-uuid",
      "projectId": "project-uuid",
      "projectName": "项目名称",
      "price": 100.00,
      "quantity": 1,
      "duration": "60分钟",
      "technicianId": "technician-uuid",
      "timeSlot": "10:00-11:00"
    }
  ]
}
```

#### 3.1.3 获取用户订单列表
**接口地址**: `GET /api/order/list?userId={userId}&page=0&size=10`

**响应格式**:
```json
{
  "content": [
    {
      "orderId": "order-uuid",
      "userId": "user-uuid",
      "status": "pending",
      "totalPrice": 100.00,
      "orderNo": "ORDER16409952000001234",
      "createTime": "2024-01-01T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 100,
  "totalPages": 10,
  "first": true,
  "last": false
}
```

### 3.2 订单优惠券接口 (`/api/order/coupon`)

| 接口 | 方法 | 功能 | 权限 | 限流 |
|------|------|------|------|------|
| `/validate` | POST | 验证优惠券 | 无 | 每用户每分钟20次 |
| `/available` | GET | 获取可用优惠券 | 无 | 每分钟100次 |
| `/calculate` | POST | 计算最终金额 | 无 | 每用户每分钟20次 |

#### 3.2.1 验证优惠券
**接口地址**: `POST /api/order/coupon/validate`

**请求参数**:
```json
{
  "couponCode": "DISCOUNT10",
  "userId": "user-uuid",
  "orderAmount": 100.00,
  "projectIds": ["project1", "project2"]
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "valid": true,
    "message": "优惠券验证成功",
    "couponId": "coupon-uuid",
    "couponName": "新用户优惠券",
    "discountAmount": 10.00,
    "finalAmount": 90.00
  }
}
```

#### 3.2.2 获取可用优惠券
**接口地址**: `GET /api/order/coupon/available?userId={userId}&orderAmount=100.00&projectIds=proj1,proj2`

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "coupon-uuid",
      "couponCode": "DISCOUNT10",
      "couponName": "新用户优惠券",
      "couponType": "PERCENTAGE",
      "discountValue": 10.00,
      "minOrderAmount": 50.00,
      "maxDiscountAmount": 20.00,
      "validFrom": "2024-01-01T00:00:00",
      "validUntil": "2024-12-31T23:59:59"
    }
  ]
}
```

#### 3.2.3 计算最终金额
**接口地址**: `POST /api/order/coupon/calculate`

**请求参数**:
```json
{
  "originalAmount": 100.00,
  "couponCode": "DISCOUNT10",
  "userId": "user-uuid",
  "projectIds": ["project1", "project2"]
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "originalAmount": 100.00,
    "finalAmount": 90.00,
    "discountAmount": 10.00
  }
}
```

### 3.3 管理员订单接口 (`/api/order/admin`)

| 接口 | 方法 | 功能 | 权限 | 限流 |
|------|------|------|------|------|
| `/all` | GET | 获取所有订单 | 无 | 每分钟100次 |
| `/detail` | GET | 获取订单详情 | 无 | 每分钟100次 |
| `/status/{orderId}` | PUT | 更新订单状态 | 无 | 每分钟100次 |
| `/statistics` | GET | 获取订单统计 | 无 | 每分钟100次 |

#### 3.3.1 获取所有订单
**接口地址**: `GET /api/order/admin/all?page=1&pageSize=10&status=pending&userId=user-uuid&search=搜索词`

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [
      {
        "orderId": "order-uuid",
        "userId": "user-uuid",
        "status": "pending",
        "totalPrice": 100.00,
        "orderNo": "ORDER16409952000001234",
        "createTime": "2024-01-01T10:00:00"
      }
    ]
  }
}
```

#### 3.3.2 获取订单统计
**接口地址**: `GET /api/order/admin/statistics`

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalOrders": 1000,
    "pendingOrders": 100,
    "shippingOrders": 200,
    "completedOrders": 600,
    "aftersaleOrders": 100,
    "todayOrders": 50
  }
}
```

### 3.4 支付相关接口 (`/api/order/payment`)

| 接口 | 方法 | 功能 | 权限 | 限流 |
|------|------|------|------|------|
| `/wechat/params` | POST | 获取微信支付参数 | 无 | 每用户每分钟10次 |
| `/status` | GET | 查询支付状态 | 无 | 每分钟100次 |
| `/callback` | POST | 支付回调处理 | 无 | 每分钟200次 |

#### 3.4.1 获取微信支付参数
**接口地址**: `POST /api/order/payment/wechat/params`

**请求参数**:
```json
{
  "orderId": "order-uuid"
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "timeStamp": "1640995200",
    "nonceStr": "randomstring",
    "package": "prepay_id=wx123456789",
    "signType": "MD5",
    "paySign": "signature"
  }
}
```

#### 3.4.2 查询支付状态
**接口地址**: `GET /api/order/payment/status?orderId={orderId}`

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": "order-uuid",
    "status": "paid",
    "payTime": "2024-01-01T10:05:00",
    "transactionId": "wx123456789"
  }
}
```

## 4. 微信小程序接口

### 4.1 微信登录接口 (`/api/wechat`)

| 接口 | 方法 | 功能 | 权限 | 限流 |
|------|------|------|------|------|
| `/miniprogram/login` | POST | 微信小程序登录 | 无 | 每分钟100次 |
| `/miniprogram/refresh` | POST | 刷新访问令牌 | 无 | 每分钟100次 |
| `/miniprogram/validate` | POST | 验证访问令牌 | 无 | 每分钟100次 |
| `/miniprogram/userinfo` | GET | 获取微信用户信息 | 无 | 每分钟100次 |

#### 4.1.1 微信小程序登录
**接口地址**: `POST /api/wechat/miniprogram/login`

**请求参数**:
```json
{
  "code": "wx-login-code",
  "nickname": "微信昵称",
  "avatarUrl": "头像URL",
  "gender": 1,
  "city": "城市",
  "province": "省份",
  "country": "国家",
  "language": "zh_CN"
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "jwt-token",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "userId": "user-uuid",
    "openId": "wx-openid",
    "unionId": "wx-unionid",
    "isNewUser": false,
    "username": "微信用户",
    "avatarUrl": "头像URL",
    "role": "USER",
    "enabled": true
  }
}
```

## 5. 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 1001 | 参数错误 | 检查必填参数是否完整，参数格式是否正确 |
| 2001 | 认证失败 | 检查登录状态和权限 |
| 3001 | 系统错误 | 联系后端开发人员 |
| 4001 | 业务错误 | 根据具体错误信息处理 |

## 6. 限流说明

| 接口类型 | 限流策略 | 说明 |
|----------|----------|------|
| 登录相关 | 每用户每分钟10次 | 防止暴力破解 |
| 订单创建 | 每用户每分钟20次 | 防止恶意下单 |
| 优惠券验证 | 每用户每分钟20次 | 防止恶意验证 |
| 支付相关 | 每用户每分钟10次 | 防止恶意支付 |
| 其他接口 | 每分钟100-200次 | 正常业务限制 |

## 7. 前端集成建议

### 7.1 认证处理
- 所有需要权限的接口都需要在请求头中携带认证信息
- 建议使用JWT Token进行认证
- Token过期时自动刷新或重新登录

### 7.2 错误处理
- 统一处理API响应中的错误码和错误信息
- 对于限流错误，提示用户稍后再试
- 对于认证错误，跳转到登录页面

### 7.3 分页处理
- 所有列表接口都支持分页
- 建议实现无限滚动或分页器
- 注意处理空数据状态

### 7.4 实时更新
- 订单状态变化时及时更新UI
- 优惠券使用后及时刷新列表
- 用户信息修改后及时同步

---

**文档版本**: v1.0  
**更新时间**: 2025-09-13  
**维护人员**: 后端开发团队
