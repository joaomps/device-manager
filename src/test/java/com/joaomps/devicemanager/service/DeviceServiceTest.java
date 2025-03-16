package com.joaomps.devicemanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.joaomps.devicemanager.dto.DeviceCreationRequest;
import com.joaomps.devicemanager.exception.DeviceNotFoundException;
import com.joaomps.devicemanager.exception.InvalidOperationException;
import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.model.DeviceState;
import com.joaomps.devicemanager.repository.DeviceRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
  void createDevice_withNullRequest_throwsNullPointerException() {
    assertThrows(NullPointerException.class, () -> deviceService.createDevice(null));
  }

  @Test
  void createDevice_withValidRequest_setsCreationTimeAndSavesDevice() {
    DeviceCreationRequest request = new DeviceCreationRequest("Device1", "BrandA");
    Device device = new Device();
    device.setName("Device1");
    device.setBrand("BrandA");
    device.setState(DeviceState.AVAILABLE);
    device.setCreationTime(LocalDateTime.now());

    when(deviceRepository.save(any(Device.class))).thenReturn(device);

    Device result = deviceService.createDevice(request);

    assertNotNull(result.getCreationTime());
    assertEquals("Device1", result.getName());
    assertEquals("BrandA", result.getBrand());
    assertEquals(DeviceState.AVAILABLE, result.getState());
    verify(deviceRepository, times(1)).save(any(Device.class));
  }

  @Test
  void findById_withExistingId_returnsDevice() {
    Device device = new Device();
    device.setId(1L);
    when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

    Optional<Device> result = deviceService.findById(1L);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
  }

  @Test
  void findById_withNonExistingId_returnsEmptyOptional() {
    when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<Device> result = deviceService.findById(1L);

    assertFalse(result.isPresent());
  }

  @Test
  void findAll_returnsListOfDevices() {
    Device device1 = new Device();
    Device device2 = new Device();
    when(deviceRepository.findAll()).thenReturn(List.of(device1, device2));

    List<Device> result = deviceService.findAll();

    assertEquals(2, result.size());
  }

  @Test
  void findByBrand_withExistingBrand_returnsListOfDevices() {
    Device device = new Device();
    device.setBrand("BrandA");
    when(deviceRepository.findByBrand("BrandA")).thenReturn(List.of(device));

    List<Device> result = deviceService.findByBrand("BrandA");

    assertEquals(1, result.size());
    assertEquals("BrandA", result.getFirst().getBrand());
  }

  @Test
  void findByBrand_withNonExistingBrand_returnsEmptyList() {
    when(deviceRepository.findByBrand("BrandA")).thenReturn(List.of());

    List<Device> result = deviceService.findByBrand("BrandA");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByState_withExistingState_returnsListOfDevices() {
    Device device = new Device();
    device.setState(DeviceState.AVAILABLE);
    when(deviceRepository.findByState(DeviceState.AVAILABLE)).thenReturn(List.of(device));

    List<Device> result = deviceService.findByState(DeviceState.AVAILABLE);

    assertEquals(1, result.size());
    assertEquals(DeviceState.AVAILABLE, result.getFirst().getState());
  }

  @Test
  void findByState_withNonExistingState_returnsEmptyList() {
    when(deviceRepository.findByState(DeviceState.AVAILABLE)).thenReturn(List.of());

    List<Device> result = deviceService.findByState(DeviceState.AVAILABLE);

    assertTrue(result.isEmpty());
  }

  @Test
  void deleteById_withExistingIdAndAvailableState_deletesDevice() {
    Device device = new Device();
    device.setId(1L);
    device.setState(DeviceState.AVAILABLE);
    when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

    deviceService.deleteById(1L);

    verify(deviceRepository, times(1)).delete(device);
  }

  @Test
  void deleteById_withNonExistingId_throwsDeviceNotFoundException() {
    when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(DeviceNotFoundException.class, () -> deviceService.deleteById(1L));
  }

  @Test
  void deleteById_withInUseState_throwsInvalidOperationException() {
    Device device = new Device();
    device.setId(1L);
    device.setState(DeviceState.IN_USE);
    when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

    assertThrows(InvalidOperationException.class, () -> deviceService.deleteById(1L));
  }
}