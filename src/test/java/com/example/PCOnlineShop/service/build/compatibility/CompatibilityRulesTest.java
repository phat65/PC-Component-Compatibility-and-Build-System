package com.example.PCOnlineShop.service.build.compatibility;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.model.build.Mainboard;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompatibilityRulesTest {

    @Test
    void caseFormFactorCompatibilityRespectsCaseSize() {
        assertThat(CompatibilityRules.isCaseFormFactorCompatible("ATX", "ATX")).isTrue();
        assertThat(CompatibilityRules.isCaseFormFactorCompatible("ATX", "Micro ATX")).isTrue();
        assertThat(CompatibilityRules.isCaseFormFactorCompatible("ATX", "Mini ITX")).isTrue();

        assertThat(CompatibilityRules.isCaseFormFactorCompatible("Micro ATX", "ATX")).isFalse();
        assertThat(CompatibilityRules.isCaseFormFactorCompatible("Micro ATX", "Micro ATX")).isTrue();
        assertThat(CompatibilityRules.isCaseFormFactorCompatible("Micro ATX", "Mini ITX")).isTrue();

        assertThat(CompatibilityRules.isCaseFormFactorCompatible("Mini ITX", "ATX")).isFalse();
        assertThat(CompatibilityRules.isCaseFormFactorCompatible("Mini ITX", "Micro ATX")).isFalse();
        assertThat(CompatibilityRules.isCaseFormFactorCompatible("Mini ITX", "Mini ITX")).isTrue();
    }

    @Test
    void pcieParserHandlesRealWorldLabels() {
        assertThat(CompatibilityRules.parsePcieVersion("PCIe 4.0 x16")).isEqualTo(4.0);
        assertThat(CompatibilityRules.parsePcieVersion("PCIe5.0")).isEqualTo(5.0);
        assertThat(CompatibilityRules.parsePcieVersion("Gen 3.0")).isEqualTo(3.0);
    }

    @Test
    void mainboardRejectsGpuWithHigherPcieRequirement() {
        Mainboard mainboard = new Mainboard();
        mainboard.setPcieVersion("PCIe 3.0 x16");

        GPU gpu = new GPU();
        gpu.setPcieVersion("PCIe 4.0 x16");

        BuildItemDto buildItem = new BuildItemDto();
        buildItem.setGpu(gpu);

        CompatibilityResult result = new MainboardCompatibilityValidator()
                .validate(buildItem, mainboard);

        assertThat(result.compatible()).isFalse();
        assertThat(result.reasons()).contains("Mainboard PCIe version is lower than selected GPU requirement");
    }

    @Test
    void mainboardAcceptsGpuWithLowerPcieRequirement() {
        Mainboard mainboard = new Mainboard();
        mainboard.setPcieVersion("PCIe 5.0 x16");

        GPU gpu = new GPU();
        gpu.setPcieVersion("PCIe 4.0 x16");

        BuildItemDto buildItem = new BuildItemDto();
        buildItem.setGpu(gpu);

        CompatibilityResult result = new MainboardCompatibilityValidator()
                .validate(buildItem, mainboard);

        assertThat(result.compatible()).isTrue();
    }
}
