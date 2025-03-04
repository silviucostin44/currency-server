# Currency Converter App Project

This is a Java / Maven / Spring Boot (version 3.4.3) application. It exposes an API for currency conversion.

## How to Run

This application is packaged as a war which has Tomcat embedded. No Tomcat or JBoss installation is necessary. You run
it using the ```java -jar``` command.

* Clone this repository
* Make sure you are using JDK 17 and Maven 3.x
* You can build the project and run the tests by running ```mvn clean package```, or only build without the tests with ```mvn clean package -DskipTests``` 
* Once successfully built, you can run the service by one of these two methods:

```
        java -jar target/currency-server-0.0.1-SNAPSHOT.jar
or
        mvn spring-boot:run 
        (the second option is doing both building and running)
```

* Check the stdout or boot_example.log file to make sure no exceptions are thrown

Once the application runs you should see something like this

```
2025-03-04T20:57:13.827+02:00  INFO 15452 --- [currency-server] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-03-04T20:57:13.875+02:00  INFO 15452 --- [currency-server] [           main] o.e.c.CurrencyServerApplication          : Started CurrencyServerApplication in 7.555 seconds (process running for 9.863)
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
