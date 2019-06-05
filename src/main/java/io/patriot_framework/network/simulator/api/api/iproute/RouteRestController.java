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

package io.patriot_framework.network.simulator.api.api.iproute;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patriot_framework.network.simulator.api.api.RestController;
import io.patriot_framework.network.simulator.api.model.devices.router.NetworkInterface;
import io.patriot_framework.network.simulator.api.model.routes.Route;

import java.io.IOException;
import java.util.List;

/**
 * RestController for ip tables api.
 * Implementing RestController. Is used for work with http ip-route rest api running in container.
 *
 */
public class RouteRestController extends RestController {
    /**
     * Add route string.
     *
     * @param route the route
     * @param ip ip of route
     * @param port port
     * @return the string
     */
    public String addRoute(Route route, String ip, Integer port) {
        String path = "/iproutes/mod?" + route.toAPIFormat();
        return executeHttpRequest(path, "PUT", ip, port);
    }

    /**
     * Delete route string.
     *
     * @param route the route
     * @param ip the ip
     * @param port the port
     * @return the string
     */
    public String deleteRoute(Route route, String ip, Integer port) {
        String path = "/iproutes/mod?" + route.toAPIFormat();
        return executeHttpRequest(path, "DELETE", ip, port);
    }

    /**
     * Gets routers.
     *
     * @param ip the ip
     * @param port the port
     * @return the routers
     */
    public List<Route> getRoutes(String ip, Integer port) {
        String path = "/iproutes";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String input = executeHttpRequest(path, "GET", ip, port);
            return objectMapper.readValue(input, new TypeReference<List<Route>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add default gw string.
     *
     * @param defaultGW the default gw
     * @param ip the ip
     * @param port the port
     * @return the string
     */
    public String addDefaultGW(Route defaultGW, String ip, Integer port) {
        String path = "/iproutes/default?interface=" + defaultGW.getrNetworkInterface().getIp();
        return executeHttpRequest(path, "PUT", ip, port);
    }

    /**
     * Del default gw string.
     *
     * @param defaultGW the default gw
     * @param ip the ip
     * @param port the port
     * @return the string
     */
    public String delDefaultGW(Route defaultGW, String ip, Integer port) {
        String path = "/iproutes/default?interface=" + defaultGW.getrNetworkInterface().getIp();
        return executeHttpRequest(path, "DELETE", ip, port);
    }

    /**
     * Remove default gateway
     * @param ip ip of container
     * @param port control port of container
     * @return result of executed action
     */
    public String delDefaultGw(String ip, Integer port) {
        String path = "/iproutes/default";
        return executeHttpRequest(path, "DELETE", ip, port);
    }



    /**
     * Gets interfaces.
     *
     * @param ip the ip
     * @param port the port
     * @return the interfaces
     */
    public List<NetworkInterface> getInterfaces(String ip, Integer port) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String input = executeHttpRequest("/interfaces", "GET", ip, port);
            List<NetworkInterface> networkInterfaces =
                    objectMapper.readValue(input, new TypeReference<List<NetworkInterface>>(){});
            return networkInterfaces;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
