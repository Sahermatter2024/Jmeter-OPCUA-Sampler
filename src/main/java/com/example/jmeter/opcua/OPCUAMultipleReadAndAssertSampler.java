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
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OPCUAMultipleReadAndAssertSampler extends AbstractSampler {
    private static final Logger log = LogManager.getLogger(OPCUAMultipleReadAndAssertSampler.class);

    private static final int MAX_FIELDS = 10;

    public static final String NODE_ID_NAMESPACE_INDEX = "OPCUAMultipleReadAndAssertSampler.nodeIdNamespaceIndex";
    public static final String NODE_ID_IDENTIFIER = "OPCUAMultipleReadAndAssertSampler.nodeIdIdentifier";
    public static final String EXPECTED_VALUE = "OPCUAMultipleReadAndAssertSampler.expectedValue";
    public static final String TIMEOUT = "OPCUAMultipleReadAndAssertSampler.timeout";
    public static final String IGNORE_ASSERTIONS = "OPCUAMultipleReadAndAssertSampler.ignoreAssertions";

    public String getNodeIdNamespaceIndex(int index) {
        return getPropertyAsString(NODE_ID_NAMESPACE_INDEX + index);
    }

    public String getNodeIdIdentifier(int index) {
        return getPropertyAsString(NODE_ID_IDENTIFIER + index);
    }

    public String getExpectedValue(int index) {
        return getPropertyAsString(EXPECTED_VALUE + index);
    }

    public String getTimeout() {
        return getPropertyAsString(TIMEOUT);
    }

    public boolean getIgnoreAssertions() {
        return getPropertyAsBoolean(IGNORE_ASSERTIONS);
    }

    @Override
    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.sampleStart();

        OpcUaClient opcUaClient = (OpcUaClient) getThreadContext().getVariables().getObject("opcUaClient");
        if (opcUaClient == null) {
            result.setResponseMessage("OPC UA client connection is not available.");
            result.setSuccessful(false);
            result.sampleEnd();
            return result;
        }

        List<NodeId> nodeIds = new ArrayList<>();
        List<String> expectedValues = new ArrayList<>();

        for (int i = 1; i <= MAX_FIELDS; i++) {
            addNodeIdAndExpectedValue(nodeIds, expectedValues, getNodeIdNamespaceIndex(i), getNodeIdIdentifier(i), getExpectedValue(i));
        }

        String timeoutStr = getTimeout();
        int timeout = 2000; // default timeout 2 seconds
        if (timeoutStr != null && !timeoutStr.isEmpty()) {
            try {
                timeout = Integer.parseInt(timeoutStr);
            } catch (NumberFormatException nfe) {
                log.warn("Invalid timeout value provided. Using default value of 2000 ms.");
            }
        }

        boolean ignoreAssertions = getIgnoreAssertions();
        StringBuilder responseMessage = new StringBuilder();
        responseMessage.append("Results:\n");

        try {
            boolean allSuccessful = true;

            for (int i = 0; i < nodeIds.size(); i++) {
                NodeId nodeId = nodeIds.get(i);
                String expectedValue = expectedValues.get(i);

                try {
                    DataValue dataValue = opcUaClient.readValue(0.0, TimestampsToReturn.Both, nodeId)
                            .get(timeout, TimeUnit.MILLISECONDS);
                    Object actualValue = dataValue.getValue().getValue();

                    log.info("NodeId: " + nodeId + ", Actual Value: " + actualValue);

                    if (ignoreAssertions) {
                        if (actualValue == null) {
                            responseMessage.append("Failed to read value from NodeId ").append(nodeId).append(": Node not found or value is null\n");
                            continue;
                        }
                        responseMessage.append("NodeId=").append(nodeId)
                                .append(", Actual=").append(actualValue)
                                .append("\n");
                    } else {
                        if (actualValue == null) {
                            responseMessage.append("Failed to read value from NodeId ").append(nodeId).append(": Node not found or value is null\n");
                            allSuccessful = false;
                            continue;
                        }

                        responseMessage.append("NodeId=").append(nodeId)
                                .append(", Expected=").append(expectedValue)
                                .append(", Actual=").append(actualValue)
                                .append("\n");

                        AssertionResult assertionResult = new AssertionResult("Assertion for NodeId " + nodeId);
                        if (expectedValue != null && !expectedValue.isEmpty()) {
                            if (!actualValue.toString().equals(expectedValue)) {
                                assertionResult.setFailure(true);
                                assertionResult.setFailureMessage("Assertion failed for NodeId " + nodeId + ". Expected: " + expectedValue + ", Actual: " + actualValue);
                                result.addAssertionResult(assertionResult);
                                allSuccessful = false;
                            }
                        } else {
                            assertionResult.setFailure(true);
                            assertionResult.setFailureMessage("Expected value for NodeId " + nodeId + " is empty.");
                            result.addAssertionResult(assertionResult);
                            allSuccessful = false;
                        }
                    }
                } catch (Exception ex) {
                    responseMessage.append("Failed to read value from NodeId ").append(nodeId).append(": ").append(ex.getMessage()).append("\n");
                    log.error("Error reading values from nodes", ex);
                    allSuccessful = false;
                }
            }

            result.setSuccessful(allSuccessful);
            result.setResponseMessage(responseMessage.toString());
            result.setResponseData(responseMessage.toString(), "UTF-8");
        } catch (Exception ex) {
            log.error("Error reading values from nodes", ex);
            result.setResponseMessage("Error reading values from nodes: " + ex.getMessage());
            result.setResponseData("Exception: " + ex.getMessage(), "UTF-8");
            result.setSuccessful(false);
        } finally {
            result.sampleEnd();
        }

        return result;
    }

    private void addNodeIdAndExpectedValue(List<NodeId> nodeIds, List<String> expectedValues, String namespaceIndexStr, String identifierStr, String expectedValue) {
        if (namespaceIndexStr != null && !namespaceIndexStr.isEmpty() && identifierStr != null && !identifierStr.isEmpty()) {
            try {
                int namespaceIndex = Integer.parseInt(namespaceIndexStr);
                int identifier = Integer.parseInt(identifierStr);
                nodeIds.add(new NodeId(namespaceIndex, identifier));
                expectedValues.add(expectedValue);
            } catch (NumberFormatException nfe) {
                log.warn("Invalid NodeId format: ns=" + namespaceIndexStr + ", id=" + identifierStr);
            }
        }
    }
}
