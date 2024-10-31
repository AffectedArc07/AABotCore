# AABotCore

Javacord bot framework focussed around OOP slash command usage

## About

This project exists as a means of making my bot framework more accessible, mainly so I can manage other projects without keeping all my bots in one monolith repo. This framework is far from perfect and lacks a lot, but I use it so that slash commands can have a proper OOP implementation while also self-handling registration and re-registration from the Discord API.

## Usage

Step 1, add the following to your `pom.xml` repositories

```xml
        <repository>
            <id>github</id>
            <url>https://maven.pkg.github.com/AffectedArc07/AABotCore</url>
        </repository>
```

Step 2, add the following to your dependencies

```xml
        <dependency>
            <groupId>me.aa07</groupId>
            <artifactId>botcore</artifactId>
            <version>1.0.2</version>
            <type>pom</type>
        </dependency>
```

Step 3, use in your project. Create a class that extends `AABotCore` and go off of what methdods need implementing

## License

This project is licensed under GNU GPL-3.0 | You can find a copy inside the `LICENSE` file.
