# workshift-system

![build](https://github.com/hamsatom-psql/workshift-system/actions/workflows/gradle.yml/badge.svg)

## A small RESTful backend for a workshift system

The system should be able to:

- Create a user
- Create a shop
- Add a user to a shop
- Create a shift
- Add a user to a shift at a shop

### Rules:

- A user can work in different shops
- No user is allowed to work in the same shop for more than 8 hours, within a 24-hour window.
- No user can work more than 5 days in a row in the same shop.
- A user can not work in multiple shops at the same time.

## Running the solution

Clone and run the solution in Docker by executing

```shell
git clone git@github.com:hamsatom-psql/workshift-system.git && docker build --tag workshift-system workshift-system/ && docker run -p 8080:8080 workshift-system
```

API's base url is [localhost:8080](http://localhost:8080)  
Swagger at [localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### API locally

1. build by executing

```shell
./gradlew build
```

2. run Java application with main class [SpringBootMainClass](src/main/java/org/SpringBootMainClass.java)
