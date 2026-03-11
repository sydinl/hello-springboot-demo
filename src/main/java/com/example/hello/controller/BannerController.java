package com.example.hello.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.hello.common.ApiResponse;
import com.example.hello.entity.Banner;
import com.example.hello.service.BannerService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public ApiResponse<List<BannerResponse>> getHomeBanners(
            @RequestParam(value = "position", required = false, defaultValue = "home") String position) {

        List<Banner> banners = bannerService.getActiveBanners(position, false);
        List<BannerResponse> result = banners.stream()
                .map(BannerResponse::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.success(result);
    }

    @Data
    @AllArgsConstructor
    private static class BannerResponse {
        private String id;
        private String title;
        private String imageUrl;
        private Integer jumpType;
        private String jumpTarget;
        private Integer sort;

        static BannerResponse fromEntity(Banner banner) {
            return new BannerResponse(
                    banner.getId(),
                    banner.getTitle(),
                    banner.getImageUrl(),
                    banner.getJumpType(),
                    banner.getJumpTarget(),
                    banner.getSort());
        }
    }
}

