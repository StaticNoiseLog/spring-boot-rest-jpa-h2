# Spring Initalizr

## Dependencies
These dependencies were selected:
* Spring Data JPA
* REST Repositories
* H2 Database

# Gradle

## Rename Project
Renaming a Gradle project requires changing the name in `settings.gradle`.

# API
## REST
With a single annotation `@RepositoryRestResource` your model class is exposed as a HATEOAS REST endpoint.

# Persistence
## JPA
Use the `@Entity` annotation on model class.

Provide a `@RepositoryRestResource` repository interface.

## H2
It is enough to bring in the H2 dependency and JPA will use the in-memory H2 DB by default.
Spring Initializr has provided this dependency in this example:

    runtimeOnly 'com.h2database:h2'

You do not have to set anything in `application.properties` for H2, but you could set `spring.datasource.* properties`
if you don't want the defaults.

Manually adding this property to `application.properties` enables the console at <http://localhost:8080/h2-console>:

    spring.h2.console.enabled=true

By default you get an in-memory DB called "testdb". In the h2-console you have to use these settings:
* Driver Class: org.h2.Driver
* JDBC URL: jdbc:h2:mem:testdb
* User Name: sa
* Password: <leave empty>

(For the h2-console web app to work your application must act as a web server. The above listed dependencies seem to 
bring in all that is needed. Essentially you need `spring-boot-starter-web`.)

# Testing

## REST API ##
You can use MockMvc, without bringing in the entire Spring MVC dependencies.

This was added manually to build.gradle:

    testImplementation 'org.springframework:spring-test'

Java test class:

    @AutoConfigureMockMvc
    public class DemoApplicationTests {

        @Autowired
        private MockMvc mvc;

        @Test
        public void catsReflectedInRead() throws Exception {
            final MediaType halJson = MediaType.parseMediaType("application/hal+json;charset=UTF-8");
            this.mvc.perform(get("/cats"));
            ...
         }
    }


# Other Source Code Examples
* [Cloud Native Java](https://github.com/cloud-native-java)
* [Building Microservices (LiveLessons InformIT)](https://github.com/livelessons-spring/building-microservices)
