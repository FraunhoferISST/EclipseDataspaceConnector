/*
 *  Copyright (c) 2022 Fraunhofer Institute for Software and Systems Engineering
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

package org.eclipse.dataspaceconnector.auditlogging.auditloggingapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.dataspaceconnector.auditlogging.AuditLoggingManagerService;
import org.eclipse.dataspaceconnector.auditlogging.Log;
import org.eclipse.dataspaceconnector.auditlogging.auditloggingapi.model.LoggingBody;



@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Path("/auditlogging")
public class AuditLoggingApiController implements AuditLoggingApi {
    private final AuditLoggingManagerService loggingService;

    public AuditLoggingApiController(@NotNull AuditLoggingManagerService pLoggingService) {
        loggingService = pLoggingService;
    }


    @Override
    @GET
    public void getLogs(@Suspended AsyncResponse response) {
        response.resume(loggingService.getAllLogs().toString());
    }

    @Override
    @POST
    @Path("/newLogs")
    public String createNewLog(@Valid LoggingBody body) {
        loggingService.addLog(new Log(body.getSourceID(), body.getTimestamp(), body.getLogText()));
        return "Log was added";
    }
}