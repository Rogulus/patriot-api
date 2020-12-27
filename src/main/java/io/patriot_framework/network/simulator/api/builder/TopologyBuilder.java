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

import io.patriot_framework.network.simulator.api.model.Topology;

/**
 * The type Topology builder.
 */
public class TopologyBuilder {
    /**
     * The Topology.
     */
    private Topology topology;
    private Object currentCreator;

    public Object getCurrentCreator() {
        return currentCreator;
    }
    public TopologyBuilder withCreator(Object creator) {
        currentCreator = creator;
        return this;
    }

    /**
     * Instantiates new NetworkBuilder.
     *
     * @param name the network name
     * @return the network builder
     */
    public NetworkBuilder withNetwork(String name) {
        return new NetworkBuilder(this, name);
    }

    /**
     * Instantiates a new Topology builder.
     *
     * @param networkCount the network count
     */
    public TopologyBuilder(int networkCount) {
        topology = new Topology(networkCount);
    }

    public TopologyBuilder(Topology topology) {
        this.topology = topology;
    }

    /**
     * Builds topology.
     *
     * @return the topology
     */
    public Topology build() {
        return topology;
    }

    /**
     * Instantiates new CalcRouteBuilder.
     *
     * @return the calc route builder
     */
    public CalcRouteBuilder withRoutes() {
        return new CalcRouteBuilder(this);
    }

    /**
     * Instantiates new CalcRouteBuilder.
     *
     * @return the router builder
     */
    public RouterBuilder withRouters() {
        return new RouterBuilder(this);
    }

    public Topology getTopology() {
        return topology;
    }
}
