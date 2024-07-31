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
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class OPCUAReadSingleSamplerGui extends AbstractSamplerGui {
    private static final Logger log = LogManager.getLogger(OPCUAReadSingleSamplerGui.class);

    private JTextField namespaceIndexField;
    private JTextField identifierField;
    private JTextField timeoutField;
    private JTextField expectedValueField;
    private JCheckBox ignoreAssertionCheckBox;

    public OPCUAReadSingleSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        JPanel mainPanel = new VerticalPanel();
        mainPanel.add(makeTitlePanel());

        namespaceIndexField = new JTextField(5);
        identifierField = new JTextField(5);
        timeoutField = new JTextField(5);
        expectedValueField = new JTextField(5);
        ignoreAssertionCheckBox = new JCheckBox("Ignore Assertion");

        // Add action listener to ignoreAssertionCheckBox
        ignoreAssertionCheckBox.addActionListener(e -> expectedValueField.setEnabled(!ignoreAssertionCheckBox.isSelected()));

        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2));
        fieldsPanel.add(new JLabel("NodeId Namespace Index (ns=):"));
        fieldsPanel.add(namespaceIndexField);
        fieldsPanel.add(new JLabel("NodeId Identifier (i=):"));
        fieldsPanel.add(identifierField);
        fieldsPanel.add(new JLabel("Timeout (ms):"));
        fieldsPanel.add(timeoutField);
        fieldsPanel.add(new JLabel("Expected Value (true/false):"));
        fieldsPanel.add(expectedValueField);
        fieldsPanel.add(ignoreAssertionCheckBox);

        mainPanel.add(fieldsPanel);
        add(mainPanel, BorderLayout.CENTER);

        // Set default value for timeoutField
        timeoutField.setText("3000");

        // Add developer information and hyperlink
        JPanel footerPanel = getjPanel();

        add(footerPanel, BorderLayout.SOUTH);
    }

    private static @NotNull JPanel getjPanel() {
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
        return footerPanel;
    }

    @Override
    public String getLabelResource() {
        return "opcuaReadSingleSampler_title";
    }

    @Override
    public String getStaticLabel() {
        return "OPC UA Read Single";
    }

    @Override
    public TestElement createTestElement() {
        OPCUAReadSingleSampler sampler = new OPCUAReadSingleSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        element.setProperty(OPCUAReadSingleSampler.NODE_ID_NAMESPACE_INDEX, namespaceIndexField.getText());
        element.setProperty(OPCUAReadSingleSampler.NODE_ID_IDENTIFIER, identifierField.getText());
        element.setProperty(OPCUAReadSingleSampler.TIMEOUT, timeoutField.getText().isEmpty() ? "3000" : timeoutField.getText());
        element.setProperty(OPCUAReadSingleSampler.EXPECTED_VALUE, expectedValueField.getText());
        element.setProperty(OPCUAReadSingleSampler.IGNORE_ASSERTION, ignoreAssertionCheckBox.isSelected());
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof OPCUAReadSingleSampler) {
            OPCUAReadSingleSampler sampler = (OPCUAReadSingleSampler) element;
            namespaceIndexField.setText(sampler.getNodeIdNamespaceIndex());
            identifierField.setText(sampler.getNodeIdIdentifier());
            timeoutField.setText(sampler.getTimeout().equals("0") ? "3000" : sampler.getTimeout());
            expectedValueField.setText(sampler.getExpectedValue());
            ignoreAssertionCheckBox.setSelected(sampler.isIgnoreAssertion());
            expectedValueField.setEnabled(!ignoreAssertionCheckBox.isSelected());
        }
    }

    @Override
    public void clearGui() {
        super.clearGui();
        namespaceIndexField.setText("");
        identifierField.setText("");
        timeoutField.setText("3000");  // Default to 3000
        expectedValueField.setText("");
        ignoreAssertionCheckBox.setSelected(false);
        expectedValueField.setEnabled(true);
    }
}
