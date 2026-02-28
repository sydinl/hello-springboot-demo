-- SPA休闲会所项目数据库表结构定义
-- MySQL数据库

-- 用户表
CREATE TABLE `users` (
  `user_id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '用户ID',
  `nickname` VARCHAR(100) NULL COMMENT '用户昵称',
  `real_name` VARCHAR(100) NULL COMMENT '真实姓名',
  `avatar` VARCHAR(500) NULL COMMENT '用户头像',
  `gender` VARCHAR(10) NULL COMMENT '性别',
  `birthdate` DATE NULL COMMENT '出生日期',
  `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号码',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `points` INT DEFAULT 0 COMMENT '积分',
  `balance` DOUBLE DEFAULT 0.0 COMMENT '余额',
  `member_level` VARCHAR(50) DEFAULT '普通会员' COMMENT '会员等级',
  `role` VARCHAR(50) DEFAULT 'USER' COMMENT '用户角色',
  `enabled` BOOLEAN DEFAULT TRUE COMMENT '账户是否启用',
  `account_non_expired` BOOLEAN DEFAULT TRUE COMMENT '账户是否未过期',
  `account_non_locked` BOOLEAN DEFAULT TRUE COMMENT '账户是否未锁定',
  `credentials_non_expired` BOOLEAN DEFAULT TRUE COMMENT '凭证是否未过期',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 项目分类表
CREATE TABLE `project_categories` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '分类ID',
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(500) NULL COMMENT '分类图标',
  `project_count` INT DEFAULT 0 COMMENT '项目数量'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目分类表';

-- 项目表
CREATE TABLE `projects` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '项目ID',
  `name` VARCHAR(200) NOT NULL COMMENT '项目名称',
  `description` TEXT NULL COMMENT '项目描述',
  `price` DOUBLE NOT NULL COMMENT '项目价格',
  `image` VARCHAR(500) NULL COMMENT '项目主图',
  `duration` VARCHAR(50) NULL COMMENT '项目时长',
  `category` VARCHAR(100) NULL COMMENT '项目分类名称',
  `category_id` VARCHAR(36) NULL COMMENT '项目分类ID',
  `details` TEXT NULL COMMENT '项目详情',
  `sales_count` INT DEFAULT 0 COMMENT '销量',
  `rating` DOUBLE DEFAULT 0.0 COMMENT '评分',
  `is_hot` BOOLEAN DEFAULT FALSE COMMENT '是否热门',
  `is_recommend` BOOLEAN DEFAULT FALSE COMMENT '是否推荐',
  `status` VARCHAR(50) DEFAULT 'active' COMMENT '项目状态',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  FOREIGN KEY (`category_id`) REFERENCES `project_categories`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项目表';

-- 更新现有projects表，添加category_id字段
ALTER TABLE `projects`
ADD COLUMN `category_id` VARCHAR(36) NULL COMMENT '项目分类ID' AFTER `category`,
ADD FOREIGN KEY (`category_id`) REFERENCES `project_categories`(`id`) ON DELETE SET NULL;


-- 项目图片表
CREATE TABLE `project_images` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '图片ID',
  `project_id` VARCHAR(36) NOT NULL COMMENT '所属项目ID',
  `url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `description` VARCHAR(255) NULL COMMENT '图片描述',
  FOREIGN KEY (`project_id`) REFERENCES `projects`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目图片表';

-- 门店表
CREATE TABLE `stores` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '门店ID',
  `name` VARCHAR(200) NOT NULL COMMENT '门店名称',
  `address` VARCHAR(500) NOT NULL COMMENT '门店地址',
  `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `business_hours` VARCHAR(255) NULL COMMENT '营业时间',
  `latitude` DOUBLE NULL COMMENT '纬度',
  `longitude` DOUBLE NULL COMMENT '经度'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店信息表';

-- 技师表
CREATE TABLE `technicians` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '技师ID',
  `name` VARCHAR(100) NOT NULL COMMENT '技师姓名',
  `avatar` VARCHAR(500) NULL COMMENT '技师头像',
  `experience` VARCHAR(255) NULL COMMENT '工作经验',
  `rating` DOUBLE DEFAULT 0.0 COMMENT '评分',
  `store_id` VARCHAR(36) NULL COMMENT '所属门店ID',
  FOREIGN KEY (`store_id`) REFERENCES `stores`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技师信息表';

-- 技师擅长项目关联表
CREATE TABLE `technician_services` (
  `technician_id` VARCHAR(36) NOT NULL COMMENT '技师ID',
  `service_name` VARCHAR(200) NOT NULL COMMENT '擅长项目名称',
  PRIMARY KEY (`technician_id`, `service_name`),
  FOREIGN KEY (`technician_id`) REFERENCES `technicians`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技师擅长项目关联表';

-- 订单表
CREATE TABLE `orders` (
  `order_id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '订单ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `status` VARCHAR(50) NOT NULL DEFAULT 'pending' COMMENT '订单状态：pending(待支付)/shipping(进行中)/completed(已完成)/aftersale(售后)',
  `total_price` DOUBLE NOT NULL COMMENT '订单总价',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `pay_time` DATETIME NULL COMMENT '支付时间',
  `service_time` DATETIME NULL COMMENT '服务时间',
  `address_id` VARCHAR(36) NULL COMMENT '地址ID',
  `technician_id` VARCHAR(36) NULL COMMENT '技师ID',
  `coupon_id` VARCHAR(36) NULL COMMENT '优惠券ID',
  `payment_method` VARCHAR(20) NULL COMMENT '支付方式：balance(余额)/wechat(微信)',
  `order_no` VARCHAR(50) NULL COMMENT '订单号',
  `source` VARCHAR(20) NULL COMMENT '订单来源：cart/detail/order',
  `wechat_transaction_id` VARCHAR(100) NULL COMMENT '微信支付交易号',
  `wechat_prepay_id` VARCHAR(100) NULL COMMENT '微信预支付ID',
  `expire_time` DATETIME NULL COMMENT '订单过期时间',
  `remarks` TEXT NULL COMMENT '备注',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`),
  FOREIGN KEY (`technician_id`) REFERENCES `technicians`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单信息表';

-- 订单项表
CREATE TABLE `order_items` (
  `item_id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '订单项ID',
  `order_id` VARCHAR(36) NOT NULL COMMENT '所属订单ID',
  `project_id` VARCHAR(36) NOT NULL COMMENT '项目ID',
  `name` VARCHAR(200) NOT NULL COMMENT '项目名称',
  `price` DOUBLE NOT NULL COMMENT '项目价格',
  `count` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `image` VARCHAR(500) NULL COMMENT '项目图片',
  `duration` VARCHAR(50) NULL COMMENT '项目时长',
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`order_id`) ON DELETE CASCADE,
  FOREIGN KEY (`project_id`) REFERENCES `projects`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- 评价表
CREATE TABLE `reviews` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '评价ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `project_id` VARCHAR(36) NOT NULL COMMENT '项目ID',
  `rating` INT NOT NULL COMMENT '评分(1-5)',
  `content` TEXT NULL COMMENT '评价内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `technician_rating` INT NULL COMMENT '技师评分',
  `environment_rating` INT NULL COMMENT '环境评分',
  `service_rating` INT NULL COMMENT '服务评分',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`),
  FOREIGN KEY (`project_id`) REFERENCES `projects`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价信息表';

-- 评价图片关联表
CREATE TABLE `review_images` (
  `review_id` VARCHAR(36) NOT NULL COMMENT '评价ID',
  `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  PRIMARY KEY (`review_id`, `image_url`),
  FOREIGN KEY (`review_id`) REFERENCES `reviews`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价图片关联表';

-- 评论回复表
CREATE TABLE `replies` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '回复ID',
  `content` TEXT NOT NULL COMMENT '回复内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `review_id` VARCHAR(36) NOT NULL UNIQUE COMMENT '评价ID',
  FOREIGN KEY (`review_id`) REFERENCES `reviews`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论回复表';

-- 积分记录表
CREATE TABLE `point_records` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '记录ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `type` VARCHAR(50) NOT NULL COMMENT '类型：income(收入)/expense(支出)',
  `amount` INT NOT NULL COMMENT '积分数量',
  `source` VARCHAR(255) NULL COMMENT '来源/去向',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- 余额记录表
CREATE TABLE `balance_records` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '记录ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `type` VARCHAR(50) NOT NULL COMMENT '类型：recharge(充值)/consume(消费)/withdraw(提现)',
  `amount` DOUBLE NOT NULL COMMENT '金额',
  `source` VARCHAR(255) NULL COMMENT '来源/去向',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='余额记录表';

-- 提现记录表
CREATE TABLE `withdrawals` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '提现记录ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `amount` DOUBLE NOT NULL COMMENT '提现金额',
  `status` VARCHAR(50) DEFAULT 'pending' COMMENT '状态',
  `create_time` DATETIME NOT NULL COMMENT '申请时间',
  `complete_time` DATETIME NULL COMMENT '完成时间',
  `account_info` TEXT NULL COMMENT '提现账户信息',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现记录表';

-- 优惠券表
CREATE TABLE `coupons` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '优惠券ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `name` VARCHAR(200) NOT NULL COMMENT '优惠券名称',
  `value` DOUBLE NOT NULL COMMENT '优惠金额',
  `min_order_amount` DOUBLE NOT NULL COMMENT '最低消费金额',
  `expiry_date` DATETIME NOT NULL COMMENT '过期日期',
  `status` VARCHAR(50) DEFAULT 'unused' COMMENT '状态：unused(未使用)/used(已使用)/expired(已过期)',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 会员卡表
CREATE TABLE `cards` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '卡券ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `name` VARCHAR(200) NOT NULL COMMENT '卡券名称',
  `type` VARCHAR(100) NOT NULL COMMENT '卡券类型',
  `balance` DOUBLE NOT NULL COMMENT '余额/次数',
  `expiry_date` DATETIME NOT NULL COMMENT '过期日期',
  `status` VARCHAR(50) DEFAULT 'unused' COMMENT '状态：unused(未使用)/used(已使用)/expired(已过期)',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员卡表';

-- 地址表
CREATE TABLE `addresses` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '地址ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `consignee` VARCHAR(100) NOT NULL COMMENT '收货人姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `province` VARCHAR(100) NOT NULL COMMENT '省份',
  `city` VARCHAR(100) NOT NULL COMMENT '城市',
  `district` VARCHAR(100) NOT NULL COMMENT '区县',
  `detail_address` VARCHAR(500) NOT NULL COMMENT '详细地址',
  `is_default` BOOLEAN DEFAULT FALSE COMMENT '是否默认地址',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- 收藏表
CREATE TABLE `favorites` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '收藏ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `project_id` VARCHAR(36) NOT NULL COMMENT '项目ID',
  `collect_time` DATETIME NOT NULL COMMENT '收藏时间',
  UNIQUE KEY `uk_user_project` (`user_id`, `project_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`),
  FOREIGN KEY (`project_id`) REFERENCES `projects`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 分销数据表
CREATE TABLE `distribution_data` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT 'ID',
  `user_id` VARCHAR(36) NOT NULL UNIQUE COMMENT '用户ID',
  `total_commission` DOUBLE DEFAULT 0.0 COMMENT '总佣金',
  `available_commission` DOUBLE DEFAULT 0.0 COMMENT '可提现佣金',
  `team_count` INT DEFAULT 0 COMMENT '团队人数',
  `today_order_count` INT DEFAULT 0 COMMENT '今日订单数',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分销数据表';

-- 分销订单表
CREATE TABLE `distribution_orders` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '分销订单ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `order_id` VARCHAR(36) NOT NULL COMMENT '订单ID',
  `customer_name` VARCHAR(100) NULL COMMENT '客户名称',
  `commission` DOUBLE NOT NULL COMMENT '佣金金额',
  `status` VARCHAR(50) DEFAULT 'pending' COMMENT '状态',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`),
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分销订单表';