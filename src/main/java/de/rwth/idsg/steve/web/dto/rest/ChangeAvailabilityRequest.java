package de.rwth.idsg.steve.web.dto.rest;

import de.rwth.idsg.steve.web.dto.ocpp.AvailabilityType;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class ChangeAvailabilityRequest {

    @NotNull(message = "Availability Type is required")
    private AvailabilityType availType;
}