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