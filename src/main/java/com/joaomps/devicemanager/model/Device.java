package com.joaomps.devicemanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "model_devices")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Device {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Name cannot be blank")
  private String name;
  @NotBlank(message = "Brand cannot be blank")
  private String brand;

  @Enumerated(EnumType.STRING)
  private DeviceState state;

  @Column(updatable = false)
  private LocalDateTime creationTime;
}
