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

package io.patriot_framework.network.simulator.api.api.monitoring;

import io.patriot_framework.network.simulator.api.api.RestController;

/**
 * Utility class to set monitor address
 */
@Deprecated
public class MonitoringRestController extends RestController {

    /**
     * Sets monitoring address to associated router
     * @param elasticAddr address of elastic search to be used with PatrIoT
     * @param elasticPort port of elastic search
     * @param ip the ip
     * @param port the port
     * @return response from router
     */
    public String setMonitoringAddress(String elasticAddr, Integer elasticPort, String ip, Integer port)
    {
        String path = "/setLogHook?elastic=" + elasticAddr + ":" + elasticPort;
        return executeHttpRequest(path, "GET", ip, port);
    }
}
