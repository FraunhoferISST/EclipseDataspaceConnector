# Configuration

---

**Please note**: Work in progress. All content reflects the current state of discussion, not final decisions.

---

It is possible to configure the Eclipse Dataspace Connector. All configurations are retrieved by 
their key from an extension, that implements the `ConfigurationExtension` interface.

Whether this configuration is applied at start-up or runtime of the application may vary for each
configuration extension.

## Create new Setting

Settings are identified by their key. They are retrieved from the `ServiceExtensionContext` 
interface. When creating new settings, please follow the **best practices** listed below.

- setting keys start with `edc`
- setting keys are defined in `private static final` fields
- setting fields are annotated with the `@EdcSetting` marker interface
- settings have a valid default value

Example:

```java
class ExampleSetting {

    @EdcSetting
    private final static String EDC_IDS_TITLE = "edc.ids.title";

    private final static String DEFAULT_TITLE = "Default Title";

    private String getTitle(ServiceExtensionContext context) {
        return context.getSetting(EDC_IDS_TITLE, DEFAULT_TITLE);
    }

}
```

## Configuration Extension

The integration of each configuration extension may vary. Please have a look at their corresponding
`README.md` files.

The following extensions implement the `ConfigurationExtension` interface.

- [File System Configuration](../../extensions/filesystem/configuration-fs/README.md)
