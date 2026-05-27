package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.service.build.BuildService;
import com.example.PCOnlineShop.service.build.CpuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CpuController.class)
@AutoConfigureMockMvc(addFilters = false)
class CpuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CpuService cpuService;

    @MockBean
    private BuildService buildService;

    @Test
    void selectCpuRejectsPostedIdWhenComponentIsNotCompatible() throws Exception {
        BuildItemDto buildItems = new BuildItemDto();

        when(buildService.findSelectableCompatibleCpuByProductId(eq(77), same(buildItems)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/build/selectCpu")
                        .param("cpuId", "77")
                        .sessionAttr("buildItems", buildItems))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/build/cpu"))
                .andExpect(flash().attribute("error", "Selected CPU is not available or compatible."));

        assertThat(buildItems.getCpu()).isNull();
    }
}
