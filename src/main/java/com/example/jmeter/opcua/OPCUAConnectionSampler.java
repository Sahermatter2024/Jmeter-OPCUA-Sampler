/*
 * Copyright (c) 2024 Mohammed Hlayel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * This project includes the use of third-party libraries. For more information, please refer to the NOTICE file.
 */

package com.example.jmeter.opcua;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.util.EndpointUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OPCUAConnectionSampler extends AbstractSampler {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggingManager.getLoggerForClass();

    public static final String IP_ADDRESS = "OPCUAConnectionSampler.ipAddress";
    public static final String PORT = "OPCUAConnectionSampler.port";
    public static final String PROTOCOL = "OPCUAConnectionSampler.protocol";
    public static final String TIMEOUT = "OPCUAConnectionSampler.timeout";
    public static final String KEEPALIVE = "OPCUAConnectionSampler.keepalive";

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void setIpAddress(String ipAddress) {
        setProperty(IP_ADDRESS, ipAddress);
    }

    public String getIpAddress() {
        return getPropertyAsString(IP_ADDRESS);
    }

    public void setPort(String port) {
        setProperty(PORT, port);
    }

    public String getPort() {
        return getPropertyAsString(PORT);
    }

    public void setProtocol(String protocol) {
        setProperty(PROTOCOL, protocol);
    }

    public String getProtocol() {
        return getPropertyAsString(PROTOCOL);
    }

    public void setTimeout(String timeout) {
        setProperty(TIMEOUT, timeout);
    }

    public String getTimeout() {
        return getPropertyAsString(TIMEOUT);
    }

    public void setKeepalive(String keepalive) {
        setProperty(KEEPALIVE, keepalive);
    }

    public String getKeepalive() {
        return getPropertyAsString(KEEPALIVE);
    }

    @Override
    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());

        OpcUaClient opcUaClient = null;
        String ipAddress = getIpAddress();
        String port = getPort();
        String protocol = getProtocol();
        String endpointUrl = String.format("%s://%s:%s", protocol, ipAddress, port);

        long timeout;
        long keepalive;

        try {
            timeout = Long.parseLong(getTimeout());
        } catch (NumberFormatException ex) {
            timeout = 2000; // Default timeout of 2000 ms
        }

        try {
            keepalive = Long.parseLong(getKeepalive());
        } catch (NumberFormatException ex) {
            keepalive = 0; // Default keepalive of 0 ms (no keepalive)
        }

        // Log the debug information
        log.info("IP Address: " + ipAddress);
        log.info("Port: " + port);
        log.info("Protocol: " + protocol);
        log.info("Constructed Endpoint URL: " + endpointUrl);
        log.info("Timeout: " + timeout);
        log.info("Keepalive: " + keepalive);

        try {
            result.sampleStart(); // Start timing

            // Retrieve endpoints using DiscoveryClient
            CompletableFuture<List<EndpointDescription>> endpointsFuture = DiscoveryClient.getEndpoints(endpointUrl);
            List<EndpointDescription> endpoints = endpointsFuture.get(timeout, TimeUnit.MILLISECONDS);

            // Log endpoints for debugging
            log.info("Discovered Endpoints: " + endpoints);

            // Choose the desired endpoint from the list, for example, the first one
            EndpointDescription selectedEndpoint = endpoints.get(0);

            // Update the endpoint URL if needed
            EndpointDescription updatedEndpoint = EndpointUtil.updateUrl(selectedEndpoint, ipAddress);

            // Log updated endpoint
            log.info("Selected Endpoint: " + selectedEndpoint);
            log.info("Updated Endpoint: " + updatedEndpoint);

            // Build the client configuration
            OpcUaClientConfig clientConfig = new OpcUaClientConfigBuilder()
                    .setEndpoint(updatedEndpoint)
                    .setRequestTimeout(UInteger.valueOf(timeout))
                    .build();

            // Create an instance of OpcUaClient
            opcUaClient = OpcUaClient.create(clientConfig);
            opcUaClient.connect().get(timeout, TimeUnit.MILLISECONDS);

            log.info("Connected to OPC UA server.");
            result.setResponseMessage("Connected to OPC UA server.");
            result.setSuccessful(true);

            // Store the opcUaClient object in a JMeter variable
            JMeterContextService.getContext().getVariables().putObject("opcUaClient", opcUaClient);

            if (keepalive > 0) {
                // Schedule a task to close the connection after the keepalive period
                OpcUaClient finalOpcUaClient = opcUaClient;
                scheduler.schedule(() -> {
                    try {
                        finalOpcUaClient.disconnect().get();
                        log.info("Disconnected from OPC UA server due to keepalive timeout.");
                    } catch (Exception ex) {
                        log.error("Error disconnecting from OPC UA server: " + ex.getMessage(), ex);
                    }
                }, keepalive, TimeUnit.MILLISECONDS);
            }

        } catch (Exception ex) {
            log.error("Error connecting to OPC UA server: " + ex.getMessage(), ex);
            result.setResponseMessage("Failed to connect to OPC UA server: " + ex.getMessage());
            result.setSuccessful(false);
        } finally {
            result.sampleEnd(); // End timing
        }

        return result;
    }
}
