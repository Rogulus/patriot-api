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

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;

import java.util.UUID;

/**
 * Abstract class represents base for extension to  JUnit which allows configuration and Test environment
 * setup before the Test Run is started.
 */
public abstract class SetupExtension implements BeforeAllCallback, CloseableResource {

    /**
     * This method is started once the implementation is instantiated by Test Runner
     */
    public abstract void setUp();

    /**
     * This method is executed when Test Run is finished to clean environment
     */
    public abstract void tearDown();

    /**
     * Abstract method to obtain unique identifier of the extension
     *
     * @return unique identifier
     */
    protected abstract UUID getUUID();

    /**
     * Abstract method to signal Extension framework that setup method already run
     *
     * @return true if setup method was invoked
     */
    protected abstract boolean isSetUp();

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (!isSetUp()) {
            setUp();
            extensionContext.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(getUUID(), this);
        }
    }

    @Override
    public void close() throws Throwable {
        tearDown();
    }
}
