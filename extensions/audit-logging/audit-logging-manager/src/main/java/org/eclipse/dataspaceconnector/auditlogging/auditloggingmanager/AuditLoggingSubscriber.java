package org.eclipse.dataspaceconnector.auditlogging.auditloggingmanager;

import org.eclipse.dataspaceconnector.auditlogging.AuditLoggingManagerService;
import org.eclipse.dataspaceconnector.auditlogging.Log;
import org.eclipse.dataspaceconnector.spi.event.Event;
import org.eclipse.dataspaceconnector.spi.event.EventSubscriber;
import org.eclipse.dataspaceconnector.spi.event.asset.AssetCreated;
import org.eclipse.dataspaceconnector.spi.event.asset.AssetDeleted;
import org.eclipse.dataspaceconnector.spi.event.contractdefinition.ContractDefinitionCreated;
import org.eclipse.dataspaceconnector.spi.event.contractdefinition.ContractDefinitionDeleted;
import org.eclipse.dataspaceconnector.spi.event.contractnegotiation.ContractNegotiationApproved;
import org.eclipse.dataspaceconnector.spi.event.policydefinition.PolicyDefinitionCreated;
import org.eclipse.dataspaceconnector.spi.event.policydefinition.PolicyDefinitionDeleted;
import org.eclipse.dataspaceconnector.spi.event.transferprocess.*;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.policy.PolicyDefinition;
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
        String message = null;

        // [AssetEvent] #EDCID created asset ASSETID
        if (event.getPayload() instanceof AssetCreated.Payload){
            message =  String.format("[AssetEvent] #EDCID created asset ASSETID");
        }

        // [AssetEvent] #EDCID deleted asset ASSETID
        if (event.getPayload() instanceof AssetDeleted.Payload){
            message =  String.format("[AssetEvent] #EDCID deleted asset ASSETID");
        }

        // [PolicyEvent] #EDCID created policy #POLICYID
        if (event.getPayload() instanceof PolicyDefinitionCreated.Payload){
            message =  String.format("[AssetEvent] #[PolicyEvent] #EDCID created policy #POLICYID");
        }
        // [PolicyEvent] #EDCID deleted policy #POLICYID
        if (event.getPayload() instanceof PolicyDefinitionDeleted.Payload){
            message =  String.format("[PolicyEvent] #EDCID deleted policy #POLICYID");
        }

        // [ContractDefinitionEvent] #EDCID created contractdefinition #CONTRACTDEFINITIONID. With this asset #ASSETID is published with accesspolicy #POLICYID and usepolicy #POLICYID.
        if (event.getPayload() instanceof ContractDefinitionCreated.Payload){
            message =  String.format("[ContractDefinitionEvent] #EDCID created contractdefinition #CONTRACTDEFINITIONID. " +
                    "With this asset #ASSETID is published with accesspolicy #POLICYID and usepolicy #POLICYID.");
        }

        // [ContractDefinitionEvent] #EDCID deleted contractdefinition #CONTRACTDEFINITIONID.
        if (event.getPayload() instanceof ContractDefinitionDeleted.Payload){
            message =  String.format("[ContractDefinitionEvent] #EDCID deleted contractdefinition #CONTRACTDEFINITIONID.");
        }

        // [ContractNegotiationEvent] #EDCIDConsumer is negotiating with #EDCIDProvider about the asset #assetId with policy #PolicyID. Status: [Approved|Confirmed|Declined|Failed|Initiated|Offered|Requested]
        //TODO: Update branch and use ContractNegotiationEventPayload
        if (event.getPayload() instanceof ContractNegotiationApproved.Payload){
            message =  String.format("[ContractNegotiationEvent] #EDCIDConsumer is negotiating with #EDCIDProvider about the asset #assetId with policy #PolicyID. Status: [Approved|Confirmed|Declined|Failed|Initiated|Offered|Requested]");
        }

        // [TransferProcessEvent] #EDCIDConsumer is transferring an asset #ASSETID from #EDCProvider. Status: [Approved|Confirmed|Declined|Failed|Initiated|Offered|Requested]
        if (event.getPayload() instanceof TransferProcessEventPayload){
            message =  String.format("[TransferProcessEvent] #EDCIDConsumer is transferring an asset #ASSETID from #EDCProvider. Status: [Approved|Confirmed|Declined|Failed|Initiated|Offered|Requested]");
        }

        monitor.info(message);

        /*
        if (event.getPayload() instanceof TransferProcessEventPayload) {
            creatingLog(event);
        }

        if (event.getPayload() instanceof AssetCreated.Payload){
            auditLoggingManagerService.addLog(new Log("TestId","EDC ID", "timeNow", "Wir testen"));
        }
*/
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
