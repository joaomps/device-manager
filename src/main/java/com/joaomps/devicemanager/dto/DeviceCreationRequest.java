package com.joaomps.devicemanager.dto;

import jakarta.validation.constraints.NotBlank;

public record DeviceCreationRequest(
    @NotBlank(message = "Name cannot be blank") String name,
    @NotBlank(message = "Brand cannot be blank") String brand) {

}
