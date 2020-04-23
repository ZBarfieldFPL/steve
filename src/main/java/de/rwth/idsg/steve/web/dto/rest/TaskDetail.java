package de.rwth.idsg.steve.web.dto.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.rwth.idsg.steve.ocpp.RequestResult;
import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.Map;


@Data
@Builder
public class TaskDetail {

    private final String operationName;

    private final Map<String, RequestResult> resultMap;

    // https://stackoverflow.com/questions/31627992/spring-data-jpa-zoneddatetime-format-for-json-serialization
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSz")
    private final DateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSz")
    private final DateTime endDate;

    private final int errorCount;

    private final int responseCount;
}