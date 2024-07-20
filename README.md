# JMeter OPC UA Sampler

A JMeter plugin for sampling OPC UA servers. This plugin includes samplers for connecting to OPC UA servers, reading values, publishing values, and closing connections.

## Samplers
- **OPC UA Connection Sampler**: Establishes a connection to an OPC UA server.
- **OPC UA Read Single Sampler**: Reads a single value from the OPC UA server.
- **OPC UA Read Multiple Sampler**: Reads multiple values from the OPC UA server and performs assertions.
- **OPC UA Publish Sampler**: Writes a value to the OPC UA server.
- **OPC UA Close Sampler**: Closes the connection to the OPC UA server.

## Notes

- This sampler publishes and reads only **Boolean** variables, using OPUCA-NodeID structure (ns=;i=) `for example: ns=4;i=17`.
- The OPC UA server should have no security and allow anonymous users.

## Features

- Connect to OPC UA servers with different protocols (`opc.tcp`, `opc.https`, `opc.wss`)
- Read values from multiple Node IDs with assertions
- Publish values to Node IDs
- Close connections to OPC UA servers
- Configurable timeouts and keep-alive settings
- Developed by *Mohammed Hlayel*; [Help and Update](https://github.com/Sahermatter2024)

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/Sahermatter2024/Jmeter-OPCUA-Sampler.git
    cd Jmeter-OPCUA-Sampler
    ```

2. Build the project using Maven:
    ```sh
    mvn clean package
    ```

3. Copy the generated JAR file from the `target` directory to the `lib/ext` directory of your JMeter installation.
4. Restart JMeter.

**OR**
- Download and Install the plugin from the jmeter-plugins [Plugins Manager](https://jmeter-plugins.org/install/Install/). Search for "OPCUA Samplers by Mohammed Hlayel".

## Requirements

- Tested on JMeter version 5.6.3

## Usage

### OPC UA Connection Sampler

1. Add a `Thread Group` to your test plan.
2. Add `OPC UA Connection Sampler` to the `Thread Group`.
3. Configure the IP address, port, protocol, timeout, and keepalive settings.
* **IP Address**: The IP address of the OPC UA server.
* **Port**: The port of the OPC UA server.
* **Protocol**: The protocol to use (`opc.tcp`, `opc.https`, `opc.wss`).
* **Timeout**: The connection timeout in milliseconds. If left empty, the default value of 2000 ms will be used. This setting specifies how long the sampler should wait for a connection before timing out.
* **Keepalive**: The keepalive interval in milliseconds. If left 0 or empty, the connection will remain active until it is closed using the close sampler. This setting ensures the connection is periodically checked to keep it alive.

**Example:**
_IP Address: 192.168.1.1
Port: 4840
Protocol: opc.tcp
Timeout: 5000
Keepalive: 10000_

### OPC UA Read Single

1. Add `OPC UA Read Single` to the `Thread Group`.
2. Configure the Node ID namespace index, identifier, timeout, expected value, and assertion settings.
* **NodeId Namespace Index**: The namespace index of the NodeId.
* **NodeId Identifier**: The identifier of the NodeId.
* **Timeout**: The read timeout in milliseconds. If left empty, the default value of 3000 ms will be used. This specifies the maximum time the sampler will wait to read the value.
* **Expected Value**: The expected value for assertion. The sampler will compare the read value against this expected value if assertions are not ignored.
* **Ignore Assertion**: If checked, the assertion will be ignored. This can be useful for debugging or when you want to capture the read values without triggering assertion failures.

**Example: (ns=4;i=3)**
_NodeId Namespace Index: 4
NodeId Identifier: 3
Timeout: 3000
Expected Value: true
Ignore Assertion: unchecked_

### OPC UA Read Multiple

1. Add `OPC UA Read Multiple` to the `Thread Group`.
2. Configure up to 10 Node ID namespace indices, identifiers, expected values, timeout, and assertion settings.
* **Node IDs**: Multiple fields to input NodeId Namespace Index, NodeId Identifier, and Expected Value.
* **Read Timeout**: The read timeout in milliseconds. If left empty, the default value of 3000 ms will be used. This specifies the maximum time the sampler will wait to read each value.
* **Ignore Assertions**: If checked, assertions will be ignored for all Node IDs.

**Example:**
_NodeId Namespace Index 1: 4
NodeId Identifier 1: 3
Expected Value 1: true

NodeId Namespace Index 2: 4
NodeId Identifier 2: 5
Expected Value 2: false

Read Timeout: 3000
Ignore Assertions: unchecked_


### OPC UA Publish Sampler

1. Add `OPC UA Publish Sampler` to the `Thread Group`.
2. Configure the Node ID namespace index, identifier, and value to write.
* **Namespace Index**: The namespace index of the NodeId.
* **Identifier**: The identifier of the NodeId.
* **Value to Write**: The value to write to the NodeId. This allows you to update the value of a specific NodeId on the OPC UA server.

**Example:**
_Namespace Index: 4
Identifier: 3
Value to Write: true_

### OPC UA Close Sampler

1. Add `OPC UA Close Sampler` to the `Thread Group`.
2. This sampler closes the connection to the OPC UA server. No additional configuration is required. Use this sampler to properly close the connection when it is no longer needed.

## Project Background

This project is part of a larger effort to test the OPC UA protocol for exchanging and communicating with a Digital Twin developed in Unity and a Siemens PLC installed in a remote location over Node-RED.


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing
Any contribution is welcome! If you would like to contribute, please fork the repository and submit a pull request. For major changes, please open an issue first to discuss what you would like to change.

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/YourFeature`).
3. Commit your changes (`git commit -am 'Add some feature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Create a new Pull Request.

## Acknowledgments

This project utilizes the following libraries:

- [Apache JMeter](https://jmeter.apache.org/)
- [Eclipse Milo](https://github.com/eclipse/milo)
- [SLF4J](https://www.slf4j.org/)
- [Guava](https://github.com/google/guava)
- [Netty](https://netty.io/)
- [Netty Channel FSM](https://github.com/digitalpetri/netty-channel-fsm)
- [Strict Machine](https://github.com/digitalpetri/strict-machine)

## Developed by

*Developed by Mohammed Hlayel;*

[Help and Update](https://github.com/Sahermatter2024)