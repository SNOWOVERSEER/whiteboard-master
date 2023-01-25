# Distributed Shared Whiteboard - Group 3

## Compile

```sh
mvn clean compile assembly:single
```

### Change Directory

```sh
cd target/
```

### Run Manager

Please provide two command line arguments, i.e., the server port and the manager name.

```sh
java -cp distributed-shared-whiteboard-1.0-SNAPSHOT-jar-with-dependencies.jar whiteboard.Manager 8888 Quanchi
```

### Run User

Please provide three command line arguments, i.e., the server IP address, the server port, and the username.

```sh
java -cp distributed-shared-whiteboard-1.0-SNAPSHOT-jar-with-dependencies.jar whiteboard.User 127.0.0.1 8888 David
```
