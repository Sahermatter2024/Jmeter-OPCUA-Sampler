# JMeter OPC UA Sampler

A JMeter plugin for sampling OPC UA servers. This plugin includes samplers for connecting to OPC UA servers, reading values, publishing values, and closing connections.

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

## Usage

### OPC UA Connection Sampler

1. Add a `Thread Group` to your test plan.
2. Add `OPC UA Connection Sampler` to the `Thread Group`.
3. Configure the IP address, port, protocol, timeout, and keepalive settings.

### OPC UA Read Single

1. Add `OPC UA Read Single` to the `Thread Group`.
2. Configure the Node ID namespace index, identifier, timeout, expected value, and assertion settings.

### OPC UA Read Multiple

1. Add `OPC UA Read Multiple` to the `Thread Group`.
2. Configure up to 10 Node ID namespace indices, identifiers, expected values, timeout, and assertion settings.

### OPC UA Publish Sampler

1. Add `OPC UA Publish Sampler` to the `Thread Group`.
2. Configure the Node ID namespace index, identifier, and value to write.

### OPC UA Close Sampler

1. Add `OPC UA Close Sampler` to the `Thread Group`.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

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

