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

import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.configuration.Config;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Requires({WebService.class})
public class GaiaxSelfDescriptionExtension implements ServiceExtension {
    private static final String GAIA_X_SELF_DESCRIPTION_CONFIG  = "edc.samples.gaiax.selfdescription";

    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor();
        
        var config = context.getConfig(GAIA_X_SELF_DESCRIPTION_CONFIG);
        
        var serviceProvider = URI.create(config.getString("serviceprovider"));
        var resources = getResources(config, monitor);
        var termsAndConditions = getTermsAndConditions(config, monitor);
        var policies = getPolicies(config, monitor);
        var selfDescription = GaiaxSelfDescription.Builder.newInstance()
                .serviceProvider(serviceProvider)
                .resources(resources)
                .termsAndConditions(termsAndConditions)
                .policies(policies)
                .build();

        var webService = context.getService(WebService.class);
        webService.registerResource(new GaiaxSelfDescriptionController(context.getMonitor(), selfDescription));
    }
    
    private List<URI> getResources(Config config, Monitor monitor) {
        var resources = new ArrayList<URI>();
        for (var entry : config.getConfig("resource").getEntries().entrySet()) {
            try {
                resources.add(URI.create(entry.getValue()));
            } catch (IllegalArgumentException e) {
                monitor.info(format("Invalid entry for resource: [(%s), (%s)]", entry.getKey(), entry.getValue()));
            }
        }
        return resources;
    }
    
    private List<URI> getTermsAndConditions(Config config, Monitor monitor) {
        var termsAndConditions = new ArrayList<URI>();
        for (var entry : config.getConfig("termsandconditions").getEntries().entrySet()) {
            try {
                termsAndConditions.add(URI.create(entry.getValue()));
            } catch (IllegalArgumentException e) {
                monitor.info(format("Invalid entry for terms and conditions: [(%s), (%s)]", entry.getKey(), entry.getValue()));
            }
        }
        return termsAndConditions;
    }
    
    private List<GaiaxPolicy> getPolicies(Config config, Monitor monitor) {
        var policies = new ArrayList<GaiaxPolicy>();
        var policiesConfig = config.getConfig("policies");
        
        var nextExists = !policiesConfig.getEntries().isEmpty();
        var counter = 0;
        while (nextExists) {
            var policyConfig = policiesConfig.getConfig(String.valueOf(counter));
            if (!policyConfig.getEntries().isEmpty()) {
                try {
                    var type = policyConfig.getString("type");
                    var filePath = policyConfig.getString("contentpath");
                    var file = new File(filePath);
                    var inputStream = new FileInputStream(file);
                    var content = new String(inputStream.readAllBytes());
                    policies.add(new GaiaxPolicy(type, content));
                    inputStream.close();
                    counter++;
                } catch (EdcException e) {
                    monitor.info(format("Failed to find required settings for policy [%s]. Error: %s", counter, e.getMessage()));
                    counter++;
                } catch (Exception e) {
                    monitor.info(format("Failed to read policy [%s] from file. Error: %s", counter, e.getMessage()));
                    counter++;
                }
            } else {
                nextExists = false;
            }
        }
        return policies;
    }
}
