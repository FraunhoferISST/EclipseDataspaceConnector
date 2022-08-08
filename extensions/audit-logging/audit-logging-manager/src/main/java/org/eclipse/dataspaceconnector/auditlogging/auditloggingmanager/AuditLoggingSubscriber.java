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
        if (event instanceof TransferProcessInitiated) {
            creatingLog(event);
        }
        if (event instanceof TransferProcessCancelled) {
            creatingLog(event);
        }
        if (event instanceof TransferProcessCompleted) {
            creatingLog(event);
        }
        if (event instanceof TransferProcessDeprovisioned) {
            creatingLog(event);
        }
        if (event instanceof TransferProcessEnded) {
            creatingLog(event);
        }
        if (event instanceof TransferProcessFailed) {
            creatingLog(event);
        }
        if (event instanceof TransferProcessProvisioned) {
            creatingLog(event);
        }

        if (event instanceof TransferProcessRequested) {
            creatingLog(event);
        }


       // Log log = new Log("TEST ID", "TEST Source", String.valueOf(event.getAt()), "Test Message");
       // auditLoggingManagerService.addLog(log);
       // auditLoggingManagerService.getAllLogs().forEach(x -> monitor.info(x.toString()));
    }

    private void creatingLog(Event event){
        var process = transferProcessStore.find(((event.getPayload().getClass()) event).getPayload().getTransferProcessId());
        var dataAdress = process.getContentDataAddress();
        var dataRequest = process.getDataRequest();
        var transferprocessType = process.getType();
        var ressourceManifest = process.getResourceManifest();
        monitor.info(process.toString());
        monitor.info(dataAdress.toString());
        monitor.info(dataRequest.toString());
        monitor.info(transferprocessType.toString());
        monitor.info(ressourceManifest.toString());

        var log = new Log(event.getAt(),String.format("Transferprocess %s liegt im Status %s vor.",event.getPayload()))
        auditLoggingManagerService.addLog(log);
    }
}
