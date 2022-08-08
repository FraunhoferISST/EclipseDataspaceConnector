package org.eclipse.dataspaceconnector.auditlogging.auditloggingapi;

import jakarta.validation.Valid;
import jakarta.ws.rs.container.AsyncResponse;
import org.eclipse.dataspaceconnector.auditlogging.auditloggingapi.model.LoggingBody;

public interface AuditLoggingApi {
    void getLogs(AsyncResponse response);

    String createNewLog(@Valid LoggingBody body);
}
