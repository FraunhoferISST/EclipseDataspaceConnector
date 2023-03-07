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

package org.eclipse.edc.protocol.dsp.controlplane;

import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.validation.ContractValidationService;
import org.eclipse.edc.connector.service.dataaddress.DataAddressValidatorImpl;
import org.eclipse.edc.connector.service.transferprocess.TransferProcessServiceImpl;
import org.eclipse.edc.connector.transfer.spi.TransferProcessManager;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.protocol.dsp.api.configuration.DspApiConfiguration;
import org.eclipse.edc.protocol.dsp.controlplane.controller.TransferProcessController;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.dataaddress.DataAddressValidator;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.edc.web.spi.WebService;

public class DspControlPlaneExtension implements ServiceExtension {

    @Inject
    private TransferProcessStore transferProcessStore;

    @Inject
    private TransferProcessManager transferProcessManager;

    @Inject
    private TransactionContext transactionContext;

    @Inject
    private ContractNegotiationStore contractNegotiationStore;

    @Inject
    private ContractValidationService contractValidationService;

    private final DataAddressValidator dataAddressValidator = new DataAddressValidatorImpl();

    @Inject
    private TypeManager typeManager;

    @Inject
    private DspApiConfiguration apiConfiguration;

    @Inject
    private WebService webService;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var transferProcessService = new TransferProcessServiceImpl(transferProcessStore, transferProcessManager, transactionContext,
                contractNegotiationStore, contractValidationService, dataAddressValidator);
        var transferProcessController = new TransferProcessController(context.getMonitor(), transferProcessService, typeManager);
        webService.registerResource(apiConfiguration.getContextAlias(), transferProcessController);
    }
}
