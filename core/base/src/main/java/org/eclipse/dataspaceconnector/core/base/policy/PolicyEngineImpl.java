/*
 *  Copyright (c) 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *       Fraunhofer Institute for Software and Systems Engineering - resource manifest evaluation
 *
 */

package org.eclipse.dataspaceconnector.core.base.policy;

import org.eclipse.dataspaceconnector.policy.engine.PolicyEvaluator;
import org.eclipse.dataspaceconnector.policy.engine.RuleProblem;
import org.eclipse.dataspaceconnector.policy.model.Duty;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.policy.model.Prohibition;
import org.eclipse.dataspaceconnector.policy.model.Rule;
import org.eclipse.dataspaceconnector.spi.agent.ParticipantAgent;
import org.eclipse.dataspaceconnector.spi.policy.evaluation.AtomicConstraintFunction;
import org.eclipse.dataspaceconnector.spi.policy.PolicyContext;
import org.eclipse.dataspaceconnector.spi.policy.PolicyEngine;
import org.eclipse.dataspaceconnector.spi.policy.evaluation.ResourceDefinitionAtomicConstraintFunction;
import org.eclipse.dataspaceconnector.spi.policy.evaluation.ResourceDefinitionRuleFunction;
import org.eclipse.dataspaceconnector.spi.policy.evaluation.RuleFunction;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.ResourceDefinition;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.ResourceManifest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toList;

/**
 * Default implementation of the policy engine.
 */
public class PolicyEngineImpl implements PolicyEngine {
    private static final String ALL_SCOPES_DELIMITED = ALL_SCOPES + ".";

    private ScopeFilter scopeFilter;

    private Map<String, List<ConstraintFunctionEntry<Rule>>> constraintFunctions = new TreeMap<>();
    private Map<String, List<RuleFunctionEntry<Rule>>> ruleFunctions = new TreeMap<>();
    private Map<String, List<ResourceDefinitionRuleFunctionEntry<Rule, ResourceDefinition>>> resourceDefinitionRuleFunctions = new TreeMap<>();
    private Map<String, List<ResourceDefinitionConstraintFunctionEntry<Rule, ResourceDefinition>>> resourceDefinitionConstraintFunctions = new TreeMap<>();

    private List<BiFunction<Policy, PolicyContext, Boolean>> preValidators = new ArrayList<>();
    private List<BiFunction<Policy, PolicyContext, Boolean>> postValidators = new ArrayList<>();

    public PolicyEngineImpl(ScopeFilter scopeFilter) {
        this.scopeFilter = scopeFilter;
    }

    @Override
    public Policy filter(Policy policy, String scope) {
        return scopeFilter.applyScope(policy, scope);
    }

    @Override
    public Result<Policy> evaluate(String scope, Policy policy, ParticipantAgent agent) {
        var context = new PolicyContextImpl(agent);

        for (BiFunction<Policy, PolicyContext, Boolean> validator : preValidators) {
            if (!validator.apply(policy, context)) {
                return Result.failure(context.hasProblems() ? context.getProblems() : List.of("Pre-validator failed: " + validator.getClass().getName()));
            }
        }

        var evalBuilder = PolicyEvaluator.Builder.newInstance();

        final var delimitedScope = scope + ".";

        ruleFunctions.entrySet().stream().filter(entry -> scopeFilter(entry.getKey(), delimitedScope)).flatMap(entry -> entry.getValue().stream()).forEach(entry -> {
            if (Duty.class.isAssignableFrom(entry.type)) {
                evalBuilder.dutyRuleFunction((rule) -> entry.function.evaluate(rule, context));
            } else if (Permission.class.isAssignableFrom(entry.type)) {
                evalBuilder.permissionRuleFunction((rule) -> entry.function.evaluate(rule, context));
            } else if (Prohibition.class.isAssignableFrom(entry.type)) {
                evalBuilder.prohibitionRuleFunction((rule) -> entry.function.evaluate(rule, context));
            }
        });

        constraintFunctions.entrySet().stream().filter(entry -> scopeFilter(entry.getKey(), delimitedScope)).flatMap(entry -> entry.getValue().stream()).forEach(entry -> {
            if (Duty.class.isAssignableFrom(entry.type)) {
                evalBuilder.dutyFunction(entry.key, (operator, value, duty) -> entry.function.evaluate(operator, value, duty, context));
            } else if (Permission.class.isAssignableFrom(entry.type)) {
                evalBuilder.permissionFunction(entry.key, (operator, value, permission) -> entry.function.evaluate(operator, value, permission, context));
            } else if (Prohibition.class.isAssignableFrom(entry.type)) {
                evalBuilder.prohibitionFunction(entry.key, (operator, value, prohibition) -> entry.function.evaluate(operator, value, prohibition, context));
            }
        });

        var evaluator = evalBuilder.build();

        var filteredPolicy = scopeFilter.applyScope(policy, scope);

        var result = evaluator.evaluate(filteredPolicy);

        if (result.valid()) {
            for (BiFunction<Policy, PolicyContext, Boolean> validator : postValidators) {
                if (!validator.apply(policy, context)) {
                    return Result.failure(context.hasProblems() ? context.getProblems() : List.of("Post-validator failed: " + validator.getClass().getName()));
                }
            }
            return Result.success(policy);
        } else {
            return Result.failure(result.getProblems().stream().map(RuleProblem::getDescription).collect(toList()));
        }
    }
    
