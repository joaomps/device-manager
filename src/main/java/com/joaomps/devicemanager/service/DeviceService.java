package com.joaomps.devicemanager.service;

import com.joaomps.devicemanager.dto.DeviceCreationRequest;
import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.model.DeviceState;
import com.joaomps.devicemanager.repository.DeviceRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
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

}
