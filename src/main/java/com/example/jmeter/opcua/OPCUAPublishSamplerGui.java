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

public class OPCUAPublishSamplerGui extends AbstractSamplerGui {
    private static final Logger log = LogManager.getLogger(OPCUAPublishSamplerGui.class);

    private JTextField namespaceIndexField;
    private JTextField identifierField;
    private JTextField valueToWriteField;

    public OPCUAPublishSamplerGui() {
        init();
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        namespaceIndexField.setText(element.getPropertyAsString(OPCUAPublishSampler.NAMESPACE_INDEX));
        identifierField.setText(element.getPropertyAsString(OPCUAPublishSampler.IDENTIFIER));
        valueToWriteField.setText(element.getPropertyAsString(OPCUAPublishSampler.VALUE_TO_WRITE));
    }

    @Override
    public TestElement createTestElement() {
        OPCUAPublishSampler sampler = new OPCUAPublishSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        element.setProperty(OPCUAPublishSampler.NAMESPACE_INDEX, namespaceIndexField.getText());
        element.setProperty(OPCUAPublishSampler.IDENTIFIER, identifierField.getText());
        element.setProperty(OPCUAPublishSampler.VALUE_TO_WRITE, valueToWriteField.getText());
    }

    @Override
    public String getLabelResource() {
        return "opcuapublishsampler_title";
    }

    @Override
    public String getStaticLabel() {
        return "OPC UA Publish Sampler";
    }

    @Override
    public void clearGui() {
        super.clearGui();
        namespaceIndexField.setText("");
        identifierField.setText("");
        valueToWriteField.setText("");
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        JPanel mainPanel = new VerticalPanel();
        mainPanel.add(makeTitlePanel());

        JPanel settingsPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        settingsPanel.add(new JLabel("Namespace Index:"));
        namespaceIndexField = new JTextField();
        settingsPanel.add(namespaceIndexField);

        settingsPanel.add(new JLabel("Identifier:"));
        identifierField = new JTextField();
        settingsPanel.add(identifierField);

        settingsPanel.add(new JLabel("Value to Write:"));
        valueToWriteField = new JTextField();
        settingsPanel.add(valueToWriteField);

        mainPanel.add(settingsPanel);
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
}