    @Override
    public Result<ResourceManifest> evaluate(String scope, Policy policy, ResourceManifest resourceManifest) {
        var evalBuilder = PolicyEvaluator.Builder.newInstance();
        final var delimitedScope = scope + ".";
        
        resourceDefinitionRuleFunctions.entrySet().stream()
                .filter(entry -> scopeFilter(entry.getKey(), delimitedScope))
                .flatMap(entry -> entry.getValue().stream()).forEach(entry -> {
                    if (Duty.class.isAssignableFrom(entry.ruleType)) {
                        evalBuilder.resourceDefinitionDutyFunction(entry.resourceType, (duty, definition) -> entry.function.evaluate(duty, definition));
                    } else if (Permission.class.isAssignableFrom(entry.ruleType)) {
                        evalBuilder.resourceDefinitionPermissionFunction(entry.resourceType, (permission, definition) -> entry.function.evaluate(permission, definition));
                    } else if (Prohibition.class.isAssignableFrom(entry.ruleType)) {
                        evalBuilder.resourceDefinitionProhibitionFunction(entry.resourceType, (prohibition, definition) -> entry.function.evaluate(prohibition, definition));
                    }
        });
        
        resourceDefinitionConstraintFunctions.entrySet().stream()
                .filter(entry -> scopeFilter(entry.getKey(), delimitedScope))
                .flatMap(entry -> entry.getValue().stream()).forEach(entry -> {
                    if (Duty.class.isAssignableFrom(entry.ruleType)) {
                        evalBuilder.resourceDefinitionConstraintDutyFunction(entry.key, entry.resourceType,
                                (operator, rightValue, duty, definition) -> entry.function.evaluate(operator, rightValue, duty, definition));
                    } else if (Permission.class.isAssignableFrom(entry.ruleType)) {
                        evalBuilder.resourceDefinitionConstraintPermissionFunction(entry.key, entry.resourceType,
                                (operator, rightValue, permission, definition) -> entry.function.evaluate(operator, rightValue, permission, definition));
                    } else if (Prohibition.class.isAssignableFrom(entry.ruleType)) {
                        evalBuilder.resourceDefinitionConstraintProhibitionFunction(entry.key, entry.resourceType,
                                (operator, rightValue, prohibition, definition) -> entry.function.evaluate(operator, rightValue, prohibition, definition));
                    }
        });
    
        var evaluator = evalBuilder.build();
    
        var filteredPolicy = scopeFilter.applyScope(policy, scope);
        
        return evaluator.evaluateManifest(resourceManifest, filteredPolicy);
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <R extends Rule> void registerFunction(String scope, Class<R> type, String key, AtomicConstraintFunction<R> function) {
        constraintFunctions.computeIfAbsent(scope + ".", k -> new ArrayList<>()).add(new ConstraintFunctionEntry(type, key, function));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <R extends Rule> void registerFunction(String scope, Class<R> type, RuleFunction<R> function) {
        ruleFunctions.computeIfAbsent(scope + ".", k -> new ArrayList<>()).add(new RuleFunctionEntry(type, function));
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <R extends Rule, D extends ResourceDefinition> void registerFunction(String scope, Class<R> ruleType, Class<D> resourceDefinitionType,
                                                                                String key, ResourceDefinitionAtomicConstraintFunction<R, D> function) {
        resourceDefinitionConstraintFunctions.computeIfAbsent(scope + ".", k -> new ArrayList<>())
                .add(new ResourceDefinitionConstraintFunctionEntry(ruleType, resourceDefinitionType, key, function));
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <R extends Rule, D extends ResourceDefinition> void registerFunction(String scope, Class<R> ruleType, Class<D> resourceDefinitionType,
                                                                                ResourceDefinitionRuleFunction<R, D> function) {
        resourceDefinitionRuleFunctions.computeIfAbsent(scope + ".", k -> new ArrayList<>())
                .add(new ResourceDefinitionRuleFunctionEntry(ruleType, resourceDefinitionType, function));
    }
    
    @Override
    public void registerPreValidator(String scope, BiFunction<Policy, PolicyContext, Boolean> validator) {
        preValidators.add(validator);
    }

    @Override
    public void registerPostValidator(String scope, BiFunction<Policy, PolicyContext, Boolean> validator) {
        postValidators.add(validator);
    }

    private boolean scopeFilter(String entry, String scope) {
        return ALL_SCOPES_DELIMITED.equals(entry) || scope.startsWith(entry);
    }

    private static class ConstraintFunctionEntry<R extends Rule> {
        Class<R> type;
        String key;
        AtomicConstraintFunction<R> function;

        ConstraintFunctionEntry(Class<R> type, String key, AtomicConstraintFunction<R> function) {
            this.type = type;
            this.key = key;
            this.function = function;
        }
    }

    private static class RuleFunctionEntry<R extends Rule> {
        Class<R> type;
        RuleFunction<R> function;

        RuleFunctionEntry(Class<R> type, RuleFunction<R> function) {
            this.type = type;
            this.function = function;
        }
    }
    
    private static class ResourceDefinitionRuleFunctionEntry<R extends Rule, D extends ResourceDefinition> {
        Class<R> ruleType;
        Class<D> resourceType;
        ResourceDefinitionRuleFunction<R, D> function;
        
        ResourceDefinitionRuleFunctionEntry(Class<R> ruleType, Class<D> resourceType, ResourceDefinitionRuleFunction<R, D> function) {
            this.ruleType = ruleType;
            this.resourceType = resourceType;
            this.function = function;
        }
    }
    
    private static class ResourceDefinitionConstraintFunctionEntry<R extends Rule, D extends ResourceDefinition> {
        Class<R> ruleType;
        Class<D> resourceType;
        String key;
        ResourceDefinitionAtomicConstraintFunction<R, D> function;
        
        ResourceDefinitionConstraintFunctionEntry(Class<R> ruleType, Class<D> resourceType, String key, ResourceDefinitionAtomicConstraintFunction<R, D> function) {
            this.ruleType = ruleType;
            this.resourceType = resourceType;
            this.key = key;
            this.function = function;
        }
    }

}
