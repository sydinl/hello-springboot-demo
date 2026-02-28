# API权限配置说明

## 权限分类

### 1. 公开访问接口（微信小程序）
以下接口无需认证，微信小程序可以直接访问：

#### 项目相关API
- `GET /api/projects/list` - 获取项目列表
- `GET /api/projects/detail` - 获取项目详情
- `GET /api/projects/categories` - 获取项目分类
- `GET /api/projects/hot` - 获取热门项目
- `GET /api/projects/recommend` - 获取推荐项目
- `GET /api/projects/by-status` - 按状态获取项目
- `GET /api/projects/by-price-range` - 按价格范围获取项目
- `GET /api/projects/sales-ranking` - 获取销售排行
- `GET /api/projects/rating-ranking` - 获取评分排行

#### 用户相关API
- `GET /api/users/**` - 所有用户相关接口

#### 订单相关API
- `GET /api/orders/**` - 所有订单相关接口

#### 评论相关API
- `GET /api/reviews/**` - 所有评论相关接口

#### 分类相关API
- `GET /api/category/**` - 所有分类相关接口

#### 余额相关API
- `GET /api/balance/**` - 所有余额相关接口

### 2. 管理员权限接口
以下接口需要管理员权限（ROLE_ADMIN）：

#### 项目管理API
- `POST /api/projects/create` - 创建项目
- `PUT /api/projects/update/{id}` - 更新项目
- `DELETE /api/projects/delete/{id}` - 删除项目
- `DELETE /api/projects/batch-delete` - 批量删除项目
- `PUT /api/projects/set-hot/{id}` - 设置热门状态
- `PUT /api/projects/set-recommend/{id}` - 设置推荐状态
- `PUT /api/projects/update-status/{id}` - 更新项目状态
- `GET /api/projects/statistics` - 获取项目统计
- `GET /api/projects/admin/**` - 所有管理相关接口

### 3. 认证访问接口
以下接口需要用户认证：

#### 管理员页面
- `GET /admin/**` - 所有管理员页面

## CORS配置

为了支持微信小程序访问，已配置CORS：

```java
// 允许所有来源
.allowedOriginPatterns("*")

// 允许的HTTP方法
.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

// 允许所有请求头
.allowedHeaders("*")

// 允许携带凭证
.allowCredentials(true)

// 预检请求缓存时间
.maxAge(3600)
```

## 安全说明

1. **微信小程序访问**：所有业务API都公开访问，适合微信小程序直接调用
2. **管理员安全**：只有项目管理相关接口需要管理员权限
3. **CORS支持**：完全支持跨域访问，适合前端应用调用
4. **CSRF禁用**：API接口禁用CSRF保护，适合RESTful调用

## 使用示例

### 微信小程序调用示例
```javascript
// 获取项目列表
wx.request({
  url: 'https://yourdomain.com/api/projects/list',
  method: 'GET',
  success: function(res) {
    console.log(res.data);
  }
});

// 获取项目详情
wx.request({
  url: 'https://yourdomain.com/api/projects/detail',
  method: 'GET',
  data: {
    projectId: 'project-id-here'
  },
  success: function(res) {
    console.log(res.data);
  }
});
```

### 管理员API调用示例
```javascript
// 需要先登录获取认证信息
// 创建项目
fetch('/api/projects/create', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer your-token-here'
  },
  body: JSON.stringify({
    name: '新项目',
    price: 299.0,
    category: 'SPA护理'
  })
});
```

## 注意事项

1. 微信小程序调用时无需认证，直接访问API即可
2. 管理员操作需要先登录获取认证信息
3. 所有API都支持CORS，可以跨域调用
4. 建议在生产环境中配置具体的域名白名单
