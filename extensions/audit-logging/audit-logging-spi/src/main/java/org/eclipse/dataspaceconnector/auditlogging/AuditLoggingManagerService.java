package org.eclipse.dataspaceconnector.auditlogging;

import java.util.List;


public interface AuditLoggingManagerService {

    void addLog(Log log);

    List<Log> getAllLogs();

}