package com.example.hello.repository;

import com.example.hello.entity.ProjectDistributionRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectDistributionRateRepository extends JpaRepository<ProjectDistributionRate, String> {

    Optional<ProjectDistributionRate> findByProjectId(String projectId);

    void deleteByProjectId(String projectId);
}
