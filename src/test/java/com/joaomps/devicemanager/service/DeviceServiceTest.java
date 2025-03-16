package com.joaomps.devicemanager.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DeviceServiceTest {

  @Mock
  private DeviceRepository deviceRepository;

  @InjectMocks
  private DeviceService deviceService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void givenValidDevice_whenCreateDevice_thenSetsCreationTimeAndSavesDevice() {
    Device device = new Device();
    when(deviceRepository.save(device)).thenReturn(device);

    Device result = deviceService.createDevice(device);

    assertNotNull(result.getCreationTime());
    verify(deviceRepository, times(1)).save(device);
  }

  @Test
  void givenNullDevice_whenCreateDevice_thenThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> deviceService.createDevice(null));
  }
}