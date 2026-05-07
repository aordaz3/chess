# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmJ43h+P40DsIyMQinAEbSHACgwAAMhAWSFFhzBOtQ-rNG0XS9AY6j5GgBGKp8kIgr8-yArR6HwuUQEYS8hHlqpMHrPofw7F80KPMBFRvl2gnCeKGJCSJBJEmApL2RUw78gyTJTipXI3jS+6UsuMBihKboynKZbvEqpj+CiYA+O8Ao9n2MAYno65BaSKrBhquXxZyIW8neSblPZtSZf29kgdU-oxRABgAHIQGAUZolGMZxoUoGYcgqaNOmACM3TZvyebzNBRYlvUPjTJe0BIAAXigux0U2yVMmlaBGFuvnqFVNUbu6W4NXZzoOSA8QoCAGoaTsSUlc9AKYIV6qyiGMDvQxV3XZJpYAJJoCA0AouATTmZpvUoLGik6T6Yn1A042TWMOaqDNBZjPN0CLcteqrRtuwwNtDH+PYEAQOu-3boYPm3n5MCHnIKDPvEGIxdoc6hZVlQRXAE5qDAaAoJsAaXlR6DlQuApVR2VBItLL6XTd7b6aWXMetrjXA4ZQVfLBMuIfWkJXMjSuo40uFOJjxtqeRZvUZbjH0aYXi+AEXgoOgMRxIkfsB65vhYGJgqgWj0gRgJEbtBG3Q9PJqiKcMFGywNTW6drhmZ+byHu-rQMqy6Yeni5wnh+5aieYzlLHaORgoNwx6u+g54d2g-MVQ6FQRRwrdMoYuvaNqur6gXSGmE3Btlw5IxXIDlR5zAS+YCXEk1C8y850NJRgGj9uY9juPQa5jLruHMCqD4+4AGa9gglgNp7TE+7t67+Ng4oagJaIYAAHElQaEjtvKSQCE7J3sEqDO3drZ6Rsv6cY08LbaW1qXJEyAcggJzC5NEeC1C1xJA3XcAtWaMjAFzLu8FC69wVveYUkVxRPkvLFeUaDChzyVmdMe8hTBfQ1OKKgJokDri4WQj8yCdaXhFOCTa8NEbxi3r6HOaMMbryxtNfMc1iyEwVMTeIpNNoNkGjbYa2FRpOAmlos+ujCz6NLEtCiJitqNgBprZmFD6Q4LAEQ1QGIGFhQHswqhlYEAwFgXMD0PCsEungBAPsD1+oAB4iE8nKFIpBsJ-RwCSdwcAil0lKkyZvGRPpo6JOSUUgoJSYnaCyeYiotsGgn3XssaJagCwNHGF0kG0gCxjXCMEQIIJNjxF1CgN0nI9jfGSKANUMzIKLG+F0tqSpVkXBgJ0K279vYsQ4AAdjcE4FATgYgRmCHAbiAA2eAotgGJXAWow2jRWgdBgXA0YYwuEm06UqDZJk0LmNXjI-O3d-ljHWSsjBFTt6q3ZuiIhGIRZHhQEQkh9cyFUhZvSKJTIaFcOCYLIU9QopsPVvIWUnCEFxNeQveo-DgCCNVMItAojkASIQSvB4uTZF6nkacRR0YEb9XKfy+eUlNFTVzA4-GTiiauKgOtUxe9gYHxGujGxp8dGzUcQtQxyrVXuPfldXFPjm5IoxUqDEMK5gkv7kLZhj4nkNIEfS5Wqt+nSGyXyr8DIlQDIlV+KVYFoVBsGfUYZoz1WJmTJYo+ds8KYx9UMkZgQ9lNg-ixSwrcEAQClrEJACQwB5r7IWmAAApCA4o3WGH8Asx6RRE1R3Ue85kskehdIIn852PwEDADzVAfJBaoBQrTe7UF-q4QQroW7UyA6h2UFHdACdkarIhtsgy1WAArWtaAUU1vFJilAhI66khxTIPFzcqFEu7o6xczryWsLVlececVJGer4ewrcn02UwBEWI7l865a8rXlzIV3AOBKPFao5W0qdV2L1XjAmzijFuLMfvCxh8NFIdlTjeVaGlUrRVWTN+2bzXXstf5fxtqfWPsVs+lhEoMkfvlD6+WISEUJLYwI-9RUokVg4BAMWaBOowCtGiP1a8MmidUB1Lq1ociwaRvBqp2qHbIblfqhVhqSoonPTkLDGqcNavTFpgj58DUGIM2ejyFGGICe+oZjym02ZJOgJWM0NZwzZ07FrcF9axTJJg6K5RhR1Ptu1cEXVOnUOKu88Gc0MBaz1jjRhMzViYtxcI7p4jSXqypb8450wVGm71H3Se+jka1jYEHcO1dUA1j5S46SiKzJsBaHRPW2JN756qw3uBoLG8otvKG9hhNuHk2WcpoxA5vsoBDsDiW4OS35SIGDLAYA2B6uEDyAUGALbD5treQ0WO8dE7J2MIgvOo34XVRumObgeAgmM28X3ekQ826cz7G9nh5QIrSGHj1lANRLB6nFPYNr-ceOLzjYOaR-Ld5bsyxAsCGX4RTa1e0je79MBAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
