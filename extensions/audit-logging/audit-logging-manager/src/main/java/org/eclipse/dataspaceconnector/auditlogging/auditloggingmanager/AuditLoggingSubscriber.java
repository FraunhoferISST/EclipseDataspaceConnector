package org.eclipse.dataspaceconnector.auditlogging.auditloggingmanager;

import org.eclipse.dataspaceconnector.auditlogging.AuditLoggingManagerService;
import org.eclipse.dataspaceconnector.auditlogging.Log;
import org.eclipse.dataspaceconnector.spi.event.Event;
import org.eclipse.dataspaceconnector.spi.event.EventSubscriber;
import org.eclipse.dataspaceconnector.spi.event.asset.AssetEventPayload;
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
        if (event.getPayload() instanceof AssetEventPayload) {
            creatingLog(event);
        }



        Log log = new Log("TEST ID", "TEST Source", String.valueOf(event.getAt()), "Test Message");
        auditLoggingManagerService.addLog(log);
        auditLoggingManagerService.getAllLogs().forEach(x -> monitor.info(x.toString()));
    }

    private void creatingLog(Event event){

    }
}
