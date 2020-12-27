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

import io.patriot_framework.network.simulator.api.model.devices.router.Router;
import io.patriot_framework.network.simulator.api.model.network.TopologyNetwork;
import io.patriot_framework.network.simulator.api.model.routes.CalcRoute;
import io.patriot_framework.network.simulator.api.model.routes.NextHop;

import java.util.ArrayList;

/**
 * The type Calc route builder.
 */
public class CalcRouteBuilder {
    /**
     * The RouterImpl.
     */
    Router router;
    /**
     * The source network.
     */
    int sourceNetwork;
    /**
     * Cost of route (hops).
     */
    int cost;
    /**
     * The destination network.
     */
    int destNetwork;

    /**
     * The TopologyNetwork builder.
     */
    NetworkBuilder networkBuilder;
    /**
     * The Topology builder.
     */
    TopologyBuilder topologyBuilder;

    public CalcRouteBuilder(int destNetwork) {
        this.destNetwork = destNetwork;
    }

    /**
     * Instantiates a new Calc route builder.
     * This CalcRouteBuilder is used to describe all routes in
     * network topology with just one calcRoutes build.
     * Provides auto completing of routes (route ws -&gt; internet will be
     * auto completed with route internet -&gt; ws) which saves a lot of time.
     *
     * @param topologyBuilder the topology builder
     */
    public CalcRouteBuilder(TopologyBuilder topologyBuilder) {
        this.topologyBuilder = topologyBuilder;
    }

    /**
     * Instantiates a new Calc route builder.
     * This CalcRouteBuilder is used to describe CalculatedRoutes just for
     * one network without auto complete etc...
     *
     * @param networkBuilder the network builder
     */
    public CalcRouteBuilder(NetworkBuilder networkBuilder) {
        this.networkBuilder = networkBuilder;
    }

    /**
     * Finds router based on it`s name and sets it as router attribute.
     *
     * @param routerName the router name
     * @return the calc route builder
     */
    public CalcRouteBuilder viaRouter(String routerName) {
        this.router = topologyBuilder.getTopology().findRouterByName(routerName);
        return this;
    }

    /**
     * Adds router attribute.
     *
     * @param router Router
     * @return the calc route builder
     */
    public CalcRouteBuilder viaRouter(Router router) {
        this.router = router;
        return this;
    }

    /**
     * Adds cost attribute.
     *
     * @param cost the cost
     * @return the calc route builder
     */
    public CalcRouteBuilder withCost(Integer cost) {
        this.cost = cost;
        return this;
    }

    /**
     * Adds destination network attribute. Identified by name.
     * This method has to be used with topology builder,
     * not with network builder.
     *
     * @param targetNetworkName the target network name
     * @return the calc route builder
     */
    public CalcRouteBuilder withDestNetwork(String targetNetworkName) {
        this.destNetwork = findNetworkByName(targetNetworkName);
        return this;
    }

    /**
     * With dest network calc route builder.
     * Method has to be used with network builder.
     *
     * @param targetNetworkIndex the target network index
     * @return the calc route builder
     */
    public CalcRouteBuilder withDestNetwork(Integer targetNetworkIndex) {
        this.destNetwork = targetNetworkIndex;
        return this;
    }

    /**
     * Adds source network attribute. Identified by name.
     * This method has to be used with topology builder,
     * not with network builder.
     *
     * @param sourceNetworkName the source network name
     * @return the calc route builder
     */
    public CalcRouteBuilder withSourceNetwork(String sourceNetworkName) {
        this.sourceNetwork = findNetworkByName(sourceNetworkName);
        return this;
    }

    /**
     * Find network by name in topology networks.
     *
     * @param name target network name.
     * @return index of target network in topology list of networks.
     */
    private Integer findNetworkByName(String name) {
        ArrayList<TopologyNetwork> topologyNetworks = topologyBuilder.getTopology().getNetworks();
        for (int i = 0; i < topologyNetworks.size(); i++) {
            if (topologyNetworks.get(i).getName().equals(name)) {
                return i;
            }
        }
        return null;
    }

    /**
     * With source network calc route builder.
     *
     * @param sourceNetwork the source network
     * @return the calc route builder
     */
    public CalcRouteBuilder withSourceNetwork(Integer sourceNetwork) {
        this.sourceNetwork = sourceNetwork;
        return this;
    }

    /**
     * Create route with opposite direction and add both routes into
     * their positions in calcRoutesLists in networks.
     *
     * @return the calc route builder
     */
    public CalcRouteBuilder addRoute() {
        CalcRoute sR = new CalcRoute(new NextHop(router, destNetwork), cost);
        topologyBuilder.getTopology().getNetworks().get(sourceNetwork).getCalcRoutes().add(destNetwork, sR);

        if (sourceNetwork != destNetwork) {
            CalcRoute dR = new CalcRoute(new NextHop(router, sourceNetwork), cost);
            topologyBuilder.getTopology().getNetworks().get(destNetwork).getCalcRoutes().add(sourceNetwork, dR);
        }
        return this;
    }

    /**
     * Complete routes by route which point to themselves (N1 -&gt; N1) and
     * returns topology builder updated by added routes.
     *
     * @return the topology builder
     */
    public TopologyBuilder buildRoutes() {

        for (int i = 0; i < topologyBuilder.getTopology().getNetworks().size(); i++) {
            CalcRoute cR = new CalcRoute(new NextHop(null, i), null);
            topologyBuilder.getTopology().getNetworks().get(i).getCalcRoutes().add(i, cR);
        }

        return topologyBuilder;
    }

    /**
     * Creates route.
     * @return calcRoute
     */
    public CalcRoute createCalcRoute() {
        CalcRoute sR = new CalcRoute(new NextHop(router, destNetwork), cost);
        return sR;
    }
}
