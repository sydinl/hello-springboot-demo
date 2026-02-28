# 前端接口文档

## 项目概述
本文档描述了SPA休闲会所项目中前端调用的主要API接口，重点关注项目列表相关接口的使用方式和参数说明。

## 1. 项目相关接口

### 1.1 获取项目列表
- **功能说明**: 获取所有服务项目列表，支持分类筛选和分页
- **前端调用方式**: `api.projects.getList(params)`
- **接口路径**: `/api/projects/list`
- **请求方法**: `GET`
- **请求参数**:
  ```javascript
  {
    category: string,    // 项目分类名称，可选
    categoryId: string,  // 项目分类ID，可选（优先级高于category）
    page: number,        // 页码，默认1
    pageSize: number,    // 每页数量，默认10
    search: string       // 搜索关键词，可选
  }
  ```
- **返回结果**:
  ```javascript
  {
    code: number,      // 状态码，0表示成功
    message: string,   // 提示信息
    data: {
      total: number,   // 总条数
      list: [          // 项目列表
        {
          id: number,          // 项目ID
          name: string,        // 项目名称
          description: string, // 项目描述
          price: number,       // 项目价格
          categoryId: string,  // 分类ID
          imageUrl: string,    // 项目图片URL
          duration: string,    // 项目时长
          salesCount: number,  // 销量
          rating: number       // 评分
        }
      ]
    }
  }
  ```
- **使用场景**: 用于`projects.vue`页面显示完整的项目列表

### 1.2 根据分类ID获取项目列表
- **功能说明**: 根据指定的分类ID获取该分类下的所有项目
- **前端调用方式**: `api.projects.getByCategoryId(categoryId, params)`
- **接口路径**: `/api/projects/category/{categoryId}`
- **请求方法**: `GET`
- **路径参数**:
  ```javascript
  {
    categoryId: string  // 分类ID，必填
  }
  ```
- **请求参数**:
  ```javascript
  {
    page: number,      // 页码，默认1
    pageSize: number   // 每页数量，默认10
  }
  ```
- **返回结果**:
  ```javascript
  {
    code: number,      // 状态码，0表示成功
    message: string,   // 提示信息
    data: {
      total: number,   // 总条数
      list: [          // 项目列表
        {
          id: string,          // 项目ID
          name: string,        // 项目名称
          description: string, // 项目描述
          price: number,       // 项目价格
          categoryId: string,  // 分类ID
          category: string,    // 分类名称
          image: string,       // 项目图片URL
          duration: string,    // 项目时长
          salesCount: number,  // 销量
          rating: number,      // 评分
          isHot: boolean,      // 是否热门
          isRecommend: boolean // 是否推荐
        }
      ],
      categoryId: string  // 当前查询的分类ID
    }
  }
  ```
- **使用场景**: 用于分类页面显示特定分类下的项目列表
- **调用示例**:
  ```javascript
  // 获取指定分类的项目
  const fetchProjectsByCategory = async (categoryId) => {
    try {
      const response = await api.projects.getByCategoryId(categoryId, {
        page: 1,
        pageSize: 12
      });
      if (response.code === 0) {
        projectList.value = response.data.list;
        total.value = response.data.total;
      }
    } catch (error) {
      console.error('获取分类项目失败:', error);
    }
  };
  ```

### 1.3 使用场景
- **调用示例**:
  ```javascript
  // projects.vue中获取所有项目
  const fetchProjects = async () => {
    try {
      // 获取足够多的项目（100个）以展示完整列表
      const res = await api.projects.getList({ pageSize: 100 });
      if (res.code === 0 && res.data && res.data.list) {
        projects.value = res.data.list.map(project => ({
          id: project.id,
          name: project.name,
          desc: project.description || '暂无描述',
          price: project.price,
          category: project.categoryId || 'other',
          img: project.imageUrl || '/static/icons/placeholder.png'
        }));
      }
    } catch (error) {
      console.error('获取项目数据失败:', error);
      // 使用模拟数据作为备用
    }
  };
  ```

### 1.2 获取项目分类列表
- **功能说明**: 获取所有项目分类
- **前端调用方式**: `api.projects.getCategories()`
- **接口路径**: `/api/projects/categories`
- **请求方法**: `GET`
- **请求参数**: 无
- **返回结果**:
  ```javascript
  {
    code: number,      // 状态码，0表示成功
    message: string,   // 提示信息
    data: [            // 分类列表
      {
        id: string,    // 分类ID
        name: string,  // 分类名称
        count: number  // 该分类下的项目数量
      }
    ]
  }
  ```
