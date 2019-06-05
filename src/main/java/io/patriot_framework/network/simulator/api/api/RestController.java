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

package io.patriot_framework.network.simulator.api.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The type RestController provides Http request execution.
 */
public abstract class RestController {
    /**
     * Execute http request string.
     *
     * @param path   the path
     * @param method the method
     * @param ip the ip
     * @param port port
     * @return the string
     */
    public String executeHttpRequest(String path, String method, String ip, Integer port) {

        try {
            URL chainUrl = new URL("http", ip, port, path);
            HttpURLConnection connection = (HttpURLConnection) chainUrl.openConnection();
            try {
                connection.setRequestMethod(method);
                connection.setConnectTimeout(8000);
                connection.connect();
                StringBuilder sb = new StringBuilder();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                }
                return sb.toString();

            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }
}
