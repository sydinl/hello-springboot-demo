# API Rate Limiting 功能说明

本项目已集成API调用频率限制功能，用于防止API滥用和DDoS攻击。

## 功能特性

- **内存限流**: 基于内存实现，适合单体应用
- **灵活配置**: 支持全局限流和按用户限流
- **AOP实现**: 通过注解方式，无需修改业务逻辑
- **自动过期**: 限流计数器自动过期，无需手动清理
- **线程安全**: 使用ConcurrentHashMap保证并发安全

## 配置说明

### 1. 应用配置

在 `application.yml` 中配置应用信息：

```yaml
spring:
  application:
    name: hello

server:
  port: 8080
```

### 2. 限流注解参数

```java
@RateLimit(
    maxRequests = 100,        // 最大请求次数
    timeWindow = 60,          // 时间窗口（秒）
    perUser = false,          // 是否按用户限流
    keyPrefix = "api",        // 限流键前缀
    message = "请求过于频繁"    // 限流失败消息
)
```

## 使用方式

### 1. 类级别限流

```java
@RestController
@RequestMapping("/api/user")
@RateLimit(maxRequests = 200, timeWindow = 60)
public class UserController {
    // 所有方法都受此限流规则约束
}
```

### 2. 方法级别限流

```java
@PostMapping("/login")
@RateLimit(maxRequests = 10, timeWindow = 60, perUser = true)
public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
    // 登录接口：每用户每60秒最多10次请求
}
```

### 3. 覆盖类级别限流

```java
@RestController
@RequestMapping("/api/user")
@RateLimit(maxRequests = 200, timeWindow = 60)  // 类级别：每60秒200次
public class UserController {
    
    @PostMapping("/login")
    @RateLimit(maxRequests = 10, timeWindow = 60, perUser = true)  // 方法级别覆盖
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        // 此方法使用更严格的限流规则
    }
}
```

## 限流策略

### 当前配置的限流规则

| API类别 | 限流规则 | 说明 |
|---------|----------|------|
| 用户API | 200次/60秒 | 全局限流 |
| 登录API | 10次/60秒 | 按用户限流 |
| 密码修改 | 5次/5分钟 | 按用户限流 |
| 项目API | 500次/60秒 | 全局限流 |
| 订单API | 100次/60秒 | 全局限流 |
| 订单创建 | 20次/60秒 | 按用户限流 |
| 评价API | 200次/60秒 | 全局限流 |
| 评价提交 | 10次/5分钟 | 按用户限流 |

## 测试方法

### 1. 启动应用

```bash
# 启动Spring Boot应用（无需外部依赖）
mvn spring-boot:run
```

### 2. 运行测试脚本

```bash
# 运行限流测试脚本
./test_rate_limit.sh
```

### 3. 手动测试

```bash
# 测试全局限流
curl http://localhost:8080/api/test/global

# 测试用户限流（需要设置X-User-ID头）
curl -H "X-User-ID: test-user-123" http://localhost:8080/api/test/user

# 测试严格限流
curl http://localhost:8080/api/test/strict
```

## 限流响应

当请求超过限流阈值时，API将返回：

```json
{
  "success": false,
  "code": 429,
  "message": "API调用频率过高，请稍后再试",
  "data": null
}
```

## 监控和维护

### 1. 内存键格式

限流计数器在内存中的键格式：
- 全局限流: `ClassName.methodName:ip:192.168.1.1`
- 用户限流: `ClassName.methodName:user:user123`

### 2. 查看限流状态

限流数据存储在内存中，可以通过JMX或添加监控接口查看：
- 限流计数器会自动过期清理
- 应用重启后限流数据会重置
- 每30秒自动清理过期数据

### 3. 内存使用

- 使用ConcurrentHashMap存储限流数据
- 自动清理过期数据，避免内存泄漏
- 适合单体应用，不适合分布式部署

## 注意事项

1. **内存限制**: 限流数据存储在内存中，重启应用后数据会丢失
2. **用户识别**: 按用户限流需要从请求头`X-User-ID`获取用户ID
3. **IP获取**: 系统会自动获取客户端真实IP（支持代理环境）
4. **性能影响**: 使用内存操作，性能较好，但仅适合单体应用
5. **时区问题**: 限流基于服务器时间，确保服务器时间准确
6. **分布式限制**: 不适合多实例部署，每个实例独立限流

## 扩展功能

### 1. 自定义限流键

```java
@RateLimit(
    keyPrefix = "custom:api",
    maxRequests = 50,
    timeWindow = 60
)
```

### 2. 不同限流算法

当前使用固定窗口算法，可以扩展为：
- 滑动窗口算法
- 令牌桶算法
- 漏桶算法

### 3. 动态配置

可以通过配置中心动态调整限流参数，无需重启应用。
