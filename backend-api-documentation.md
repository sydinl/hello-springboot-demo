# 后端API接口文档

## 1. 用户相关接口

### 1.1 用户登录
- **URL**: `/api/user/login`
- **方法**: `POST`
- **请求参数**: 
  ```json
  {
    "phone": "string", // 手机号
    "password": "string" // 密码
  }
  ```
- **返回结果**: 
  ```json
  {
    "success": true/false,
    "userId": "string",
    "token": "string",
    "userInfo": {}
  }
  ```

### 1.2 获取用户信息
- **URL**: `/api/user/info`
- **方法**: `GET`
- **请求参数**: 
  - `userId` (UUID): 用户ID
- **返回结果**: 
  ```json
  {
    "userId": "string",
    "nickname": "string",
    "realName": "string",
    "avatar": "string",
    "gender": "string",
    "birthdate": "string",
    "phone": "string",
    "points": integer,
    "balance": double,
    "memberLevel": "string",
    "addressCount": integer,
    "favoriteCount": integer,
    "couponCount": integer,
    "cardCount": integer
  }
  ```

### 1.3 更新用户信息
- **URL**: `/api/user/info`
- **方法**: `PUT`
- **请求参数**: 
  - `userId` (UUID): 用户ID
  - 请求体: 用户信息对象
- **返回结果**: 更新后的用户信息对象

### 1.4 修改密码
- **URL**: `/api/user/password`
- **方法**: `PUT`
- **请求参数**: 
  - `userId` (UUID): 用户ID
  - 请求体: 
    ```json
    {
      "oldPassword": "string", // 旧密码
      "newPassword": "string" // 新密码
    }
    ```
- **返回结果**: 
  ```json
  {
    "success": true/false
  }
  ```

## 2. 项目相关接口

### 2.1 获取项目列表
- **URL**: `/api/project/list`
- **方法**: `GET`
- **请求参数**: 
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 项目分页列表

### 2.2 按分类获取项目列表
- **URL**: `/api/project/listByCategory`
- **方法**: `GET`
- **请求参数**: 
  - `categoryId` (string): 分类ID
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 项目分页列表

### 2.3 搜索项目
- **URL**: `/api/project/search`
- **方法**: `GET`
- **请求参数**: 
  - `keyword` (string): 搜索关键词
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 项目分页列表

### 2.4 获取项目详情
- **URL**: `/api/project/detail`
- **方法**: `GET`
- **请求参数**: 
  - `projectId` (UUID): 项目ID
- **返回结果**: 
  ```json
  {
    "id": "string",
    "name": "string",
    "description": "string",
    "price": double,
    "image": "string",
    "duration": "string",
    "category": "string",
    "details": "string",
    "salesCount": integer,
    "rating": double,
    "isHot": boolean,
    "images": [ // 项目图片列表
      {
        "id": "string",
        "projectId": "UUID",
        "url": "string",
        "description": "string"
      }
    ],
    "reviews": [ // 项目评价列表
      // 评价对象
    ],
    "createTime": "date",
    "updateTime": "date"
  }
  ```

## 3. 订单相关接口

### 3.1 创建订单
- **URL**: `/api/order`
- **方法**: `POST`
- **请求参数**: 订单对象
- **返回结果**: 创建的订单对象

### 3.2 获取订单详情
- **URL**: `/api/order/detail`
- **方法**: `GET`
- **请求参数**: 
  - `orderId` (UUID): 订单ID
- **返回结果**: 
  ```json
  {
    "orderId": "string",
    "userId": "string",
    "status": "string", // pending/shipping/completed/aftersale
    "totalPrice": double,
    "createTime": "date",
    "payTime": "date",
    "serviceTime": "date",
    "addressId": "string",
    "technicianId": "string",
    "couponId": "string",
    "remarks": "string",
    "items": [ // 订单项列表
      // 订单项对象
    ]
  }
  ```

### 3.3 按用户ID查询订单列表
- **URL**: `/api/order/list`
- **方法**: `GET`
- **请求参数**: 
  - `userId` (UUID): 用户ID
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 订单分页列表

### 3.4 按用户ID和状态查询订单列表
- **URL**: `/api/order/listByStatus`
- **方法**: `GET`
- **请求参数**: 
  - `userId` (UUID): 用户ID
  - `status` (string): 订单状态
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 订单分页列表

### 3.5 更新订单状态
- **URL**: `/api/order/status`
- **方法**: `PUT`
- **请求参数**: 
  - `orderId` (UUID): 订单ID
  - `status` (string): 新状态
- **返回结果**: 更新后的订单对象

## 4. 技师相关接口

### 4.1 获取技师列表
- **URL**: `/api/technician/list`
- **方法**: `GET`
- **请求参数**: 
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 技师分页列表

### 4.2 搜索技师
- **URL**: `/api/technician/search`
- **方法**: `GET`
- **请求参数**: 
  - `keyword` (string): 搜索关键词
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 技师分页列表

### 4.3 按门店获取技师列表
- **URL**: `/api/technician/byStore`
- **方法**: `GET`
- **请求参数**: 
  - `storeId` (UUID): 门店ID
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 技师分页列表

### 4.4 获取技师详情
- **URL**: `/api/technician/detail`
- **方法**: `GET`
- **请求参数**: 
  - `technicianId` (UUID): 技师ID
- **返回结果**: 技师详情对象

## 5. 评价相关接口

### 5.1 获取项目评价列表
- **URL**: `/api/review/projectReviews`
- **方法**: `GET`
- **请求参数**: 
  - `projectId` (UUID): 项目ID
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 评价分页列表

