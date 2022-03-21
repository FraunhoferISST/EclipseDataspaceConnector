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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;

@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/")
public class GaiaxSelfDescriptionController {

    private final Monitor monitor;
    private final GaiaxSelfDescriptionService selfDescriptionService;

    public GaiaxSelfDescriptionController(Monitor monitor, GaiaxSelfDescriptionService selfDescriptionService) {
        this.monitor = monitor;
        this.selfDescriptionService = selfDescriptionService;
    }
    
    @GET
    @Path("gaiax")
    public GaiaxSelfDescription selfDescription() {
        monitor.info("Received request for Gaia-X self description.");
        return selfDescriptionService.getSelfDescription();
    }
}
