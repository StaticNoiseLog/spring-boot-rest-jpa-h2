# Spring Initalizr

## Dependencies

These dependencies were selected:

* Spring Data JPA
* REST Repositories
* H2 Database
* Spring Security
* Spring Boot Actuator

# Gradle

## Rename Project

Renaming a Gradle project requires changing the name in `settings.gradle`.

# API

## REST

With a single annotation `@RepositoryRestResource` on
the [CatRepository](src/main/java/com/example/demo/CatRepository.java)
interface, [the model class Cat](src/main/java/com/example/demo/Cat.java) is exposed as a HATEOAS REST endpoint.

The URL is automatically derived from the model class name (`Cat -> cats`). In our example the HATEOAS REST endpoint is:

<http://localhost:8080/cats>

## Administration Endpoints

<http://localhost:8080/actuator> (Spring Boot Actuator)

<http://localhost:8080/h2-console> (in-memory DB, see H2 below)

# Persistence

## JPA

Use the `@Entity` annotation on [model class](src/main/java/com/example/demo/Cat.java).

The `@GeneratedValue` annotation on the primary key means that the default `GenerationType.AUTO` strategy is used, which
lets the persistence provider choose the generation strategy.

Hibernate (the default JPA persistence provider for Spring Boot) selects a generation strategy based on the database
specific dialect. For most popular databases, including H2, it selects `GenerationType.SEQUENCE`.

The
article ["How to generate primary keys with JPA and Hibernate"](https://thoughts-on-java.org/jpa-generate-primary-keys/)
explains alternatives to this default behavior.

Provide a `@RepositoryRestResource` repository interface.

## H2

It is enough to bring in the H2 dependency and JPA will use the in-memory H2 DB by default.
Spring Initializr has provided this dependency in this example:

    runtimeOnly("com.h2database:h2")

You do not have to set anything in `application.properties` for H2, but you could set `spring.datasource.* properties`
if you don't want the defaults.

Manually adding this property to `application.properties` enables the console at <http://localhost:8080/h2-console>:

    spring.h2.console.enabled=true

By default, you get an in-memory DB called `testdb`. In the h2-console you have to use these settings:

* Driver Class: org.h2.Driver
* JDBC URL: jdbc:h2:mem:testdb
* User Name: sa
* Password: <leave empty>

(For the h2-console web app to work your application must act as a web server. The above listed dependencies seem to
bring in all that is needed. Essentially you need `spring-boot-starter-web`.)

# Security

When you add the `spring-boot-starter-security` dependency, the following things happen:

1. Authentication and Authorization: Spring Security adds support for authentication and authorization to your
   application. It allows you to define user roles, permissions, and access rules to protect your endpoints and
   resources.
2. Default Login Page: Spring Security provides a default login page that is automatically displayed when a user tries
   to access a secured endpoint without authentication. Users can enter their credentials (username and password) on
   this login page to authenticate and gain access to protected resources.
3. User Management: Spring Security integrates with various authentication mechanisms and user management systems. By
   default, it provides an in-memory user store with username and password configurations in the application properties
   file. However, it is recommended to configure a persistent user store, such as a database or LDAP, for production
   applications.

## Using the In--Memory User Store

To configure credentials and roles for a simple demo project using the default in-memory user store provided by Spring
Security, you can make use of the application properties file (application.properties or application.yml).

    spring.security.user.name=your-username
    spring.security.user.password=your-password
    spring.security.user.roles=ROLE_USER_READ, ROLE_USER_EAT

The roles are assigned to the user by default. You can specify additional roles separated by commas if needed.

With this configuration, Spring Security will use the provided username, password, and role(s) for authentication and
authorization in your demo project. Note that this is only suitable for simple demo projects and not recommended for
production environments where a persistent user store should be used.

## Setting up Security for Production

1. **Configure User Store**: Replace the default in-memory user store with a persistent user store. This typically
   involves configuring a database or an external user management system like LDAP. You can define user details,
   including usernames, passwords, and roles, in the user store.
2. **Secure Endpoints**: Decide which endpoints or resources need to be secured and specify the necessary access rules.
   You can use annotations like `@Secured` or `@PreAuthorize` on controller methods or configure security rules in a
   separate configuration class.
3. **Define User Roles and Permissions**: Determine the roles and permissions required for your application. Assign
   roles to users and define access rules based on these roles. This can be done through annotations or Spring
   configurations.
4. **Custom Authentication**: If you need to use custom authentication mechanisms, such as integrating with an external
   authentication provider or implementing a single sign-on (SSO) solution, Spring Security provides extension points to
   implement your own authentication providers.
5. **Test and Validate**: Ensure that your security configurations are working as expected by testing the authentication
   and authorization scenarios. Write integration or unit tests to validate the behavior of secured endpoints for
   different user roles and permissions.

## Source Code Examples

By using annotations or configuration files, you can define user roles and permissions in your Spring Boot application.
This allows you to control access to different endpoints based on the user's role, providing a secure environment for
your application.

### Annotations ###

Annotations can be used to secure individual methods or endpoints within your Spring Boot application.

In this example, the @Secured("ROLE_USER") annotation is used to secure the securedEndpoint(). Only users with the
ROLE_USER role will have access to this endpoint. The publicEndpoint() method does not have any security restrictions.

    @RestController
    public class ExampleController {
   
        @GetMapping("/public")
        public String publicEndpoint() {
            return "This is a public endpoint.";
        }
   
        @Secured("ROLE_USER")
        @GetMapping("/secured")
        public String securedEndpoint() {
            return "This is a secured endpoint accessible only by users with the ROLE_USER role.";
        }
    }

### Spring Configuration Files ###

In this example, the `configure(AuthenticationManagerBuilder auth)` method is used to define two users with their roles
and passwords using the in-memory user store. The `configure(HttpSecurity http)` method configures the security rules
for different endpoints. The `/public` endpoint is accessible to all users, while the `/secured` endpoint requires the
USER role.

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                .withUser("user")
                .password("{noop}password")
                .roles("USER")
                .and()
                .withUser("admin")
                .password("{noop}password")
                .roles("ADMIN");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                .antMatchers("/public").permitAll()
                .antMatchers("/secured").hasRole("USER")
                .anyRequest().authenticated()
                .and()
                .httpBasic();
        }
    }

