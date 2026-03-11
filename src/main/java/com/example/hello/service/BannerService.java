package com.example.hello.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.example.hello.entity.Banner;
import com.example.hello.repository.BannerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public List<Banner> getActiveBanners(String position, boolean useTimeWindow) {
        String pos = StringUtils.hasText(position) ? position : "home";
        if (useTimeWindow) {
            Date now = new Date();
            return bannerRepository.findByEnabledTrueAndPositionAndStartTimeBeforeAndEndTimeAfterOrderBySortDescCreateTimeDesc(
                    pos, now, now);
        }
        return bannerRepository.findByEnabledTrueAndPositionOrderBySortDescCreateTimeDesc(pos);
    }

    public List<Banner> findAll() {
        return bannerRepository.findAll();
    }

    public Optional<Banner> findById(String id) {
        return bannerRepository.findById(id);
    }

    public Banner save(Banner banner) {
        return bannerRepository.save(banner);
    }

    public void deleteById(String id) {
        bannerRepository.deleteById(id);
    }
}

