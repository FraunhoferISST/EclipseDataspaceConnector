/*
 *  Copyright (c) 2023 Fraunhofer Institute for Software and Systems Engineering
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer Institute for Software and Systems Engineering - initial API and implementation
 *
 */

package org.eclipse.edc.protocol.dsp.controlplane.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;


import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.types.TypeManager;

import java.util.HashMap;
import java.util.Map;

@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/transfer-processes")
public class TransferProcessController {

    private Monitor monitor;

    private TransferProcessService transferProcessService;

    private ObjectMapper mapper;

    public TransferProcessController(Monitor monitor, TransferProcessService transferProcessService, TypeManager typeManager) {
        this.monitor = monitor;
        this.transferProcessService = transferProcessService;
        this.mapper = typeManager.getMapper("json-ld"); //TODO Use correct mapper
    }

    @GET
    @Path("/{id}")
    public Map<String, Object> getTransferProcess(@PathParam("id") String id) {
        var transferProcess = transferProcessService.findById(id);
        return mapper.convertValue(transferProcess,Map.class); //TODO Check if is works correct
    }

    @POST
    @Path("/request")
    public Map<String, Object> initiateTransferProcess(JsonObject jsonObject) {
        return new HashMap<>();
    }

    @POST
    @Path("/{id}/start")
    public void consumerTransferProcessStart(JsonObject jsonObject) {
    }

    @POST
    @Path("/{id}/completion")
    public void consumerTransferProcessCompletion(JsonObject jsonObject) {
    }

    @POST
    @Path("/{id}/termination")
    public void consumerTransferProcessTermination(JsonObject jsonObject) {
    }

    @POST
    @Path("/{id}/suspension")
    public void consumerTransferProcessSuspension(JsonObject jsonObject) {

    }

}
