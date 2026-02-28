# 订单与优惠券集成API文档

## 概述

本文档描述了订单与优惠券集成功能的API接口，包括新增的优惠券相关接口和修改的现有订单接口。

## 基础信息

- **基础URL**: `http://your-domain.com/api/order`
- **认证方式**: 根据现有系统认证方式
- **响应格式**: 统一使用 `ApiResponse` 格式
- **限流策略**: 部分接口有频率限制

## 新增接口

### 1. 优惠券验证接口

**接口地址**: `POST /api/order/coupon/validate`

**功能描述**: 验证优惠券是否可用，并返回优惠金额信息

**限流策略**: 每用户每分钟最多20次

**请求参数**:
```json
{
  "couponCode": "DISCOUNT10",     // 优惠券代码（必填）
  "userId": "user123",            // 用户ID（必填）
  "orderAmount": 100.00,          // 订单金额（必填）
  "projectIds": ["proj1", "proj2"] // 项目ID列表（可选）
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "valid": true,                    // 验证结果
    "message": "优惠券验证成功",        // 验证消息
    "couponId": "coupon123",         // 优惠券ID（验证成功时返回）
    "couponName": "新用户优惠券",      // 优惠券名称（验证成功时返回）
    "discountAmount": 10.00,         // 优惠金额（验证成功时返回）
    "finalAmount": 90.00             // 最终支付金额（验证成功时返回）
  }
}
```

**错误响应示例**:
```json
{
  "code": 1001,
  "message": "优惠券代码不能为空"
}
```

### 2. 获取可用优惠券列表接口

**接口地址**: `GET /api/order/coupon/available`

**功能描述**: 获取用户可用的优惠券列表

**请求参数**:
- `userId`: 用户ID（必填）
- `orderAmount`: 订单金额（必填）
- `projectIds`: 项目ID列表（可选，逗号分隔）

**请求示例**:
```
GET /api/order/coupon/available?userId=user123&orderAmount=100.00&projectIds=proj1,proj2
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "coupon123",
      "couponCode": "DISCOUNT10",
      "couponName": "新用户优惠券",
      "description": "新用户专享优惠券",
      "couponType": "PERCENTAGE",        // 优惠券类型：PERCENTAGE/FIXED_AMOUNT/FREE_SHIPPING
      "discountValue": 10.00,            // 折扣值
      "minOrderAmount": 50.00,           // 最低订单金额
      "maxDiscountAmount": 20.00,        // 最大优惠金额
      "totalQuantity": 1000,             // 总数量
      "remainingQuantity": 500,          // 剩余数量
      "validFrom": "2025-01-01T00:00:00", // 有效期开始
      "validUntil": "2025-12-31T23:59:59", // 有效期结束
      "usageLimitPerUser": 1,            // 每用户使用限制
      "isActive": true,                  // 是否激活
      "isPublic": true                   // 是否公开
    }
  ]
}
```

### 3. 计算最终金额接口

**接口地址**: `POST /api/order/coupon/calculate`

**功能描述**: 计算使用优惠券后的最终支付金额

**限流策略**: 每用户每分钟最多20次

**请求参数**:
```json
{
  "originalAmount": 100.00,       // 原金额（必填）
  "couponCode": "DISCOUNT10",     // 优惠券代码（可选）
  "userId": "user123",            // 用户ID（必填）
  "projectIds": ["proj1", "proj2"] // 项目ID列表（可选）
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "originalAmount": 100.00,     // 原金额
    "finalAmount": 90.00,         // 最终支付金额
    "discountAmount": 10.00       // 优惠金额
  }
}
```

## 修改的现有接口

### 4. 创建订单接口（已修改）

**接口地址**: `POST /api/order/create`

**功能描述**: 创建订单，现在支持优惠券功能

**新增请求参数**:
```json
{
  "items": [
    {
      "projectId": "proj1",
      "projectName": "项目名称",
      "price": 50.00,
      "quantity": 2,
      "duration": "60分钟",
      "technicianId": "tech1",
      "timeSlot": "10:00-11:00"
    }
  ],
  "totalAmount": 100.00,          // 订单总金额
  "paymentMethod": "wechat",      // 支付方式
  "source": "cart",               // 订单来源
  "couponCode": "DISCOUNT10",     // 新增：优惠券代码
  "discountAmount": 10.00,        // 新增：优惠金额
  "finalAmount": 90.00            // 新增：最终支付金额
}
```

