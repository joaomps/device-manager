package com.joaomps.devicemanager.service;

import com.joaomps.devicemanager.dto.DeviceCreationRequest;
import com.joaomps.devicemanager.exception.DeviceNotFoundException;
import com.joaomps.devicemanager.exception.InvalidOperationException;
import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.model.DeviceState;
import com.joaomps.devicemanager.repository.DeviceRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

  private final DeviceRepository deviceRepository;

  public DeviceService(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
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
        .orElseThrow(() -> new DeviceNotFoundException("Device with id " + id + " was not found"));

    if (DeviceState.IN_USE == device.getState()) {
      throw new InvalidOperationException("Cannot delete a device that is in use");
    }

    deviceRepository.delete(device);
  }

}
