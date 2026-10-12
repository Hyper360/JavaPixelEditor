# Java Pixel Editor

A 2D pixel editor built in Java with JavaFX.

## Get Started

### Requirements

- JDK 21
- Maven

### Setup

```bash
git clone https://github.com/Hyper360/JavaPixelEditor.git
cd JavaPixelEditor
```

You no longer need to confirm that Java and Maven both use Java 21 (Version of Java being used in this project). instead just use the maven wrapper to build and run the project. The wrapper will automatically download the correct version of Maven and use the correct version of Java.

Build and run the application:
```bash
# Linux / MacOS
./mvnw clean javafx:run

# Windows
mvnw.cmd clean javafx:run
```

Run the tests:
```bash
# Linux / MacOS
./mvnw test
# Windows
mvnw.cmd test
```

> [!NOTE]
> Maven downloads JavaFX automatically, so you don't need to install the SDK.

### VS Code
Install the **Extension Pack for Java**, open the repository folder, and ensure VS Code is using JDK 21.
