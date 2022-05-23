package org.eclipse.dataspaceconnector.extensions.workshop.policy;

import java.nio.file.Path;

import org.eclipse.dataspaceconnector.dataloading.AssetLoader;
import org.eclipse.dataspaceconnector.policy.model.*;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.contract.offer.store.ContractDefinitionStore;
import org.eclipse.dataspaceconnector.spi.policy.PolicyEngine;
import org.eclipse.dataspaceconnector.spi.policy.RuleBindingRegistry;
import org.eclipse.dataspaceconnector.spi.policy.store.PolicyStore;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;

import static org.eclipse.dataspaceconnector.spi.policy.PolicyEngine.ALL_SCOPES;

public class PolicyFunctionsExtension implements ServiceExtension {
    
    private final String policyTimeKey = "POLICY_EVALUATION_TIME";

    private final String assetPathSetting = "edc.samples.uc.asset.path";
    private final String policyStartDateSetting = "edc.samples.uc.constraint.date.start";
    private final String policyEndDateSetting = "edc.samples.uc.constraint.date.end";
    
    @Inject
    private RuleBindingRegistry ruleBindingRegistry;
    
    @Inject
    private PolicyEngine policyEngine;
    
    @Inject
    private PolicyStore policyStore;
    
    @Inject
    private ContractDefinitionStore contractStore;
    
    @Inject
    private AssetLoader loader;
    
    @Override
    public String name() {
        return "UC Workshop Policies";
    }
    
    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor();
        
        ruleBindingRegistry.bind("USE", ALL_SCOPES);
        ruleBindingRegistry.bind(policyTimeKey, ALL_SCOPES);
        policyEngine.registerFunction(ALL_SCOPES, Permission.class, policyTimeKey, new TimeIntervalFunction(monitor));
        
        registerDataOffer(context);
    }
    
    private void registerDataOffer(ServiceExtensionContext context) {
        // Asset -> offer file
        var assetPathString = context.getSetting(assetPathSetting, "/tmp/provider/test-document.txt");
        var assetPath = Path.of(assetPathString);
        var dataAddress = DataAddress.Builder.newInstance()
                .property("type", "File")
                .property("path", assetPath.getParent().toString())
                .property("filename", assetPath.getFileName().toString())
                .build();
        var assetId = "test-document";
        var asset = Asset.Builder.newInstance().id(assetId).build();
        loader.accept(asset, dataAddress);

        // Access Policy -> no constraints
        var usePermission = Permission.Builder.newInstance()
                .action(Action.Builder.newInstance().type("USE").build())
                .build();
        var accessPolicy = Policy.Builder.newInstance()
                .id("use")
                .permission(usePermission)
                .build();
        policyStore.save(accessPolicy);

        // Contract Policy -> constraint: only within a certain time interval
        var startDate = context.getSetting(policyStartDateSetting, "2022-01-01T00:00:00.000+02:00");
        var notBeforeConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression(policyTimeKey))
                .operator(Operator.GT)
                .rightExpression(new LiteralExpression(startDate))
                .build();

        var endDate = context.getSetting(policyEndDateSetting, "2022-12-31T23:59:00.000+02:00");
        var notAfterConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression(policyTimeKey))
                .operator(Operator.LT)
                .rightExpression(new LiteralExpression(endDate))
                .build();

        var timeRestrictedPermission = Permission.Builder.newInstance()
                .action(Action.Builder.newInstance().type("USE").build())
                .constraint(notBeforeConstraint)
                .constraint(notAfterConstraint)
                .build();
        var contractPolicy = Policy.Builder.newInstance()
                .id("use-time-restricted")
                .permission(timeRestrictedPermission)
                .build();
        policyStore.save(contractPolicy);
        
        // Contract definition -> use access and contract policy defined above
        var contractDefinition = ContractDefinition.Builder.newInstance()
                .id("1")
                .accessPolicyId(accessPolicy.getUid())
                .contractPolicyId(contractPolicy.getUid())
                .selectorExpression(AssetSelectorExpression.Builder.newInstance()
                        .whenEquals(Asset.PROPERTY_ID, "test-document")
                        .build())
                .build();
        contractStore.save(contractDefinition);
    }
    
}
