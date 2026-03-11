package com.example.hello.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.hello.entity.Banner;

public interface BannerRepository extends JpaRepository<Banner, String> {

    List<Banner> findByEnabledTrueAndPositionOrderBySortDescCreateTimeDesc(String position);

    List<Banner> findByEnabledTrueAndPositionAndStartTimeBeforeAndEndTimeAfterOrderBySortDescCreateTimeDesc(
            String position, Date now1, Date now2);
}

