package com.joaomps.devicemanager.service;

import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.repository.DeviceRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

  private final DeviceRepository deviceRepository;

  public DeviceService(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
  }

  public Device createDevice(Device device) {
    device.setCreationTime(LocalDateTime.now());
    return deviceRepository.save(device);
  }

}
