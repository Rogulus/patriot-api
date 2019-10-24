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

package io.patriot_framework.network.simulator.api.model.devices.router;

import io.patriot_framework.network.simulator.api.model.network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RouterImpl class represents docker container built as router with network-control apis.
 */
public class RouterImpl implements Router {
    private String name;
    private List<NetworkInterface> networkInterfaces;
    private List<Network> connectedTopologyNetworks = new ArrayList<>();
    private String managementIP;
    private Integer managementPort = 0;
    private String creator;
    private boolean corner = false;


    // Default go iproute2 rest api http port
    public static final int DEFAULT_PORT = 8090;

    /**
     * Instantiates a new RouterImpl.
     *
     * @param name the name
     */
    public RouterImpl(String name) {
        this.name = name;
        connectedTopologyNetworks = new ArrayList<>();
    }

    public RouterImpl(String name, String creator) {
        this.creator = creator;
        this.name = name;
    }

    /**
     * Instantiates a new RouterImpl.
     *
     * @param name              the name
     * @param networkInterfaces the network interfaces
     */
    public RouterImpl(String name, List<NetworkInterface> networkInterfaces) {
        this.name = name;
        this.networkInterfaces = networkInterfaces;
    }

    public RouterImpl(String name, String creator, boolean corner) {
        this.name = name;
        this.creator = creator;
        this.corner = corner;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public List<NetworkInterface> getInterfaces() {
        return networkInterfaces;
    }


    @Override
    public String getIPAddress() {
        return managementIP;
    }

    @Override
    public void setIPAddress(String ipAddress) {
        this.managementIP = ipAddress;
    }

    @Override
    public List<Network> getConnectedNetworks() {
        return connectedTopologyNetworks;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets network interfaces.
     *
     * @return the network interfaces
     */
    public List<NetworkInterface> getNetworkInterfaces() {
        return networkInterfaces;
    }

    /**
     * Sets network interfaces.
     *
     * @param networkInterfaces the network interfaces
     */
    public void setNetworkInterfaces(List<NetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    /**
     * Gets connected networks.
     *
     * @return the connected networks
     */
    public List<Network> getConnectedTopologyNetworks() {
        return connectedTopologyNetworks;
    }

    /**
     * Sets connected networks.
     *
     * @param connectedTopologyNetworks the connected networks
     */
    public void setConnectedTopologyNetworks(List<Network> connectedTopologyNetworks) {
        this.connectedTopologyNetworks = connectedTopologyNetworks;
    }

    /**
     * Gets mng ip.
     *
     * @return the mng ip
     */
    public String getManagementIP() {
        return managementIP;
    }

    /**
     * Sets mng ip.
     *
     * @param managementIP the mng ip
     */
    public void setManagementIP(String managementIP) {
        this.managementIP = managementIP;
    }

    /**
     * Gets mng port.
     *
     * @return the mng port
     */
    public Integer getManagementPort() {
        if (managementPort == 0) {
            return DEFAULT_PORT;
        }
        return managementPort;
    }

    /**
     * Sets mng port.
     *
     * @param managementPort the mng port
     */
    public void setManagementPort(Integer managementPort) {
        this.managementPort = managementPort;
    }

    @Override
    public Boolean isCorner() {
        return corner;
    }

    public void setCorner(Boolean corner) {
        this.corner = corner;
    }

    @Override
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String getAddressForNetwork(String networkName) {
        return null;
    }

    @Override
    public boolean addAddressForNetwork(String address, String networkName) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouterImpl router = (RouterImpl) o;
        return corner == router.corner &&
                getName().equals(router.getName()) &&
                Objects.equals(getNetworkInterfaces(), router.getNetworkInterfaces()) &&
                getConnectedTopologyNetworks().equals(router.getConnectedTopologyNetworks()) &&
                Objects.equals(getManagementIP(), router.getManagementIP()) &&
                Objects.equals(getManagementPort(), router.getManagementPort()) &&
                getCreator().equals(router.getCreator());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getNetworkInterfaces(), getConnectedTopologyNetworks(), getManagementIP(), getManagementPort(), getCreator(), corner);
    }
}
