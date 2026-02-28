# 优惠券接口文档

## 概述
本文档描述了SPA休闲会所项目中优惠券相关的API接口，包括管理员接口、用户接口和公开接口。

## 接口分类

### 1. 管理员接口（需要ADMIN角色）
- 优惠券管理：创建、查询、更新、删除优惠券
- 优惠券统计：获取优惠券使用统计信息

### 2. 用户接口（需要USER角色）
- 用户优惠券管理：领取、查看、使用优惠券
- 需要Authorization头进行身份验证

### 3. 公开接口（不需要认证）
- 适合微信小程序等公开场景使用
- 通过userId参数进行用户识别

## 基础信息

**管理员接口基础URL**: `/admin/api/coupons`
**用户接口基础URL**: `/api/coupons`
**认证方式**: JWT Token（除公开接口外）
**限流策略**: 所有接口都有适当的限流保护

## 1. 管理员接口

### 1.1 获取优惠券列表
**接口地址**: `GET /admin/api/coupons`
**权限要求**: ADMIN
**限流**: 每分钟200次

**请求参数**:
```
page: number (可选) - 页码，默认1
pageSize: number (可选) - 每页数量，默认10
status: string (可选) - 状态筛选 (true/false)
type: string (可选) - 类型筛选 (PERCENTAGE/FIXED_AMOUNT)
keyword: string (可选) - 关键词搜索
```

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

### 1.2 创建优惠券
**接口地址**: `POST /admin/api/coupons`
**权限要求**: ADMIN
**限流**: 每分钟50次

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

### 1.3 获取优惠券统计
**接口地址**: `GET /admin/api/coupons/statistics`
**权限要求**: ADMIN
**限流**: 每分钟100次

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

## 2. 用户接口（需要认证）

### 2.1 用户领取优惠券
**接口地址**: `POST /api/coupons/user/claim`
**权限要求**: 需要Authorization头
**限流**: 每分钟20次

**请求头**:
```
Authorization: Bearer <token>
```

**请求参数**:
```json
{
  "couponCode": "DISCOUNT10"
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

### 2.2 获取用户优惠券列表
**接口地址**: `GET /api/coupons/user/list`
**权限要求**: 需要Authorization头
**限流**: 每分钟200次

**请求头**:
```
Authorization: Bearer <token>
```

**请求参数**:
```
status: string (可选) - 状态筛选 (UNUSED/USED/EXPIRED)
page: number (可选) - 页码，默认1
size: number (可选) - 每页数量，默认10
```

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

### 2.3 获取用户可用优惠券
**接口地址**: `GET /api/coupons/user/available`
**权限要求**: 需要Authorization头
**限流**: 每分钟200次

**请求头**:
```
Authorization: Bearer <token>
```

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

### 2.4 使用用户优惠券
**接口地址**: `POST /api/coupons/user/use`
**权限要求**: 需要Authorization头
**限流**: 每分钟20次

**请求头**:
```
Authorization: Bearer <token>
```

**请求参数**:
```json
{
  "userCouponId": "user-coupon-uuid",
  "orderId": "order-uuid"
}
```

### 2.5 获取用户优惠券统计
**接口地址**: `GET /api/coupons/user/statistics`
**权限要求**: 需要Authorization头
**限流**: 每分钟100次

**请求头**:
```
Authorization: Bearer <token>
```

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
    "expiringSoon": 1
  }
}
```

## 3. 公开接口（不需要认证）

### 3.1 公开领取优惠券
**接口地址**: `POST /api/coupons/public/claim`
**权限要求**: 无
**限流**: 每分钟20次（按用户）

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

### 3.2 公开获取用户优惠券列表
**接口地址**: `GET /api/coupons/public/user/list`
**权限要求**: 无
**限流**: 每分钟200次

**请求参数**:
```
userId: string (必填) - 用户ID
status: string (可选) - 状态筛选 (UNUSED/USED/EXPIRED)
page: number (可选) - 页码，默认1
size: number (可选) - 每页数量，默认10
```

**响应格式**: 与用户接口相同

### 3.3 公开获取用户可用优惠券
**接口地址**: `GET /api/coupons/public/user/available`
**权限要求**: 无
**限流**: 每分钟200次

