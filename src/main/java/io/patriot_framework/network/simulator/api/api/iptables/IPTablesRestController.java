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

package io.patriot_framework.network.simulator.api.api.iptables;

import io.patriot_framework.network.simulator.api.api.RestController;
import io.patriot_framework.network.simulator.api.api.iptables.chain.Chain;
import io.patriot_framework.network.simulator.api.api.iptables.rules.FilterRule;

/**
 * The type Ip tables controller.
 */
public class IPTablesRestController extends RestController {

    /**
     * Add filter rule string.
     *
     * @param filterRule the filter rule
     * @param ip the ip
     * @param port the port
     * @return the string
     **/
    public String addFilterRule(FilterRule filterRule, String ip, Integer port) {

        return executeHttpRequest(filterRule.toAPIFormat(), "PUT", ip, port);
    }

    /**
     * Delete filter rule string.
     *
     * @param filterRule the filter rule
     * @param ip the ip
     * @param port the port
     * @return the string
     **/
    public String deleteFilterRule(FilterRule filterRule, String ip, Integer port) {
        return executeHttpRequest(filterRule.toAPIFormat(), "DELETE", ip, port);
    }


    /**
     * Add chain string.
     *
     * @param chain the chain
     * @param ip the ip
     * @param port the port
     * @return the string
     **/
    public String addChain(Chain chain, String ip, Integer port) {
        return executeHttpRequest(chain.toString(), "PUT", ip, port);
    }

    /**
     * Delete chain string.
     *
     * @param chain the chain
     * @param ip the ip
     * @param port the port
     * @return the string
     **/
    public String deleteChain(Chain chain, String ip, Integer port) {
        return executeHttpRequest(chain.getPath(), "DELETE", ip, port);
    }


    /**
     * Save ip tables string.
     *
     * @param ip the ip
     * @param port the port
     * @return the string
     **/
    public String saveIpTables(String ip, Integer port) {
        return executeHttpRequest("/save/", "GET", ip, port);
    }
}
