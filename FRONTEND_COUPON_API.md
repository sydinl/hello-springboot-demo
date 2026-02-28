# 前端优惠券接口对接文档

## 概述
本文档描述了前端用户领取和使用优惠券的完整API接口，包括新增的用户优惠券管理功能。

## 接口分类

### 1. 公开接口（无需认证）
- 适合微信小程序、H5等公开场景
- 通过userId参数进行用户识别

### 2. 认证接口（需要Token）
- 适合有完整登录体系的应用
- 通过Authorization头进行身份验证

## 1. 优惠券领取相关接口

### 1.1 获取可领取优惠券列表 ⭐ 新增
**接口地址**: `GET /api/coupons/public/available`
**权限要求**: 无
**限流**: 每分钟200次

**功能说明**: 获取所有用户可以领取的公开优惠券列表

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "coupon-uuid",
      "couponCode": "WELCOME10",
      "couponName": "新用户优惠券",
      "description": "新用户专享优惠",
      "couponType": "PERCENTAGE",
      "discountValue": 10.00,
      "minOrderAmount": 50.00,
      "maxDiscountAmount": 20.00,
      "totalQuantity": 1000,
      "usedQuantity": 150,
      "remainingQuantity": 850,
      "validFrom": "2024-01-01T00:00:00",
      "validUntil": "2024-12-31T23:59:59",
      "isActive": true,
      "isPublic": true,
      "usageLimitPerUser": 1
    }
  ]
}
```

**前端调用示例**:
```javascript
const fetchAvailableCoupons = async () => {
  try {
    const response = await fetch('/api/coupons/public/available');
    const data = await response.json();
    
    if (data.code === 200) {
      // 显示优惠券列表供用户选择
      displayCoupons(data.data);
    } else {
      console.error('获取优惠券列表失败:', data.message);
    }
  } catch (error) {
    console.error('获取优惠券列表失败:', error);
  }
};
```

### 1.2 公开领取优惠券
**接口地址**: `POST /api/coupons/public/claim`
**权限要求**: 无
**限流**: 每分钟20次（按用户）

**请求参数**:
```json
{
  "couponCode": "WELCOME10",
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
    "couponCode": "WELCOME10",
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

**前端调用示例**:
```javascript
const claimCouponPublic = async (couponCode, userId) => {
  try {
    const response = await fetch('/api/coupons/public/claim', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        couponCode: couponCode,
        userId: userId
      })
    });
    
    const data = await response.json();
    
    if (data.code === 200) {
      alert('优惠券领取成功！');
      loadUserCoupons();
    } else {
      alert('领取失败：' + data.message);
    }
  } catch (error) {
    console.error('领取优惠券失败:', error);
    alert('领取失败，请稍后重试');
  }
};
```

### 1.3 认证接口领取优惠券
**接口地址**: `POST /api/coupons/user/claim`
**权限要求**: 需要Authorization头
**限流**: 每分钟20次（按用户）

**请求头**:
```
Authorization: Bearer <token>
```

**请求参数**:
```json
{
  "couponCode": "WELCOME10"
}
```

**响应格式**: 与公开接口相同

**前端调用示例**:
```javascript
const claimCoupon = async (couponCode) => {
  try {
    const token = localStorage.getItem('token');
    const response = await fetch('/api/coupons/user/claim', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        couponCode: couponCode
      })
    });
    
    const data = await response.json();
    
    if (data.code === 200) {
      alert('优惠券领取成功！');
      loadUserCoupons();
    } else {
      alert('领取失败：' + data.message);
    }
  } catch (error) {
    console.error('领取优惠券失败:', error);
    alert('领取失败，请稍后重试');
  }
};
```

## 2. 用户优惠券管理接口

### 2.1 获取用户优惠券列表
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
        "userId": "user-uuid",
        "couponId": "coupon-uuid",
        "couponCode": "WELCOME10",
        "couponName": "新用户优惠券",
        "couponType": "PERCENTAGE",
        "discountValue": 10.00,
        "minOrderAmount": 50.00,
        "maxDiscountAmount": 20.00,
        "validFrom": "2024-01-01T00:00:00",
        "validUntil": "2024-12-31T23:59:59",
        "status": "UNUSED",
        "claimedAt": "2024-01-01T10:00:00",
        "usedAt": null,
        "orderId": null
      }
    ],
    "page": 1,
    "size": 10,
    "totalPages": 1
  }
}
```

**前端调用示例**:
```javascript
const loadUserCoupons = async (userId, status = 'UNUSED') => {
  try {
    const params = new URLSearchParams({
      userId: userId,
      status: status,
      page: 1,
      size: 10
    });
    
    const response = await fetch(`/api/coupons/public/user/list?${params}`);
    const data = await response.json();
    
    if (data.code === 200) {
      displayUserCoupons(data.data.list);
    } else {
      console.error('获取用户优惠券失败:', data.message);
    }
  } catch (error) {
    console.error('获取用户优惠券失败:', error);
  }
};
```

### 2.2 获取用户可用优惠券
**接口地址**: `GET /api/coupons/public/user/available`
**权限要求**: 无
**限流**: 每分钟200次

**请求参数**:
```
userId: string (必填) - 用户ID
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "user-coupon-uuid",
      "couponCode": "WELCOME10",
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

