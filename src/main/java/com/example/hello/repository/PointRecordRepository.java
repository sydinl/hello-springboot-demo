package com.example.hello.repository;

import com.example.hello.entity.PointRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRecordRepository extends JpaRepository<PointRecord, String> {
    
    Page<PointRecord> findByUserId(String userId, Pageable pageable);
    
    Page<PointRecord> findByUserIdAndType(String userId, String type, Pageable pageable);
}