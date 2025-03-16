package com.joaomps.devicemanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaomps.devicemanager.dto.DeviceCreationRequest;
import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.model.DeviceState;
import com.joaomps.devicemanager.service.DeviceService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeviceController.class)
@Import(DeviceService.class)
class DeviceControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private DeviceService deviceService;

  @Test
  void createDevice_withValidRequest_returnsCreatedDevice() throws Exception {
    DeviceCreationRequest request = new DeviceCreationRequest("Device1", "BrandA");
    Device createdDevice = new Device(1L, "Device1", "BrandA", DeviceState.AVAILABLE,
        LocalDateTime.now());

    when(deviceService.createDevice(any(DeviceCreationRequest.class))).thenReturn(createdDevice);

    mockMvc.perform(post("/api/v1/devices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(createdDevice.getId()))
        .andExpect(jsonPath("$.name").value(createdDevice.getName()))
        .andExpect(jsonPath("$.brand").value(createdDevice.getBrand()))
        .andExpect(jsonPath("$.state").value(createdDevice.getState().toString()));
  }

  @Test
  void createDevice_withInvalidRequest_returnsBadRequest() throws Exception {
    DeviceCreationRequest request = new DeviceCreationRequest("", "BrandA");

    mockMvc.perform(post("/api/v1/devices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createDevice_withMissingBrand_returnsBadRequest() throws Exception {
    DeviceCreationRequest request = new DeviceCreationRequest("Device1", "");

    mockMvc.perform(post("/api/v1/devices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

}