## 3. 优惠券验证和使用接口

### 3.1 验证优惠券
**接口地址**: `POST /api/coupons/validate`
**权限要求**: 无
**限流**: 每分钟200次

**请求参数**:
```json
{
  "couponCode": "WELCOME10",
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

**前端调用示例**:
```javascript
const validateCoupon = async (couponCode, userId, orderAmount) => {
  try {
    const response = await fetch('/api/coupons/validate', {
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
    
    const data = await response.json();
    
    if (data.code === 200) {
      if (data.data.isValid) {
        console.log('优惠券有效，折扣金额:', data.data.discount);
        return data.data;
      } else {
        alert('优惠券无效或已过期');
        return null;
      }
    } else {
      alert('验证失败：' + data.message);
      return null;
    }
  } catch (error) {
    console.error('验证优惠券失败:', error);
    return null;
  }
};
```

### 3.2 使用用户优惠券
**接口地址**: `POST /api/coupons/user/use`
**权限要求**: 需要Authorization头
**限流**: 每分钟20次（按用户）

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

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "user-coupon-uuid",
    "status": "USED",
    "usedAt": "2024-01-01T15:30:00",
    "orderId": "order-uuid"
  }
}
```

## 4. 管理端用户优惠券管理接口 ⭐ 新增

### 4.1 获取用户优惠券列表（管理端）
**接口地址**: `GET /admin/api/user-coupons`
**权限要求**: ADMIN
**限流**: 每分钟200次

**请求参数**:
```
page: number (可选) - 页码，默认1
pageSize: number (可选) - 每页数量，默认10
keyword: string (可选) - 关键词搜索
status: string (可选) - 状态筛选 (UNUSED/USED/EXPIRED)
type: string (可选) - 类型筛选 (PERCENTAGE/FIXED_AMOUNT/FREE_SHIPPING)
timeRange: string (可选) - 时间范围筛选
```

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [
      {
        "id": "user-coupon-uuid",
        "userId": "user-uuid",
        "couponId": "coupon-uuid",
        "couponCode": "WELCOME10",
        "couponName": "新用户优惠券",
        "couponType": "PERCENTAGE",
        "discountValue": 10.00,
        "minOrderAmount": 50.00,
        "maxDiscountAmount": 20.00,
        "validFrom": "2024-01-01T00:00:00",
        "validUntil": "2024-12-31T23:59:59",
        "status": "UNUSED",
        "claimedAt": "2024-01-01T10:00:00",
        "usedAt": null,
        "orderId": null
      }
    ],
    "page": 1,
    "size": 10,
    "totalPages": 10
  }
}
```

### 4.2 获取用户优惠券统计（管理端）
**接口地址**: `GET /admin/api/user-coupons/statistics`
**权限要求**: ADMIN
**限流**: 每分钟200次

**响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalUserCoupons": 1000,
    "unusedUserCoupons": 800,
    "usedUserCoupons": 150,
    "expiredUserCoupons": 50
  }
}
```

### 4.3 设置用户优惠券为过期（管理端）
**接口地址**: `PUT /admin/api/user-coupons/{userCouponId}/expire`
**权限要求**: ADMIN
**限流**: 每分钟200次

**响应格式**:
```json
{
  "code": 200,
  "message": "设置优惠券过期成功",
  "data": "success"
}
```

## 5. 数据模型说明

### 5.1 优惠券类型
| 类型 | 说明 | 示例 |
|------|------|------|
| PERCENTAGE | 百分比折扣 | 10%折扣 |
| FIXED_AMOUNT | 固定金额折扣 | 减10元 |
| FREE_SHIPPING | 免运费 | 免运费 |

### 5.2 用户优惠券状态
| 状态 | 说明 | 颜色标识 |
|------|------|----------|
| UNUSED | 未使用 | 绿色 |
| USED | 已使用 | 蓝色 |
| EXPIRED | 已过期 | 红色 |

