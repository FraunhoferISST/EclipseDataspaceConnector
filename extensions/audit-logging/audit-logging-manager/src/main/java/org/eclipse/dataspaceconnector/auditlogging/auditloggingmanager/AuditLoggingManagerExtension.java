package org.eclipse.dataspaceconnector.auditlogging.auditloggingmanager;

import org.eclipse.dataspaceconnector.auditlogging.AuditLoggingManagerService;
import org.eclipse.dataspaceconnector.spi.event.EventRouter;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transfer.store.TransferProcessStore;

@Provides({ AuditLoggingManagerService.class })
public class AuditLoggingManagerExtension implements ServiceExtension {

    public static final String DATABASE_LOGGING_CONFIG = "database.logging";
    @Inject
    private EventRouter eventRouter;

    @Inject
    private TransferProcessStore transferProcessStore;

    @Inject
    private Monitor monitor;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var auditConfig = context.getConfig(DATABASE_LOGGING_CONFIG);

        var auditManager = new AuditLoggingManagerServiceImpl(auditConfig, monitor);
        context.registerService(AuditLoggingManagerService.class, auditManager);
        eventRouter.register(new AuditLoggingSubscriber(auditManager, monitor, transferProcessStore));
    }
}