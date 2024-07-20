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
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

public class OPCUAPublishSampler extends AbstractSampler {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggingManager.getLoggerForClass();

    public static final String NAMESPACE_INDEX = "OPCUAPublishSampler.namespaceIndex";
    public static final String IDENTIFIER = "OPCUAPublishSampler.identifier";
    public static final String VALUE_TO_WRITE = "OPCUAPublishSampler.valueToWrite";

    public void setNamespaceIndex(String namespaceIndex) {
        setProperty(NAMESPACE_INDEX, namespaceIndex);
    }

    public String getNamespaceIndex() {
        return getPropertyAsString(NAMESPACE_INDEX);
    }

    public void setIdentifier(String identifier) {
        setProperty(IDENTIFIER, identifier);
    }

    public String getIdentifier() {
        return getPropertyAsString(IDENTIFIER);
    }

    public void setValueToWrite(String valueToWrite) {
        setProperty(VALUE_TO_WRITE, valueToWrite);
    }

    public String getValueToWrite() {
        return getPropertyAsString(VALUE_TO_WRITE);
    }

    @Override
    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());

        OpcUaClient opcUaClient = null;
        String namespaceIndex = getNamespaceIndex();
        String identifier = getIdentifier();
        String valueToWrite = getValueToWrite();

        // Retrieve the opcUaClient object from the JMeter variable
        opcUaClient = (OpcUaClient) getThreadContext().getVariables().getObject("opcUaClient");

        if (opcUaClient != null) {
            try {
                result.sampleStart(); // Start timing

                // Define the NodeId
                int nsIndex = Integer.parseInt(namespaceIndex);
                int id = Integer.parseInt(identifier);
                NodeId nodeId = new NodeId(nsIndex, id);

                // Define the value to write
                boolean value = Boolean.parseBoolean(valueToWrite);

                // Create a DataValue object with the value to write
                DataValue dataValue = new DataValue(new Variant(value));

                // Write the value to the node
                opcUaClient.writeValue(nodeId, dataValue).get();

                log.info("Successfully wrote value " + value + " to node " + nodeId);
                result.setResponseMessage("Successfully wrote value " + value + " to node " + nodeId);
                result.setSuccessful(true);
            } catch (Exception ex) {
                log.error("Error writing value to node: " + ex.getMessage(), ex);
                result.setResponseMessage("Error writing value to node: " + ex.getMessage());
                result.setSuccessful(false);
            } finally {
                result.sampleEnd(); // End timing
            }
        } else {
            log.error("OPC UA client connection is not available.");
            result.setResponseMessage("OPC UA client connection is not available.");
            result.setSuccessful(false);
        }

        return result;
    }
}
