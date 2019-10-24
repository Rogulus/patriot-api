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

package io.patriot_framework.network.simulator.api.model.devices;

import io.patriot_framework.network.simulator.api.model.EnvironmentPart;
import io.patriot_framework.network.simulator.api.model.network.Network;

import java.util.List;

/**
 * Interface extends EnvironmentPart. Could be used for implementation of
 * application, router or any other device that should have some management access.
 */
public interface Device extends EnvironmentPart {
    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets ip address.
     *
     * @return the ip address
     */
    String getIPAddress();

    /**
     * Sets ip address.
     *
     * @param ipAddress the ip address
     */
    void setIPAddress(String ipAddress);

    /**
     * Gets connected networks.
     *
     * @return the connected networks
     */
    List<Network> getConnectedNetworks();

    /**
     *
     */
    String getAddressForNetwork(String networkName);

    /**
     *
     */
    boolean addAddressForNetwork(String address, String networkName);

    /**
     * Gets management port.
     *
     * @return the management port
     */
    Integer getManagementPort();

    /**
     * Sets management port.
     *
     * @param managementPort the management port
     */
    void setManagementPort(Integer managementPort);
}
