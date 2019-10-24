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

package io.patriot_framework.network.simulator.api.manager;

import com.github.jgonian.ipmath.Ipv4;
import com.github.jgonian.ipmath.Ipv4Range;
import io.patriot_framework.network.simulator.api.api.iproute.RouteRestController;
import io.patriot_framework.network.simulator.api.control.Controller;
import io.patriot_framework.network.simulator.api.model.EnvironmentPart;
import io.patriot_framework.network.simulator.api.model.Topology;
import io.patriot_framework.network.simulator.api.model.devices.Device;
import io.patriot_framework.network.simulator.api.model.devices.router.NetworkInterface;
import io.patriot_framework.network.simulator.api.model.devices.router.Router;
import io.patriot_framework.network.simulator.api.model.network.TopologyNetwork;
import io.patriot_framework.network.simulator.api.model.routes.CalcRoute;
import io.patriot_framework.network.simulator.api.model.routes.NextHop;
import io.patriot_framework.network.simulator.api.model.routes.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Manager is used for managing topology (deploying, destroying).
 */
public class Manager {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private List<Controller> controllers;
    private String monitoringAddr;
    private String routerTag;
    private int monitoringPort = 0;
    private HashMap<String, ArrayList<Route>> processedRoutes = new HashMap<>();

    /**
     * Constructor
     * @param routerTag tag of router to be used
     */
    public Manager(String routerTag) {
        this.routerTag = routerTag;
    }

    /**
     * Getter for monitoring address
     * @return address of monitoring component
     */
    public String getMonitoringAddr() {
        return monitoringAddr;
    }

    /**
     * Setter for monitoring address
     * @param monitoringAddr Address of monitoring service (greylog)
     * @param monitoringPort port of monitoring service
     */
    public void setMonitoring(String monitoringAddr, int monitoringPort) {
        this.monitoringAddr = monitoringAddr;
        this.monitoringPort = monitoringPort;
    }

    /**
     * Constructor
     * @param controllers to be used
     */
    public Manager(List<Controller> controllers) {
        this.controllers = controllers;
    }

    /**
     * Constructor
     * @param controllers to be used
     * @param routerTag tag of the router
     */
    public Manager(List<Controller> controllers, String routerTag) {
        this.controllers = controllers;
        this.routerTag = routerTag;
    }

    /**
     * Setter for controllers
     * @param controllers controllers to be used by manager
     */
    public void setControllers(List<Controller> controllers) {
        this.controllers = controllers;
    }

    /**
     * Constructor
     * @param controllers controllers used by manager
     * @param routerTag tag of router image
     * @param monitoringAddr ip address of monitoring service
     * @param monitoringPort port of monitoring service
     */
    public Manager(List<Controller> controllers, String routerTag, String monitoringAddr, int monitoringPort) {
        this.controllers = controllers;
        this.monitoringAddr = monitoringAddr;
        this.routerTag = routerTag;
        this.monitoringPort = monitoringPort;
    }

    /**
     * Getter for processed routes
     * @return refined routes after application of rules
     */
    public HashMap<String, ArrayList<Route>> getProcessedRoutes() {
        return processedRoutes;
    }

    /**
     * Deploy all topology. Creates all virtual devices (routers), networks. Connect them as topology describes.
     * Calculate routes and set them to routes.
     * @param topology
     */
    public void deployTopology(Topology topology) {
        createNetworks(topology.getNetworks());
        createRouters(topology);
        connectNetworks(topology);
        updateRouters(topology);
        calcRoutes(topology);
        processRoutes(topology);
        setRoutes(topology);
        setMasquerade(topology);
    }

