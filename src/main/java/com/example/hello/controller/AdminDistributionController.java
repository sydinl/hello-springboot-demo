package com.example.hello.controller;

import com.example.hello.common.ApiResponse;
import com.example.hello.entity.DistributionConfig;
import com.example.hello.entity.ProjectDistributionRate;
import com.example.hello.service.DistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员：分销比例配置（全局默认 + 按项目覆盖）
 */
@RestController
@RequestMapping("/admin/api/distribution")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDistributionController {

    @Autowired
    private DistributionService distributionService;

    /** 获取全局默认比例 */
    @GetMapping("/config")
    public ResponseEntity<ApiResponse<DistributionConfig>> getGlobalConfig() {
        DistributionConfig config = distributionService.getGlobalConfig();
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    /** 更新全局默认比例。body: { "level1Rate": 0.10, "level2Rate": 0.05 } */
    @PutMapping("/config")
    public ResponseEntity<ApiResponse<DistributionConfig>> updateGlobalConfig(@RequestBody Map<String, Object> body) {
        double level1 = toDouble(body.get("level1Rate"), 0.10);
        double level2 = toDouble(body.get("level2Rate"), 0.05);
        DistributionConfig updated = distributionService.updateGlobalConfig(level1, level2);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /** 获取所有项目单独设置的分销比例列表 */
    @GetMapping("/project-rates")
    public ResponseEntity<ApiResponse<List<ProjectDistributionRate>>> listProjectRates() {
        List<ProjectDistributionRate> list = distributionService.listProjectRates();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /** 获取指定项目的分销比例（未设置则返回 404） */
    @GetMapping("/project-rates/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDistributionRate>> getProjectRate(@PathVariable String projectId) {
        ProjectDistributionRate rate = distributionService.getProjectRate(projectId);
        if (rate == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "该项目未单独设置分销比例"));
        }
        return ResponseEntity.ok(ApiResponse.success(rate));
    }

    /** 设置/覆盖某项目的分销比例。body: { "level1Rate": 0.10, "level2Rate": 0.05 } */
    @PostMapping("/project-rates/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDistributionRate>> saveProjectRate(
            @PathVariable String projectId,
            @RequestBody Map<String, Object> body) {
        double level1 = toDouble(body.get("level1Rate"), 0.10);
        double level2 = toDouble(body.get("level2Rate"), 0.05);
        try {
            ProjectDistributionRate saved = distributionService.saveProjectRate(projectId, level1, level2);
            return ResponseEntity.ok(ApiResponse.success(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.error(400, e.getMessage()));
        }
    }

    private static double toDouble(Object v, double defaultVal) {
        if (v == null) return defaultVal;
        if (v instanceof Number) return ((Number) v).doubleValue();
        try {
            return Double.parseDouble(v.toString());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /** 删除某项目的单独比例（删除后该商品使用全局默认比例） */
    @DeleteMapping("/project-rates/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProjectRate(@PathVariable String projectId) {
        distributionService.deleteProjectRate(projectId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
