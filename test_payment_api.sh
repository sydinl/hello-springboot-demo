#!/bin/bash

# 支付API测试脚本
# 用于测试微信支付相关接口

BASE_URL="http://localhost:8080"

echo "=== 支付API测试脚本 ==="
echo "测试目标: $BASE_URL"
echo ""

# 测试1: 创建订单
echo "1. 测试创建订单"
echo "发送创建订单请求..."
create_order_response=$(curl -s -X POST "$BASE_URL/api/orders/create" \
  -H "Content-Type: application/json" \
  -d '{
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
    "source": "cart"
  }')

echo "创建订单响应:"
echo "$create_order_response" | python3 -m json.tool 2>/dev/null || echo "$create_order_response"
echo ""

# 提取订单ID
order_id=$(echo "$create_order_response" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data['data']['orderId'])" 2>/dev/null)

if [ -z "$order_id" ]; then
    echo "无法获取订单ID，测试终止"
    exit 1
fi

echo "订单ID: $order_id"
echo ""

# 测试2: 获取订单详情
echo "2. 测试获取订单详情"
echo "获取订单ID: $order_id 的详情..."
order_detail_response=$(curl -s "$BASE_URL/api/orders/detail?orderId=$order_id")

echo "订单详情响应:"
echo "$order_detail_response" | python3 -m json.tool 2>/dev/null || echo "$order_detail_response"
echo ""

# 测试3: 获取微信支付参数
echo "3. 测试获取微信支付参数"
echo "获取订单ID: $order_id 的微信支付参数..."
payment_params_response=$(curl -s -X POST "$BASE_URL/api/payment/wechat/params" \
  -H "Content-Type: application/json" \
  -d "{\"orderId\": \"$order_id\"}")

echo "微信支付参数响应:"
echo "$payment_params_response" | python3 -m json.tool 2>/dev/null || echo "$payment_params_response"
echo ""

# 测试4: 查询支付状态
echo "4. 测试查询支付状态"
echo "查询订单ID: $order_id 的支付状态..."
payment_status_response=$(curl -s "$BASE_URL/api/payment/status?orderId=$order_id")

echo "支付状态响应:"
echo "$payment_status_response" | python3 -m json.tool 2>/dev/null || echo "$payment_status_response"
echo ""

# 测试5: 支付回调测试
echo "5. 测试支付回调处理"
echo "发送模拟支付回调..."
callback_data='{
  "return_code": "SUCCESS",
  "result_code": "SUCCESS",
  "out_trade_no": "ORDER'$(date +%s)'123",
  "transaction_id": "wx'$(date +%s)'456",
  "total_fee": "12800",
  "time_end": "'$(date +%Y%m%d%H%M%S)'"
}'

callback_response=$(curl -s -X POST "$BASE_URL/api/payment/callback" \
  -H "Content-Type: application/json" \
  -d "$callback_data")

echo "支付回调响应:"
echo "$callback_response" | python3 -m json.tool 2>/dev/null || echo "$callback_response"
echo ""

echo "=== 测试完成 ==="
echo "注意: 这是模拟测试，实际微信支付需要真实的商户配置"