**请求参数**:
```
userId: string (必填) - 用户ID
```

**响应格式**: 与用户接口相同

### 3.4 验证优惠券
**接口地址**: `POST /api/coupons/validate`
**权限要求**: 无
**限流**: 每分钟200次

**请求参数**:
```json
{
  "couponCode": "DISCOUNT10",
  "userId": "user-uuid",
  "orderAmount": 100.00
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "isValid": true,
    "discount": 10.00
  }
}
```

## 4. 数据模型

### 4.1 优惠券类型
| 类型 | 说明 | 示例 |
|------|------|------|
| PERCENTAGE | 百分比折扣 | 10%折扣 |
| FIXED_AMOUNT | 固定金额折扣 | 减10元 |

### 4.2 用户优惠券状态
| 状态 | 说明 | 颜色标识 |
|------|------|----------|
| UNUSED | 未使用 | 绿色 |
| USED | 已使用 | 灰色 |
| EXPIRED | 已过期 | 红色 |

### 4.3 优惠券字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| couponCode | string | 优惠券代码（唯一标识） |
| couponName | string | 优惠券名称 |
| couponType | string | 优惠券类型 |
| discountValue | number | 折扣值 |
| minOrderAmount | number | 最低订单金额 |
| maxDiscountAmount | number | 最大折扣金额 |
| totalQuantity | number | 总数量 |
| usedQuantity | number | 已使用数量 |
| remainingQuantity | number | 剩余数量 |
| validFrom | datetime | 有效期开始时间 |
| validUntil | datetime | 有效期结束时间 |
| isActive | boolean | 是否激活 |
| isPublic | boolean | 是否公开 |
| usageLimitPerUser | number | 每用户使用限制 |

## 5. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 1001 | 参数错误 |
| 2001 | 认证失败 |
| 3001 | 系统错误 |

## 6. 使用示例

### 6.1 微信小程序领取优惠券
```javascript
// 领取优惠券
const claimCoupon = async (couponCode, userId) => {
  try {
    const response = await fetch('/admin/api/coupons/public/claim', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        couponCode: couponCode,
        userId: userId
      })
    });
    
    const result = await response.json();
    if (result.code === 200) {
      console.log('领取成功:', result.data);
    } else {
      console.error('领取失败:', result.message);
    }
  } catch (error) {
    console.error('网络错误:', error);
  }
};
```

### 6.2 获取用户优惠券列表
```javascript
// 获取用户优惠券列表
const getUserCoupons = async (userId, status = 'UNUSED') => {
  try {
    const response = await fetch(`/admin/api/coupons/public/user/list?userId=${userId}&status=${status}&page=1&size=10`);
    const result = await response.json();
    
    if (result.code === 200) {
      return result.data.list;
    } else {
      console.error('获取失败:', result.message);
      return [];
    }
  } catch (error) {
    console.error('网络错误:', error);
    return [];
  }
};
```

### 6.3 验证优惠券
```javascript
// 验证优惠券
const validateCoupon = async (couponCode, userId, orderAmount) => {
  try {
    const response = await fetch('/admin/api/coupons/validate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        couponCode: couponCode,
        userId: userId,
        orderAmount: orderAmount
      })
    });
    
    const result = await response.json();
    return result.data;
  } catch (error) {
    console.error('验证失败:', error);
    return { isValid: false, discount: 0 };
  }
};
```

## 7. 注意事项

1. **限流策略**: 所有接口都有适当的限流保护，请合理控制调用频率
2. **参数验证**: 所有必填参数都会进行验证，请确保参数完整性
3. **错误处理**: 请根据返回的错误码进行相应的错误处理
4. **事务支持**: 优惠券领取和使用都使用事务，确保数据一致性
5. **防重复领取**: 系统会自动检查用户是否已领取过相同优惠券
6. **次数限制**: 支持优惠券的每用户领取次数限制
7. **有效期管理**: 系统会自动管理优惠券的过期状态

## 8. 更新日志

- **2024-09-17**: 添加公开接口，支持微信小程序等公开场景使用
- **2024-09-13**: 完成用户优惠券管理功能开发
- **2024-09-01**: 完成优惠券基础功能开发
