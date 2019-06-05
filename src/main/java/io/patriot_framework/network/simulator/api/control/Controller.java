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

package io.patriot_framework.network.simulator.api.control;

import io.patriot_framework.network.simulator.api.model.devices.Device;
import io.patriot_framework.network.simulator.api.model.network.Network;

import java.io.File;
import java.util.List;


/**
 * The Controller should be used to communicate with actual creator
 * of environment.
 */
public interface Controller {

    /**
     * Connects device to network.
     *
     * @param device  the device
     * @param network the network
     */
    void connectDeviceToNetwork(Device device, Network network);

    /**
     * Connects device to each network in network array.
     *
     * @param device   the device
     * @param networks the networks
     */
    void connectDeviceToNetwork(Device device, List<Network> networks);

    /**
     * Stops device.
     *
     * @param router the router
     */
    void stopDevice(Device router);

    /**
     * Disconnects device from network.
     *
     * @param device  the device
     * @param network the network
     */
    void disconnectDevice(Device device, Network network);

    /**
     * Destroys device means first stops device container and
     * then remove container.
     *
     * @param device the device
     */
    void destroyDevice(Device device);

    /**
     * Creates network.
     *
     * @param network the network
     */
    void createNetwork(Network network);

    /**
     * Destroys network.
     *
     * @param network the network
     */
    void destroyNetwork(Network network);

    /**
     * Deploys device from built image.
     *
     * @param device the device
     * @param tag    the tag
     */
    void deployDevice(Device device, String tag);

    /**
     * Deploys device to network and set monitoring attributes to container.
     * @param device the device
     * @param tag tag to be set
     * @param monitoringIP ip address for monitoring
     * @param monitoringPort port for monitoring
     */
    void deployDevice(Device device, String tag, String monitoringIP, int monitoringPort);

    /**
     * Builds image from file and then create container from built image.
     *
     * @param device the device
     * @param file   the file
     */
    void deployDevice(Device device, File file);

    /**
     * Builds image from file.
     *
     * @param image the image
     * @param tag   the tag
     */
    void buildImage(File image, String tag);

    /**
     * Returns network ip address of default gw.
     * @param device device to be searched
     * @return address of gateway
     */
    String findGWNetworkIPAddress(Device device);

    /**
     * Returns ip address of default gw interface.
     * @param device device which address is searched
     * @return string representation of address
     */
    String findGWIPAddress(Device device);

    /**
     * Returns network mask of default gw interface.
     * @param device device which mask will be returned
     * @return network mask of device
     */
    Integer findGWMask(Device device);

    /**
     * Returns String identifier of controller, Docker/Rocket/VM, ...
     * @return identifier
     */
    String getIdentifier();

    /**
     * Executes command in device.
     * @param device where command will be executed
     * @param command which will be executed
     */
    void executeCommand(Device device, String command);

    /**
     * Starts device.
     * @param device device to start
     */
    void startDevice(Device device);
}
