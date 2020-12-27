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

package io.patriot_framework.network.simulator.api.builder;

import io.patriot_framework.network.simulator.api.CalculatedRouteList;
import io.patriot_framework.network.simulator.api.model.network.TopologyNetwork;
import io.patriot_framework.network.simulator.api.model.routes.CalcRoute;

/**
 * TopologyNetwork Builder provides functions for building network and adding it into topology.
 * TopologyNetwork Builder have to cooperate with CalcRouteBuilder and Topology builder.
 */
public class NetworkBuilder {

    /**
     * Creator of network (docker, VM, ...)
     */
    private Object creator;
    private String ipAddress;
    private String name;
    private int mask = 0;
    private Boolean internet = false;

    /**
     * CalculatedRouteList is list with routes to other networks, usually created with CalcRouteBuilder.
     */
    public CalculatedRouteList<CalcRoute> calcRoutes = new CalculatedRouteList<>();

    /**
     * Topology builder.
     */
    private TopologyBuilder topologyBuilder;

    /**
     * Instantiates a new TopologyNetwork builder.
     *
     * @param topologyBuilder    the topologyBuilder
     * @param name the network name.
     */
    public NetworkBuilder(TopologyBuilder topologyBuilder, String name) {
        this.topologyBuilder = topologyBuilder;
        this.name = name;
    }

    /**
     * Instantiates a new TopologyNetwork builder.
     *
     * @param name the network name.
     */
    public NetworkBuilder(String name){
        this.name = name;
    }

    /**
     * Add ip attribute to network.
     *
     * @param ip the ip
     * @return the network builder
     */
    public NetworkBuilder withIP(String ip) {
        this.ipAddress = ip;
        return this;
    }

    /**
     * Add mask attribute to network.
     *
     * @param mask the mask
     * @return the network builder
     */
    public NetworkBuilder withMask(Integer mask) {
        this.mask = mask;
        return this;
    }

    /**
     * Add internet attribute to network.
     * If this network is last step between your ws and topology,
     * network must have attribute internet sets to true!
     *
     * @param internet the internet
     * @return the network builder
     */
    public NetworkBuilder withInternet(Boolean internet) {
        this.internet = internet;
        return this;
    }

    public NetworkBuilder withCreator(Object creator) {
        this.creator = creator;
        return this;
    }

    /**
     * Add calcRoutes attribute to network.
     *
     * @param calcRoutes the calc routes
     * @return the network builder
     */
    public NetworkBuilder withCalcRoutes(CalculatedRouteList<CalcRoute> calcRoutes) {
        this.calcRoutes = calcRoutes;
        return this;
    }

    /**
     * Instantiates new CalcRouteBuilder used to create calcRoutes.
     *
     * @return new CalcRouteBuilder
     */
    public CalcRouteBuilder withCalcRoute(){
        return new CalcRouteBuilder(this);
    }


    /**
     * Sets docker as network creator.
     *
     * @return the network builder
     */
    public NetworkBuilder docker() {
        this.creator = "Docker";
        return this;
    }

    /**
     * Builds network object.
     *
     * @return the network
     */
    public TopologyNetwork build() {
        TopologyNetwork n = new TopologyNetwork();
        n.setCalcRoutes(calcRoutes);
        n.setInternet(internet);
        n.setIPAddress(ipAddress);
        n.setMask(mask);
        n.setName(name);
        n.setCreator(creator);
        return n;
    }

    /**
     * Builds network and adds it into topology.
     *
     * @return the topology builder
     */
    public TopologyBuilder create() {
        if (creator == null) {
            creator = topologyBuilder.getCurrentCreator();
        }
        topologyBuilder.getTopology().getNetworks().add(build());
        return topologyBuilder;
    }

}
