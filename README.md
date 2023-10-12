# workshift-system

![build](https://github.com/hamsatom-psql/workshift-system/actions/workflows/gradle.yml/badge.svg)

## Running the solution

Clone and run the solution in Docker by executing

```shell
git clone git@github.com:hamsatom-psql/workshift-system.git && docker build --tag workshift-system workshift-system/ && docker run -p 8080:8080 workshift-system
```

API's base url is [localhost:8080](http://localhost:8080)

### API locally

1. build by executing

```shell
./gradlew build
```

2. run Java application with main class [SpringBootMainClass](src/main/java/org/SpringBootMainClass.java)