- **使用场景**: 用于`projects.vue`页面顶部的分类导航
- **调用示例**:
  ```javascript
  // projects.vue中获取分类数据
  const fetchCategories = async () => {
    try {
      const res = await api.projects.getCategories();
      if (res.code === 0 && res.data) {
        // 确保分类数据中包含'全部'选项
        categories.value = [{ id: 'all', name: '全部' }, ...res.data];
      }
    } catch (err) {
      console.error('获取分类数据失败:', err);
      // 使用默认分类数据
      categories.value = [
        { id: 'all', name: '全部' },
        { id: 'spa', name: 'SPA' },
        { id: 'foot', name: '足道' },
        { id: 'small', name: '小项' },
        { id: 'other', name: '其他' }
      ];
    }
  };
  ```

### 1.3 获取热门项目列表
- **功能说明**: 获取热门服务项目列表
- **前端调用方式**: `api.projects.getHotProjects(params)`
- **接口路径**: `/api/projects/hot`
- **请求方法**: `GET`
- **请求参数**:
  ```javascript
  {
    page: number,     // 页码，默认1
    pageSize: number  // 每页数量，默认10
  }
  ```
- **返回结果**:
  ```javascript
  {
    code: number,      // 状态码，0表示成功
    message: string,   // 提示信息
    data: {
      total: number,   // 总条数
      list: [          // 热门项目列表
        {
          id: number,          // 项目ID
          name: string,        // 项目名称
          description: string, // 项目描述
          price: number,       // 项目价格
          imageUrl: string,    // 项目图片URL
          duration: string,    // 项目时长
          salesCount: number,  // 销量
          rating: number       // 评分
        }
      ]
    }
  }
  ```
- **使用场景**: 用于`home.vue`页面显示热门项目
- **调用示例**:
  ```javascript
  // home.vue中获取热门项目
  const fetchProjects = async () => {
    try {
      // 获取热门项目
      const hotRes = await api.projects.getHotProjects({ pageSize: 4 });
      if (hotRes.code === 0 && hotRes.data && hotRes.data.list) {
        services.value = hotRes.data.list.map(project => ({
          id: project.id,
          name: project.name,
          desc: project.description || '暂无描述',
          price: project.price,
          img: project.imageUrl || '/static/icons/placeholder.png'
        }));
      }
    } catch (error) {
      console.error('获取项目数据失败:', error);
      // 使用模拟数据作为备用
    }
  };
  ```

### 1.4 获取推荐项目列表
- **功能说明**: 获取个性化推荐服务项目列表
- **前端调用方式**: `api.projects.getRecommendProjects(params)`
- **接口路径**: `/api/projects/recommend`
- **请求方法**: `GET`
- **请求参数**:
  ```javascript
  {
    page: number,     // 页码，默认1
    pageSize: number  // 每页数量，默认10
  }
  ```
- **返回结果**:
  ```javascript
  {
    code: number,      // 状态码，0表示成功
    message: string,   // 提示信息
    data: {
      total: number,   // 总条数
      list: [          // 推荐项目列表
        {
          id: number,          // 项目ID
          name: string,        // 项目名称
          description: string, // 项目描述
          price: number,       // 项目价格
          imageUrl: string,    // 项目图片URL
          duration: string,    // 项目时长
          salesCount: number,  // 销量
          rating: number       // 评分
        }
      ]
    }
  }
  ```
- **使用场景**: 用于`home.vue`页面显示套餐推荐
- **调用示例**:
  ```javascript
  // home.vue中获取套餐数据（使用推荐项目接口）
  const fetchProjects = async () => {
    try {
      // 获取套餐数据（可以使用推荐项目接口）
      const recommendRes = await api.projects.getRecommendProjects({ pageSize: 2 });
      if (recommendRes.code === 0 && recommendRes.data && recommendRes.data.list) {
        packages.value = recommendRes.data.list.map(project => ({
          id: project.id,
          name: project.name,
          price: project.price,
          duration: project.duration || '60分钟',
          img: project.imageUrl || '/static/icons/placeholder.png'
        }));
      }
    } catch (error) {
      console.error('获取项目数据失败:', error);
      // 使用模拟数据作为备用
    }
  };
  ```

## 2. 基础请求方法

### 2.1 request方法
- **功能说明**: 前端统一的请求封装，基于uni.request实现
- **导出方式**: `export { request }`
- **参数说明**:
  ```javascript
  request(url, method, data = {}, options = {})
  ```
  - `url`: 请求地址
  - `method`: 请求方法（GET/POST等）
  - `data`: 请求数据
  - `options`: 额外选项
- **功能特性**:
  - 自动携带token（从uni.getStorageSync('userToken')获取）
  - 统一的错误处理和提示
  - 401未授权自动跳转到登录页
  - 支持GET/POST等多种请求方式
