# 项目管理相关接口文档

## 1. 项目接口概述

本文档详细描述了项目管理系统中的项目相关API接口，包括获取项目列表、分类列表、热门项目、推荐项目以及项目详情等功能。

## 2. 接口列表

### 2.1 获取项目列表

**接口地址**：`/api/projects/list`
**请求方式**：GET
**接口描述**：获取项目列表，支持分类筛选、搜索和分页功能

**请求参数**：

| 参数名 | 类型 | 是否必填 | 默认值 | 描述 |
|--------|------|----------|--------|------|
| category | String | 否 | 无 | 项目分类ID，可选值 |
| page | int | 否 | 1 | 页码，从1开始 |
| pageSize | int | 否 | 10 | 每页数量 |
| search | String | 否 | 无 | 搜索关键词，根据项目名称搜索 |

**返回示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [
      {
        "id": "uuid",
        "name": "项目名称",
        "description": "项目描述",
        "price": 128.00,
        "category": "美容护肤",
        "image": "项目主图URL",
        "salesCount": 100,
        "rating": 4.8
      },
      // 更多项目...
    ]
  }
}
```

### 2.2 获取项目分类列表

**接口地址**：`/api/projects/categories`
**请求方式**：GET
**接口描述**：获取所有项目分类列表

**请求参数**：无

**返回示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "uuid",
      "name": "美容护肤",
      "description": "美容护肤类项目"
    },
    {
      "id": "uuid",
      "name": "健康养生",
      "description": "健康养生类项目"
    },
    // 更多分类...
  ]
}
```

### 2.3 获取热门项目列表

**接口地址**：`/api/projects/hot`
**请求方式**：GET
**接口描述**：获取热门项目列表，按热度排序

**请求参数**：

| 参数名 | 类型 | 是否必填 | 默认值 | 描述 |
|--------|------|----------|--------|------|
| page | int | 否 | 1 | 页码，从1开始 |
| pageSize | int | 否 | 10 | 每页数量 |

**返回示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 20,
    "list": [
      {
        "id": "uuid",
        "name": "热门项目1",
        "description": "项目描述",
        "price": 198.00,
        "category": "美容护肤",
        "image": "项目主图URL",
        "salesCount": 500,
        "rating": 4.9
      },
      // 更多热门项目...
    ]
  }
}
```

### 2.4 获取推荐项目列表

**接口地址**：`/api/projects/recommend`
**请求方式**：GET
**接口描述**：获取推荐项目列表，根据算法推荐

**请求参数**：

| 参数名 | 类型 | 是否必填 | 默认值 | 描述 |
|--------|------|----------|--------|------|
| page | int | 否 | 1 | 页码，从1开始 |
| pageSize | int | 否 | 10 | 每页数量 |

**返回示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 30,
    "list": [
      {
        "id": "uuid",
        "name": "推荐项目1",
        "description": "项目描述",
        "price": 158.00,
        "category": "健康养生",
        "image": "项目主图URL",
        "salesCount": 200,
        "rating": 4.7
      },
      // 更多推荐项目...
    ]
  }
}
```

### 2.5 获取项目详情

**接口地址**：`/api/projects/detail`
**请求方式**：GET
**接口描述**：根据项目ID获取项目详细信息

**请求参数**：

| 参数名 | 类型 | 是否必填 | 默认值 | 描述 |
|--------|------|----------|--------|------|
| projectId | UUID | 是 | 无 | 项目ID |

**返回示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "uuid",
    "name": "项目名称",
    "description": "详细项目描述",
    "price": 128.00,
    "category": "美容护肤",
    "image": "项目主图URL",
    "salesCount": 100,
    "rating": 4.8,
    "createTime": "2023-01-01T00:00:00",
    "updateTime": "2023-01-01T00:00:00",
    "projectImages": [
      {
        "id": "uuid",
        "projectId": "uuid",
        "url": "图片URL"
      }
    ],
    "reviews": [
      {
        "id": "uuid",
        "projectId": "uuid",
        "userId": "uuid",
        "content": "评价内容",
        "rating": 5,
        "createTime": "2023-01-01T00:00:00"
      }
    ]
  }
}
```

## 3. 数据结构

### 3.1 项目（Project）

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | UUID | 项目唯一标识 |
| name | String | 项目名称 |
| description | String | 项目描述 |
| price | Double | 项目价格 |
| category | String | 项目分类 |
| image | String | 项目主图URL |
| salesCount | Integer | 销售数量 |
| rating | Double | 项目评分 |
| isHot | Boolean | 是否热门 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |
| projectImages | List<ProjectImage> | 项目图片列表 |
| reviews | List<Review> | 项目评价列表 |

### 3.2 项目分类（ProjectCategory）

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | UUID | 分类唯一标识 |
| name | String | 分类名称 |
| description | String | 分类描述 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

### 3.3 统一响应格式（ApiResponse）

| 字段名 | 类型 | 描述 |
|--------|------|------|
| code | Integer | 响应状态码（200成功，500失败） |
| message | String | 响应消息 |
| data | T | 响应数据，泛型类型 |

## 4. 错误码说明

| 错误码 | 描述 |
|--------|------|
| 200 | 请求成功 |
| 500 | 服务器内部错误 |

## 5. 接口调用说明

1. 所有接口均使用RESTful风格设计
2. 所有请求参数和响应数据均使用JSON格式
3. 分页参数统一使用page（页码）和pageSize（每页数量）
4. UUID类型参数请使用标准UUID格式字符串