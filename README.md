# Getting Started

### Environment requirements:
* Java 11 or newer
* Docker (docker-compose)
* Postman for interact with api - **collection included**

### How to start application:
* start docker compose for creating database: 
<br> In docker-file directory (root) execute command: 
<br>***docker-compose -f docker-compose.yml up***
<br> 
<br>
* initialize database: 
<br> In root directory execute command: ***mvn clean flyway:migrate***
<br>
<br>
* start application in ide or with command: 
<br> In root director execute command ***mvn spring-boot:run***
<br>
<br>
### All steps can be reproduced in IDE.
#### IMPORTANT: ModelMapper required mvn::package to initialize mapper class

### PORTS: 
Application starts on port: 8000
<br> Postgres database start on port: 5432

### X-API-KEY AUTH:
Header name is **X-API-KEY**
<br> Initializing script creates two users:
<br> **id/ x-api-key/ name** 
<br> 1 **/** api_key1 **/** user1
<br> 2 **/** api_key2 **/** user2


