package org.eclipse.dataspaceconnector.auditlogging.auditloggingmanager;

import io.codenotary.immudb4j.ImmuClient;
import io.codenotary.immudb4j.exceptions.CorruptedDataException;
import io.codenotary.immudb4j.exceptions.KeyNotFoundException;
import io.codenotary.immudb4j.exceptions.VerificationException;
import org.eclipse.dataspaceconnector.auditlogging.AuditLoggingManagerService;
import org.eclipse.dataspaceconnector.auditlogging.Log;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.configuration.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLoggingManagerServiceImpl implements AuditLoggingManagerService {
    private ImmuClient immuClient;
    private Config databaseConfig;
    private Monitor monitor;

    public AuditLoggingManagerServiceImpl(Config conf, Monitor monitor) {
        this.databaseConfig = conf;
        this.immuClient = ImmuClient.newBuilder()
                .withServerUrl(databaseConfig.getString("url"))
                .withServerPort(databaseConfig.getInteger("port"))
                .build();
        this.monitor = monitor;
    }

    @Override
    public void addLog(Log log) {
        immuClient.login(databaseConfig.getString("user"), databaseConfig.getString("userpassword"));
        if (!immuClient.databases().contains(databaseConfig.getString("db"))) {
            immuClient.createDatabase(databaseConfig.getString("db"));
        }

        immuClient.useDatabase(databaseConfig.getString("db"));

        try {
            immuClient.set(log.getUid(), logToByte(log));
        } catch (CorruptedDataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Log> getAllLogs() {
        try {
            List<Log> returnListLogs = new ArrayList<>();
            var listLogs = immuClient.scan("");
            listLogs.forEach(x -> {
                returnListLogs.add(byteToLog(x.getValue()));
                monitor.info(byteToLog(x.getValue()).toString());
            });
            return returnListLogs;
        } catch (KeyNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] logToByte(Log log) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(log);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Log byteToLog(byte[] pByte) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(pByte);
            ObjectInputStream in = new ObjectInputStream(bis);

            return (Log) in.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
