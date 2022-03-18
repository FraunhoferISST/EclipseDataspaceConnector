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

package org.eclipse.dataspaceconnector.extensions.gaiax;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;

import static java.lang.String.format;

@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/")
public class GaiaxSelfDescriptionController {

    private final Monitor monitor;
    private final GaiaxSelfDescription gaiaxSelfDescription;
    private final ObjectMapper objectMapper;

    public GaiaxSelfDescriptionController(Monitor monitor, GaiaxSelfDescription gaiaxSelfDescription) {
        this.monitor = monitor;
        this.gaiaxSelfDescription = gaiaxSelfDescription;
        this.objectMapper = new ObjectMapper();
    }
    
    @GET
    @Path("gaiax")
    public String selfDescription() {
        monitor.info("Received request for Gaia-X self description.");
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gaiaxSelfDescription);
        } catch (JsonProcessingException e) {
            return format("Error generating self description: %s", e.getMessage());
        }
    }
}
