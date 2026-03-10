package com.example.taskapi;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.entity.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTask_returnsCreatedTask() throws Exception {
        TaskRequest request = new TaskRequest("Fix bug", "Fix the null pointer exception", TaskStatus.OPEN);

        MvcResult result = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Fix bug"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn();

        TaskResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), TaskResponse.class);
        assertThat(response.getId()).isNotNull();
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    void getAllTasks_returnsListOfTasks() throws Exception {
        TaskRequest request = new TaskRequest("Task 1", "Description", TaskStatus.OPEN);
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Task 1"));
    }

    @Test
    void getTaskById_returnsTask() throws Exception {
        TaskRequest request = new TaskRequest("My task", "Detail", TaskStatus.IN_PROGRESS);
        MvcResult createResult = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        TaskResponse created = objectMapper.readValue(createResult.getResponse().getContentAsString(), TaskResponse.class);

        mockMvc.perform(get("/tasks/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("My task"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void getTaskById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with id: 999"));
    }

    @Test
    void updateTask_updatesAndReturnsTask() throws Exception {
        TaskRequest createRequest = new TaskRequest("Old title", "Old desc", TaskStatus.OPEN);
        MvcResult createResult = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        TaskResponse created = objectMapper.readValue(createResult.getResponse().getContentAsString(), TaskResponse.class);

        TaskRequest updateRequest = new TaskRequest("New title", "New desc", TaskStatus.COMPLETE);
        mockMvc.perform(put("/tasks/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.status").value("COMPLETE"));
    }

    @Test
    void deleteTask_deletesAndReturns204() throws Exception {
        TaskRequest request = new TaskRequest("To delete", null, TaskStatus.OPEN);
        MvcResult createResult = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        TaskResponse created = objectMapper.readValue(createResult.getResponse().getContentAsString(), TaskResponse.class);

        mockMvc.perform(delete("/tasks/{id}", created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTask_withBlankTitle_returns400() throws Exception {
        TaskRequest request = new TaskRequest("", "desc", TaskStatus.OPEN);
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_withNullStatus_returns400() throws Exception {
        TaskRequest request = new TaskRequest("Title", "desc", null);
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
