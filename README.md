# ratpack-hocon

HOCON extension for Ratpack

[![CircleCI](https://circleci.com/bb/minebreaker_tf/ratpack-hocon.svg?style=svg)](https://circleci.com/bb/minebreaker_tf/ratpack-hocon)
![](https://img.shields.io/badge/maturity-experimental-green.svg)


This tiny library provides `HoconConfigSource` to use HOCON configuration file
as a Ratpack `ConfigSource`.

[Ratpack](https://ratpack.io/)  
[Ratpack manual for config](https://ratpack.io/manual/current/config.html)  
[Typesafe Config](https://lightbend.github.io/config/)  
[HOCON](https://github.com/lightbend/config/blob/master/HOCON.md)  


## How to use

Instantiate `HoconConfigSource` and give it to the `ServerConfig`.

```java
ServerConfig serverConfig = ServerConfig.builder()
                                        .add( new HoconConfigSource() )
                                        .build();
```

`application.conf`

```
// Don't forget to add `ratpack` prefix.
ratpack {
    server.development: true
    server.port: 8000
}
```

`HoconConfigSource` will use `Config.load()` by default.
You can pass `Config` for flexible behavior.
