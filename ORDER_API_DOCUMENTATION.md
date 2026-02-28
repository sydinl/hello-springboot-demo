# 订单接口文档

## 概述

本文档描述了订单管理相关的API接口，包括订单创建、查询、状态更新、支付等功能。

## 基础信息

- **API基础路径**: `/api/order`
- **请求方式**: `POST` / `GET` / `PUT`
- **数据格式**: `JSON`
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

## 1. 订单管理接口

### 1.1 创建订单（简单方式）

**接口地址**: `POST /api/order`

**请求参数**:
```json
{
  "userId": "user-uuid-string",
  "status": "pending",
  "totalPrice": 159.00,
  "paymentMethod": "wechat",
  "orderNo": "ORDER16409952000001234",
  "source": "app",
  "addressId": "address-uuid-string",
  "technicianId": "technician-uuid-string",
  "couponId": "coupon-uuid-string",
  "remarks": "订单备注",
  "items": [
    {
      "projectId": "project-uuid-string",
      "projectName": "唐足道",
      "price": 159.00,
      "quantity": 1,
      "duration": "100分钟",
      "technicianId": "technician-uuid-string",
      "timeSlot": "14:00-15:00"
    }
  ]
}
```

**响应数据**:
```json
{
  "orderId": "order-uuid-string",
  "userId": "user-uuid-string",
  "status": "pending",
  "totalPrice": 159.00,
  "paymentMethod": "wechat",
  "orderNo": "ORDER16409952000001234",
  "source": "app",
  "createTime": "2024-01-01T10:00:00.000Z",
  "payTime": null,
  "serviceTime": null,
  "expireTime": "2024-01-01T10:30:00.000Z",
  "addressId": "address-uuid-string",
  "technicianId": "technician-uuid-string",
  "couponId": "coupon-uuid-string",
  "remarks": "订单备注",
  "wechatTransactionId": null,
  "wechatPrepayId": null,
  "items": [...]
}
```

### 1.2 创建订单（支付方式）

**接口地址**: `POST /api/order/create`

**请求参数**:
```json
{
  "items": [
    {
      "projectId": "9c5fe36d-91e2-4969-a1bb-c16ba5229120",
      "projectName": "唐足道",
      "price": 159,
      "quantity": 1,
      "duration": "100分钟",
      "technicianId": "",
      "timeSlot": ""
    }
  ],
  "totalAmount": 159,
  "paymentMethod": "wechat",
  "source": "app"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": "order-uuid-string",
    "orderNo": "ORDER16409952000001234",
    "status": "pending",
    "totalAmount": 159,
    "paymentMethod": "wechat",
    "createTime": "2024-01-01T10:00:00.000Z",
    "expireTime": "2024-01-01T10:30:00.000Z"
  }
}
```

### 1.3 获取订单详情

**接口地址**: `GET /api/order/detail`

**请求参数**:
```
orderId: string (必填) - 订单ID
```

**响应数据**:
```json
{
  "orderId": "order-uuid-string",
  "userId": "user-uuid-string",
  "status": "pending",
  "totalPrice": 159.00,
  "paymentMethod": "wechat",
  "orderNo": "ORDER16409952000001234",
  "source": "app",
  "createTime": "2024-01-01T10:00:00.000Z",
  "payTime": null,
  "serviceTime": null,
  "expireTime": "2024-01-01T10:30:00.000Z",
  "addressId": "address-uuid-string",
  "technicianId": "technician-uuid-string",
  "couponId": "coupon-uuid-string",
  "remarks": "订单备注",
  "wechatTransactionId": null,
  "wechatPrepayId": null,
  "items": [...]
}
```

### 1.4 获取用户订单列表

**接口地址**: `GET /api/order/list`

**请求参数**:
```
userId: string (必填) - 用户ID
page: int (可选) - 页码，默认0
size: int (可选) - 每页大小，默认10
```

