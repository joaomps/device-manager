package com.joaomps.devicemanager.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaomps.devicemanager.dto.DeviceCreationRequest;
import com.joaomps.devicemanager.exception.DeviceNotFoundException;
import com.joaomps.devicemanager.exception.InvalidOperationException;
import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.model.DeviceState;
import com.joaomps.devicemanager.service.DeviceService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

  @Test
  void getDeviceById_withExistingId_returnsDevice() throws Exception {
    Device device = new Device(1L, "Device1", "BrandA", DeviceState.AVAILABLE, LocalDateTime.now());
    when(deviceService.findById(1L)).thenReturn(Optional.of(device));

    mockMvc.perform(get("/api/v1/devices/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(device.getId()))
        .andExpect(jsonPath("$.name").value(device.getName()))
        .andExpect(jsonPath("$.brand").value(device.getBrand()))
        .andExpect(jsonPath("$.state").value(device.getState().toString()));
  }

  @Test
  void getDeviceById_withNonExistingId_returnsNotFound() throws Exception {
    when(deviceService.findById(anyLong())).thenReturn(Optional.empty());
    doThrow(new DeviceNotFoundException("Device with id 999 was not found"))
        .when(deviceService).findById(999L);

    mockMvc.perform(get("/api/v1/devices/999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void getAllDevices_returnsListOfDevices() throws Exception {
    Device device1 = new Device(1L, "Device1", "BrandA", DeviceState.AVAILABLE,
        LocalDateTime.now());
    Device device2 = new Device(2L, "Device2", "BrandB", DeviceState.IN_USE, LocalDateTime.now());

    when(deviceService.findAll()).thenReturn(Arrays.asList(device1, device2));

    mockMvc.perform(get("/api/v1/devices"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id").value(device1.getId()))
        .andExpect(jsonPath("$[0].name").value(device1.getName()))
        .andExpect(jsonPath("$[1].id").value(device2.getId()))
        .andExpect(jsonPath("$[1].name").value(device2.getName()));
  }

  @Test
  void getDevicesByBrand_returnsFilteredDevices() throws Exception {
    Device device1 = new Device(1L, "Device1", "BrandA", DeviceState.AVAILABLE,
        LocalDateTime.now());
    Device device2 = new Device(2L, "Device2", "BrandA", DeviceState.IN_USE, LocalDateTime.now());

    when(deviceService.findByBrand("BrandA")).thenReturn(Arrays.asList(device1, device2));

    mockMvc.perform(get("/api/v1/devices?brand=BrandA"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].brand").value("BrandA"))
        .andExpect(jsonPath("$[1].brand").value("BrandA"));
  }

  @Test
  void getDevicesByState_returnsFilteredDevices() throws Exception {
    Device device1 = new Device(1L, "Device1", "BrandA", DeviceState.AVAILABLE,
        LocalDateTime.now());
    Device device2 = new Device(2L, "Device2", "BrandB", DeviceState.AVAILABLE,
        LocalDateTime.now());

    when(deviceService.findByState(DeviceState.AVAILABLE)).thenReturn(
        Arrays.asList(device1, device2));

    mockMvc.perform(get("/api/v1/devices?state=AVAILABLE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].state").value("AVAILABLE"))
        .andExpect(jsonPath("$[1].state").value("AVAILABLE"));
  }

  @Test
  void deleteDevice_withExistingId_returnsOk() throws Exception {
    doNothing().when(deviceService).deleteById(1L);

    mockMvc.perform(delete("/api/v1/devices/1"))
        .andExpect(status().isOk());
  }

  @Test
  void deleteDevice_withNonExistingId_returnsNotFound() throws Exception {
    doThrow(new DeviceNotFoundException("Device with id 999 was not found"))
        .when(deviceService).deleteById(999L);

    mockMvc.perform(delete("/api/v1/devices/999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteDevice_withDeviceInUse_returnsBadRequest() throws Exception {
    doThrow(new InvalidOperationException("Cannot delete a device that is in use"))
        .when(deviceService).deleteById(1L);

    mockMvc.perform(delete("/api/v1/devices/1"))
        .andExpect(status().isConflict());
  }

  @Test
  void updateDevice_withValidDetails_returnsUpdatedDevice() throws Exception {
    Device deviceDetails = new Device(null, "UpdatedDevice", "UpdatedBrand", DeviceState.AVAILABLE,
        null);
    Device updatedDevice = new Device(1L, "UpdatedDevice", "UpdatedBrand", DeviceState.AVAILABLE,
        LocalDateTime.now());

    when(deviceService.updateDevice(eq(1L), any(Device.class))).thenReturn(updatedDevice);

    mockMvc.perform(put("/api/v1/devices/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(deviceDetails)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(updatedDevice.getId()))
        .andExpect(jsonPath("$.name").value(updatedDevice.getName()))
        .andExpect(jsonPath("$.brand").value(updatedDevice.getBrand()))
        .andExpect(jsonPath("$.state").value(updatedDevice.getState().toString()));
  }

  @Test
  void updateDevice_withNonExistingId_returnsNotFound() throws Exception {
    Device deviceDetails = new Device(null, "UpdatedDevice", "UpdatedBrand", DeviceState.AVAILABLE,
        null);

    doThrow(new DeviceNotFoundException("Device with id 999 not found"))
        .when(deviceService).updateDevice(eq(999L), any(Device.class));

    mockMvc.perform(put("/api/v1/devices/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(deviceDetails)))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateDevice_withInvalidChange_returnsBadRequest() throws Exception {
    Device deviceDetails = new Device(null, "UpdatedDevice", "UpdatedBrand", DeviceState.AVAILABLE,
        null);

    doThrow(new InvalidOperationException("Cannot update name or brand of a device that is in use"))
        .when(deviceService).updateDevice(eq(1L), any(Device.class));

    mockMvc.perform(put("/api/v1/devices/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(deviceDetails)))
        .andExpect(status().isConflict());
  }

  @Test
  void partialUpdateDevice_withValidUpdates_returnsUpdatedDevice() throws Exception {
    Map<String, Object> updates = new HashMap<>();
    updates.put("name", "UpdatedDevice");
    updates.put("state", "AVAILABLE");

    Device updatedDevice = new Device(1L, "UpdatedDevice", "OriginalBrand", DeviceState.AVAILABLE,
        LocalDateTime.now());

    when(deviceService.partialUpdateDevice(eq(1L), any(Map.class))).thenReturn(updatedDevice);

    mockMvc.perform(patch("/api/v1/devices/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updates)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(updatedDevice.getId()))
        .andExpect(jsonPath("$.name").value(updatedDevice.getName()))
        .andExpect(jsonPath("$.brand").value(updatedDevice.getBrand()))
        .andExpect(jsonPath("$.state").value(updatedDevice.getState().toString()));
  }

  @Test
  void partialUpdateDevice_withNonExistingId_returnsNotFound() throws Exception {
    Map<String, Object> updates = new HashMap<>();
    updates.put("name", "UpdatedDevice");

    doThrow(new DeviceNotFoundException("Device with id 999 not found"))
        .when(deviceService).partialUpdateDevice(eq(999L), any(Map.class));

    mockMvc.perform(patch("/api/v1/devices/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updates)))
        .andExpect(status().isNotFound());
  }

  @Test
  void partialUpdateDevice_withInvalidChange_returnsBadRequest() throws Exception {
    Map<String, Object> updates = new HashMap<>();
    updates.put("name", "UpdatedDevice");

    doThrow(new InvalidOperationException("Cannot update name or brand of a device that is in use"))
        .when(deviceService).partialUpdateDevice(eq(1L), any(Map.class));

    mockMvc.perform(patch("/api/v1/devices/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updates)))
        .andExpect(status().isConflict());
  }

  @Test
  void partialUpdateDevice_withCreationTimeUpdate_returnsBadRequest() throws Exception {
    Map<String, Object> updates = new HashMap<>();
    updates.put("creationTime", "2023-01-01T10:00:00");

    doThrow(new InvalidOperationException("Creation time cannot be updated"))
        .when(deviceService).partialUpdateDevice(eq(1L), any(Map.class));

    mockMvc.perform(patch("/api/v1/devices/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updates)))
        .andExpect(status().isConflict());
  }
}
