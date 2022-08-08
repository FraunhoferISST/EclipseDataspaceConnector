package org.eclipse.dataspaceconnector.auditlogging.auditloggingapiconfiguration;

import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import static java.lang.String.format;


@Provides(AuditLoggingApiConfiguration.class)
public class AuditLoggingApiConfigurationExtension implements ServiceExtension {

    public static final String WEB_LOGGING_CONFIG = "web.http.logging";
    private static final String DEFAULT_AUDITLOGGING_ALIAS = "default";

    @Inject
    private WebService webService;

    @Override
    public String name() {
        return "Audit Logging API configuration";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var contextAlias = DEFAULT_AUDITLOGGING_ALIAS;

        var monitor = context.getMonitor();

        var port = 8181;
        var path = "/logging";
        var config = context.getConfig(WEB_LOGGING_CONFIG);

        if(config.getEntries().isEmpty()){
            monitor.warning("No [web.http.logging.port] or [web.http.logging.path] configuration has been provided, therefor the default will be used. ");
        } else {
            contextAlias= "logging";
            port = config.getInteger("port",port);
            path = config.getString("path",path);
        }

        monitor.info(format("The AuditLoggingApi will be available under port=%s, path=%s", port, path));

        context.registerService(AuditLoggingApiConfiguration.class, new AuditLoggingApiConfiguration(contextAlias));
    }
}
