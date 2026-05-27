package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.dto.build.BuildPlanDto;
import com.example.PCOnlineShop.dto.build.BuildRequestDto;
import com.example.PCOnlineShop.model.build.BuildPreset;
import com.example.PCOnlineShop.service.build.RuleBasedBuildService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/build")
@RequiredArgsConstructor
@Slf4j
public class BuildSuggestionController {

    private final RuleBasedBuildService buildService;

    @GetMapping("/presets")
    public ResponseEntity<List<PresetInfo>> getPresets() {
        log.info("Fetching all build presets");

        List<PresetInfo> presets = Arrays.stream(BuildPreset.values())
            .map(preset -> new PresetInfo(
                preset.name(),
                preset.getName(),
                preset.getDescription(),
                preset.getSuggestedMinBudget()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(presets);
    }

    @PostMapping("/suggest")
    public ResponseEntity<?> suggestBuild(@RequestBody BuildRequestDto request) {
        String validationError = validateSuggestRequest(request);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }

        log.info("Received build suggestion request: preset={}, budget={}",
                request.getPreset(), request.getBudget());

        try {
            BuildPlanDto plan = buildService.suggestBuild(
                request.getPreset(),
                request.getBudget()
            );

            // Log the results
            log.info("Build suggestion successful");
            log.info("   - CPU: {}", plan.getCpu() != null ? plan.getCpu().getProductName() : "Not found");
            log.info("   - GPU: {}", plan.getGpu() != null ? plan.getGpu().getProductName() : "Not found");
            log.info("   - Mainboard: {}", plan.getMainboard() != null ? plan.getMainboard().getProductName() : "Not found");
            log.info("   - Memory: {}", plan.getMemory() != null ? plan.getMemory().getProductName() : "Not found");
            log.info("   - Storage: {}", plan.getStorage() != null ? plan.getStorage().getProductName() : "Not found");
            log.info("   - PSU: {}", plan.getPowerSupply() != null ? plan.getPowerSupply().getProductName() : "Not found");
            log.info("   - Case: {}", plan.getPcCase() != null ? plan.getPcCase().getProductName() : "Not found");
            log.info("   - Cooling: {}", plan.getCooling() != null ? plan.getCooling().getProductName() : "Not found");

            return ResponseEntity.ok(plan);

        } catch (IllegalArgumentException e) {
            log.error("Invalid build suggestion request: {}", request.getPreset());
            return ResponseEntity.badRequest()
                .body("Invalid preset: " + request.getPreset());

        } catch (Exception e) {
            log.error("Error generating build suggestion", e);
            return ResponseEntity.internalServerError()
                .body("Error generating build suggestion: " + e.getMessage());
        }
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyBuild(@RequestBody BuildPlanDto plan, HttpSession session) {
        log.info("Applying suggested build to session");

        try {
            if (!hasAnyComponent(plan)) {
                return ResponseEntity.badRequest()
                    .body("Build plan must contain at least one component");
            }

            // Convert BuildPlanDto to BuildItemDto
            BuildItemDto buildItems = buildService.convertPlanToItems(plan);

            // Store in session
            session.setAttribute("buildItems", buildItems);

            log.info("Build applied to session successfully");
            return ResponseEntity.ok()
                .body(Map.of("message", "Build applied successfully", "redirect", "/build/mainboard"));

        } catch (IllegalArgumentException e) {
            log.error("Invalid build plan", e);
            return ResponseEntity.badRequest()
                .body("Invalid build plan: " + e.getMessage());

        } catch (Exception e) {
            log.error("Error applying build to session", e);
            return ResponseEntity.internalServerError()
                .body("Error applying build: " + e.getMessage());
        }
    }

    private String validateSuggestRequest(BuildRequestDto request) {
        if (request == null) {
            return "Build request is required";
        }
        if (request.getPreset() == null || request.getPreset().isBlank()) {
            return "Preset is required";
        }
        if (request.getBudget() <= 0) {
            return "Budget must be greater than 0";
        }
        return null;
    }

    private boolean hasAnyComponent(BuildPlanDto plan) {
        return plan != null && (
            plan.getMainboard() != null
                || plan.getCpu() != null
                || plan.getGpu() != null
                || plan.getMemory() != null
                || plan.getStorage() != null
                || plan.getPowerSupply() != null
                || plan.getPcCase() != null
                || plan.getCooling() != null
        );
    }

    // Inner DTO class for preset information
    public record PresetInfo(
        String id,
        String name,
        String description,
        double minBudget
    ) {}
}

