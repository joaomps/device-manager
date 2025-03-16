package com.joaomps.devicemanager.controller;

import com.joaomps.devicemanager.dto.DeviceCreationRequest;
import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

  private final DeviceService deviceService;

  public DeviceController(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  @PostMapping
  public ResponseEntity<Device> createDevice(@Valid @RequestBody DeviceCreationRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(deviceService.createDevice(request));
  }
}
