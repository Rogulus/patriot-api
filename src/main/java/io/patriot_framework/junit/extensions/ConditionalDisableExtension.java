/*
 * Copyright 2019 Patriot project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.patriot_framework.junit.extensions;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

/**
 * Extension implements the ExecutionCondition base class and enables test
 * skip on failure of any dependent tests (by annotation)
 */
public class ConditionalDisableExtension implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        Optional<AnnotatedElement> annotation = context.getElement();
        Optional<DisableByState> disabledCondition = findAnnotation(annotation, DisableByState.class);
        if (!disabledCondition.isPresent()) {
            return ConditionEvaluationResult.enabled("Not annotated");
        }

        DisableByState cond = disabledCondition.get();
        if (TestResultRegistry.getState(cond.value()) == cond.requiredState()) {
            return ConditionEvaluationResult.disabled("Disabled when failed: The condition was met");
        }

        return ConditionEvaluationResult.enabled("Condition was not met");
    }
}