### 5.2 获取用户评价列表
- **URL**: `/api/review/userReviews`
- **方法**: `GET`
- **请求参数**: 
  - `userId` (UUID): 用户ID
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 评价分页列表

### 5.3 添加评价
- **URL**: `/api/review/add`
- **方法**: `POST`
- **请求参数**: 评价对象
- **返回结果**: 添加的评价对象

### 5.4 回复评价
- **URL**: `/api/review/reply`
- **方法**: `POST`
- **请求参数**: 
  - `reviewId` (UUID): 评价ID
  - 请求体: 回复内容字符串
- **返回结果**: 更新后的评价对象

## 6. 门店相关接口

### 6.1 获取门店列表
- **URL**: `/api/store/list`
- **方法**: `GET`
- **请求参数**: 
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 门店分页列表

### 6.2 搜索门店
- **URL**: `/api/store/search`
- **方法**: `GET`
- **请求参数**: 
  - `keyword` (string): 搜索关键词
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 门店分页列表

### 6.3 按地区获取门店列表
- **URL**: `/api/store/byArea`
- **方法**: `GET`
- **请求参数**: 
  - `area` (string): 地区
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 门店分页列表

### 6.4 获取门店详情
- **URL**: `/api/store/detail`
- **方法**: `GET`
- **请求参数**: 
  - `storeId` (UUID): 门店ID
- **返回结果**: 门店详情对象

## 7. 项目分类相关接口

### 7.1 获取所有项目分类
- **URL**: `/api/category/all`
- **方法**: `GET`
- **请求参数**: 无
- **返回结果**: 项目分类列表

### 7.2 获取分类详情
- **URL**: `/api/category/detail`
- **方法**: `GET`
- **请求参数**: 
  - `categoryId` (string): 分类ID
- **返回结果**: 分类详情对象

## 8. 积分相关接口

### 8.1 获取用户积分余额
- **URL**: `/api/point/balance`
- **方法**: `GET`
- **请求参数**: 
  - `userId` (UUID): 用户ID
- **返回结果**: 
  ```json
  {
    "points": integer
  }
  ```

### 8.2 获取用户积分记录列表
- **URL**: `/api/point/records`
- **方法**: `GET`
- **请求参数**: 
  - `userId` (UUID): 用户ID
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 积分记录分页列表

### 8.3 获取用户指定类型的积分记录
- **URL**: `/api/point/recordsByType`
- **方法**: `GET`
- **请求参数**: 
  - `userId` (UUID): 用户ID
  - `type` (string): 记录类型
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 积分记录分页列表

## 9. 会员卡相关接口

### 9.1 获取用户卡券列表
- **URL**: `/api/card/list`
- **方法**: `GET`
- **请求参数**: 
  - `userId` (UUID): 用户ID
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 卡券分页列表

### 9.2 获取用户指定类型的卡券
- **URL**: `/api/card/listByType`
- **方法**: `GET`
- **请求参数**: 
  - `userId` (UUID): 用户ID
  - `type` (string): 卡券类型
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 卡券分页列表

### 9.3 激活卡券
- **URL**: `/api/card/activate`
- **方法**: `POST`
- **请求参数**: 
  - `userId` (UUID): 用户ID
  - `cardCode` (string): 卡券码
- **返回结果**: 激活的卡券对象

## 10. 余额相关接口

### 10.1 获取用户余额
- **URL**: `/api/balance/balance`
- **方法**: `GET`
- **请求参数**: 
  - `userId` (UUID): 用户ID
- **返回结果**: 
  ```json
  {
    "balance": double
  }
  ```

### 10.2 获取用户余额记录列表
- **URL**: `/api/balance/records`
- **方法**: `GET`
- **请求参数**: 
  - `userId` (UUID): 用户ID
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 余额记录分页列表

### 10.3 获取用户指定类型的余额记录
- **URL**: `/api/balance/recordsByType`
- **方法**: `GET`
- **请求参数**: 
  - `userId` (UUID): 用户ID
  - `type` (string): 记录类型
  - `page` (int, 默认: 0): 页码
  - `size` (int, 默认: 10): 每页条数
- **返回结果**: 余额记录分页列表

## 11. 数据模型定义

### 11.1 Project (项目)
```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "price": double,
  "image": "string",
  "duration": "string",
  "category": "string",
  "details": "string",
  "salesCount": integer,
  "rating": double,
  "isHot": boolean,
  "images": [ProjectImage],
  "reviews": [Review],
  "createTime": "date",
  "updateTime": "date"
}
```

### 11.2 UserInfo (用户信息)
```json
{
  "userId": "string",
  "nickname": "string",
  "realName": "string",
  "avatar": "string",
  "gender": "string",
  "birthdate": "string",
  "phone": "string",
  "points": integer,
  "balance": double,
  "memberLevel": "string",
  "addressCount": integer,
  "favoriteCount": integer,
  "couponCount": integer,
  "cardCount": integer
}
```

### 11.3 Order (订单)
```json
{
  "orderId": "string",
  "userId": "string",
  "status": "string",
  "totalPrice": double,
  "createTime": "date",
  "payTime": "date",
  "serviceTime": "date",
  "addressId": "string",
  "technicianId": "string",
  "couponId": "string",
  "remarks": "string",
  "items": [OrderItem]
}
```

### 11.4 ProjectImage (项目图片)
```json
{
  "id": "string",
  "projectId": "UUID",
  "url": "string",
  "description": "string"
}
```