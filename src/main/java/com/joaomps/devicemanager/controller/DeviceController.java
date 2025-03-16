package com.joaomps.devicemanager.controller;

import com.joaomps.devicemanager.dto.DeviceCreationRequest;
import com.joaomps.devicemanager.exception.DeviceNotFoundException;
import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.model.DeviceState;
import com.joaomps.devicemanager.service.DeviceService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping("/{id}")
  public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
    return deviceService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new DeviceNotFoundException("Device with id " + id + " was not found"));
  }

  @GetMapping
  public ResponseEntity<List<Device>> getAllDevices() {
    List<Device> devices = deviceService.findAll();
    return ResponseEntity.ok(devices);
  }

  @GetMapping(params = "brand")
  public ResponseEntity<List<Device>> getDevicesByBrand(@Nullable @RequestParam String brand) {
    List<Device> devices = deviceService.findByBrand(brand);
    return ResponseEntity.ok(devices);
  }

  @GetMapping(params = "state")
  public ResponseEntity<List<Device>> getDevicesByState(@Nullable @RequestParam DeviceState state) {
    List<Device> devices = deviceService.findByState(state);
    return ResponseEntity.ok(devices);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
    deviceService.deleteById(id);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<Device> updateDevice(@PathVariable Long id,
      @Valid @RequestBody Device deviceDetails) {
    Device updatedDevice = deviceService.updateDevice(id, deviceDetails);
    return ResponseEntity.ok(updatedDevice);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Device> partialUpdateDevice(@PathVariable Long id,
      @RequestBody Map<String, Object> updates) {
    Device updatedDevice = deviceService.partialUpdateDevice(id, updates);
    return ResponseEntity.ok(updatedDevice);
  }
}
