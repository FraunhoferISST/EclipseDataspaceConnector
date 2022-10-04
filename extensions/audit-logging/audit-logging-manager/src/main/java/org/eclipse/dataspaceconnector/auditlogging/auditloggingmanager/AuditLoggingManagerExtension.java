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

package org.eclipse.dataspaceconnector.auditlogging.auditloggingmanager;

import org.eclipse.dataspaceconnector.auditlogging.AuditLoggingManagerService;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.store.ContractNegotiationStore;
import org.eclipse.dataspaceconnector.spi.contract.offer.store.ContractDefinitionStore;
import org.eclipse.dataspaceconnector.spi.event.EventRouter;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.policy.store.PolicyDefinitionStore;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transfer.store.TransferProcessStore;

@Provides({ AuditLoggingManagerService.class })
public class AuditLoggingManagerExtension implements ServiceExtension {

    public static final String ELASTICSEARCH_LOGGING_CONFIG = "elasticsearch.logging";
    @Inject
    private EventRouter eventRouter;

    @Inject
    private TransferProcessStore transferProcessStore;

    @Inject
    private ContractNegotiationStore contractNegotiationStore;

    @Inject
    private ContractDefinitionStore contractDefinitionStore;

    @Inject
    private PolicyDefinitionStore policyDefinitionStore;

    @Inject
    private Monitor monitor;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var auditConfig = context.getConfig(ELASTICSEARCH_LOGGING_CONFIG);

        var auditManager = new AuditLoggingManagerServiceImpl(auditConfig, monitor);
        context.registerService(AuditLoggingManagerService.class, auditManager);
        eventRouter.register(new AuditLoggingSubscriber(auditManager, monitor, transferProcessStore,contractDefinitionStore,contractNegotiationStore,policyDefinitionStore, context.getConfig()));
    }
}