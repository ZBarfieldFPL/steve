package de.rwth.idsg.steve.web.dto.rest;

import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class TriggerMessageRequest {

    @NotNull(message = "Trigger message is required")
    private TriggerMessageEnum triggerMessage;
}