# Web UI

"Dynamic HTML content" refers to web page content that can change or update dynamically without requiring a full page
reload. In a Spring Boot application, Spring MVC is commonly used to serve dynamic HTML content generated on the
server-side. Spring MVC supports a variety of templating technologies, including Thymeleaf, FreeMarker, and JSPs. These
server-side templating technologies allow selective rendering and inclusion of necessary components or templates,
avoiding the need to reload the entire page.

But we can also leverage client-side technologies to generate dynamic HTML content for the web UI of our Spring Boot
application. Typically, we will implement Single-Page Applications (SPAs), possibly using frameworks like React, Vue.js,
or Svelte.

## Static Web Resources

From the perspective of a Spring service, an HTML Single-Page Application (SPA) is served as a static web resource since
it is not dynamically generated on the server using templating.

Spring Boot comes with a pre-configured implementation of ResourceHttpRequestHandler to facilitate serving static
resources. By default, this handler serves static content from any of these directories that are on the classpath:

- `/META-INF/resources`
- `/resources` (yes, this would mean `src/main/resources/resources` if you really want to go there)
- `/static`
- `/public`

Since `src/main/resources` is typically on the classpath by default, we can place any of these directories there.

For example, if we put an `about.html` file inside the `/static` directory in our classpath, then we can access that
file via http://localhost:8080/about.html. Similarly, we can achieve the same result by adding that file in the other
mentioned directories. If more than one of these directories contains an `about.html` file, the directory order given
above determines which `about.html` is will be loaded (`/static` takes precedence over `/public`, for example).

