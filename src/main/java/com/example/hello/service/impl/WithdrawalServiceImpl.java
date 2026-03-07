package com.example.hello.service.impl;

import com.example.hello.entity.Withdrawal;
import com.example.hello.repository.DistributionOrderRepository;
import com.example.hello.repository.WithdrawalRepository;
import com.example.hello.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Autowired
    private DistributionOrderRepository distributionOrderRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Withdrawal applyWithdrawal(UUID userId, Double amount) {
        if (userId == null || amount == null || amount <= 0) {
            throw new IllegalArgumentException("参数无效");
        }
        String uid = userId.toString();
        Double pendingCommission = distributionOrderRepository.sumCommissionByReferrerIdAndStatus(uid, "pending");
        Double pendingWithdrawalSum = withdrawalRepository.sumAmountByUserIdAndStatus(uid, "pending");
        double available = (pendingCommission != null ? pendingCommission : 0) - (pendingWithdrawalSum != null ? pendingWithdrawalSum : 0);
        if (amount > available) {
            throw new IllegalArgumentException("可提现佣金不足");
        }
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(uid);
        withdrawal.setAmount(amount);
        withdrawal.setStatus("pending");
        return withdrawalRepository.save(withdrawal);
    }

    @Override
    public Page<Withdrawal> getWithdrawalRecords(UUID userId, Pageable pageable) {
        if (userId == null) return Page.empty(pageable);
        return withdrawalRepository.findByUserId(userId.toString(), pageable);
    }

    @Override
    public Page<Withdrawal> getWithdrawalRecordsByStatus(UUID userId, String status, Pageable pageable) {
        if (userId == null) return Page.empty(pageable);
        return withdrawalRepository.findByUserIdAndStatus(userId.toString(), status, pageable);
    }
}