package com.example.test_rest_api_wrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.test_rest_api_wrapper.models.Script;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.example.test_rest_api_wrapper.services.ScriptExecutorService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class TestRestApiWrapperApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScriptExecutorService scriptExecutorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testExecuteScript() throws Exception {
        String scriptJson = "{\"code\":\"console.log('Hello, world!');\"}";

        MvcResult result = mockMvc.perform(post("/scripts/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(scriptJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Script script = objectMapper.readValue(responseContent, Script.class);

        assertThat(script.getStatus()).isEqualTo("executing");
        assertThat(script.getCode()).isEqualTo("{\"code\":\"console.log('Hello, world!');\"}");
    }

    @Test
    public void testGetScript() throws Exception {
        String id = "test-id";
        String code = "console.log('Hello, world!');";
        scriptExecutorService.executeScript(id, code, false);

        mockMvc.perform(get("/scripts/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.code").value(code));
    }

    @Test
    public void testListScripts() throws Exception {
        String code = "console.log('Hello, world!');";
        scriptExecutorService.executeScript("test-id-1", code, false);
        scriptExecutorService.executeScript("test-id-2", code, false);

        mockMvc.perform(get("/scripts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].code").value(code))
                .andExpect(jsonPath("$[1].code").value(code));
    }

    @Test
    public void testStopScript() throws Exception {
        String id = "test-id";
        String code = "while(true) {}"; // Infinite loop script
        scriptExecutorService.executeScript(id, code, false);

        mockMvc.perform(post("/scripts/" + id + "/stop"))
                .andExpect(status().isOk());

        Script script = scriptExecutorService.getScript(id);
        assertThat(script.getStatus()).isEqualTo("stopped");
    }

    @Test
    public void testRemoveScript() throws Exception {
        String id = "test-id";
        String code = "console.log('Hello, world!');";
        scriptExecutorService.executeScript(id, code, false);

        mockMvc.perform(delete("/scripts/" + id))
                .andExpect(status().isOk());

        assertThat(scriptExecutorService.getScript(id)).isNull();
    }
}