Note that web applications look for `index.html` files by default, this is why you can use <http://localhost:8080/> as
well as <http://localhost:8080/index.html>.

If you are not happy with the default directories for serving static resources, you can override the list with the
property `spring.web.resources.static-locations` in [application.properties](src/main/resources/application.properties):

    spring.web.resources.static-locations=classpath:/webui/,classpath:/static/

In our case we want to store our HTML5 app in [src/main/java/resources/webui](src/main/resources/webui) and preserve
[src/main/java/resources/static](src/main/resources/static) as a secondary location for files that do not need
JavaScript and may be accessed by anyone. Note that this is not a clever way of organizing web content. It just
illustrates the use of `spring.web.resources.static-locations`.

By default, Spring Boot serves all static content under the root part of the request, /**,
i.e. <http://localhost:8080/about.html>. If you wish, you can introduce a so-called **context path** or **application
path**  with the `spring.mvc.static-path-pattern` property
in [application.properties](src/main/resources/application.properties):

    spring.mvc.static-path-pattern=/catapp/**

With this configuration the URL becomes <http://localhost:8080/catapp/about.html>.

See [Serve Static Resources with Spring](https://www.baeldung.com/spring-mvc-static-resources) for more details.

## Security for the UI

As soon as you add `implementation("org.springframework.boot:spring-boot-starter-security")` as a dependency, Spring
will enable basic authentication and send you to a provided default login page at <http://localhost:8080/login>.

By introducing [a security configuration](src/main/java/com/example/demo/SecurityConfig.java) you can tailor access to
HTML pages (and, of course, to REST endpoints).

With `antMatchers`, you can select directories and files. The wildcard `*` matches objects at the same level, while `**`
matches objects at the same level and anything below.
Note that `antMatchers` are applied to the URL used by the client. They do *not* necessarily correspond directly with
the directory structure of the project. Keep this in mind if you want to set `spring.mvc.static-path-pattern`.

### httpBasic() vs. formLogin()
BOOK:
Form authentication involves having a nice HTML form, which can be stylized to match the theme
of the web application. Spring Security even provides a default one (which this chapter shall use).
Form authentication also supports logging out.
Basic authentication has nothing to do with HTML and form rendering but instead involves a popup
baked into every browser. There is no support for customization, and the only way to throw away
credentials is to either shut down or restart the browser. Basic authentication also makes it simple to
authenticate with a command-line tool, such as curl.
In general, by having both form and basic authentication, the application will defer to form-based
authentication in the browser while still allowing basic authentication from command-line tools.



The `http.httpBasic()` and `http.formLogin()` security configurations in Spring Boot are used to authenticate users.

For the browser, both `http.httpBasic()` and `http.formLogin()` are relevant, but http.formLogin() takes precedence.
This means that if the browser supports form-based login, it will be used to authenticate the user. If the browser does
not support form-based login, then HTTP Basic will be used as a fallback mechanism.

For REST API clients calling a URL that is a REST endpoint, not an HTML page, only `http.httpBasic()` will apply. This
is because REST API clients do not typically support form-based login.

`http.httpBasic()`: This configuration enables HTTP Basic authentication, which is a simple authentication mechanism
that sends the user's credentials in the HTTP headers of each request. With this configuration, the browser will
typically display a login dialog box that prompts the user to enter their credentials. The default behavior is to use a
standard HTTP 401 response to indicate that authentication is required, and to send a WWW-Authenticate header with the
realm name. This configuration is often used for REST APIs or other stateless applications.

`http.formLogin()`: This configuration enables form-based authentication, which is a more complex authentication
mechanism that uses a login form to collect the user's credentials. With this configuration, the browser will typically
display a login page that prompts the user to enter their credentials. The default behavior is to use a standard HTML
form to collect the user's credentials, and to redirect the user to a default success URL after successful
authentication. This configuration is often used for web applications that require user sessions.

### ROLE_ Confusion

Starting with Spring Security 4, the `ROLE_` prefix is automatically added by any role-related method. This means the
following in practice:

In [application.properties](src/main/resources/application.properties) you *must not* use the `ROLE_` prefix with
`spring.security.user.roles` (or your service will not even start).

The only method call usages that work are (the same holds true for the `.hasAny*` variants):

    .hasRole("DOG")             // role-based method (adding the ROLE_ prefix is an error)
    .hasAuthority("ROLE_DOG")   // not a role-based method, ROLE_ prefix required

**Bottom line:** Don't use the "ROLE_" string anywhere and work with the `hasRole` or `hasAnyRole` methods.

Yes, you can give unrestricted access to "localhost:8080/index.html" just like it was done for "/cats" by adding an
antMatcher for that specific URL and permitting all access. Here's how you can modify the configuration:

```java
@Override
protected void configure(HttpSecurity http)throws Exception{
        http.authorizeRequests()
        .antMatchers("/cats").permitAll()
        .antMatchers("/index.html").permitAll() // Add this line
        .antMatchers("/secured").hasRole("USER")
        .anyRequest().authenticated()
        .and()
        .httpBasic();
        }
```

By adding `.antMatchers("/index.html").permitAll()`, you are allowing unrestricted access (permit all) to the "
index.html" file. Now, any user, authenticated or not, will be able to access "localhost:8080/index.html" without any
role or authentication requirement.

## Wildcards

Yes, you can use wildcards with `antMatchers` in Spring Security to specify pattern-based URL matching. Here's how you
can achieve the desired configurations:

1. Permit all files in the same folder that start with "read" (read*):

```java
.antMatchers("/folder/read*").permitAll()
```

This configuration allows unrestricted access to any file in the "/folder" directory that starts with "read". For
example, "/folder/readme.txt" or "/folder/readfile.html" will be accessible.

2. Permit all access to the "/unsecure" folder and its contents, including subfolders of arbitrary nesting level:

```java
.antMatchers("/unsecure/**").permitAll()
```

This configuration allows unrestricted access to the "/unsecure" folder and all its contents, including subfolders and
their contents. For example, "/unsecure/file.txt", "/unsecure/subfolder/file.html", and so on, will be accessible.

Note the use of the double asterisks (**) in the pattern to match any number of subdirectories within "/unsecure".

By incorporating these configurations into your Spring Security setup, you can permit access to specific file patterns
and folders with the desired access rules.

spring.web.resources.static-locations=classpath:/webui/
...  http://localhost:8080/

spring.web.resources.static-locations=classpath:/static/,classpath:/webui/

# Testing

Work with JUnit 5 (Jupiter), which is the default for modern Spring releases.

The following configuration is needed in `build.gradle.kts` (Spring Initializr creates adds this):

    tasks.withType<Test> {
        useJUnitPlatform()
    }

## REST API ##

You can test the REST API with `MockMvc`.
See [DemoApplicationTests](src/test/java/com/example/demo/DemoApplicationTests.java).

    @SpringBootTest
    @AutoConfigureMockMvc
    public class DemoApplicationTests {

        @Autowired
        private MockMvc mvc;

        @Test
        public void catsReflectedInRead() throws Exception {
            final MediaType halJson = MediaType.parseMediaType("application/hal+json");
            this.mvc.perform(get("/cats"))
                    .andExpect(status().isOk())
                    ...
        }
    }

## Security in Tests ##

Add this dependency:

    testImplementation("org.springframework.security:spring-security-test")

In a `@SpringBootTest` you can either disable security filters ...

    @AutoConfigureMockMvc(addFilters = false)

... or you can work a with a mock user and arbitrary credentials (username and password are *not* verified):

    @AutoConfigureMockMvc
    @WithMockUser(username = "testuser", password = "testpassword")

# Other Source Code Examples

* [Cloud Native Java](https://github.com/cloud-native-java)
* [Building Microservices (LiveLessons InformIT)](https://github.com/livelessons-spring/building-microservices)