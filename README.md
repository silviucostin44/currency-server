# Currency Converter App Project

This is a Java / Maven / Spring Boot (version 3.4.3) application. It exposes an API for currency conversion.

## How to Run

This application is packaged as a war which has Tomcat embedded. No Tomcat or JBoss installation is necessary. You run
it using the ```java -jar``` command.

* Clone this repository
* Make sure you are using JDK 17 and Maven 3.x
* You can build the project and run the tests by running ```mvn clean package```
* Once successfully built, you can run the service by one of these two methods:

```
        java -jar target/currency-server-0.0.1-SNAPSHOT.war
or
        mvn spring-boot:run
```

* Check the stdout or boot_example.log file to make sure no exceptions are thrown

Once the application runs you should see something like this

```
2017-08-29 17:31:23.091  INFO 19387 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8090 (http)
2017-08-29 17:31:23.097  INFO 19387 --- [           main] com.khoubyari.example.Application        : Started Application in 22.285 seconds (JVM running for 23.032)
```

## About the Service

The service currency conversion REST service. The exposed REST API endpoints are defined in
```org/example/currencyserver/controller/ConversionController.java``` on **port 8080**. (see below)

## API:

### Retrieve a list of available currencies for conversion

```
GET /conversion/v1/currencies
Accept: application/json
Content-Type: application/json

Response example:
[
  {
    "code" : "EUR",
    "name" : "Euro",
  },
  {
    "code": "GBP",
    "name": "Pound sterling",
  },
]
RESPONSE: HTTP 200 (Success)
Complete url: http://localhost:8080/conversion/v1/currencies
```

### Retrieve a computed value for conversion from a source currency (base) to a target currency (quote)

```
GET /conversion/v1/convert/{base}/{quote}?amount=10
Accept: application/json
Content-Type: application/json

Response example:
{
  24.242
}
Response value is computed based on the relation: 1 * base = quote * amount

RESPONSE: HTTP 200 (Success)
Complete url: http://localhost:8080/convert/{base}/{quote}?amount=10
Example url: http://localhost:8080/convert/EUR/USD?amount=10
```