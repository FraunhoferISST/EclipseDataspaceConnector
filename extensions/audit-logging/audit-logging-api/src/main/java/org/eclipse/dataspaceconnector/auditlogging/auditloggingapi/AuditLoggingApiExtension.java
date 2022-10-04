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

import org.eclipse.dataspaceconnector.auditlogging.AuditLoggingManagerService;
import org.eclipse.dataspaceconnector.auditlogging.Log;
import org.eclipse.dataspaceconnector.auditlogging.auditloggingapiconfiguration.AuditLoggingApiConfiguration;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.system.*;


public class AuditLoggingApiExtension implements ServiceExtension {
    @Inject
    WebService webService;

    @Inject
    AuditLoggingApiConfiguration config;

    @Inject
    AuditLoggingManagerService logger;

    @Override
    public String name(){return "Audit Logging API";}

    @Override
    public void initialize(ServiceExtensionContext context) {
        webService.registerResource(config.getContextAlias(), new AuditLoggingApiController(logger));
    }
}