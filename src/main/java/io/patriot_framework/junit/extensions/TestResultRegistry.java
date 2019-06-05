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

import java.util.HashMap;
import java.util.Map;


/**
 * Class implements simple registry for Test Class results.
 * It is used by ConditionalDisableExtension, which controls
 * the state of Test Class result to disable concrete Test
 */
class TestResultRegistry {
    private static Map<Class, TestResultState> results = new HashMap<>();
    private static boolean stopExecution = false;
    private static Class breakingClass;

    /**
     * Checks which what is recorded state for Test Class
     *
     * @param c Test Class to be checked
     * @return state of test class
     */
    public static TestResultState getState(Class c) {
        return results.get(c);
    }

    /**
     * Method to store or update current state of Test Class
     * <p>
     * Method evaluates cases in order SUCCESS, ABORTED, DISABLED, FAILED
     * where the last has the highest priority.
     *
     * @param c  class to be associated with test result
     * @param st state of Test class
     */
    public static void putState(Class c, TestResultState st) {
        if (results.containsKey(c)) {
            TestResultState oldState = results.get(c);
            switch (oldState) {
                case SUCCESS:
                    results.put(c, st);
                    return;
                case ABORTED:
                case DISABLED:
                    if (st != TestResultState.SUCCESS) {
                        results.put(c, st);
                    }
                case FAILED:
                    return;
            }
        }
        results.put(c, st);
    }

    /**
     * Method sets the stop state for all other tests
     *
     * @param clazz Class which failure stops the execution
     */
    public static void setBreakPoint(Class clazz) {
        stopExecution = true;
        breakingClass = clazz;
    }

    /**
     * Check if execution should be stopped.
     *
     * @return true if execution is stopped, false otherwise
     */
    public static boolean stoppedExecution() {
        return stopExecution;
    }

    /**
     * Get the test class which stopped execution
     *
     * @return class which failed
     */
    public static Class getBreakingClass() {
        return breakingClass;
    }
}
