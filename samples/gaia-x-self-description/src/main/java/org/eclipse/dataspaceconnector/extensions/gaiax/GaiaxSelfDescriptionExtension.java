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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.contract.offer.store.ContractDefinitionStore;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.configuration.Config;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Requires({WebService.class})
public class GaiaxSelfDescriptionExtension implements ServiceExtension {
    private static final String GAIA_X_SELF_DESCRIPTION_CONFIG  = "edc.samples.gaiax.selfdescription";
    
    @Inject
    private ContractDefinitionStore contractStore;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor();
        
        var gaiaxConfig = context.getConfig(GAIA_X_SELF_DESCRIPTION_CONFIG);
        
        readAndStoreContractDefinitions(monitor, gaiaxConfig);
        
        var selfDescriptionService = new GaiaxSelfDescriptionService(monitor, gaiaxConfig, contractStore);

        var webService = context.getService(WebService.class);
        webService.registerResource(new GaiaxSelfDescriptionController(context.getMonitor(), selfDescriptionService));
    }
    
    private void readAndStoreContractDefinitions(Monitor monitor, Config config) {
        try (var inputStream = new FileInputStream(config.getString("contractdefinitions"))) {
            var fileContent = new String(inputStream.readAllBytes());
            var contractDefinitions = new ObjectMapper()
                    .readValue(fileContent, new TypeReference<List<ContractDefinition>>(){});
            contractDefinitions.forEach(contractStore::save);
        } catch (EdcException e) {
            monitor.info("No file with contract definitions supplied. Ignoring.");
        } catch (IOException e) {
            monitor.warning("Failed to read contract definitions from file: " + e.getMessage());
        }
    }
}
