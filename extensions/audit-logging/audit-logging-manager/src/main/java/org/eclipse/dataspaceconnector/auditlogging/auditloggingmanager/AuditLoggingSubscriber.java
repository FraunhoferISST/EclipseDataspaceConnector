package org.eclipse.dataspaceconnector.auditlogging.auditloggingmanager;

import org.eclipse.dataspaceconnector.auditlogging.AuditLoggingManagerService;
import org.eclipse.dataspaceconnector.auditlogging.Log;
import org.eclipse.dataspaceconnector.spi.event.Event;
import org.eclipse.dataspaceconnector.spi.event.EventSubscriber;
import org.eclipse.dataspaceconnector.spi.event.transferprocess.*;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.transfer.store.TransferProcessStore;


public class AuditLoggingSubscriber implements EventSubscriber {

    private AuditLoggingManagerService auditLoggingManagerService;
    private Monitor monitor;


    private TransferProcessStore transferProcessStore;

    public AuditLoggingSubscriber(AuditLoggingManagerService auditLoggingManagerService, Monitor monitor, TransferProcessStore transferProcessStore) {
        this.auditLoggingManagerService = auditLoggingManagerService;
        this.monitor = monitor;
        this.transferProcessStore = transferProcessStore;
    }

    @Override
    public void on(Event<?> event) {
        if (event.getPayload() instanceof TransferProcessEventPayload) {
            creatingLog(event);
        }
    }

    private void creatingLog(Event event){
        var eventPayload = (TransferProcessEventPayload) event.getPayload();
        var transfer = transferProcessStore.find(eventPayload.getTransferProcessId());
        var request = transfer.getDataRequest();
        var message = String.format("Das Asset %s wird vom Dataprovider %s:%s zu dem DatenConsumer %s mit Hilfe der Dataplane versendet. Aktueller Dataplanestatus: %s",
                transfer.getDataRequest().getAssetId(),
                request.getConnectorId(),
                request.getConnectorAddress(),
                request.getDataDestination().getProperties().get("baseUrl"),
                event.getClass().getSimpleName()
        );
        Log log = new Log(transfer.getDataRequest().getAssetId(), "EDC ID", String.valueOf(event.getAt()), message);
        auditLoggingManagerService.addLog(log);
    }
}
