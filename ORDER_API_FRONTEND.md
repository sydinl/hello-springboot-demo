# 订单接口文档 - 前端对接专用

## 概述

本文档整理了订单相关的所有前端接口，供前端开发人员对接使用。不包含管理员专用接口。

## 基础信息

- **基础URL**: `http://your-domain.com`
- **认证方式**: JWT Bearer Token（微信登录用户）
- **响应格式**: 统一使用 `ApiResponse` 格式
- **字符编码**: `UTF-8`

## 认证说明

### JWT Token认证
所有用户相关接口都需要在请求头中携带JWT token：

```
Authorization: Bearer <your-jwt-token>
```

### 获取Token
通过微信小程序登录接口获取token：
- 接口：`POST /api/wechat/miniprogram/login`
- 响应中包含 `accessToken` 字段

## 通用响应格式

```json
{
  "code": 200,           // 状态码：200-成功，其他-失败
  "message": "success",  // 响应消息
  "data": {},            // 响应数据
  "timestamp": 1640995200000 // 时间戳
}
```

## 1. 订单基础接口

### 1.1 创建订单（简单方式）
**接口地址**: `POST /api/order/`

**功能**: 创建简单订单，不涉及支付

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
  "source": "cart",
  "remarks": "订单备注"
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
    "createTime": "2024-01-01T10:00:00",
    "expireTime": "2024-01-01T10:30:00"
  }
}
```

### 1.2 创建订单（支付方式）
**接口地址**: `POST /api/order/create`

**功能**: 创建订单并处理支付

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
  "finalAmount": 90.00,
  "remarks": "订单备注"
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

### 1.3 获取订单详情
**接口地址**: `GET /api/order/detail?orderId={orderId}`

**功能**: 获取指定订单的详细信息

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
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
}
```

### 1.4 获取用户订单列表
**接口地址**: `GET /api/order/list?page=0&size=10`

**功能**: 获取当前用户的订单列表

**请求头**:
```
Authorization: Bearer <your-jwt-token>
```

**请求参数**:
- `page`: 页码，从0开始（可选，默认0）
- `size`: 每页大小（可选，默认10）

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "orderId": "order-uuid",
        "userId": "user-uuid",
        "status": "pending",
        "totalPrice": 100.00,
        "discountAmount": 10.00,
        "finalAmount": 90.00,
        "couponCode": "DISCOUNT10",
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
}
```

### 1.5 按状态获取订单列表
**接口地址**: `GET /api/order/listByStatus?status={status}&page=0&size=10`

**功能**: 按状态筛选获取当前用户订单列表

**请求头**:
```
Authorization: Bearer <your-jwt-token>
```

**请求参数**:
- `status`: 订单状态（必填）
- `page`: 页码，从0开始（可选，默认0）
- `size`: 每页大小（可选，默认10）

**订单状态值**:
- `pending`: 待支付
- `paid`: 已支付
- `shipping`: 服务中
- `completed`: 已完成
- `cancelled`: 已取消
- `refunded`: 已退款

**响应格式**: 同订单列表接口

### 1.6 更新订单状态
**接口地址**: `PUT /api/order/status?orderId={orderId}&status={status}`

**功能**: 更新订单状态

**请求参数**:
- `orderId`: 订单ID（必填）
- `status`: 新状态（必填）

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": "order-uuid",
    "status": "paid",
    "updateTime": "2024-01-01T10:05:00"
  }
}
```

## 2. 订单优惠券接口

### 2.1 验证优惠券
**接口地址**: `POST /api/order/coupon/validate`

**功能**: 验证优惠券是否可用

**请求头**:
```
Authorization: Bearer <your-jwt-token>
```

**请求参数**:
```json
{
  "couponCode": "DISCOUNT10",
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
    "couponType": "PERCENTAGE",
    "discountValue": 10.00,
    "minOrderAmount": 50.00,
    "maxDiscountAmount": 20.00,
    "discountAmount": 10.00,
    "finalAmount": 90.00
  }
}
```

**错误响应**:
```json
{
  "code": 4001,
  "message": "优惠券不存在或已过期",
  "data": null
}
```

### 2.2 获取可用优惠券
**接口地址**: `GET /api/order/coupon/available?orderAmount={orderAmount}&projectIds={projectIds}`

**功能**: 获取当前用户可用的优惠券列表

