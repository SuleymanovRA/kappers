# Честный каппер
| Technology | Badge |
|:-----------:|:-----:|
| Travis CI[RS] | [![Build Status](https://travis-ci.com/SuleymanovRA/kappers.svg?branch=master)](https://travis-ci.com/SuleymanovRA/kappers) |
| Travis CI[AS] | [![Build Status](https://travis-ci.org/soufee/kappers.svg?branch=master)](https://travis-ci.org/soufee/kappers) |
| CodeCov | [![codecov](https://codecov.io/gh/SuleymanovRA/kappers/branch/master/graph/badge.svg)](https://codecov.io/gh/SuleymanovRA/kappers) |
| Codebeat | [![codebeat badge](https://codebeat.co/badges/7aa7c56b-e4d4-4978-9f42-ce0657aa27a1)](https://codebeat.co/projects/github-com-suleymanovra-kappers-master) |

## Информация о проекте
Проект предполагает создание веб-сервиса для ведения честного рейтинга аналитиков спорта (каппера), занимающихся продажей прогнозов на спорт для ставок.
Для этого предполагается написание смарт-контракта на блокчейне Ethereum, который будет генерировать токен-репутации и управлять их трансферами.
В системе пользователи могут регистрироваться в одной из двух ролей: Каппер или Пользователь.
Поддерживаемые языки интерфейса:
- русский
- английский

За дополнительными подробностями о бизнес-задачах проекта обратитесь к авторам в контактах.

### Полезные ссылки
- [Инструкция по развертыванию проекта (локально)](https://github.com/soufee/kappers/wiki/%D0%98%D0%BD%D1%81%D1%82%D1%80%D1%83%D0%BA%D1%86%D0%B8%D1%8F-%D0%BF%D0%BE-%D1%80%D0%B0%D0%B7%D0%B2%D0%B5%D1%80%D1%82%D1%8B%D0%B2%D0%B0%D0%BD%D0%B8%D1%8E)

## Стек технологий и языки программирования
### Back-end
- Java 8 SE (java.lang, Сollections, multi-threading, Stream API, лямбды)
- Kotlin
- Spring Framework 5 (IoC, Core, Boot 2, Security, MVC, Data, JPA, Транзакции, JDBC, JAX-RS (REST), Test, Test DBUnit)
- Lombok
- Swagger 2
- Joda Money
- Jadira Framework
- log4j (Logger) и slf4j
- JPA 2 (Hibernate), язык JPQL и HQL
- PostgreSQL, язык SQL, управление миграциями баз данных Flyway
- Apache Maven
- Сервер приложений (контейнер сервлетов) Apache Tomcat
- Модульные тесты (unit-тесты на JUnit, Mockito Framework, Hamcrest), интеграционные тесты (Spring Test, Spring Test DBUnit)
- Интеграция с API Центробанка РФ для получения курсов валют
- Интеграция с API букмекерских контор (ООО "Леон")
- DevOps, Travis CI / CD, CodeCov, диплой на Amazon Web Services
- Amazon Web Services: AWS Elastic Beanstalk, AWS IAM, AWS EC2, AWS RDS, AWS S3, AWS CloudWatch
- Планируется JMS (возможно будет ActiveMQ)
- Планируются смарт-контракты на языке Solidity для блокчейн Ethereum, интеграция через Web3j

### Front-end
- JavaScript
- Jquery
- AngularJS 1 (планируется переход на React.js).

## Контакты
### Ашамаз Шомахов
* **Роль:** основатель проекта и разработчик
* **GitHub:** [soufee](https://github.com/soufee/kappers)

### Руслан Сулейманов
* **Роль:** разработчик, CI/CD
* **GitHub:** [SuleymanovRA](https://github.com/SuleymanovRA/kappers)
* **Email:** rusdenis82@gmail.com

