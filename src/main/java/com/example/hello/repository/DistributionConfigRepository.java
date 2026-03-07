package com.example.hello.repository;

import com.example.hello.entity.DistributionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributionConfigRepository extends JpaRepository<DistributionConfig, String> {
}
