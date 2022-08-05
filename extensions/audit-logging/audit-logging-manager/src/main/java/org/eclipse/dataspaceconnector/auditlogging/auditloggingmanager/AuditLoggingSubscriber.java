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
            var process = transferProcessStore.find(((TransferProcessInitiated) event).getPayload().getTransferProcessId());
            monitor.info(process.toString());
        }
        if (event instanceof TransferProcessCancelled) {
            var process = transferProcessStore.find(((TransferProcessCancelled) event).getPayload().getTransferProcessId());
            monitor.info(process.toString());
        }
        if (event instanceof TransferProcessCompleted) {
            var process = transferProcessStore.find(((TransferProcessCompleted) event).getPayload().getTransferProcessId());
            monitor.info(process.toString());
        }
        if (event instanceof TransferProcessDeprovisioned) {
            var process = transferProcessStore.find(((TransferProcessDeprovisioned) event).getPayload().getTransferProcessId());
            monitor.info(process.toString());
        }
        if (event instanceof TransferProcessEnded) {
            var process = transferProcessStore.find(((TransferProcessEnded) event).getPayload().getTransferProcessId());
            monitor.info(process.toString());
        }
        if (event instanceof TransferProcessFailed) {
            var process = transferProcessStore.find(((TransferProcessFailed) event).getPayload().getTransferProcessId());
            monitor.info(process.toString());
        }
        if (event instanceof TransferProcessProvisioned) {
            var process = transferProcessStore.find(((TransferProcessProvisioned) event).getPayload().getTransferProcessId());
            monitor.info(process.toString());
        }

        if (event instanceof TransferProcessRequested) {
            var process = transferProcessStore.find(((TransferProcessRequested) event).getPayload().getTransferProcessId());
            monitor.info(process.toString());
        }


        Log log = new Log("TEST ID", "TEST Source", String.valueOf(event.getAt()), "Test Message");
        auditLoggingManagerService.addLog(log);
        auditLoggingManagerService.getAllLogs().forEach(x -> monitor.info(x.toString()));
    }
}
