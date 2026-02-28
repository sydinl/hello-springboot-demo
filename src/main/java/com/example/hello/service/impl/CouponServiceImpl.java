package com.example.hello.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.hello.entity.Coupon;
import com.example.hello.entity.UserCoupon;
import com.example.hello.repository.CouponRepository;
import com.example.hello.repository.UserCouponRepository;
import com.example.hello.service.CouponService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Autowired
    private UserCouponRepository userCouponRepository;
    
    // ========== 基础CRUD操作 ==========
    
    @Override
    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        try {
            // 验证优惠券代码唯一性
            if (couponRepository.findByCouponCodeIgnoreCase(coupon.getCouponCode()).isPresent()) {
                throw new RuntimeException("优惠券代码已存在");
            }
            
            // 设置默认值
            if (coupon.getUsedQuantity() == null) {
                coupon.setUsedQuantity(0);
            }
            if (coupon.getRemainingQuantity() == null) {
                coupon.setRemainingQuantity(coupon.getTotalQuantity());
            }
            if (coupon.getIsActive() == null) {
                coupon.setIsActive(true);
            }
            if (coupon.getIsPublic() == null) {
                coupon.setIsPublic(true);
            }
            if (coupon.getUsageLimitPerUser() == null) {
                coupon.setUsageLimitPerUser(1);
            }
            
            Coupon savedCoupon = couponRepository.save(coupon);
            log.info("创建优惠券成功，优惠券ID：{}", savedCoupon.getId());
            return savedCoupon;
            
        } catch (Exception e) {
            log.error("创建优惠券失败", e);
            throw new RuntimeException("创建优惠券失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public Coupon updateCoupon(String couponId, Coupon coupon) {
        try {
            Coupon existingCoupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new RuntimeException("优惠券不存在，优惠券ID：" + couponId));
            
            // 检查优惠券代码唯一性（排除当前优惠券）
            if (!existingCoupon.getCouponCode().equals(coupon.getCouponCode())) {
                if (couponRepository.findByCouponCodeIgnoreCase(coupon.getCouponCode()).isPresent()) {
                    throw new RuntimeException("优惠券代码已存在");
                }
            }
            
            // 更新优惠券信息
            existingCoupon.setCouponCode(coupon.getCouponCode());
            existingCoupon.setCouponName(coupon.getCouponName());
            existingCoupon.setDescription(coupon.getDescription());
            existingCoupon.setCouponType(coupon.getCouponType());
            existingCoupon.setDiscountValue(coupon.getDiscountValue());
            existingCoupon.setMinOrderAmount(coupon.getMinOrderAmount());
            existingCoupon.setMaxDiscountAmount(coupon.getMaxDiscountAmount());
            existingCoupon.setTotalQuantity(coupon.getTotalQuantity());
            existingCoupon.setValidFrom(coupon.getValidFrom());
            existingCoupon.setValidUntil(coupon.getValidUntil());
            existingCoupon.setIsActive(coupon.getIsActive());
            existingCoupon.setIsPublic(coupon.getIsPublic());
            existingCoupon.setUsageLimitPerUser(coupon.getUsageLimitPerUser());
            existingCoupon.setApplicableCategories(coupon.getApplicableCategories());
            existingCoupon.setApplicableProjects(coupon.getApplicableProjects());
            
            // 重新计算剩余数量
            existingCoupon.setRemainingQuantity(existingCoupon.getTotalQuantity() - existingCoupon.getUsedQuantity());
            
            Coupon updatedCoupon = couponRepository.save(existingCoupon);
            log.info("更新优惠券成功，优惠券ID：{}", couponId);
            return updatedCoupon;
            
        } catch (Exception e) {
            log.error("更新优惠券失败，优惠券ID：{}", couponId, e);
            throw new RuntimeException("更新优惠券失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void deleteCoupon(String couponId) {
        try {
            if (!couponRepository.existsById(couponId)) {
                throw new RuntimeException("优惠券不存在，优惠券ID：" + couponId);
            }
            
            couponRepository.deleteById(couponId);
            log.info("删除优惠券成功，优惠券ID：{}", couponId);
            
        } catch (Exception e) {
            log.error("删除优惠券失败，优惠券ID：{}", couponId, e);
            throw new RuntimeException("删除优惠券失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void deleteCoupons(String[] couponIds) {
        try {
            for (String couponId : couponIds) {
                if (couponRepository.existsById(couponId)) {
                    couponRepository.deleteById(couponId);
                }
            }
            log.info("批量删除优惠券成功，删除数量：{}", couponIds.length);
            
        } catch (Exception e) {
            log.error("批量删除优惠券失败", e);
            throw new RuntimeException("批量删除优惠券失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public Coupon getCouponById(String couponId) {
        try {
            return couponRepository.findById(couponId)
                    .orElseThrow(() -> new RuntimeException("优惠券不存在，优惠券ID：" + couponId));
        } catch (Exception e) {
            log.error("获取优惠券详情失败，优惠券ID：{}", couponId, e);
            throw new RuntimeException("获取优惠券详情失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public Coupon getCouponByCode(String couponCode) {
        try {
            return couponRepository.findByCouponCode(couponCode)
                    .orElseThrow(() -> new RuntimeException("优惠券不存在，优惠券代码：" + couponCode));
        } catch (Exception e) {
            log.error("获取优惠券详情失败，优惠券代码：{}", couponCode, e);
            throw new RuntimeException("获取优惠券详情失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public Page<Coupon> getAllCoupons(Pageable pageable) {
        try {
            return couponRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("获取优惠券列表失败", e);
            throw new RuntimeException("获取优惠券列表失败：" + e.getMessage(), e);
        }
    }
    
    // ========== 查询操作 ==========
    
    @Override
    public Page<Coupon> getCouponsByStatus(Boolean isActive, Pageable pageable) {
        try {
            return couponRepository.findByIsActive(isActive, pageable);
        } catch (Exception e) {
            log.error("按状态查询优惠券失败，状态：{}", isActive, e);
            throw new RuntimeException("查询优惠券失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public Page<Coupon> getCouponsByType(Coupon.CouponType couponType, Pageable pageable) {
        try {
            return couponRepository.findByCouponType(couponType, pageable);
        } catch (Exception e) {
            log.error("按类型查询优惠券失败，类型：{}", couponType, e);
            throw new RuntimeException("查询优惠券失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public Page<Coupon> getCouponsByCreator(String createdBy, Pageable pageable) {
        try {
            return couponRepository.findByCreatedBy(createdBy, pageable);
        } catch (Exception e) {
            log.error("按创建者查询优惠券失败，创建者：{}", createdBy, e);
            throw new RuntimeException("查询优惠券失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public Page<Coupon> searchCoupons(String keyword, Pageable pageable) {
        try {
            // 先按名称搜索
            Page<Coupon> nameResults = couponRepository.findByCouponNameContaining(keyword, pageable);
            if (nameResults.hasContent()) {
                return nameResults;
            }
            // 再按代码搜索
            return couponRepository.findByCouponCodeContaining(keyword, pageable);
        } catch (Exception e) {
            log.error("搜索优惠券失败，关键词：{}", keyword, e);
            throw new RuntimeException("搜索优惠券失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public Page<Coupon> getExpiringCoupons(int days, Pageable pageable) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endDate = now.plusDays(days);
            return couponRepository.findExpiringCoupons(now, endDate, pageable);
        } catch (Exception e) {
            log.error("获取即将过期优惠券失败，天数：{}", days, e);
            throw new RuntimeException("获取即将过期优惠券失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public Page<Coupon> getExpiredCoupons(Pageable pageable) {
        try {
            return couponRepository.findExpiredCoupons(LocalDateTime.now(), pageable);
        } catch (Exception e) {
            log.error("获取已过期优惠券失败", e);
            throw new RuntimeException("获取已过期优惠券失败：" + e.getMessage(), e);
        }
    }
    
    // ========== 状态管理 ==========
    
    @Override
    @Transactional
    public Coupon toggleCouponStatus(String couponId, Boolean isActive) {
        try {
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new RuntimeException("优惠券不存在，优惠券ID：" + couponId));
            
            coupon.setIsActive(isActive);
            Coupon updatedCoupon = couponRepository.save(coupon);
            
            log.info("切换优惠券状态成功，优惠券ID：{}，新状态：{}", couponId, isActive);
            return updatedCoupon;
            
        } catch (Exception e) {
            log.error("切换优惠券状态失败，优惠券ID：{}", couponId, e);
            throw new RuntimeException("切换优惠券状态失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public Coupon setCouponPublicStatus(String couponId, Boolean isPublic) {
        try {
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new RuntimeException("优惠券不存在，优惠券ID：" + couponId));
            
            coupon.setIsPublic(isPublic);
            Coupon updatedCoupon = couponRepository.save(coupon);
            
            log.info("设置优惠券公开状态成功，优惠券ID：{}，公开状态：{}", couponId, isPublic);
            return updatedCoupon;
            
        } catch (Exception e) {
            log.error("设置优惠券公开状态失败，优惠券ID：{}", couponId, e);
            throw new RuntimeException("设置优惠券公开状态失败：" + e.getMessage(), e);
        }
    }
    
    // ========== 使用管理 ==========
    
    @Override
    @Transactional
    public Coupon useCoupon(String couponCode, String userId) {
        try {
            Coupon coupon = couponRepository.findValidCoupon(couponCode, LocalDateTime.now())
                    .orElseThrow(() -> new RuntimeException("优惠券无效或已过期"));
            
            if (coupon.getRemainingQuantity() <= 0) {
                throw new RuntimeException("优惠券已用完");
            }
            
            // 增加使用次数
            coupon.setUsedQuantity(coupon.getUsedQuantity() + 1);
            coupon.setRemainingQuantity(coupon.getTotalQuantity() - coupon.getUsedQuantity());
            
            Coupon updatedCoupon = couponRepository.save(coupon);
            log.info("使用优惠券成功，优惠券代码：{}，用户ID：{}", couponCode, userId);
            return updatedCoupon;
            
        } catch (Exception e) {
            log.error("使用优惠券失败，优惠券代码：{}，用户ID：{}", couponCode, userId, e);
            throw new RuntimeException("使用优惠券失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isCouponValid(String couponCode, String userId, BigDecimal orderAmount) {
        try {
            Coupon coupon = couponRepository.findValidCoupon(couponCode, LocalDateTime.now())
                    .orElse(null);
            
            if (coupon == null) {
                return false;
            }
            
            // 检查最低订单金额
            if (coupon.getMinOrderAmount() != null && orderAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("验证优惠券失败，优惠券代码：{}", couponCode, e);
            return false;
        }
    }
    
    @Override
    public BigDecimal calculateDiscount(String couponCode, BigDecimal orderAmount) {
        try {
            Coupon coupon = couponRepository.findValidCoupon(couponCode, LocalDateTime.now())
                    .orElseThrow(() -> new RuntimeException("优惠券无效或已过期"));
            
            if (!isCouponValid(couponCode, null, orderAmount)) {
                return BigDecimal.ZERO;
            }
            
            BigDecimal discount = BigDecimal.ZERO;
            
            switch (coupon.getCouponType()) {
                case PERCENTAGE:
                    discount = orderAmount.multiply(coupon.getDiscountValue()).divide(new BigDecimal("100"));
                    break;
                case FIXED_AMOUNT:
                    discount = coupon.getDiscountValue();
                    break;
                case FREE_SHIPPING:
                    // 免运费的情况，这里返回0，实际处理在订单中
                    discount = BigDecimal.ZERO;
                    break;
            }
            
            // 应用最大折扣限制
            if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discount = coupon.getMaxDiscountAmount();
            }
            
            // 确保折扣不超过订单金额
            if (discount.compareTo(orderAmount) > 0) {
                discount = orderAmount;
            }
            
            return discount;
            
        } catch (Exception e) {
            log.error("计算优惠金额失败，优惠券代码：{}", couponCode, e);
            return BigDecimal.ZERO;
        }
    }
    
    @Override
    public List<Coupon> getAvailableCouponsForUser(String userId) {
        try {
            return couponRepository.findAvailableCoupons(LocalDateTime.now());
        } catch (Exception e) {
            log.error("获取用户可用优惠券失败，用户ID：{}", userId, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Coupon> getAvailableCouponsByCategory(String categoryId) {
        try {
            return couponRepository.findAvailableCouponsByCategory(LocalDateTime.now(), categoryId);
        } catch (Exception e) {
            log.error("根据分类获取可用优惠券失败，分类ID：{}", categoryId, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Coupon> getAvailableCouponsByProject(String projectId) {
        try {
            return couponRepository.findAvailableCouponsByProject(LocalDateTime.now(), projectId);
        } catch (Exception e) {
            log.error("根据项目获取可用优惠券失败，项目ID：{}", projectId, e);
            return new ArrayList<>();
        }
    }
    
    // ========== 统计信息 ==========
    
    @Override
    public Map<String, Object> getCouponStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总优惠券数
            Long totalCoupons = couponRepository.countAllCoupons();
            statistics.put("totalCoupons", totalCoupons);
            
            // 有效优惠券数
            Long validCoupons = couponRepository.countValidCoupons(LocalDateTime.now());
            statistics.put("validCoupons", validCoupons);
            
            // 已使用优惠券数
            Long usedCoupons = couponRepository.countUsedCoupons();
            statistics.put("usedCoupons", usedCoupons);
            
            // 即将过期的优惠券数（7天内过期）
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sevenDaysLater = now.plusDays(7);
            Page<Coupon> expiringCoupons = couponRepository.findExpiringCoupons(now, sevenDaysLater, PageRequest.of(0, Integer.MAX_VALUE));
            statistics.put("expiringCoupons", expiringCoupons.getTotalElements());
            
            // 按类型分组统计
            Object[][] typeStats = couponRepository.countCouponsByType();
            Map<String, Long> typeMap = new HashMap<>();
            for (Object[] stat : typeStats) {
                typeMap.put(((Coupon.CouponType) stat[0]).name(), (Long) stat[1]);
            }
            statistics.put("typeStatistics", typeMap);
            
            // 按状态分组统计
            Object[][] statusStats = couponRepository.countCouponsByStatus();
            Map<String, Long> statusMap = new HashMap<>();
            for (Object[] stat : statusStats) {
                statusMap.put(stat[0].toString(), (Long) stat[1]);
            }
            statistics.put("statusStatistics", statusMap);
            
            return statistics;
            
        } catch (Exception e) {
            log.error("获取优惠券统计信息失败", e);
            throw new RuntimeException("获取优惠券统计信息失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> getCouponUsageStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总使用次数
            List<Coupon> allCoupons = couponRepository.findAll();
            int totalUsage = allCoupons.stream().mapToInt(Coupon::getUsedQuantity).sum();
            statistics.put("totalUsage", totalUsage);
            
            // 使用率最高的优惠券
            Coupon mostUsedCoupon = allCoupons.stream()
                    .max(Comparator.comparing(Coupon::getUsedQuantity))
                    .orElse(null);
            if (mostUsedCoupon != null) {
                Map<String, Object> mostUsed = new HashMap<>();
                mostUsed.put("couponCode", mostUsedCoupon.getCouponCode());
                mostUsed.put("couponName", mostUsedCoupon.getCouponName());
                mostUsed.put("usageCount", mostUsedCoupon.getUsedQuantity());
                statistics.put("mostUsedCoupon", mostUsed);
            }
            
            return statistics;
            
        } catch (Exception e) {
            log.error("获取优惠券使用统计失败", e);
            throw new RuntimeException("获取优惠券使用统计失败：" + e.getMessage(), e);
        }
    }
    
    // ========== 批量操作 ==========
    
    @Override
    @Transactional
    public int batchUpdateCouponStatus(List<String> couponIds, Boolean isActive) {
        try {
            int updatedCount = 0;
            for (String couponId : couponIds) {
                try {
                    Coupon coupon = couponRepository.findById(couponId).orElse(null);
                    if (coupon != null) {
                        coupon.setIsActive(isActive);
                        couponRepository.save(coupon);
                        updatedCount++;
                    }
                } catch (Exception e) {
                    log.warn("更新优惠券状态失败，优惠券ID：{}", couponId, e);
                }
            }
            log.info("批量更新优惠券状态成功，更新数量：{}", updatedCount);
            return updatedCount;
        } catch (Exception e) {
            log.error("批量更新优惠券状态失败", e);
            throw new RuntimeException("批量更新优惠券状态失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public int batchDeleteCoupons(List<String> couponIds) {
        try {
            int deletedCount = 0;
            for (String couponId : couponIds) {
                try {
                    if (couponRepository.existsById(couponId)) {
                        couponRepository.deleteById(couponId);
                        deletedCount++;
                    }
                } catch (Exception e) {
                    log.warn("删除优惠券失败，优惠券ID：{}", couponId, e);
                }
            }
            log.info("批量删除优惠券成功，删除数量：{}", deletedCount);
            return deletedCount;
        } catch (Exception e) {
            log.error("批量删除优惠券失败", e);
            throw new RuntimeException("批量删除优惠券失败：" + e.getMessage(), e);
        }
    }
    
    // ========== 验证操作 ==========
    
    @Override
    public boolean isCouponCodeAvailable(String couponCode, String excludeCouponId) {
        try {
            Optional<Coupon> existingCoupon = couponRepository.findByCouponCodeIgnoreCase(couponCode);
            if (existingCoupon.isPresent()) {
                return excludeCouponId != null && existingCoupon.get().getId().equals(excludeCouponId);
            }
            return true;
        } catch (Exception e) {
            log.error("检查优惠券代码可用性失败，优惠券代码：{}", couponCode, e);
            return false;
        }
    }
    
    @Override
    public boolean validateCouponData(Coupon coupon) {
        try {
            // 基本验证
            if (coupon.getCouponCode() == null || coupon.getCouponCode().trim().isEmpty()) {
                return false;
            }
            if (coupon.getCouponName() == null || coupon.getCouponName().trim().isEmpty()) {
                return false;
            }
            if (coupon.getCouponType() == null) {
                return false;
            }
            if (coupon.getDiscountValue() == null || coupon.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
            if (coupon.getTotalQuantity() == null || coupon.getTotalQuantity() <= 0) {
                return false;
            }
            if (coupon.getValidFrom() == null || coupon.getValidUntil() == null) {
                return false;
            }
            if (coupon.getValidFrom().isAfter(coupon.getValidUntil())) {
                return false;
            }
            
            // 类型特定验证
            if (coupon.getCouponType() == Coupon.CouponType.PERCENTAGE) {
                if (coupon.getDiscountValue().compareTo(new BigDecimal("100")) > 0) {
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("验证优惠券数据失败", e);
            return false;
        }
    }
    
    // ========== 用户优惠券管理 ==========
    
    @Override
    @Transactional
    public UserCoupon claimCoupon(String couponCode, String userId) {
        try {
            // 1. 查找优惠券
            Coupon coupon = couponRepository.findValidCoupon(couponCode, LocalDateTime.now())
                    .orElseThrow(() -> new RuntimeException("优惠券不存在或已失效"));
            
            // 2. 检查是否已经领取过
            Optional<UserCoupon> existingUserCoupon = userCouponRepository.findByUserIdAndCouponId(userId, coupon.getId());
            if (existingUserCoupon.isPresent()) {
                throw new RuntimeException("您已经领取过此优惠券");
            }
            
            // 3. 检查用户领取次数限制
            if (coupon.getUsageLimitPerUser() != null) {
                long userClaimedCount = userCouponRepository.countByUserIdAndCouponId(userId, coupon.getId());
                if (userClaimedCount >= coupon.getUsageLimitPerUser()) {
                    throw new RuntimeException("您已达到此优惠券的领取次数限制");
                }
            }
            
            // 4. 创建用户优惠券记录
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setCouponId(coupon.getId());
            userCoupon.setCouponCode(coupon.getCouponCode());
            userCoupon.setCouponName(coupon.getCouponName());
            userCoupon.setCouponType(coupon.getCouponType());
            userCoupon.setDiscountValue(coupon.getDiscountValue());
            userCoupon.setMinOrderAmount(coupon.getMinOrderAmount());
            userCoupon.setMaxDiscountAmount(coupon.getMaxDiscountAmount());
            userCoupon.setValidFrom(coupon.getValidFrom());
            userCoupon.setValidUntil(coupon.getValidUntil());
            userCoupon.setStatus(UserCoupon.UserCouponStatus.UNUSED);
            userCoupon.setClaimedAt(LocalDateTime.now());
            
            UserCoupon savedUserCoupon = userCouponRepository.save(userCoupon);
            
            log.info("用户领取优惠券成功，用户ID：{}，优惠券代码：{}", userId, couponCode);
            return savedUserCoupon;
            
        } catch (Exception e) {
            log.error("用户领取优惠券失败，用户ID：{}，优惠券代码：{}", userId, couponCode, e);
            throw new RuntimeException("领取优惠券失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public Page<UserCoupon> getUserCoupons(String userId, UserCoupon.UserCouponStatus status, Pageable pageable) {
        try {
            if (status != null) {
                return userCouponRepository.findByUserIdAndStatus(userId, status, pageable);
            } else {
                return userCouponRepository.findByUserId(userId, pageable);
            }
        } catch (Exception e) {
            log.error("获取用户优惠券列表失败，用户ID：{}，状态：{}", userId, status, e);
            throw new RuntimeException("获取用户优惠券列表失败", e);
        }
    }
    
    @Override
    public List<UserCoupon> getAvailableUserCoupons(String userId) {
        try {
            return userCouponRepository.findAvailableCouponsByUserId(userId, LocalDateTime.now());
        } catch (Exception e) {
            log.error("获取用户可用优惠券失败，用户ID：{}", userId, e);
            throw new RuntimeException("获取用户可用优惠券失败", e);
        }
    }
    
    @Override
    @Transactional
    public UserCoupon useUserCoupon(String userCouponId, String orderId) {
        try {
            UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                    .orElseThrow(() -> new RuntimeException("用户优惠券不存在"));
            
            if (userCoupon.getStatus() != UserCoupon.UserCouponStatus.UNUSED) {
                throw new RuntimeException("优惠券已使用或已过期");
            }
            
            if (userCoupon.getValidUntil().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("优惠券已过期");
            }
            
            // 更新优惠券状态
            userCoupon.setStatus(UserCoupon.UserCouponStatus.USED);
            userCoupon.setUsedAt(LocalDateTime.now());
            userCoupon.setOrderId(orderId);
            
            UserCoupon updatedUserCoupon = userCouponRepository.save(userCoupon);
            
            log.info("使用用户优惠券成功，用户优惠券ID：{}，订单ID：{}", userCouponId, orderId);
            return updatedUserCoupon;
            
        } catch (Exception e) {
            log.error("使用用户优惠券失败，用户优惠券ID：{}，订单ID：{}", userCouponId, orderId, e);
            throw new RuntimeException("使用用户优惠券失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> getUserCouponStatistics(String userId) {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总优惠券数
            long totalCoupons = userCouponRepository.countByUserId(userId);
            statistics.put("totalCoupons", totalCoupons);
            
            // 各状态优惠券数
            long unusedCoupons = userCouponRepository.countByUserIdAndStatus(userId, UserCoupon.UserCouponStatus.UNUSED);
            long usedCoupons = userCouponRepository.countByUserIdAndStatus(userId, UserCoupon.UserCouponStatus.USED);
            long expiredCoupons = userCouponRepository.countByUserIdAndStatus(userId, UserCoupon.UserCouponStatus.EXPIRED);
            
            statistics.put("unusedCoupons", unusedCoupons);
            statistics.put("usedCoupons", usedCoupons);
            statistics.put("expiredCoupons", expiredCoupons);
            
            // 即将过期的优惠券（7天内）
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sevenDaysLater = now.plusDays(7);
            List<UserCoupon> expiringCoupons = userCouponRepository.findExpiringCouponsByUserId(userId, now, sevenDaysLater);
            statistics.put("expiringCoupons", expiringCoupons.size());
            
            log.info("获取用户优惠券统计成功，用户ID：{}", userId);
            return statistics;
            
        } catch (Exception e) {
            log.error("获取用户优惠券统计失败，用户ID：{}", userId, e);
            throw new RuntimeException("获取用户优惠券统计失败", e);
        }
    }
    
    // ========== 管理端用户优惠券管理 ==========
    
    @Override
    public Page<UserCoupon> getAllUserCoupons(Pageable pageable) {
        try {
            return userCouponRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("获取所有用户优惠券失败", e);
            throw new RuntimeException("获取所有用户优惠券失败", e);
        }
    }
    
    @Override
    public long getTotalUserCoupons() {
        try {
            return userCouponRepository.count();
        } catch (Exception e) {
            log.error("获取用户优惠券总数失败", e);
            throw new RuntimeException("获取用户优惠券总数失败", e);
        }
    }
    
    @Override
    public long getUserCouponsCountByStatus(UserCoupon.UserCouponStatus status) {
        try {
            return userCouponRepository.countByStatus(status);
        } catch (Exception e) {
            log.error("根据状态获取用户优惠券数量失败，状态：{}", status, e);
            throw new RuntimeException("根据状态获取用户优惠券数量失败", e);
        }
    }
    
    @Override
    public void expireUserCoupon(String userCouponId) {
        try {
            UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new RuntimeException("用户优惠券不存在"));
            
            if (userCoupon.getStatus() != UserCoupon.UserCouponStatus.UNUSED) {
                throw new RuntimeException("只能将未使用的优惠券设为过期");
            }
            
            userCoupon.setStatus(UserCoupon.UserCouponStatus.EXPIRED);
            userCoupon.setExpiredAt(LocalDateTime.now());
            userCouponRepository.save(userCoupon);
            
            log.info("设置用户优惠券过期成功，ID：{}", userCouponId);
            
        } catch (Exception e) {
            log.error("设置用户优惠券过期失败，ID：{}", userCouponId, e);
            throw new RuntimeException("设置用户优惠券过期失败", e);
        }
    }
}