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

import java.util.concurrent.ExecutionException;

public class OPCUACloseSampler extends AbstractSampler {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggingManager.getLoggerForClass();

    @Override
    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());

        OpcUaClient opcUaClient = (OpcUaClient) JMeterContextService.getContext().getVariables().getObject("opcUaClient");

        if (opcUaClient != null) {
            try {
                result.sampleStart(); // Start timing
                opcUaClient.disconnect().get();
                log.info("Disconnected from OPC UA server.");
                result.setResponseMessage("Disconnected from OPC UA server.");
                result.setSuccessful(true);
            } catch (InterruptedException | ExecutionException ex) {
                log.error("Error disconnecting from OPC UA server: " + ex.getMessage(), ex);
                result.setResponseMessage("Failed to disconnect from OPC UA server: " + ex.getMessage());
                result.setSuccessful(false);
            } finally {
                result.sampleEnd(); // End timing
            }
        } else {
            log.error("OPC UA client is not connected.");
            result.setResponseMessage("OPC UA client is not connected.");
            result.setSuccessful(false);
            result.sampleEnd(); // End timing
        }

        return result;
    }
}
