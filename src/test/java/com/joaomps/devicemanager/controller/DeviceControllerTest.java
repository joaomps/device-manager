package com.joaomps.devicemanager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class DeviceControllerTest {

  @Mock
  private DeviceService deviceService;

  @InjectMocks
  private DeviceController deviceController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void givenValidDevice_whenCreateDevice_thenReturnsCreatedStatus() {
    Device device = new Device();
    when(deviceService.createDevice(any(Device.class))).thenReturn(device);

    ResponseEntity<Device> response = deviceController.createDevice(device);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(device, response.getBody());
  }
}
