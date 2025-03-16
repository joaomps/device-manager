package com.joaomps.devicemanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.joaomps.devicemanager.dto.DeviceCreationRequest;
import com.joaomps.devicemanager.exception.DeviceNotFoundException;
import com.joaomps.devicemanager.exception.InvalidOperationException;
import com.joaomps.devicemanager.model.Device;
import com.joaomps.devicemanager.model.DeviceState;
import com.joaomps.devicemanager.repository.DeviceRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DeviceServiceTest {

  @Mock
  private DeviceRepository deviceRepository;
  @Mock
  private ObjectMapper objectMapper;

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

  @Test
  void updateDevice_withValidDetails_updatesDevice() {
    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setCreationTime(LocalDateTime.now());
    existingDevice.setState(DeviceState.AVAILABLE);

    Device newDeviceDetails = new Device();
    newDeviceDetails.setName("UpdatedName");
    newDeviceDetails.setBrand("UpdatedBrand");

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));
    when(deviceRepository.save(any(Device.class))).thenReturn(newDeviceDetails);

    Device result = deviceService.updateDevice(1L, newDeviceDetails);

    assertEquals("UpdatedName", result.getName());
    assertEquals("UpdatedBrand", result.getBrand());
    assertEquals(existingDevice.getCreationTime(), result.getCreationTime());
    verify(deviceRepository, times(1)).save(any(Device.class));
  }

  @Test
  void updateDevice_withNonExistingId_throwsDeviceNotFoundException() {
    Device newDeviceDetails = new Device();
    when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(DeviceNotFoundException.class,
        () -> deviceService.updateDevice(1L, newDeviceDetails));
  }

  @Test
  void updateDevice_withInUseStateAndImmutableProperties_throwsInvalidOperationException() {
    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setState(DeviceState.IN_USE);
    existingDevice.setName("OriginalName");
    existingDevice.setBrand("OriginalBrand");

    Device newDeviceDetails = new Device();
    newDeviceDetails.setName("UpdatedName");
    newDeviceDetails.setBrand("UpdatedBrand");

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));

    assertThrows(InvalidOperationException.class,
        () -> deviceService.updateDevice(1L, newDeviceDetails));
  }

  @Test
  void partialUpdateDevice_withValidUpdates_updatesDevice() throws IOException {

    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setCreationTime(LocalDateTime.now());
    existingDevice.setState(DeviceState.AVAILABLE);

    Map<String, Object> updates = Map.of("name", "UpdatedName", "brand", "UpdatedBrand");

    JsonNode jsonNode = mock(JsonNode.class);

    Iterator<String> fieldNamesIterator = mock(Iterator.class);
    when(fieldNamesIterator.hasNext()).thenReturn(true, true, false);
    when(fieldNamesIterator.next()).thenReturn("name", "brand");
    when(jsonNode.fieldNames()).thenReturn(fieldNamesIterator);

    when(objectMapper.valueToTree(updates)).thenReturn(jsonNode);

    var objectReader = mock(ObjectReader.class);
    when(objectMapper.readerForUpdating(existingDevice)).thenReturn(objectReader);

    when(objectReader.readValue(jsonNode)).thenAnswer(invocation -> {
      existingDevice.setName("UpdatedName");
      existingDevice.setBrand("UpdatedBrand");
      return existingDevice;
    });

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));
    when(deviceRepository.save(any(Device.class))).thenReturn(existingDevice);

    Device result = deviceService.partialUpdateDevice(1L, updates);

    assertEquals("UpdatedName", result.getName());
    assertEquals("UpdatedBrand", result.getBrand());
    verify(deviceRepository, times(1)).save(any(Device.class));
  }

  @Test
  void partialUpdateDevice_withNonExistingId_throwsDeviceNotFoundException() {
    Map<String, Object> updates = Map.of("name", "UpdatedName");

    when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(DeviceNotFoundException.class,
        () -> deviceService.partialUpdateDevice(1L, updates));
  }

  @Test
  void partialUpdateDevice_withInUseStateAndImmutableFields_throwsInvalidOperationException() {
    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setState(DeviceState.IN_USE);
    existingDevice.setName("OriginalName");
    existingDevice.setBrand("OriginalBrand");

    Map<String, Object> updates = Map.of("name", "UpdatedName");

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));

    assertThrows(InvalidOperationException.class,
        () -> deviceService.partialUpdateDevice(1L, updates));
  }

  @Test
  void partialUpdateDevice_withCreationTimeUpdate_throwsInvalidOperationException() {
    Device existingDevice = new Device();
    existingDevice.setId(1L);

    Map<String, Object> updates = Map.of("creationTime", LocalDateTime.now());

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));

    assertThrows(InvalidOperationException.class,
        () -> deviceService.partialUpdateDevice(1L, updates));
  }

  @Test
  void validateImmutablePropertiesForInUseDevice_withNameChange_throwsInvalidOperationException() {
    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setState(DeviceState.IN_USE);
    existingDevice.setName("OriginalName");
    existingDevice.setBrand("OriginalBrand");

    Device newDeviceDetails = new Device();
    newDeviceDetails.setName("ChangedName");
    newDeviceDetails.setBrand("OriginalBrand");

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));

    InvalidOperationException exception = assertThrows(InvalidOperationException.class,
        () -> deviceService.updateDevice(1L, newDeviceDetails));

    assertEquals("Cannot update name or brand of a device that is in use", exception.getMessage());
  }

  @Test
  void validateImmutablePropertiesForInUseDevice_withBrandChange_throwsInvalidOperationException() {
    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setState(DeviceState.IN_USE);
    existingDevice.setName("OriginalName");
    existingDevice.setBrand("OriginalBrand");

    Device newDeviceDetails = new Device();
    newDeviceDetails.setName("OriginalName");
    newDeviceDetails.setBrand("ChangedBrand");

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));

    InvalidOperationException exception = assertThrows(InvalidOperationException.class,
        () -> deviceService.updateDevice(1L, newDeviceDetails));

    assertEquals("Cannot update name or brand of a device that is in use", exception.getMessage());
  }

  @Test
  void validateImmutablePropertiesForInUseDevice_withBothNameAndBrandChanges_throwsInvalidOperationException() {
    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setState(DeviceState.IN_USE);
    existingDevice.setName("OriginalName");
    existingDevice.setBrand("OriginalBrand");

    Device newDeviceDetails = new Device();
    newDeviceDetails.setName("ChangedName");
    newDeviceDetails.setBrand("ChangedBrand");

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));

    InvalidOperationException exception = assertThrows(InvalidOperationException.class,
        () -> deviceService.updateDevice(1L, newDeviceDetails));

    assertEquals("Cannot update name or brand of a device that is in use", exception.getMessage());
  }

  @Test
  void validateImmutablePropertiesForInUseDevice_withNoChanges_updatesSuccessfully() {
    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setState(DeviceState.IN_USE);
    existingDevice.setName("OriginalName");
    existingDevice.setBrand("OriginalBrand");

    Device newDeviceDetails = new Device();
    newDeviceDetails.setName("OriginalName");
    newDeviceDetails.setBrand("OriginalBrand");
    newDeviceDetails.setState(DeviceState.IN_USE);

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));
    when(deviceRepository.save(any(Device.class))).thenReturn(newDeviceDetails);

    Device result = deviceService.updateDevice(1L, newDeviceDetails);

    assertEquals("OriginalName", result.getName());
    assertEquals("OriginalBrand", result.getBrand());
  }

  @Test
  void validateImmutableFieldsForInUseDevice_withNameChange_throwsInvalidOperationException() {
    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setState(DeviceState.IN_USE);
    existingDevice.setName("OriginalName");
    existingDevice.setBrand("OriginalBrand");

    Map<String, Object> updates = Map.of("name", "ChangedName");

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));

    InvalidOperationException exception = assertThrows(InvalidOperationException.class,
        () -> deviceService.partialUpdateDevice(1L, updates));

    assertEquals("Cannot update name or brand of a device that is in use", exception.getMessage());
  }

  @Test
  void validateImmutableFieldsForInUseDevice_withBrandChange_throwsInvalidOperationException() {
    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setState(DeviceState.IN_USE);
    existingDevice.setName("OriginalName");
    existingDevice.setBrand("OriginalBrand");

    Map<String, Object> updates = Map.of("brand", "ChangedBrand");

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));

    InvalidOperationException exception = assertThrows(InvalidOperationException.class,
        () -> deviceService.partialUpdateDevice(1L, updates));

    assertEquals("Cannot update name or brand of a device that is in use", exception.getMessage());
  }

  @Test
  void validateImmutableFieldsForInUseDevice_withBothNameAndBrandChanges_throwsInvalidOperationException() {
    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setState(DeviceState.IN_USE);
    existingDevice.setName("OriginalName");
    existingDevice.setBrand("OriginalBrand");

    Map<String, Object> updates = Map.of("name", "ChangedName", "brand", "ChangedBrand");

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));

    InvalidOperationException exception = assertThrows(InvalidOperationException.class,
        () -> deviceService.partialUpdateDevice(1L, updates));

    assertEquals("Cannot update name or brand of a device that is in use", exception.getMessage());
  }

  @Test
  void validateImmutableFieldsForInUseDevice_withNoImmutableChanges_updatesSuccessfully()
      throws IOException {
    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setState(DeviceState.IN_USE);
    existingDevice.setName("OriginalName");
    existingDevice.setBrand("OriginalBrand");

    Map<String, Object> updates = Map.of("state", DeviceState.AVAILABLE);

    JsonNode jsonNode = mock(JsonNode.class);
    when(objectMapper.valueToTree(updates)).thenReturn(jsonNode);

    ObjectReader objectReader = mock(ObjectReader.class);
    when(objectMapper.readerForUpdating(existingDevice)).thenReturn(objectReader);

    when(objectReader.readValue(jsonNode)).thenAnswer(invocation -> {
      existingDevice.setState(DeviceState.AVAILABLE);
      return existingDevice;
    });

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));
    when(deviceRepository.save(any(Device.class))).thenReturn(existingDevice);

    Device result = deviceService.partialUpdateDevice(1L, updates);

    assertEquals(DeviceState.AVAILABLE, result.getState());
    assertEquals("OriginalName", result.getName());
    assertEquals("OriginalBrand", result.getBrand());
  }

  @Test
  void validateImmutableFieldsForInUseDevice_withSameNameAndBrandValues_updatesSuccessfully()
      throws IOException {
    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setState(DeviceState.IN_USE);
    existingDevice.setName("OriginalName");
    existingDevice.setBrand("OriginalBrand");

    Map<String, Object> updates = Map.of("name", "OriginalName", "brand", "OriginalBrand");

    JsonNode jsonNode = mock(JsonNode.class);
    when(objectMapper.valueToTree(updates)).thenReturn(jsonNode);

    ObjectReader objectReader = mock(ObjectReader.class);
    when(objectMapper.readerForUpdating(existingDevice)).thenReturn(objectReader);

    when(objectReader.readValue(jsonNode)).thenReturn(existingDevice);

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));
    when(deviceRepository.save(any(Device.class))).thenReturn(existingDevice);

    Device result = deviceService.partialUpdateDevice(1L, updates);

    assertEquals("OriginalName", result.getName());
    assertEquals("OriginalBrand", result.getBrand());
  }

  @Test
  void partialUpdateDevice_withIOException_throwsRuntimeException() throws IOException {

    Device existingDevice = new Device();
    existingDevice.setId(1L);
    existingDevice.setState(DeviceState.AVAILABLE);
    existingDevice.setName("OriginalName");
    existingDevice.setBrand("OriginalBrand");

    Map<String, Object> updates = Map.of("name", "UpdatedName", "brand", "UpdatedBrand");

    when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));

    JsonNode jsonNode = mock(JsonNode.class);
    when(objectMapper.valueToTree(updates)).thenReturn(jsonNode);

    ObjectReader objectReader = mock(ObjectReader.class);
    when(objectMapper.readerForUpdating(existingDevice)).thenReturn(objectReader);
    when(objectReader.readValue(jsonNode)).thenThrow(new IOException("Simulated IOException"));

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> deviceService.partialUpdateDevice(1L, updates));

    assertEquals("Error processing update request", exception.getMessage());
    assertInstanceOf(IOException.class, exception.getCause());
    assertEquals("Simulated IOException", exception.getCause().getMessage());
  }
}