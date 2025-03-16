package com.joaomps.devicemanager.repository;

import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.model.DeviceState;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {

  List<Device> findByBrand(String brand);

  List<Device> findByState(DeviceState state);
}
