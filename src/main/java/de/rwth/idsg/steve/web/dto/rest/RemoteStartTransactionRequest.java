package de.rwth.idsg.steve.web.dto.rest;

import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class RemoteStartTransactionRequest {

    @NotNull(message = "OCPP tag id is required")
    private String tagId;
}