package com.joaomps.devicemanager.controller;

import com.joaomps.devicemanager.dto.DeviceCreationRequest;
import com.joaomps.devicemanager.exception.DeviceNotFoundException;
import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.model.DeviceState;
import com.joaomps.devicemanager.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  @Operation(summary = "Create a new device", description = "Creates a device with the specified name and brand. The device will be set to AVAILABLE state by default. Please note name and brand cannot be blank")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Device created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Device.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(mediaType = "application/json"))})
  @PostMapping
  public ResponseEntity<Device> createDevice(@Valid @RequestBody DeviceCreationRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(deviceService.createDevice(request));
  }

  @Operation(summary = "Get device by ID", description = "Retrieves a device by its unique identifier")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Device found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Device.class))),
      @ApiResponse(responseCode = "404", description = "Device not found", content = @Content(mediaType = "application/json"))})
  @GetMapping("/{id}")
  public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
    return deviceService.findById(id).map(ResponseEntity::ok)
        .orElseThrow(() -> new DeviceNotFoundException("Device with id " + id + " was not found"));
  }

  @Operation(summary = "Get all devices", description = "Retrieves a list of all devices in the system. Optionally we can filter the results by brand and/or state")
  @ApiResponse(responseCode = "200", description = "List of devices retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Device.class, type = "array")))
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

  @Operation(summary = "Delete a device", description = "Deletes a device by its ID. Device cannot be deleted if it's in use")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Device deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Device not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "409", description = "Device is in use and cannot be deleted", content = @Content(mediaType = "application/json"))})
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
    deviceService.deleteById(id);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Update a device", description = "Fully updates a device with new details. Some properties cannot be modified if device is in use")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Device updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Device.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Device not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "409", description = "Cannot update properties of device in use", content = @Content(mediaType = "application/json"))})
  @PutMapping("/{id}")
  public ResponseEntity<Device> updateDevice(@PathVariable Long id,
      @Valid @RequestBody Device deviceDetails) {
    Device updatedDevice = deviceService.updateDevice(id, deviceDetails);
    return ResponseEntity.ok(updatedDevice);
  }

  @Operation(summary = "Partially update a device", description = "Updates only the specified fields of a device. Some properties cannot be modified if device is in use")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Device partially updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Device.class))),
      @ApiResponse(responseCode = "404", description = "Device not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "409", description = "Cannot update certain properties of device in use", content = @Content(mediaType = "application/json"))})
  @PatchMapping("/{id}")
  public ResponseEntity<Device> partialUpdateDevice(@PathVariable Long id,
      @RequestBody Map<String, Object> updates) {
    Device updatedDevice = deviceService.partialUpdateDevice(id, updates);
    return ResponseEntity.ok(updatedDevice);
  }
}