**请求头**:
```
Authorization: Bearer <your-jwt-token>
```

**请求参数**:
- `orderAmount`: 订单金额（必填）
- `projectIds`: 项目ID列表，逗号分隔（可选）

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
      "validUntil": "2024-12-31T23:59:59",
      "description": "新用户专享优惠券"
    }
  ]
}
```

### 2.3 计算最终金额
**接口地址**: `POST /api/order/coupon/calculate`

**功能**: 计算使用优惠券后的最终金额

**请求头**:
```
Authorization: Bearer <your-jwt-token>
```

**请求参数**:
```json
{
  "originalAmount": 100.00,
  "couponCode": "DISCOUNT10",
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
    "discountAmount": 10.00,
    "finalAmount": 90.00,
    "couponCode": "DISCOUNT10",
    "couponName": "新用户优惠券"
  }
}
```

## 3. 支付相关接口

### 3.1 获取微信支付参数
**接口地址**: `POST /api/order/payment/wechat/params`

**功能**: 获取微信支付所需的参数

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

### 3.2 查询支付状态
**接口地址**: `GET /api/order/payment/status?orderId={orderId}`

**功能**: 查询订单支付状态

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

### 3.3 支付回调处理
**接口地址**: `POST /api/order/payment/callback`

**功能**: 处理支付平台回调（内部使用）

**说明**: 此接口由支付平台调用，前端无需直接调用

## 4. 订单状态说明

| 状态 | 中文名称 | 说明 |
|------|----------|------|
| `pending` | 待支付 | 订单已创建，等待支付 |
| `paid` | 已支付 | 支付成功，等待服务 |
| `shipping` | 服务中 | 正在提供服务 |
| `completed` | 已完成 | 服务完成 |
| `cancelled` | 已取消 | 订单已取消 |
| `refunded` | 已退款 | 订单已退款 |

## 5. 支付方式说明

| 支付方式 | 说明 |
|----------|------|
| `wechat` | 微信支付 |
| `alipay` | 支付宝支付 |
| `cash` | 现金支付 |
| `card` | 刷卡支付 |

## 6. 优惠券类型说明

| 类型 | 说明 |
|------|------|
| `PERCENTAGE` | 百分比折扣 |
| `FIXED_AMOUNT` | 固定金额折扣 |

## 7. 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 1001 | 参数错误 | 检查必填参数是否完整，参数格式是否正确 |
| 2001 | 认证失败 | 检查登录状态和权限 |
| 3001 | 系统错误 | 联系后端开发人员 |
| 4001 | 业务错误 | 根据具体错误信息处理 |
| 4002 | 优惠券不可用 | 检查优惠券状态、有效期、使用条件 |
| 4003 | 订单状态错误 | 检查订单当前状态是否允许该操作 |
| 4004 | 支付失败 | 检查支付参数和网络连接 |

## 8. 前端集成建议

### 8.1 订单创建流程
1. 用户选择商品/服务
2. 选择优惠券（可选）
3. 调用验证优惠券接口
4. 调用计算最终金额接口
5. 调用创建订单接口
6. 调用支付接口（如需要）

### 8.2 优惠券使用流程
1. 用户输入优惠券代码
2. 调用验证优惠券接口
3. 显示优惠券信息和折扣金额
4. 调用计算最终金额接口
5. 更新订单金额显示

### 8.3 支付流程
1. 用户确认订单信息
2. 调用获取支付参数接口
3. 调用支付平台SDK
4. 轮询查询支付状态
5. 支付成功后更新订单状态

### 8.4 订单列表展示
1. 调用获取订单列表接口
2. 按状态筛选（可选）
3. 分页加载
4. 显示订单基本信息
5. 点击查看详情

### 8.5 实时更新
- 订单状态变化时及时更新UI
- 优惠券使用后及时刷新列表
- 支付成功后自动跳转

## 9. 注意事项

1. **金额精度**: 所有金额字段使用两位小数
2. **时间格式**: 使用ISO 8601格式（YYYY-MM-DDTHH:mm:ss）
3. **分页**: 页码从0开始
4. **限流**: 部分接口有调用频率限制
5. **缓存**: 建议对优惠券信息进行适当缓存
6. **错误处理**: 统一处理API响应中的错误码和错误信息

---

**文档版本**: v1.0  
**更新时间**: 2024-01-01  
**维护人员**: 后端开发团队
