#!/bin/bash

# Rate Limiting测试脚本
# 用于测试API限流功能

BASE_URL="http://localhost:8080"
USER_ID="test-user-123"

echo "=== API Rate Limiting 测试脚本 (内存版) ==="
echo "测试目标: $BASE_URL"
echo "注意: 使用内存限流，无需Redis"
echo ""

# 测试1: 全局限流测试
echo "1. 测试全局限流 (每60秒最多10次请求)"
echo "发送15次请求到 /api/test/global..."
for i in {1..15}; do
    response=$(curl -s -w "HTTP_CODE:%{http_code}" "$BASE_URL/api/test/global")
    http_code=$(echo "$response" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)
    body=$(echo "$response" | sed 's/HTTP_CODE:[0-9]*$//')
    
    if [ "$http_code" = "200" ]; then
        echo "请求 $i: 成功"
    else
        echo "请求 $i: 被限流 (HTTP $http_code)"
    fi
    sleep 0.1
done
echo ""

# 测试2: 用户限流测试
echo "2. 测试用户限流 (每用户每30秒最多5次请求)"
echo "发送8次请求到 /api/test/user..."
for i in {1..8}; do
    response=$(curl -s -w "HTTP_CODE:%{http_code}" -H "X-User-ID: $USER_ID" "$BASE_URL/api/test/user")
    http_code=$(echo "$response" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)
    body=$(echo "$response" | sed 's/HTTP_CODE:[0-9]*$//')
    
    if [ "$http_code" = "200" ]; then
        echo "请求 $i: 成功"
    else
        echo "请求 $i: 被限流 (HTTP $http_code)"
    fi
    sleep 0.1
done
echo ""

# 测试3: 严格限流测试
echo "3. 测试严格限流 (每10秒最多2次请求)"
echo "发送5次请求到 /api/test/strict..."
for i in {1..5}; do
    response=$(curl -s -w "HTTP_CODE:%{http_code}" "$BASE_URL/api/test/strict")
    http_code=$(echo "$response" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)
    body=$(echo "$response" | sed 's/HTTP_CODE:[0-9]*$//')
    
    if [ "$http_code" = "200" ]; then
        echo "请求 $i: 成功"
    else
        echo "请求 $i: 被限流 (HTTP $http_code)"
    fi
    sleep 0.5
done
echo ""

# 测试4: 无限制接口测试
echo "4. 测试无限制接口"
echo "发送5次请求到 /api/test/unlimited..."
for i in {1..5}; do
    response=$(curl -s -w "HTTP_CODE:%{http_code}" "$BASE_URL/api/test/unlimited")
    http_code=$(echo "$response" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)
    body=$(echo "$response" | sed 's/HTTP_CODE:[0-9]*$//')
    
    if [ "$http_code" = "200" ]; then
        echo "请求 $i: 成功"
    else
        echo "请求 $i: 失败 (HTTP $http_code)"
    fi
    sleep 0.1
done
echo ""

echo "=== 测试完成 ==="
echo "注意: 使用内存限流，重启应用后限流计数器会重置"
