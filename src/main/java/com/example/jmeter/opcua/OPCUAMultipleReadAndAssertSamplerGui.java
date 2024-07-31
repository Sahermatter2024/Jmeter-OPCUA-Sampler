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

public class OPCUAMultipleReadAndAssertSamplerGui extends AbstractSamplerGui {
    private static final Logger log = LogManager.getLogger(OPCUAMultipleReadAndAssertSamplerGui.class);

    private static final int MAX_FIELDS = 10;
    private JTextField[] nodeIdNamespaceIndices;
    private JTextField[] nodeIdIdentifiers;
    private JTextField[] expectedValues;
    private JTextField timeoutField;
    private JCheckBox ignoreAssertionsCheckBox;

    public OPCUAMultipleReadAndAssertSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        JPanel mainPanel = new VerticalPanel();
        mainPanel.add(makeTitlePanel());

        nodeIdNamespaceIndices = new JTextField[MAX_FIELDS];
        nodeIdIdentifiers = new JTextField[MAX_FIELDS];
        expectedValues = new JTextField[MAX_FIELDS];
        timeoutField = new JTextField(5);
        ignoreAssertionsCheckBox = new JCheckBox("Ignore Assertions");

        // Add action listener to ignoreAssertionsCheckBox
        ignoreAssertionsCheckBox.addActionListener(e -> {
            boolean ignore = ignoreAssertionsCheckBox.isSelected();
            for (JTextField expectedValueField : expectedValues) {
                expectedValueField.setEnabled(!ignore);
            }
        });

        JPanel nodeIdsPanel = new JPanel(new GridBagLayout());
        nodeIdsPanel.setBorder(BorderFactory.createTitledBorder("Node IDs"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        for (int i = 0; i < MAX_FIELDS; i++) {
            nodeIdNamespaceIndices[i] = new JTextField(3);
            nodeIdIdentifiers[i] = new JTextField(3);
            expectedValues[i] = new JTextField(3);

            gbc.gridx = 0;
            gbc.gridy = i;
            nodeIdsPanel.add(new JLabel("Namespace Index (ns=) " + ":"), gbc);

            gbc.gridx = 1;
            nodeIdsPanel.add(nodeIdNamespaceIndices[i], gbc);

            gbc.gridx = 2;
            nodeIdsPanel.add(new JLabel("Identifier (i=)" + ":"), gbc);

            gbc.gridx = 3;
            nodeIdsPanel.add(nodeIdIdentifiers[i], gbc);

            gbc.gridx = 4;
            nodeIdsPanel.add(new JLabel("Expected Value (true/false) " + ":"), gbc);

            gbc.gridx = 5;
            nodeIdsPanel.add(expectedValues[i], gbc);
        }

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        settingsPanel.add(new JLabel("Read Timeout (ms):"), gbc);

        gbc.gridx = 1;
        settingsPanel.add(timeoutField, gbc);

        gbc.gridx = 2;
        settingsPanel.add(ignoreAssertionsCheckBox, gbc);

        mainPanel.add(nodeIdsPanel);
        mainPanel.add(settingsPanel);
        add(mainPanel, BorderLayout.CENTER);

        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFooterPanel() {
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
        return "opcuaMultipleReadAndAssertSampler_title";
    }

    @Override
    public String getStaticLabel() {
        return "OPC UA Read Multiple";
    }

    @Override
    public TestElement createTestElement() {
        OPCUAMultipleReadAndAssertSampler sampler = new OPCUAMultipleReadAndAssertSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        for (int i = 0; i < MAX_FIELDS; i++) {
            element.setProperty(OPCUAMultipleReadAndAssertSampler.NODE_ID_NAMESPACE_INDEX + (i + 1), nodeIdNamespaceIndices[i].getText());
            element.setProperty(OPCUAMultipleReadAndAssertSampler.NODE_ID_IDENTIFIER + (i + 1), nodeIdIdentifiers[i].getText());
            element.setProperty(OPCUAMultipleReadAndAssertSampler.EXPECTED_VALUE + (i + 1), expectedValues[i].getText());
        }
        element.setProperty(OPCUAMultipleReadAndAssertSampler.TIMEOUT, timeoutField.getText());
        element.setProperty(OPCUAMultipleReadAndAssertSampler.IGNORE_ASSERTIONS, ignoreAssertionsCheckBox.isSelected());
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof OPCUAMultipleReadAndAssertSampler) {
            OPCUAMultipleReadAndAssertSampler sampler = (OPCUAMultipleReadAndAssertSampler) element;
            for (int i = 0; i < MAX_FIELDS; i++) {
                nodeIdNamespaceIndices[i].setText(sampler.getNodeIdNamespaceIndex(i + 1));
                nodeIdIdentifiers[i].setText(sampler.getNodeIdIdentifier(i + 1));
                expectedValues[i].setText(sampler.getExpectedValue(i + 1));
            }
            timeoutField.setText(sampler.getTimeout());
            ignoreAssertionsCheckBox.setSelected(sampler.getIgnoreAssertions());

            // Set the state of the expected values fields based on the checkbox
            boolean ignore = ignoreAssertionsCheckBox.isSelected();
            for (JTextField expectedValueField : expectedValues) {
                expectedValueField.setEnabled(!ignore);
            }
        }
    }

    @Override
    public void clearGui() {
        super.clearGui();
        for (int i = 0; i < MAX_FIELDS; i++) {
            nodeIdNamespaceIndices[i].setText("");
            nodeIdIdentifiers[i].setText("");
            expectedValues[i].setText("");
        }
        timeoutField.setText("");
        ignoreAssertionsCheckBox.setSelected(false);

        // Enable expected value fields by default
        for (JTextField expectedValueField : expectedValues) {
            expectedValueField.setEnabled(true);
        }
    }
}
