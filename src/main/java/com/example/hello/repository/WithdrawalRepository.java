package com.example.hello.repository;

import com.example.hello.entity.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, String> {

    Page<Withdrawal> findByUserId(String userId, Pageable pageable);

    Page<Withdrawal> findByUserIdAndStatus(String userId, String status, Pageable pageable);

    /** 某用户某状态的提现金额总和（用于可提现/已提现统计） */
    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM Withdrawal w WHERE w.userId = :userId AND w.status = :status")
    Double sumAmountByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);
}