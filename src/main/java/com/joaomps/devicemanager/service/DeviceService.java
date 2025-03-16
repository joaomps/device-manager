package com.joaomps.devicemanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaomps.devicemanager.dto.DeviceCreationRequest;
import com.joaomps.devicemanager.exception.DeviceNotFoundException;
import com.joaomps.devicemanager.exception.InvalidOperationException;
import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.model.DeviceState;
import com.joaomps.devicemanager.repository.DeviceRepository;
import com.joaomps.devicemanager.util.Constants;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

  private final DeviceRepository deviceRepository;
  private final ObjectMapper objectMapper;

  public DeviceService(DeviceRepository deviceRepository, ObjectMapper objectMapper) {
    this.deviceRepository = deviceRepository;
    this.objectMapper = objectMapper;
  }

  public Device createDevice(@Valid DeviceCreationRequest device) {
    Device newDevice = new Device();
    newDevice.setName(device.name());
    newDevice.setBrand(device.brand());
    newDevice.setCreationTime(LocalDateTime.now());
    newDevice.setState(DeviceState.AVAILABLE);
    return deviceRepository.save(newDevice);
  }

  public Optional<Device> findById(Long id) {
    return deviceRepository.findById(id);
  }

  public List<Device> findAll() {
    return deviceRepository.findAll();
  }

  public List<Device> findByBrand(String brand) {
    return deviceRepository.findByBrand(brand);
  }

  public List<Device> findByState(DeviceState state) {
    return deviceRepository.findByState(state);
  }

  public void deleteById(Long id) {
    Device device = deviceRepository.findById(id)
        .orElseThrow(
            () -> new DeviceNotFoundException(Constants.DEVICE_WITH_ID + id + " was not found"));

    if (DeviceState.IN_USE == device.getState()) {
      throw new InvalidOperationException("Cannot delete a device that is in use");
    }

    deviceRepository.delete(device);
  }

  private static void validateImmutablePropertiesForInUseDevice(Device newDeviceDetails,
      Device existingDevice) {
    if (!existingDevice.getName().equals(newDeviceDetails.getName()) ||
        !existingDevice.getBrand().equals(newDeviceDetails.getBrand())) {
      throw new InvalidOperationException(
          "Cannot update name or brand of a device that is in use");
    }
  }

  private static void validateImmutableFieldsForInUseDevice(Map<String, Object> updates,
      Device existingDevice) {
    if ((updates.containsKey("name") && !existingDevice.getName().equals(updates.get("name"))) ||
        (updates.containsKey("brand") && !existingDevice.getBrand()
            .equals(updates.get("brand")))) {
      throw new InvalidOperationException(
          "Cannot update name or brand of a device that is in use");
    }
  }

  public Device updateDevice(Long id, Device newDeviceDetails) {
    Device existingDevice = deviceRepository.findById(id)
        .orElseThrow(
            () -> new DeviceNotFoundException(Constants.DEVICE_WITH_ID + id + " not found"));

    newDeviceDetails.setId(id);
    newDeviceDetails.setCreationTime(existingDevice.getCreationTime());

    if (existingDevice.getState() == DeviceState.IN_USE) {
      validateImmutablePropertiesForInUseDevice(newDeviceDetails, existingDevice);
    }

    return deviceRepository.save(newDeviceDetails);
  }

  public Device partialUpdateDevice(Long id, Map<String, Object> updates) {
    Device existingDevice = deviceRepository.findById(id)
        .orElseThrow(
            () -> new DeviceNotFoundException(Constants.DEVICE_WITH_ID + id + " not found"));

    if (updates.containsKey("creationTime")) {
      throw new InvalidOperationException("Creation time cannot be updated");
    }

    if (existingDevice.getState() == DeviceState.IN_USE) {
      validateImmutableFieldsForInUseDevice(updates, existingDevice);
    }

    try {
      JsonNode updatesNode = objectMapper.valueToTree(updates);
      Device deviceUpdates = objectMapper.readerForUpdating(existingDevice).readValue(updatesNode);

      return deviceRepository.save(deviceUpdates);
    } catch (IOException e) {
      throw new RuntimeException("Error processing update request", e);
    }
  }
}
