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

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class OPCUAConnectionSamplerGui extends AbstractSamplerGui {
    private static final Logger log = LogManager.getLogger(OPCUAConnectionSamplerGui.class);

    private JTextField ipAddressField;
    private JTextField portField;
    private JComboBox<String> protocolDropdown;
    private JTextField timeoutField;
    private JTextField keepaliveField;

    public OPCUAConnectionSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        JPanel mainPanel = new VerticalPanel();
        mainPanel.add(makeTitlePanel());

        ipAddressField = new JTextField(20);
        portField = new JTextField(5);
        protocolDropdown = new JComboBox<>(new String[]{"opc.tcp", "opc.https", "opc.wss"});
        timeoutField = new JTextField(10);
        keepaliveField = new JTextField(10);

        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2));
        fieldsPanel.add(new JLabel("IP Address:"));
        fieldsPanel.add(ipAddressField);
        fieldsPanel.add(new JLabel("Port:"));
        fieldsPanel.add(portField);
        fieldsPanel.add(new JLabel("Protocol:"));
        fieldsPanel.add(protocolDropdown);
        fieldsPanel.add(new JLabel("Timeout (ms):"));
        fieldsPanel.add(timeoutField);
        fieldsPanel.add(new JLabel("Keepalive (ms):"));
        fieldsPanel.add(keepaliveField);

        mainPanel.add(fieldsPanel);
        add(mainPanel, BorderLayout.CENTER);

        // Add developer information and hyperlink
        JLabel developerLabel = new JLabel("<html><i>Developed by Mohammed Hlayel;</i></html>");
        JLabel githubLink = new JLabel("<html><a href='https://github.com/Sahermatter2024'>Help and Update</a></html>");
        githubLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        githubLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/Sahermatter2024"));
                } catch (Exception ex) {
                    log.error("Error opening link", ex);
                }
            }
        });

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.add(developerLabel);
        footerPanel.add(githubLink);

        add(footerPanel, BorderLayout.SOUTH);
    }

    @Override
    public String getLabelResource() {
        return "opcuaConnectionSampler_title";
    }

    @Override
    public String getStaticLabel() {
        return "OPC UA Connection Sampler";
    }

    @Override
    public TestElement createTestElement() {
        OPCUAConnectionSampler sampler = new OPCUAConnectionSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        element.setProperty(OPCUAConnectionSampler.IP_ADDRESS, ipAddressField.getText());
        element.setProperty(OPCUAConnectionSampler.PORT, portField.getText());
        element.setProperty(OPCUAConnectionSampler.PROTOCOL, protocolDropdown.getSelectedItem().toString());
        element.setProperty(OPCUAConnectionSampler.TIMEOUT, timeoutField.getText().isEmpty() ? "2000" : timeoutField.getText());
        element.setProperty(OPCUAConnectionSampler.KEEPALIVE, keepaliveField.getText().isEmpty() ? "0" : keepaliveField.getText());
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof OPCUAConnectionSampler) {
            OPCUAConnectionSampler sampler = (OPCUAConnectionSampler) element;
            ipAddressField.setText(sampler.getIpAddress());
            portField.setText(sampler.getPort());
            protocolDropdown.setSelectedItem(sampler.getProtocol());
            timeoutField.setText(sampler.getTimeout().equals("0") ? "" : sampler.getTimeout());
            keepaliveField.setText(sampler.getKeepalive().equals("0") ? "" : sampler.getKeepalive());
        }
    }

    @Override
    public void clearGui() {
        super.clearGui();
        ipAddressField.setText("");
        portField.setText("");
        protocolDropdown.setSelectedIndex(0);
        timeoutField.setText("");
        keepaliveField.setText("");
    }
}
