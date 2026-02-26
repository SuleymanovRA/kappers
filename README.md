# Data Scout
<p align="center">
  <a href="https://github.com/SuleymanovRA/kappers">English</a> |
  <a href="https://github.com/SuleymanovRA/kappers/blob/master/readme/README_ru_RU.md">Русский</a>
</p>

---

| Technology | Badge |
|:-----------:|:-----:|
| GitHub Actions CI[RS] | [![Java-CI-with-Maven](https://github.com/SuleymanovRA/kappers/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/SuleymanovRA/kappers/actions/workflows/maven.yml) |
| CodeCov | [![codecov](https://codecov.io/gh/SuleymanovRA/kappers/branch/master/graph/badge.svg)](https://codecov.io/gh/SuleymanovRA/kappers) |

## Information about project

The "Data Scout" project is a web-service for providing a fair statistics and raiting of sport analitics (kappers), who sales their forecasts for betting.
Supposed to create smart-contracts on Ethereum blockchain, that will generate tokens of reputation and control transfers.
Users can have one of two roles: KAPPER or USER. 
It is an open-source project. Anyone can connect to it.
For additional information apply to founders. Contacts are below.

### Useful links
- [Manual for local deployment](https://github.com/soufee/kappers/wiki/%D0%98%D0%BD%D1%81%D1%82%D1%80%D1%83%D0%BA%D1%86%D0%B8%D1%8F-%D0%BF%D0%BE-%D1%80%D0%B0%D0%B7%D0%B2%D0%B5%D1%80%D1%82%D1%8B%D0%B2%D0%B0%D0%BD%D0%B8%D1%8E)

## Technology stack
### Back-end
- Java 8 SE (java.lang, Сollections, multi-threading, Stream API, lyambdas)
- Kotlin
- Spring Framework 5 (IoC, Core, Boot 2, Security, MVC, Data, JPA, Transactions, JDBC, JAX-RS (REST), Test, Test DBUnit)
- Lombok
- Swagger 2
- Joda Money
- Jadira Framework
- log4j (Logger) and slf4j
- JPA 2 (Hibernate), JPQL and HQL
- PostgreSQL, SQL,  Flyway
- Apache Maven
- Servlet container Apache Tomcat
- Unit-tests on JUnit, Mockito Framework, Hamcrest), integration tests (Spring Test, Spring Test DBUnit)
- Integration with API of CBRF for getting currency rates
- Integration with API of betting company Leon
- DevOps, GitHub Actions CI / CD, CodeCov, deployment on Amazon Web Services
- Amazon Web Services: AWS Elastic Beanstalk, AWS IAM, AWS EC2, AWS RDS, AWS S3, AWS CloudWatch
- in plans using JMS (perhaps ActiveMQ)
- smart-contracts will be written on Solidity for Ethereum. Integration by Web3j

### Front-end
- JavaScript
- Jquery
- AngularJS 1 (planning to change to React.js).

## Contacts
### Ashamaz Shomakhov
* **Role:** founder, backend-developer
* **GitHub:** [soufee](https://github.com/soufee/kappers)

### Ruslan Suleymanov
* **Role:** developer, CI/CD
* **GitHub:** [SuleymanovRA](https://github.com/SuleymanovRA/kappers)
* **Email:** rusdenis82@gmail.com