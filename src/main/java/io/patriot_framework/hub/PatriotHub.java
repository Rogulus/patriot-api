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

package io.patriot_framework.hub;


import io.patriot_framework.network.simulator.api.manager.Manager;
import io.patriot_framework.network.simulator.api.model.Topology;
import io.patriot_framework.network.simulator.api.model.devices.application.Application;
import io.patriot_framework.network.simulator.api.model.network.TopologyNetwork;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry interface to the Patriot Framework
 * It contains all controlling APIs that are available to use
 *
 * The class implements singleton design pattern, which means there is
 * always at most one PatriotHub within one JVM process
 */
public class PatriotHub {

    public static Logger log = Logger.getLogger(PatriotHub.class.toString());

    private static PatriotHub singleton = null;

    private Manager manager;
    private DeviceRegistry registry;
    private ApplicationRegistry apps;
    private Topology topology;

    private Properties properties;
    private static final String PATRIOT_ROUTER_TAG = "patriotframework/patriot-router:latest";

    public DeviceRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(DeviceRegistry registry) {
        this.registry = registry;
    }

    /**
     * Private constructor to build singleton instance
     * @throws PropertiesNotLoadedException thrown when property io.patriot_framework.router is not defined
     */
    private PatriotHub() throws PropertiesNotLoadedException  {

        properties = new Properties();
        IOException exception = null;
        InputStream reader = PatriotHub.class.getClassLoader().getResourceAsStream("patriot.properties");
        if (reader == null) {
            log.info("Patriot properties not loaded - switching to system properties");
        } else {
            try {
                properties.load(reader);
            } catch (IOException e) {
                log.warning("Could not read properties file!");
                log.log(Level.FINE, e.getMessage(), e);
            }
        }
        properties.putAll(System.getProperties());

        manager = new Manager((properties.containsKey("io.patriot_framework.router") ?
                properties.getProperty("io.patriot_framework.router") : PATRIOT_ROUTER_TAG));
        if (properties.containsKey("io.patriot_framework.monitoring.addr")) {
            manager.setMonitoring(properties.getProperty("io.patriot_framework.monitoring.addr"),
                    Integer.valueOf(properties.getProperty("io.patriot_framework.monitoring.port")));
        }

        registry = new DeviceRegistry();
        apps = new ApplicationRegistry();
    }

    /**
     * Singleton accessor
     * @return PatriotHub instance, this cannot return null
     * @throws PropertiesNotLoadedException when creation of instance fails due to missing property
     */
    public static PatriotHub getInstance() throws PropertiesNotLoadedException {
        if (singleton == null) {
            singleton = new PatriotHub();
        }
        return singleton;
    }

    public void deployTopology(Topology top) {
        if (topology != null) {
            throw new IllegalArgumentException("Topology already deployed");
        }
        topology = top;
        manager.deployTopology(top);
    }

    public void deployApplication(Application app, String networkName, String tag, List<String> envVars) {
        Optional<TopologyNetwork> net = topology.getNetworks().stream().filter(it -> it.getName().equals(networkName)).findFirst();
        if (!net.isPresent()) {
            return;
        }
        if (envVars == null) {
            envVars = new ArrayList<>();
        }

        manager.deployDeviceToNetwork(app, net.get(), topology, tag, envVars);
        apps.putDevice(app);
    }

    public void deployApplication(Application app, String networkName, String tag) {
        deployApplication(app, networkName, tag, null);
    }

    public Application getApplication(String name) {
        return apps.getDevice(name);
    }


    /**
     * Accessor to the simulators NetworkManager, which is main controlling interface for simulated
     * network
     * @return current NetworkManager
     */
    public Manager getManager() {
        return manager;
    }

    /**
     * Method cleans up all resources and destroys its instance
     */
    public void destroyHub() {
        manager.cleanUp(topology);
        singleton = null;
    }

}
