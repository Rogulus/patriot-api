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

package io.patriot_framework.network.simulator.api.model;

import io.patriot_framework.network.simulator.api.model.devices.Device;
import io.patriot_framework.network.simulator.api.model.devices.router.Router;
import io.patriot_framework.network.simulator.api.model.network.TopologyNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Wrapper representing full network topology.
 */
public class Topology {
    /**
     * Routers located in topology.
     */
    private List<Router> routers = new ArrayList<>();

    /**
     * Networks in topology.
     */
    private ArrayList<TopologyNetwork> networks;

    private List<Device> devices = new ArrayList<>();

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    /**
     * Instantiates a new Topology.
     *
     * @param routers    the routers
     * @param networks the network top
     */
    public Topology(List<Router> routers, ArrayList<TopologyNetwork> networks) {
        this.routers = routers;
        this.networks = networks;
    }

    /**
     * Instantiates a new Topology.
     *
     * @param networks the network top
     */
    public Topology(ArrayList<TopologyNetwork> networks) {
        this.networks = networks;
    }

    /**
     * Instantiates a new Topology.
     *
     * @param networkCount the network count
     */
    public Topology(Integer networkCount) {
        this.networks = new ArrayList<>(networkCount);
    }

    /**
     * Gets routers.
     *
     * @return the routers
     */
    public List<Router> getRouters() {
        return routers;
    }

    /**
     * Sets routers.
     *
     * @param routers the routers
     */
    public void setRouters(List<Router> routers) {
        this.routers = routers;
    }

    /**
     * Gets network top.
     *
     * @return the network top
     */
    public ArrayList<TopologyNetwork> getNetworks() {
        return networks;
    }

    /**
     * Sets network top.
     *
     * @param networks the network top
     */
    public void setNetworks(ArrayList<TopologyNetwork> networks) {
        this.networks = networks;
    }

    /**
     * Finds router in list by name.
     * @param name name of router
     * @return router object
     */
    public Router findRouterByName(String name) {
        for (Router r : routers) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topology topology = (Topology) o;
        return getRouters().equals(topology.getRouters()) &&
                getNetworks().equals(topology.getNetworks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRouters(), getNetworks());
    }
}