### 5.3 优惠券字段说明
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
| usageLimitPerUser | number | 每用户领取次数限制 |

## 6. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 1001 | 参数错误 |
| 2001 | 认证失败 |
| 3001 | 系统错误 |

## 7. 完整的前端实现示例

```javascript
class CouponManager {
  constructor() {
    this.userId = this.getUserId();
    this.baseUrl = '/api/coupons';
  }
  
  // 获取可领取优惠券列表
  async getAvailableCoupons() {
    try {
      const response = await fetch(`${this.baseUrl}/public/available`);
      const data = await response.json();
      
      if (data.code === 200) {
        return data.data;
      } else {
        throw new Error(data.message);
      }
    } catch (error) {
      console.error('获取可领取优惠券失败:', error);
      throw error;
    }
  }
  
  // 领取优惠券
  async claimCoupon(couponCode) {
    try {
      const response = await fetch(`${this.baseUrl}/public/claim`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          couponCode: couponCode,
          userId: this.userId
        })
      });
      
      const data = await response.json();
      
      if (data.code === 200) {
        return data.data;
      } else {
        throw new Error(data.message);
      }
    } catch (error) {
      console.error('领取优惠券失败:', error);
      throw error;
    }
  }
  
  // 获取用户优惠券列表
  async getUserCoupons(status = 'UNUSED', page = 1, size = 10) {
    try {
      const params = new URLSearchParams({
        userId: this.userId,
        status: status,
        page: page,
        size: size
      });
      
      const response = await fetch(`${this.baseUrl}/public/user/list?${params}`);
      const data = await response.json();
      
      if (data.code === 200) {
        return data.data;
      } else {
        throw new Error(data.message);
      }
    } catch (error) {
      console.error('获取用户优惠券失败:', error);
      throw error;
    }
  }
  
  // 验证优惠券
  async validateCoupon(couponCode, orderAmount) {
    try {
      const response = await fetch(`${this.baseUrl}/validate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          couponCode: couponCode,
          userId: this.userId,
          orderAmount: orderAmount
        })
      });
      
      const data = await response.json();
      
      if (data.code === 200) {
        return data.data;
      } else {
        throw new Error(data.message);
      }
    } catch (error) {
      console.error('验证优惠券失败:', error);
      throw error;
    }
  }
  
  // 工具方法
  getUserId() {
    return localStorage.getItem('userId') || 'default-user-id';
  }
  
  formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('zh-CN');
  }
  
  formatDateTime(dateString) {
    return new Date(dateString).toLocaleString('zh-CN');
  }
  
  getCouponTypeText(type) {
    const types = {
      'PERCENTAGE': '百分比折扣',
      'FIXED_AMOUNT': '固定金额',
      'FREE_SHIPPING': '免运费'
    };
    return types[type] || '未知';
  }
  
  getStatusText(status) {
    const statuses = {
      'UNUSED': '未使用',
      'USED': '已使用',
      'EXPIRED': '已过期'
    };
    return statuses[status] || '未知';
  }
}

// 使用示例
const couponManager = new CouponManager();

// 页面加载时获取数据
document.addEventListener('DOMContentLoaded', async () => {
  try {
    // 获取可领取优惠券
    const availableCoupons = await couponManager.getAvailableCoupons();
    displayAvailableCoupons(availableCoupons);
    
    // 获取用户优惠券
    const userCoupons = await couponManager.getUserCoupons();
    displayUserCoupons(userCoupons);
  } catch (error) {
    console.error('加载优惠券数据失败:', error);
  }
});
```

## 8. 注意事项

1. **限流保护**: 所有接口都有适当的限流保护，请合理控制调用频率
2. **错误处理**: 请妥善处理各种错误情况，给用户友好的提示
3. **数据验证**: 前端应进行基本的数据验证，但最终以服务端验证为准
4. **用户体验**: 建议在关键操作时显示加载状态，提升用户体验
5. **缓存策略**: 可考虑对优惠券列表进行适当缓存，减少不必要的请求

## 更新日志

### v1.1.0 (2024-01-01)
- ⭐ 新增：获取可领取优惠券列表接口 (`GET /api/coupons/public/available`)
- ⭐ 新增：管理端用户优惠券管理接口
- ⭐ 新增：管理端用户优惠券统计接口
- ⭐ 新增：管理端设置用户优惠券过期接口
- 🔧 优化：完善用户优惠券管理功能
- 📝 更新：完善前端对接文档
