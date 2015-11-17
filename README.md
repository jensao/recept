# Recept


## Description

A simple __[Dropwizard](http://www.dropwizard.io)__ (__Jetty__, __Jersey__, __Jackson__) example with __Hibernate/JPA__ persistance and __[TestNG](http://testng.org)__, __[REST-assured](https://github.com/jayway/rest-assured)__, __[process-exec-maven-plugin](https://github.com/bazaarvoice/maven-process-plugin)__, __Hamcreast__ for test.

The datamodel for this project consist of a __Course__ that has zero-to-many __Recipe__ which has zero-to-many __RecipeInstruction__ and __Ingridient__. 

## Getting started

### Maven goals

* __`:> mvn test`__ - To only run the unit test
* __`:> mvn exec:java`__ - To start the application without test
* __`:> mvn clean package`__ - To to package the artifact in to an uber-jar (Apache Maven Shade Plugin is used for this)
* __`:> mvn clean verify`__ - To run integration test (process-exec-maven-plugin, REST-assured, Hamcrest is used)
* __`:>`__

### To run the application

1. __`:> mvn clean package`__
2. __`:> java -jar target/recept-1.0-SNAPSHOT.jar server testConfig.yml`__


### URLs

* DELETE  /courses/{id}
* DELETE  /courses/{id}/recipes/{id}
* GET     /courses
* GET     /courses/{id}
* GET     /courses/{id}/recipes
* GET     /courses/{id}/recipes/{id}
* POST    /courses
* POST    /courses/{id}/recipes
* PUT     /courses/{id}
* PUT     /courses/{id}/recipes/{id}
* [Operational Menu](http://localhost:8081/)


### A curl example
1. __`:> curl -v -H "Content-Type: application/json" -X POST -d '{"title":"Course title","comment":"some cuurse comment"}' http://localhost:8080/courses`__
2. __`:> curl -v -H "Accept: application/json" http://localhost:8080/courses/1	`__
3. __`:> curl -v -H "Content-Type: application/json"  -X POST -d '{"name":"Recipe 1","instructions": ["instruction 1", "instruction 2"]}' http://localhost:8080/courses/1/recipes`__
4. __`:> curl -v -H "Content-Type: application/json"  -X POST -d '{"name":"Recipe 2","instructions": ["instruction 1", "instruction 2"]}' http://localhost:8080/courses/1/recipes`__
5. __`:> curl -v -H "Accept: application/json" http://localhost:8080/courses/1/recipes`__
6. __`:> curl -v -X DELETE http://localhost:8080/courses/1/recipes/2`__


## TODO 

* Implement HATEOAS
* Uniform JSON error response
* Add login capabilities
* Finish Recipe <-> ingredients funct and test
* build frontend in ...? [aurelia](http://aurelia.io/), [vue](http://vuejs.org/), [React](https://facebook.github.io/react/) ?
* Multiple post to /courses/1/recipes creates a exception and a 500, handle this better
* Verify that parallelle updates to /course/{id} is handled correctly.


## Good reading ##

* [RFC 7231 -Request Method Definitions](http://tools.ietf.org/html/rfc7231#section-4.3)