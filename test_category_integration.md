# 分类ID关联测试说明

## 修改内容总结

### 1. Project实体修改
- 添加了`categoryId`字段，直接存储分类ID
- 移除了`@ManyToOne`关联，避免Hibernate冲突

### 2. Repository修改
- 更新查询方法使用`findByCategoryId`而不是关联查询
- 简化了查询逻辑

### 3. Service修改
- 更新Service实现使用新的Repository方法
- 在`updateProject`方法中添加了`categoryId`字段的更新

### 4. 前端修改
- 分类选择器现在使用分类ID作为值而不是分类名称
- 项目保存时同时发送`category`和`categoryId`字段
- 项目编辑时使用`categoryId`来设置选择器值
- 项目列表显示时通过`getCategoryName`函数根据ID获取分类名称
- 分类筛选使用`categoryId`参数而不是`category`参数

## 测试步骤

1. 启动应用
2. 访问管理页面
3. 创建新项目时选择分类，验证categoryId是否正确保存
4. 编辑现有项目，验证分类选择是否正确
5. 查看项目列表，验证分类名称是否正确显示
6. 使用分类筛选功能，验证筛选是否正常工作

## 预期结果

- 项目创建/编辑时能正确关联分类ID
- 项目列表能正确显示分类名称
- 分类筛选功能正常工作
- 数据库中的项目记录包含正确的categoryId字段




