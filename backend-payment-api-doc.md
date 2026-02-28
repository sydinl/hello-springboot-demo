# 支付接口文档

## 概述

本文档描述了前端支付功能所需的后端API接口规范，包括订单管理、支付处理、状态查询等功能。

## 基础信息

- **API基础路径**: `/api`
- **请求方式**: `POST` / `GET`
- **数据格式**: `JSON`
- **字符编码**: `UTF-8`

## 通用响应格式

```json
{
  "code": 0,           // 状态码：0-成功，非0-失败
  "message": "success", // 响应消息
  "data": {},          // 响应数据
  "timestamp": 1640995200000 // 时间戳
}
```

## 1. 订单管理接口

### 1.1 创建订单

**接口地址**: `POST /api/orders/create`

**请求参数**:
```json
{
  "items": [
    {
      "projectId": "proj_001",
      "projectName": "经典足道",
      "price": 128.00,
      "quantity": 1,
      "duration": "60分钟",
      "technicianId": "tech_001",
      "timeSlot": "14:00-15:00"
    }
  ],
  "totalAmount": 128.00,
  "paymentMethod": "wechat",
  "source": "cart",
  "createTime": "2024-01-01T10:00:00.000Z",
  "orderNo": "ORDER16409952000001234",
  "status": "pending"
}
```

**响应数据**:
```json
{
  "code": 0,
  "message": "订单创建成功",
  "data": {
    "orderId": "order_123456789",
    "orderNo": "ORDER16409952000001234",
    "status": "pending",
    "totalAmount": 128.00,
    "createTime": "2024-01-01T10:00:00.000Z",
    "expireTime": "2024-01-01T10:30:00.000Z"
  }
}
```

### 1.2 获取订单详情

**接口地址**: `GET /api/orders/detail`

**请求参数**:
```
orderId: string (必填) - 订单ID
```

**响应数据**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "orderId": "order_123456789",
    "orderNo": "ORDER16409952000001234",
    "status": "paid",
    "totalAmount": 128.00,
    "items": [...],
    "createTime": "2024-01-01T10:00:00.000Z",
    "payTime": "2024-01-01T10:05:00.000Z"
  }
}
```

## 2. 支付接口

### 2.1 获取微信支付参数

**接口地址**: `POST /api/payment/wechat/params`

**请求参数**:
```json
{
  "orderId": "order_123456789"
}
```

**响应数据**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "timeStamp": "1640995200",
    "nonceStr": "5K8264ILTKCH16CQ2502SI8ZNMTM67VS",
    "package": "prepay_id=wx123456789",
    "signType": "RSA",
    "paySign": "signature_string"
  }
}
```

### 2.2 查询支付状态

**接口地址**: `GET /api/payment/status`

**请求参数**:
```
orderId: string (必填) - 订单ID
```

**响应数据**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "orderId": "order_123456789",
    "status": "paid",
    "payTime": "2024-01-01T10:05:00.000Z",
    "transactionId": "wx123456789"
  }
}
```

### 2.3 支付回调处理

**接口地址**: `POST /api/payment/callback`

**请求参数**: 微信支付回调数据

**响应数据**:
```json
{
  "code": 0,
  "message": "success"
}
```

## 3. 状态码说明

### 3.1 订单状态
- `pending` - 待支付
- `paid` - 已支付
- `failed` - 支付失败
- `cancelled` - 已取消
- `expired` - 已过期

### 3.2 支付方式
- `wechat` - 微信支付
- `alipay` - 支付宝
- `balance` - 余额支付

### 3.3 订单来源
- `cart` - 购物车
- `detail` - 商品详情
- `order` - 订单页面

## 4. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 0 | 成功 |
| 1001 | 参数错误 |
| 1002 | 订单不存在 |
| 1003 | 订单状态错误 |
| 1004 | 支付参数生成失败 |
| 1005 | 支付状态查询失败 |
| 2001 | 用户未登录 |
| 2002 | 权限不足 |
| 3001 | 系统错误 |
| 3002 | 网络错误 |

## 5. 安全要求

1. **签名验证**: 所有支付相关接口需要验证签名
2. **参数校验**: 严格校验所有输入参数
3. **幂等性**: 支付回调接口需要保证幂等性
4. **日志记录**: 记录所有支付相关操作日志

## 6. 测试用例

### 6.1 创建订单测试
```bash
curl -X POST http://api.example.com/api/orders/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer token" \
  -d '{
    "items": [{"projectId": "proj_001", "projectName": "测试项目", "price": 100, "quantity": 1}],
    "totalAmount": 100,
    "paymentMethod": "wechat",
    "source": "cart"
  }'
```

### 6.2 获取支付参数测试
```bash
curl -X POST http://api.example.com/api/payment/wechat/params \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer token" \
  -d '{"orderId": "order_123456789"}'
```

## 7. 部署说明

1. 配置微信支付商户号
2. 配置支付回调地址
3. 配置支付密钥
4. 配置数据库连接
5. 配置Redis缓存（可选）

## 8. 监控告警

1. 支付成功率监控
2. 支付失败率告警
3. 订单创建异常告警
4. 支付回调异常告警
