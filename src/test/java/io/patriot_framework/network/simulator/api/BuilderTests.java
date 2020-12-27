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

package io.patriot_framework.network.simulator.api;

import io.patriot_framework.network.simulator.api.builder.CalcRouteBuilder;
import io.patriot_framework.network.simulator.api.builder.NetworkBuilder;
import io.patriot_framework.network.simulator.api.builder.RouterBuilder;
import io.patriot_framework.network.simulator.api.builder.TopologyBuilder;
import io.patriot_framework.network.simulator.api.model.Topology;
import io.patriot_framework.network.simulator.api.model.devices.router.Router;
import io.patriot_framework.network.simulator.api.model.devices.router.RouterImpl;
import io.patriot_framework.network.simulator.api.model.network.TopologyNetwork;
import io.patriot_framework.network.simulator.api.model.routes.CalcRoute;
import io.patriot_framework.network.simulator.api.model.routes.NextHop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class BuilderTests {

    @Test
    public void calcRouteBuilderTest() {
        RouterImpl router = new RouterImpl("TestRt", "Docker");
        int destNetwork = 1;
        int cost = 10;
        NextHop nextHop = new NextHop(router, destNetwork);
        CalcRoute calcRoute = new CalcRoute(nextHop, cost);
        CalcRouteBuilder calcRouteBuilder = new CalcRouteBuilder(destNetwork);
        CalcRoute builderRoute = calcRouteBuilder.withCost(10)
                .viaRouter(router)
                .createCalcRoute();
        Assertions.assertTrue(calcRoute.equals(builderRoute));


    }

    @Test
    public void networkBuilderTest() {
        CalculatedRouteList calcRoutes = new CalculatedRouteList();
        NextHop nextHop = new NextHop(new RouterImpl("TestRt", "Docker"), 1);
        CalcRoute calcRoute = new CalcRoute(nextHop, 10);
        calcRoutes.add(calcRoute);

        TopologyNetwork topologyNetwork = new TopologyNetwork();
        topologyNetwork.setCreator("Docker");
        topologyNetwork.setMask(24);
        topologyNetwork.setIPAddress("192.168.1.0");
        topologyNetwork.setInternet(true);
        topologyNetwork.setName("TestNet");
        topologyNetwork.setCalcRoutes(calcRoutes);

        TopologyNetwork builderNetwork = new NetworkBuilder("TestNet")
                .withCalcRoutes(calcRoutes)
                .withCreator("Docker")
                .withInternet(true)
                .withIP("192.168.1.0")
                .withMask(24)
                .build();
        Assertions.assertTrue(topologyNetwork.equals(builderNetwork));
    }

    @Test
    public void networkBuilderObjectTest() {
        CalculatedRouteList calcRoutes = new CalculatedRouteList();
        NextHop nextHop = new NextHop(new RouterImpl("TestRt", 'x'), 1);
        CalcRoute calcRoute = new CalcRoute(nextHop, 10);
        calcRoutes.add(calcRoute);

        TopologyNetwork topologyNetwork = new TopologyNetwork();
        topologyNetwork.setCreator('x');
        topologyNetwork.setMask(24);
        topologyNetwork.setIPAddress("192.168.1.0");
        topologyNetwork.setInternet(true);
        topologyNetwork.setName("TestNet");
        topologyNetwork.setCalcRoutes(calcRoutes);

        TopologyNetwork builderNetwork = new NetworkBuilder("TestNet")
                .withCalcRoutes(calcRoutes)
                .withCreator('x')
                .withInternet(true)
                .withIP("192.168.1.0")
                .withMask(24)
                .build();
        Assertions.assertEquals(builderNetwork, topologyNetwork);
    }

    @Test
    public void routerBuilderTest() {
        RouterImpl rtImpl = new RouterImpl("TestRt", "Docker", true);
        Router buildedRouter = new RouterBuilder("TestRt")
                .withCorner(true)
                .withCreator("Docker")
                .build();
        Assertions.assertTrue(rtImpl.equals(buildedRouter));

    }

    @Test
    public void routerBuilderObjectTest() {
        RouterImpl rtImpl = new RouterImpl("TestRt", 5, true);
        Router buildedRouter = new RouterBuilder("TestRt")
                .withCorner(true)
                .withCreator(5)
                .build();
        Assertions.assertEquals(buildedRouter, rtImpl);

    }

    @Test
    public void topologyBuilderTest() {
        RouterImpl rtImpl = new RouterImpl("TestRt", "Docker", true);
        Topology topology = new Topology(2);
        topology.setRouters(Arrays.asList(rtImpl));
        topology.setNetworks(prepareNetwork(rtImpl));

        Topology builderTopology = new TopologyBuilder(2)
                .withCreator("Docker")
                .withRouters()
                    .withCorner(true)
                    .withName("TestRt")
                    .createRouter()
                .addRouters()
                .withNetwork("TestNet1")
                    .withMask(24)
                    .withIP("192.168.1.0")
                    .withInternet(true)
                    .create()
                .withNetwork("TestNet2")
                    .withMask(24)
                    .withIP("192.168.2.0")
                    .withInternet(false)
                    .create()
                .withRoutes()
                    .viaRouter("TestRt")
                    .withDestNetwork(1)
                    .withSourceNetwork(0)
                    .withCost(1)
                    .addRoute()
                    .buildRoutes()
                .build();
        Assertions.assertTrue(builderTopology.equals(topology));
    }

    @Test
    public void topologyBuilderObjectTest() {
        RouterImpl rtImpl = new RouterImpl("TestRt", 15, true);
        Topology topology = new Topology(2);
        topology.setRouters(Arrays.asList(rtImpl));
        topology.setNetworks(prepareNetwork(rtImpl));

        Topology builderTopology = new TopologyBuilder(2)
                .withCreator("Docker")
                .withRouters()
                .withCorner(true)
                .withName("TestRt")
                .withCreator(15)
                .createRouter()
                .addRouters()
                .withNetwork("TestNet1")
                .withMask(24)
                .withIP("192.168.1.0")
                .withInternet(true)
                .create()
                .withNetwork("TestNet2")
                .withMask(24)
                .withIP("192.168.2.0")
                .withInternet(false)
                .create()
                .withRoutes()
                .viaRouter("TestRt")
                .withDestNetwork(1)
                .withSourceNetwork(0)
                .withCost(1)
                .addRoute()
                .buildRoutes()
                .build();
        Assertions.assertTrue(builderTopology.equals(topology));
    }

    private ArrayList<TopologyNetwork> prepareNetwork(Router router) {
        ArrayList<TopologyNetwork> topologyNetworks = new ArrayList<>();
        NextHop nextHop = new NextHop(router, 1);
        CalcRoute calcRoute = new CalcRoute(nextHop, 1);

        NextHop nextHop1 = new NextHop(router, 0);
        CalcRoute calcRoute1 = new CalcRoute(nextHop1, 1);

        CalculatedRouteList calcRoutes = new CalculatedRouteList<>();
        CalculatedRouteList calcRoutes2 = new CalculatedRouteList();
        calcRoutes2.add(0, calcRoute1);
        calcRoutes2.add(1, new CalcRoute(new NextHop(null, 1), null));
        calcRoutes.add(1, calcRoute);
        calcRoutes.add(0, new CalcRoute(new NextHop(null, 0), null));

        TopologyNetwork tpN1 = new TopologyNetwork();
        tpN1.setCreator("Docker");
        tpN1.setMask(24);
        tpN1.setIPAddress("192.168.1.0");
        tpN1.setInternet(true);
        tpN1.setName("TestNet1");
        tpN1.setCalcRoutes(calcRoutes);
        topologyNetworks.add(tpN1);

        TopologyNetwork tpN2 = new TopologyNetwork();
        tpN2.setCreator("Docker");
        tpN2.setMask(24);
        tpN2.setIPAddress("192.168.2.0");
        tpN2.setInternet(false);
        tpN2.setName("TestNet2");
        tpN2.setCalcRoutes(calcRoutes2);
        topologyNetworks.add(tpN2);

        return topologyNetworks;
    }
}
