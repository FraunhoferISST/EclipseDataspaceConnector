package org.eclipse.dataspaceconnector.auditlogging.auditloggingapiconfiguration;

public class AuditLoggingApiConfiguration {

    private final String contextAlias;

    public AuditLoggingApiConfiguration(String contextAlias) {
        this.contextAlias = contextAlias;
    }

    public String getContextAlias() {
        return contextAlias;
    }

}
