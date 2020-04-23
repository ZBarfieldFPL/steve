package de.rwth.idsg.steve.web.controller.rest;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.ChargePointService16_Client;
import de.rwth.idsg.steve.web.dto.ocpp.*;
import de.rwth.idsg.steve.web.dto.rest.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static de.rwth.idsg.steve.web.dto.rest.CreateTaskRequest.TaskType.*;


@RestController
@RequestMapping("/api/tasks")
public class TaskRestController {
    @Autowired
    @Qualifier("ChargePointService16_Client")
    private ChargePointService16_Client client16;

    @Autowired
    private TaskStore taskStore;

    private Map<CreateTaskRequest.TaskType, Function<CreateTaskRequest, Integer>> taskProcessors = new HashMap<>();
    {
        taskProcessors.put(CHANGE_AVAILABILITY, this::changeAvailability);
        taskProcessors.put(TRIGGER_MESSAGE, this::triggerMessage);
        taskProcessors.put(REMOTE_START_TRANSACTION, this::remoteStartTransaction);
        taskProcessors.put(REMOTE_STOP_TRANSACTION, this::remoteStopTransaction);
    }

    @PostMapping
    public ResponseEntity<Integer> createTask(@RequestBody CreateTaskRequest request) {

        final Function<CreateTaskRequest, Integer> processor = taskProcessors.get(request.getType());

        if (processor != null) {
            return ResponseEntity.ok(processor.apply(request));
        }

        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDetail> getTask(@PathVariable int taskId) {
        final CommunicationTask task = taskStore.get(taskId);

        return ResponseEntity.ok(TaskDetail.builder()
                .operationName(task.getOperationName())
                .resultMap(task.getResultMap())
                .startDate(task.getStartTimestamp())
                .endDate(task.getEndTimestamp())
                .errorCount(task.getErrorCount().get())
                .responseCount(task.getErrorCount().get())
                .build());
    }

    private void fillChargePoint(CreateTaskRequest request, MultipleChargePointSelect params) {
        params.setChargePointSelectList(Collections.singletonList(
                new ChargePointSelect(OcppTransport.JSON, request.getChargeBoxId())));
    }

    private void fillChargePoint(CreateTaskRequest request, SingleChargePointSelect params) {
        params.setChargePointSelectList(Collections.singletonList(
                new ChargePointSelect(OcppTransport.JSON, request.getChargeBoxId())));
    }

    private Integer changeAvailability(CreateTaskRequest request) {
        final ChangeAvailabilityRequest changeAvailability = request.getChangeAvailability();
        final ChangeAvailabilityParams params = new ChangeAvailabilityParams();

        Assert.notNull(changeAvailability, "Required parameter changeAvailability missing.");

        params.setConnectorId(request.getConnectorId());
        params.setAvailType(changeAvailability.getAvailType());
        fillChargePoint(request, params);

        return client16.changeAvailability(params);
    }

    private Integer triggerMessage(CreateTaskRequest request) {
        final TriggerMessageRequest triggerMessage = request.getTriggerMessage();
        final TriggerMessageParams params = new TriggerMessageParams();

        Assert.notNull(triggerMessage, "Required parameter triggerMessage missing.");

        params.setConnectorId(request.getConnectorId());
        params.setTriggerMessage(triggerMessage.getTriggerMessage());
        fillChargePoint(request, params);

        return client16.triggerMessage(params);
    }

    private Integer remoteStartTransaction(CreateTaskRequest request) {
        final RemoteStartTransactionRequest remoteStartTransaction = request.getRemoteStartTransaction();
        final RemoteStartTransactionParams params = new RemoteStartTransactionParams();

        Assert.notNull(remoteStartTransaction, "Required parameter remoteStartTransaction missing.");

        params.setConnectorId(request.getConnectorId());
        params.setIdTag(remoteStartTransaction.getTagId());
        fillChargePoint(request, params);

        return client16.remoteStartTransaction(params);
    }

    private Integer remoteStopTransaction(CreateTaskRequest request) {
        final RemoteStopTransactionRequest remoteStopTransaction = request.getRemoteStopTransaction();
        final RemoteStopTransactionParams params = new RemoteStopTransactionParams();

        Assert.notNull(remoteStopTransaction, "Required parameter remoteStopTransaction missing.");

        params.setTransactionId(remoteStopTransaction.getTransactionId());
        fillChargePoint(request, params);

        return client16.remoteStopTransaction(params);
    }
}