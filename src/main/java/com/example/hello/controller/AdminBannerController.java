package com.example.hello.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.hello.common.ApiResponse;
import com.example.hello.entity.Banner;
import com.example.hello.service.BannerService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/api/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> list() {
        List<Banner> all = bannerService.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("content", all);
        result.put("totalElements", all.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Banner>> get(@PathVariable String id) {
        Optional<Banner> bannerOpt = bannerService.findById(id);
        return bannerOpt
                .map(banner -> ResponseEntity.ok(ApiResponse.success(banner)))
                .orElseGet(() -> ResponseEntity.ok(ApiResponse.error("轮播图不存在")));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Banner>> create(@RequestBody Banner banner) {
        Banner saved = bannerService.save(banner);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Banner>> update(@PathVariable String id, @RequestBody Banner banner) {
        banner.setId(id);
        Banner saved = bannerService.save(banner);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable String id) {
        bannerService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功"));
    }
}