- **使用示例**:
  ```javascript
  import { request } from '../../utils/api';
  
  const fetchSomeData = async () => {
    try {
      const res = await request('/api/some/path', 'GET', { id: 1 });
      return res.data;
    } catch (error) {
      console.error('请求失败:', error);
      throw error;
    }
  };
  ```

## 3. 错误处理机制

### 3.1 统一错误处理
- **全局错误处理**: request方法中已实现统一的错误处理，包括网络错误、状态码错误等
- **提示方式**: 使用`uni.showToast`显示错误信息
- **重试机制**: 在页面中实现了点击错误提示重试的功能

### 3.2 备用数据机制
- **实现方式**: 所有API调用都包含try-catch块，当API调用失败时使用模拟数据作为备用
- **使用场景**: 确保在后端服务不可用或网络异常时，页面仍能展示内容
- **示例代码**:
  ```javascript
  try {
    // API调用代码
  } catch (error) {
    console.error('获取数据失败:', error);
    // 使用模拟数据作为备用
    data.value = mockData;
  }
  ```

## 4. 跨域处理方案

### 4.1 代理配置
- **实现方式**: 通过vite.config.js中的proxy配置实现跨域请求转发
- **配置示例**:
  ```javascript
  server: {
    proxy: {
      '/api': {
        target: 'https://iousxaoupndv.sealoshzh.site',
        secure: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      }
    }
  }
  ```
- **API基础路径**: 前端请求使用相对路径，通过代理转发到后端

## 5. 数据模型

### 5.1 项目模型(Project)
```javascript
{
  id: number,          // 项目ID
  name: string,        // 项目名称
  desc: string,        // 项目描述
  price: number,       // 项目价格
  category: string,    // 分类ID
  img: string          // 项目图片URL
}
```

### 5.2 分类模型(Category)
```javascript
{
  id: string,          // 分类ID
  name: string         // 分类名称
}
```

## 6. 管理接口

### 6.1 获取所有项目（管理用）
- **功能说明**: 管理员获取所有项目列表，支持多种筛选条件
- **前端调用方式**: `api.projects.getAllProjects(params)`
- **接口路径**: `/api/projects/admin/all`
- **请求方法**: `GET`
- **请求参数**:
  ```javascript
  {
    page: number,        // 页码，默认1
    pageSize: number,    // 每页数量，默认10
    status: string,      // 项目状态筛选，可选
    category: string,    // 项目分类名称筛选，可选
    categoryId: string,  // 项目分类ID筛选，可选（优先级高于category）
    search: string       // 搜索关键词，可选
  }
  ```
- **返回结果**:
  ```javascript
  {
    code: number,      // 状态码，0表示成功
    message: string,   // 提示信息
    data: {
      total: number,   // 总条数
      list: [          // 项目列表
        {
          id: string,          // 项目ID
          name: string,        // 项目名称
          description: string, // 项目描述
          price: number,       // 项目价格
          categoryId: string,  // 分类ID
          category: string,    // 分类名称
          image: string,       // 项目图片URL
          duration: string,    // 项目时长
          status: string,      // 项目状态
          salesCount: number,  // 销量
          rating: number,      // 评分
          isHot: boolean,      // 是否热门
          isRecommend: boolean, // 是否推荐
          createTime: string,  // 创建时间
          updateTime: string   // 更新时间
        }
      ]
    }
  }
  ```
- **使用场景**: 用于管理后台的项目管理页面
- **调用示例**:
  ```javascript
  // 管理后台获取项目列表
  const fetchAllProjects = async (filters = {}) => {
    try {
      const response = await api.projects.getAllProjects({
        page: 1,
        pageSize: 10,
        ...filters
      });
      if (response.code === 0) {
        projectList.value = response.data.list;
        total.value = response.data.total;
      }
    } catch (error) {
      console.error('获取项目列表失败:', error);
    }
  };
  ```

## 7. 调用流程图

### 7.1 项目列表加载流程
1. 页面加载时调用`loadAllData()`
2. 并行调用`fetchCategories()`和`fetchProjects()`
3. 获取数据成功后更新页面状态
4. 获取数据失败时显示错误信息并使用模拟数据

```
页面加载 → loadAllData() → 并行调用API → 更新状态/显示错误 → 渲染页面
```

## 7. 性能优化建议

### 7.1 API调用优化
1. **减少请求次数**: 合理合并API请求，避免不必要的重复请求
2. **分页加载**: 对于大量数据，使用分页加载而不是一次性获取全部数据
3. **缓存策略**: 对不经常变动的数据可考虑添加本地缓存

### 7.2 错误处理优化
1. **更细致的错误类型判断**: 根据不同的错误类型提供不同的错误提示和处理方式
2. **错误日志记录**: 重要错误可考虑添加远程日志记录，便于排查问题

---
文档更新时间: " + new Date().toLocaleString('zh-CN') + "