**响应格式（新增字段）**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": "order123",
    "orderNo": "ORDER123456789",
    "status": "pending",
    "totalAmount": 100.00,        // 订单总金额
    "discountAmount": 10.00,      // 新增：优惠金额
    "finalAmount": 90.00,         // 新增：最终支付金额
    "couponCode": "DISCOUNT10",   // 新增：优惠券代码
    "paymentMethod": "wechat",
    "createTime": "2025-01-01T10:00:00",
    "expireTime": "2025-01-01T10:30:00"
  }
}
```

## 优惠券类型说明

### CouponType 枚举值

- `PERCENTAGE`: 百分比折扣
- `FIXED_AMOUNT`: 固定金额折扣
- `FREE_SHIPPING`: 免运费

## 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 1001 | 参数错误 | 检查必填参数是否完整，参数格式是否正确 |
| 3001 | 系统错误 | 联系后端开发人员 |

## 限流说明

| 接口 | 限流策略 | 说明 |
|------|----------|------|
| 优惠券验证 | 每用户每分钟20次 | 防止恶意验证 |
| 计算最终金额 | 每用户每分钟20次 | 防止恶意计算 |
| 获取可用优惠券 | 无特殊限流 | 正常业务查询 |

## 前端集成建议

### 1. 订单创建流程

```javascript
// 1. 获取可用优惠券列表
async function getAvailableCoupons(userId, orderAmount, projectIds) {
  const response = await fetch(`/api/order/coupon/available?userId=${userId}&orderAmount=${orderAmount}&projectIds=${projectIds.join(',')}`);
  return await response.json();
}

// 2. 验证优惠券
async function validateCoupon(couponCode, userId, orderAmount, projectIds) {
  const response = await fetch('/api/order/coupon/validate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      couponCode,
      userId,
      orderAmount,
      projectIds
    })
  });
  return await response.json();
}

// 3. 计算最终金额
async function calculateFinalAmount(originalAmount, couponCode, userId, projectIds) {
  const response = await fetch('/api/order/coupon/calculate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      originalAmount,
      couponCode,
      userId,
      projectIds
    })
  });
  return await response.json();
}

// 4. 创建订单（包含优惠券）
async function createOrder(orderData) {
  const response = await fetch('/api/order/create', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(orderData)
  });
  return await response.json();
}
```

### 2. 优惠券选择组件示例

```html
<!-- 优惠券选择器 -->
<div class="coupon-selector">
  <input type="text" id="couponCode" placeholder="请输入优惠券代码" />
  <button onclick="validateCoupon()">验证</button>
  <div id="couponResult"></div>
  
  <!-- 可用优惠券列表 -->
  <div class="available-coupons">
    <h4>可用优惠券</h4>
    <div id="couponList"></div>
  </div>
</div>

<!-- 价格显示 -->
<div class="price-display">
  <div>原价: <span id="originalPrice">¥100.00</span></div>
  <div>优惠: <span id="discountAmount">¥0.00</span></div>
  <div>实付: <span id="finalPrice">¥100.00</span></div>
</div>
```

### 3. 错误处理示例

```javascript
function handleApiResponse(response) {
  if (response.code === 200) {
    // 成功处理
    return response.data;
  } else if (response.code === 1001) {
    // 参数错误
    showError(response.message);
  } else if (response.code === 3001) {
    // 系统错误
    showError('系统繁忙，请稍后再试');
  } else {
    // 其他错误
    showError('操作失败，请重试');
  }
}
```

## 注意事项

1. **优惠券验证**: 在创建订单前必须先验证优惠券
2. **金额计算**: 最终支付金额 = 原金额 - 优惠金额
3. **错误处理**: 所有接口都可能返回错误，需要做好错误处理
4. **限流处理**: 验证和计算接口有频率限制，需要处理限流错误
5. **用户体验**: 建议在用户输入优惠券代码时实时验证，提供即时反馈

## 测试建议

1. 测试各种优惠券类型的验证和计算
2. 测试优惠券的适用范围限制
3. 测试最低订单金额限制
4. 测试用户使用次数限制
5. 测试优惠券过期情况
6. 测试限流机制

---

**文档版本**: v1.0  
**更新时间**: 2025-09-13  
**维护人员**: 后端开发团队