    /**
     * Calculates routers via Floyd-Warshall algo.
     * @param topology the representation of topology where shortest paths are to be found
     */
    public void calcRoutes(Topology topology) {
        ArrayList<TopologyNetwork> topologyNetworks = topology.getNetworks();
        LOGGER.info("Calculating network routes.");
        int size = topologyNetworks.size();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    if (i == k || i == j || j == k) {
                        continue;
                    }
                    int cost = topologyNetworks.get(i).getCalcRoutes().get(j).getCost()
                            + topologyNetworks.get(j).getCalcRoutes().get(k).getCost();
                    int actualCost = topologyNetworks.get(i).getCalcRoutes().get(k).getCost();

                    if (actualCost == (size + 1) || actualCost > cost) {
                        swapCalcRoutes(topologyNetworks, i, j, k);
                    }
                }
            }
        }
    }

    private void swapCalcRoutes(ArrayList<TopologyNetwork> topologyNetworks, int i, int j, int k) {
        NextHop nextHop = new NextHop(topologyNetworks.get(i).getCalcRoutes()
                .get(j).getNextHop().getRouter(),
                topologyNetworks.get(i).getCalcRoutes().get(j).getNextHop().getNetwork());

        CalcRoute c = new CalcRoute(nextHop, topologyNetworks.get(i).getCalcRoutes().get(j).getCost()
                + topologyNetworks.get(j).getCalcRoutes().get(k).getCost());
        topologyNetworks.get(i).getCalcRoutes().set(k, c);
    }


    /**
     * Parses calculated routes to actual route objects.
     * @param topology topology of network
     */
    public void processRoutes(Topology topology) {
        LOGGER.info("Processing routes to ipRoute2 format.");
        ArrayList<TopologyNetwork> calculatedTop = topology.getNetworks();
        int size = calculatedTop.size();
        for (int i = 0; i < size; i++){
            for (int j = 0; j < calculatedTop.get(i).getCalcRoutes().size(); j++) {
                if (i == j) {
                    continue;
                }

                Router r = calculatedTop.get(i).getCalcRoutes().get(j).getNextHop().getRouter();
                Router iRouter = selectNextHopRouter(calculatedTop, i, j);
                Route route = completeRoute(calculatedTop, r, iRouter, i, j);
                updateProcessedRoutes(route, r);
            }
        }
    }

    /**
     * Completes route object.
     * @param topologyNetworks networks
     * @param r router
     * @param i source network index
     * @param j destination network index
     * @return complete route object with all attributes
     */
    private Route completeRoute(ArrayList<TopologyNetwork> topologyNetworks, Router r, Router nextHopRouter, int i, int j) {
        Route route = new Route();

        route.setTargetRouter(r);

        route.setSource(topologyNetworks.get(i));
        route.setDest(topologyNetworks.get(j));

        int nextNetwork = topologyNetworks.get(i).getCalcRoutes().get(j).getNextHop().getNetwork();
        NetworkInterface target;
        if (nextHopRouter == null) {
            target = new NetworkInterface(topologyNetworks.get(j).getInternetInterfaceIP());
        } else {
            target = findCorrectInterface(nextHopRouter, topologyNetworks.get(nextNetwork));
        }
        route.setrNetworkInterface(target);
        return route;
    }

    /**
     * Finds next hop router. If router last step to dest network func returns actual nexthop
     * router if not func returns nexthop + 1 router.
     * @param calculatedTop
     * @param actualNet
     * @param destNet
     * @return
     */
    private Router selectNextHopRouter(ArrayList<TopologyNetwork> calculatedTop, int actualNet, int destNet) {
        if (calculatedTop.get(actualNet).getCalcRoutes().get(destNet).getCost() == 1 && calculatedTop.get(destNet).getInternet() ) {
            return null;
        } else if ((calculatedTop.get(actualNet).getCalcRoutes().get(destNet).getCost() - 1) != 0) {
            int nextHopNetwork = calculatedTop.get(actualNet).getCalcRoutes().get(destNet).getNextHop().getNetwork();
            return calculatedTop.get(nextHopNetwork).getCalcRoutes().get(destNet).getNextHop().getRouter();
        } else {
            return calculatedTop.get(actualNet).getCalcRoutes().get(destNet).getNextHop().getRouter();
        }
    }

    /**
     * Checks if route not processed yet, if not puts it into routes map.
     * @param route
     * @param r
     */
    private void updateProcessedRoutes(Route route, Router r) {
        if (!processedRoutes.containsKey(r.getName())) {
            processedRoutes.put(r.getName(), new ArrayList<>(Arrays.asList(route)));
        } else {
            if (isProcessedRoute(processedRoutes.get(r.getName()), route)) {
                return;
            }
            processedRoutes.get(r.getName()).add(route);
        }
    }


    /**
     * Finds correct network interface on router for target network.
     * @param source
     * @param topologyNetwork
     * @return network interface which is in target network
     */
    private NetworkInterface findCorrectInterface(Router source, TopologyNetwork topologyNetwork) {
        LOGGER.info("Finding correct interface for " + topologyNetwork.getName() + " on " + source.getName());
        Ipv4Range range = Ipv4Range.parse(topologyNetwork.getIPAddress() + "/" + topologyNetwork.getMask());

        for (int i = 0; i < source.getInterfaces().size(); i++) {
            if (range.contains(Ipv4.of(source.getInterfaces().get(i).getIp()))) {
                LOGGER.debug("Found correct interface: " + source.getInterfaces().get(i));
                return source.getInterfaces().get(i);
            }
        }
        return null;
    }

    /**
     * Checks if route is already present in list.
     * @param routes
     * @param route
     * @return true if route is present
     */
    private boolean isProcessedRoute(ArrayList<Route> routes, Route route) {
        for (Route r : routes) {
            if (r.getDest() == route.getDest() && r.getrNetworkInterface() == route.getrNetworkInterface()) {
                return true;
            }
        }
        return false;
    }

    /**
     Sets routes to routing table via REST API on targeted routers.
     * @param topology topology of network
     */
    public void setRoutes(Topology topology) {
        RouteRestController routeController = new RouteRestController();
        for (Map.Entry<String, ArrayList<Route>> entry: processedRoutes.entrySet()) {
            Router r = topology.findRouterByName(entry.getKey());
            LOGGER.info("Setting routes to routing table on " + r.getName());

            for (Route route : entry.getValue()) {
                if (route.getDest().getInternet()){
                    LOGGER.info("Setting default route");
                    routeController.delDefaultGw(r.getIPAddress(), r.getManagementPort());
                    routeController.addDefaultGW(route, r.getIPAddress(), r.getManagementPort());
                } else {
                    routeController.addRoute(route, r.getIPAddress(), r.getManagementPort());
                }
            }
        }
    }



    /**
     * Method updates interfaces of routers. Usually have to be called after
     * routers are connected to networks.
     * @param topology topology of network
     */
    private void updateRouters(Topology topology) {
        RouteRestController restController = new RouteRestController();
        LOGGER.info("Requesting information about routers interfaces.");
        for (Router r : topology.getRouters()) {
            r.setNetworkInterfaces(restController.getInterfaces(r.getIPAddress(), r.getManagementPort()));
        }
    }

    /**
     * Connects networks as topology describes.
     * @param topology
     */
    private void connectNetworks(Topology topology) {
        LOGGER.info("Initializing corner network address");
        initializeCornerNetworkIP(topology);
        LOGGER.info("Connecting networks.");
        HashMap<String, List<TopologyNetwork>> directConnections = filterConnected(topology);
        for (Map.Entry<String, List<TopologyNetwork>> entry : directConnections.entrySet()) {
            Router r = topology.findRouterByName(entry.getKey());
            Controller controller = findController(r);
            for (TopologyNetwork network : entry.getValue()) {
                LOGGER.info("Connecting router " + r.getName() + "with " + network.getName());
                if (!network.getInternet()) {
                    controller.connectDeviceToNetwork(r, network);
                    r.getConnectedNetworks().add(network);
                }
            }
        }
    }

    /**
     * Initializes ip address last hop in current LAN.
     * @param topology topology of network
     */
    private void initializeCornerNetworkIP(Topology topology) {
        for (TopologyNetwork n : topology.getNetworks()) {
            if (n.getInternet()) {
                Device device = findDeviceWithCreator(topology, n.getCreator());
                Controller controller = findController(device);
                n.setInternetInterfaceIP(controller.findGWIPAddress(device));
                n.setIPAddress(controller.findGWNetworkIPAddress(device));
                n.setMask(controller.findGWMask(device));
            }
        }
    }

    /**
     * Returns device with created by target creator.
     * @param topology topology of network
     * @param creator creating object
     * @return
     */
    private Device findDeviceWithCreator(Topology topology, String creator) {
        for (Device device : topology.getRouters()) {
            if (device.getCreator().equals(creator)) {
                return device;
            }
        }
        return null;
    }

    /**
     * Method provides filtering of networks which have to be physically connected
     * to other network via router, as it's described in topology.
     * @param topology
     * @return Hash map with router`s name as key and networks with which router has to be connected
     */
    private HashMap<String, List<TopologyNetwork>> filterConnected(Topology topology) {
        LOGGER.info("Filtering direct connections.");
        HashMap<String, List<TopologyNetwork>> connections = new HashMap<>();

        int topologySize = topology.getNetworks().size();
        ArrayList<TopologyNetwork> networks = topology.getNetworks();

        for (int i = 0; i < topologySize; i++) {
            TopologyNetwork network = networks.get(i);
            for (int y = 0; y < network.getCalcRoutes().size(); y++) {
                if (i == y) continue;
                CalcRoute calcRoute = network.getCalcRoutes().get(y);
                if (calcRoute.getCost() == 1) {
                    String rName = calcRoute.getNextHop().getRouter().getName();
                    TopologyNetwork dNetwork = networks.get(calcRoute.getNextHop().getNetwork());
                    connectionExists(rName, new ArrayList<>(Arrays.asList(network, dNetwork)), connections);
                }
            }
        }
        return connections;
    }

    /**
     * Method checks if target networks are stored in connections array. If not, they are inserted into connections
     * map, if router name is already stored in map, method checks if networks are stored too. If they are not
     * present, value is updated.
     * @param rName
     * @param networks
     * @param connections
     */
    private void connectionExists(String rName, List<TopologyNetwork> networks, HashMap<String, List<TopologyNetwork>> connections) {
        LOGGER.debug("Controlling if direct connection is already stored.");
        if (connections.keySet().contains(rName)) {
            for (int i = 0; i < 2; i++) {
                if (!connections.get(rName).contains(networks.get(i))) {
                    connections.get(rName).add(networks.get(i));
                }
            }
        } else {
            connections.put(rName, networks);
        }
    }

    /**
     * Creates networks.
     * @param networkList
     */
    private void createNetworks(ArrayList<TopologyNetwork> networkList) {
        for (TopologyNetwork network : networkList) {
            if (!network.getInternet()) {
                LOGGER.debug("Creating network: " + network.getName());
               findController(network).createNetwork(network);
            }
        }
    }

    /**
     * Creates routers and updates mng IP.
     * @param topology
     */
    private void createRouters(Topology topology) {
        for (Router router : topology.getRouters()) {
            LOGGER.debug("Creating router: " + router.getName());
            if (monitoringAddr != null && monitoringPort != 0) {
                findController(router).deployDevice(router, routerTag, monitoringAddr, monitoringPort);
            } else {
                findController(router).deployDevice(router, routerTag);
            }

        }
    }

    /**
     * Finds controller by string identifier. For 'Docker' will return Docker implementation of
     * controller.
     * @param environmentPart part of creating environment like network, router, ...
     * @return
     */
    private Controller findController(EnvironmentPart environmentPart) {
        for (Controller controller : controllers) {
            if (environmentPart.getCreator().equals(controller.getIdentifier())) {
                LOGGER.debug("Found right controller for: " + environmentPart.getCreator());
                return controller;
            }
        }
        LOGGER.error("Cannot find matching creator!");
        return null;
    }

    /**
     * Clean all routers, networks in topology (stop and delete).
     * @param topology
     */
    public void cleanUp(Topology topology) {
        Controller controller;
        for (Router r : topology.getRouters()) {
            controller = findController(r);
            LOGGER.info("Destroying router " + r.getName());
            controller.destroyDevice(r);
        }
        for (Device device : topology.getDevices()) {
            controller = findController(device);
            LOGGER.info("Destroying device " + device.getName());
            controller.destroyDevice(device);
        }
        for (TopologyNetwork n : topology.getNetworks()) {
            if (!n.getInternet()) {
                controller = findController(n);
                LOGGER.info("Destroying network " + n.getName());
                controller.destroyNetwork(n);
            }
        }
    }

    /**
     * Deploys device to selected environment.
     *
     * @param device the device
     * @param tag    the tag
     */
    public void deployDevice(Device device, String tag) {
        findController(device).deployDevice(device, tag);
    }

    /**
     * Sets masquerade to iptables on corner router, which provides full
     * connectivity to internet for all networks communicating with corner router.
     * @param topology topology of network
     */
    public void setMasquerade(Topology topology) {
        for (Router r : topology.getRouters()) {
            if (r.isCorner()) {
                for (TopologyNetwork n : topology.getNetworks()) {
                    if (n.getInternet()) {
                        NetworkInterface nI = findCorrectInterface(r, n);
                        findController(r).executeCommand(r, "/nat " + nI.getName());
                    }
                }
            }
        }
    }

    /**
     * Stops device, connects device to target network and again start device.
     * @param device device to be connected
     * @param network network where device will be connected
     * @param calculatedTopology the topology which of network
     * @param tag tag of device
     * @param envVars environment variables passed to the container
     */
    public void deployDeviceToNetwork(Device device, TopologyNetwork network, Topology calculatedTopology, String tag, List<String> envVars) {
        ArrayList<TopologyNetwork> networks = calculatedTopology.getNetworks();
        calculatedTopology.addDevice(device);
        deployToNetwork(device, tag, network, envVars);
        int internet = 0, sourceNet = 0;
        for (int i = 0; i < networks.size(); i++) {
            if (networks.get(i).getInternet()) {
                internet = i;
            }
            if (networks.get(i).getName().equals(network.getName())) {
                sourceNet = i;
            }
        }
        Router nearRouter = networks.get(sourceNet).getCalcRoutes().get(internet).getNextHop().getRouter();
        NetworkInterface nI = findCorrectInterface(nearRouter, network);

        RouteRestController routeRestController = new RouteRestController();
        Route route = new Route();
        route.setrNetworkInterface(nI);
        routeRestController.delDefaultGw(device.getIPAddress(), device.getManagementPort());
        routeRestController.addDefaultGW(route, device.getIPAddress(), device.getManagementPort());
    }

    /**
     * Deploys device to network means create device, connect it to target network and start it.
     * @param device
     * @param tag
     * @param network
     * @param envVars
     */
    private void deployToNetwork(Device device, String tag, TopologyNetwork network, List<String> envVars) {
        Controller deviceController = findController(device);
        deviceController.deployDevice(device, tag, monitoringAddr, monitoringPort, envVars);
        deviceController.connectDeviceToNetwork(device, network);
        device.getConnectedNetworks().add(network);
    }


}