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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OPCUAReadSingleSampler extends AbstractSampler {
    private static final Logger log = LogManager.getLogger(OPCUAReadSingleSampler.class);

    public static final String NODE_ID_NAMESPACE_INDEX = "OPCUAReadSingleSampler.nodeIdNamespaceIndex";
    public static final String NODE_ID_IDENTIFIER = "OPCUAReadSingleSampler.nodeIdIdentifier";
    public static final String TIMEOUT = "OPCUAReadSingleSampler.timeout";
    public static final String EXPECTED_VALUE = "OPCUAReadSingleSampler.expectedValue";
    public static final String IGNORE_ASSERTION = "OPCUAReadSingleSampler.ignoreAssertion";

    public void setNodeIdNamespaceIndex(String namespaceIndex) {
        setProperty(NODE_ID_NAMESPACE_INDEX, namespaceIndex);
    }

    public String getNodeIdNamespaceIndex() {
        return getPropertyAsString(NODE_ID_NAMESPACE_INDEX);
    }

    public void setNodeIdIdentifier(String identifier) {
        setProperty(NODE_ID_IDENTIFIER, identifier);
    }

    public String getNodeIdIdentifier() {
        return getPropertyAsString(NODE_ID_IDENTIFIER);
    }

    public void setTimeout(String timeout) {
        setProperty(TIMEOUT, timeout);
    }

    public String getTimeout() {
        String timeout = getPropertyAsString(TIMEOUT);
        return timeout.isEmpty() ? "3000" : timeout;  // Default to 3000 if empty
    }

    public void setExpectedValue(String expectedValue) {
        setProperty(EXPECTED_VALUE, expectedValue);
    }

    public String getExpectedValue() {
        return getPropertyAsString(EXPECTED_VALUE);
    }

    public void setIgnoreAssertion(boolean ignoreAssertion) {
        setProperty(IGNORE_ASSERTION, ignoreAssertion);
    }

    public boolean isIgnoreAssertion() {
        return getPropertyAsBoolean(IGNORE_ASSERTION);
    }

    @Override
    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.sampleStart();

        OpcUaClient opcUaClient = (OpcUaClient) JMeterContextService.getContext().getVariables().getObject("opcUaClient");
        if (opcUaClient == null) {
            result.setResponseMessage("OPC UA client connection is not available.");
            result.setSuccessful(false);
            result.sampleEnd();
            return result;
        }

        int namespaceIndex = Integer.parseInt(getNodeIdNamespaceIndex());
        int identifier = Integer.parseInt(getNodeIdIdentifier());
        NodeId nodeId = new NodeId(namespaceIndex, identifier);
        long timeout = Long.parseLong(getTimeout());
        String expectedValue = getExpectedValue();
        boolean ignoreAssertion = isIgnoreAssertion();

        try {
            DataValue dataValue = opcUaClient.readValue(0.0, TimestampsToReturn.Both, nodeId).get(timeout, TimeUnit.MILLISECONDS);
            Object actualValue = dataValue.getValue().getValue();

            log.info("Read value from NodeId {}: {}", nodeId, actualValue);

            String responseMessage;
            if (actualValue == null) {
                responseMessage = String.format("Failed to read value from NodeId %s: Node not found or value is null", nodeId);
                result.setSuccessful(false);
            } else {
                responseMessage = String.format("Successfully read value from NodeId %s: %s", nodeId, actualValue);
                if (ignoreAssertion) {
                    result.setSuccessful(true);
                } else {
                    result.setSuccessful(actualValue.toString().equals(expectedValue));
                    if (!actualValue.toString().equals(expectedValue)) {
                        responseMessage += String.format("\nAssertion failed: expected [%s] but found [%s]", expectedValue, actualValue);
                    }
                }
            }

            result.setResponseMessage(responseMessage);
            result.setResponseData(responseMessage, "UTF-8");
        } catch (ExecutionException | InterruptedException | TimeoutException ex) {
            log.error("Error reading value from node", ex);
            result.setResponseMessage("Error reading value from node: " + ex.getMessage());
            result.setResponseData("Exception: " + ex.getMessage(), "UTF-8");
            result.setSuccessful(false);
        } finally {
            result.sampleEnd();
        }

        return result;
    }
}
