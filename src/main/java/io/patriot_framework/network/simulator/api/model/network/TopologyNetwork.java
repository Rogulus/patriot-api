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

package io.patriot_framework.network.simulator.api.model.network;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.patriot_framework.network.simulator.api.CalculatedRouteList;
import io.patriot_framework.network.simulator.api.model.routes.CalcRoute;

import java.util.Objects;

/**
 * TopologyNetwork class representing docker network with additional informations
 * like calculated routes to other networks.
 */
public class TopologyNetwork extends Network {

    @JsonIgnore
    private CalculatedRouteList<CalcRoute> calcRoutes = new CalculatedRouteList<>();
    @JsonIgnore
    private Boolean internet = false;
    @JsonIgnore
    private Object creator;
    @JsonIgnore
    private String internetInterfaceIP = null;

    /**
     * Instantiates a new TopologyNetwork.
     */
    public TopologyNetwork() {
    }

    /**
     * Instantiates a new TopologyNetwork.
     *
     * @param calcRoutes the calc routes
     * @param name       the name
     */
    public TopologyNetwork(CalculatedRouteList<CalcRoute> calcRoutes, String name) {
        this.calcRoutes = calcRoutes;
        super.setName(name);
    }

    /**
     * Instantiates a new TopologyNetwork.
     *
     * @param calcRoutes the calc routes
     */
    public TopologyNetwork(CalculatedRouteList<CalcRoute> calcRoutes) {
        this.calcRoutes = calcRoutes;
    }

    /**
     * Gets calc routes.
     *
     * @return the calc routes
     */
    public CalculatedRouteList<CalcRoute> getCalcRoutes() {
        return calcRoutes;
    }

    /**
     * Sets calc routes.
     *
     * @param calcRoutes the calc routes
     */
    public void setCalcRoutes(CalculatedRouteList<CalcRoute> calcRoutes) {
        this.calcRoutes = calcRoutes;
    }

    public Boolean getInternet() {
        return internet;
    }

    public void setInternet(Boolean internet) {
        this.internet = internet;
    }


    public void setCreator(Object creator) {
        this.creator = creator;
    }

    @Override
    public Object getCreator() {
        return creator;
    }

    public String getInternetInterfaceIP() {
        return internetInterfaceIP;
    }

    public void setInternetInterfaceIP(String internetInterfaceIP) {
        this.internetInterfaceIP = internetInterfaceIP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopologyNetwork that = (TopologyNetwork) o;
        return calcRoutes.equals(that.calcRoutes) &&
                internet.equals(that.internet) &&
                creator.equals(that.creator) &&
                this.getName().equals(((TopologyNetwork) o).getName()) &&
                this.getIPAddress().equals(((TopologyNetwork) o).getIPAddress()) &&
                this.getMask().equals(((TopologyNetwork) o).getMask());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCalcRoutes(), getInternet(), getCreator(), getInternetInterfaceIP());
    }
}