**响应数据**:
```json
{
  "content": [...], // 订单列表
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

### 1.5 按状态获取用户订单列表

**接口地址**: `GET /api/order/listByStatus`

**请求参数**:
```
userId: string (必填) - 用户ID
status: string (必填) - 订单状态
page: int (可选) - 页码，默认0
size: int (可选) - 每页大小，默认10
```

**响应数据**:
```json
{
  "content": [...], // 订单列表
  "totalElements": 50,
  "totalPages": 5,
  "size": 10,
  "number": 0
}
```

### 1.6 更新订单状态

**接口地址**: `PUT /api/order/status`

**请求参数**:
```
orderId: string (必填) - 订单ID
status: string (必填) - 新状态
```

**响应数据**:
```json
{
  "orderId": "order-uuid-string",
  "status": "shipping",
  "updateTime": "2024-01-01T10:05:00.000Z"
}
```

## 2. 支付相关接口

### 2.1 获取微信支付参数

**接口地址**: `POST /api/order/payment/wechat/params`

**请求参数**:
```json
{
  "orderId": "order-uuid-string"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "timeStamp": "1640995200",
    "nonceStr": "random-string",
    "packageValue": "prepay_id=wx123456789",
    "signType": "MD5",
    "paySign": "signature-string"
  }
}
```

### 2.2 查询支付状态

**接口地址**: `GET /api/order/payment/status`

**请求参数**:
```
orderId: string (必填) - 订单ID
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": "order-uuid-string",
    "status": "paid",
    "payTime": "2024-01-01T10:05:00.000Z",
    "transactionId": "wx123456789"
  }
}
```

### 2.3 支付回调处理

**接口地址**: `POST /api/order/payment/callback`

**请求参数**:
```json
{
  "callbackData": "微信支付回调数据"
}
```

**响应数据**:
```json
{
  "code": 0,
  "message": "success"
}
```

## 3. 管理员接口

### 3.1 获取所有订单（管理员）

**接口地址**: `GET /api/order/admin/all`

**请求参数**:
```
page: int (可选) - 页码，默认1
pageSize: int (可选) - 每页大小，默认10
status: string (可选) - 订单状态筛选
userId: string (可选) - 用户ID筛选
search: string (可选) - 搜索关键词
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [...] // 订单列表
  }
}
```

### 3.2 获取订单详情（管理员）

**接口地址**: `GET /api/order/admin/detail`

**请求参数**:
```
orderId: string (必填) - 订单ID
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 订单详情对象
  }
}
```

### 3.3 更新订单状态（管理员）

**接口地址**: `PUT /api/order/admin/status/{orderId}`

**请求参数**:
```
orderId: string (路径参数) - 订单ID
status: string (必填) - 新状态
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 更新后的订单对象
  }
}
```

### 3.4 获取订单统计信息

**接口地址**: `GET /api/order/admin/statistics`

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalOrders": 1000,
    "pendingOrders": 50,
    "shippingOrders": 30,
    "completedOrders": 900,
    "aftersaleOrders": 20,
    "todayOrders": 25
  }
}
```

## 4. 数据模型

### 4.1 订单状态

| 状态值 | 说明 | 颜色标识 |
|--------|------|----------|
| pending | 待处理 | 黄色 |
| paid | 已支付 | 蓝色 |
| shipping | 进行中 | 青色 |
| completed | 已完成 | 绿色 |
| aftersale | 售后 | 红色 |

### 4.2 支付方式

| 支付方式 | 说明 | 颜色标识 |
|----------|------|----------|
| balance | 余额支付 | 蓝色 |
| wechat | 微信支付 | 绿色 |

### 4.3 订单来源

| 来源 | 说明 |
|------|------|
| cart | 购物车 |
| detail | 详情页 |
| order | 订单页 |

## 5. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 1001 | 参数错误 |
| 1002 | 订单不存在 |
| 1004 | 支付参数生成失败 |
| 1005 | 支付状态查询失败 |
| 3001 | 系统错误 |

## 6. 请求示例

### 6.1 创建订单示例

```javascript
// 创建订单
const createOrder = async () => {
  const orderData = {
    items: [
      {
        projectId: "9c5fe36d-91e2-4969-a1bb-c16ba5229120",
        projectName: "唐足道",
        price: 159,
        quantity: 1,
        duration: "100分钟",
        technicianId: "",
        timeSlot: ""
      }
    ],
    totalAmount: 159,
    paymentMethod: "wechat",
    source: "app"
  };

  try {
    const response = await fetch('/api/order/create', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(orderData)
    });
    
    const result = await response.json();
    console.log('订单创建结果:', result);
  } catch (error) {
    console.error('创建订单失败:', error);
  }
};
```

### 6.2 获取微信支付参数示例

```javascript
// 获取微信支付参数
const getWechatPaymentParams = async (orderId) => {
  try {
    const response = await fetch('/api/order/payment/wechat/params', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ orderId })
    });
    
    const result = await response.json();
    if (result.code === 200) {
      // 调用微信支付
      wx.chooseWXPay({
        timestamp: result.data.timeStamp,
        nonceStr: result.data.nonceStr,
        package: result.data.packageValue,
        signType: result.data.signType,
        paySign: result.data.paySign,
        success: function(res) {
          console.log('支付成功');
        },
        fail: function(res) {
          console.log('支付失败');
        }
      });
    }
  } catch (error) {
    console.error('获取支付参数失败:', error);
  }
};
```

### 6.3 查询订单列表示例

```javascript
// 查询用户订单列表
const getOrderList = async (userId, page = 0, size = 10) => {
  try {
    const response = await fetch(`/api/order/list?userId=${userId}&page=${page}&size=${size}`);
    const result = await response.json();
    console.log('订单列表:', result);
  } catch (error) {
    console.error('获取订单列表失败:', error);
  }
};
```

## 7. 注意事项

1. **订单创建**: 使用 `/api/order/create` 接口创建订单时，必须提供 `items` 数组和 `paymentMethod` 字段
2. **支付方式**: 目前支持 `balance`（余额支付）和 `wechat`（微信支付）两种方式
3. **订单状态**: 订单状态变更需要通过相应的接口进行，不能直接修改
4. **分页查询**: 所有列表接口都支持分页，默认页码从0开始
5. **错误处理**: 所有接口都返回统一的错误格式，请根据 `code` 字段判断请求是否成功
6. **权限控制**: 管理员接口需要相应的权限才能访问

## 8. 更新日志

- **2025-09-13**: 初始版本，包含基础订单管理功能
- **2025-09-13**: 添加支付相关接口
- **2025-09-13**: 重构Order和PaymentOrder，统一订单管理
- **2025-09-13**: 添加支付方式字段和订单来源字段


