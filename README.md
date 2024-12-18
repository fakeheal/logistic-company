# Приложение „Логистична компания“

## Задание
Да се реализира уеб приложение „Логистична компания“, което да служи за управление на процесите в логистична компания. Основната дейност на компанията е да осъществява услуги по приемане и доставяне на пратки. Компанията разполага с офиси на различни места и в нея работят два вида служители: куриери (извършват доставката на пратките) и офис-служители (обслужват клиенти в офисите на компанията). Клиентите на компанията изпращат и/или получават пратки от офисите на компанията или от друг адрес. Пратките имат подател, получател, адрес за доставка и тегло. За определяне на цената на за доставка играе роля теглото на пратката и дали тя ще се доставя до офис или до точен адрес. Доставките до офис са по-евтини, отколкото тези до адрес. Служителите на компанията могат да виждат всички пратки, които са регистрирани в системата. Всеки клиент може да вижда само пратките, които е изпратил, получили или очаква да получи.

### Функционални изисквания на системата

Уеб приложението „Логистична компания“ трябва да включва минимум следните
функционалности:
1. Регистриране на потребители и вход в системата
1. Възможност за задаване на роли на потребителите (служител, клиент)
1. Въвеждане, показване, редактиране и изтриване на данни за:
     - Логистична компания
     - Служител на компания
     - Клиент на компания
     - Офис на компания
     - Пратка
1. Служителите на компанията трябва да могат да регистрират изпратените и получените
пратки.
1. Справки за:
     - Всички служители в компанията
     - Всички клиенти на компанията
     - Всички пратки, които са били регистрирани
     - Всички пратки, които са регистрирани от даден служител
     - Всички пратки, които са изпратени, но не да получени
     - Всички пратки, които са изпратени от даден клиент
     - Всички пратки, които са получени от даден клиент
     - Всички приходи на фирмата за определен период от време
1. Всеки служител може да вижда всички пратки
1. Всеки клиент може да вижда пратките, които е изпратил или получил

### Технологични изисквания
Приложението трябва да бъде уеб базирано, с възможност за визуализиране в най-популярните браузери (Chrome, Mozilla Firefox, Internet Explorer). Дизайнът му трябва да бъде респонсив (подходящ за визуализиране и на мобилни устройства). Разработената система трябва да се състои от код, база данни и документация. Кодът трябва да включва подробни коментари. Документацията трябва се състои от детайлно описание на функционалностите на системата, включително екрани, на които ясно се вижда коя част от програмата за какво се използва. 

Необходимо е да се посочат задачите, които са били изпълнени от всеки от участниците в
екипа.

### Development - Prerequisites
Java 21, Gradle 8.1 - language SDK and build tool

### SQL Server Authentication - Setup
In the application.properties file of the project are used environment variables for the SQL Server
authentication and accessing the database:
URL (Example: jdbc:mysql://localhost:3306/logistic_company), Username, Password

The environment variables should be set up as follows:
#### Windows
Open cmd -> Execute the following commands:

```cmd
setx LOGISTIC_COMPANY_DB_URL {your-db-url}

setx LOGISTIC_COMPANY_DB_USERNAME {your-db-username}

setx LOGISTIC_COMPANY_DB_PASSWORD {your-db-password}
```
#### Linux & Mac
Open Terminal -> Execute the Following Commands:

```terminal
export LOGISTIC_COMPANY_DB_URL={your-db-url}

export LOGISTIC_COMPANY_DB_USERNAME={your-db-username}

export LOGISTIC_COMPANY_DB_PASSWORD={your-db-password}
```
## Reference
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Hibernate ORM](https://hibernate.org/orm/)
- [thymeleaf](https://www.thymeleaf.org/)
- [gradle-node-plugin](https://github.com/node-gradle/gradle-node-plugin)
- [tailwindcss](https://tailwindcss.com/)

## Run

```bash
./gradlew npmInstall tailwindcss rollup bootRun
```

## Deploy

Build & tag image:
```bash
docker build -t itodorova/logistic-company -t itodorova/logistic-company:0.0.2 -t registry.digitalocean.com/itodorova/logistic-company -t registry.digitalocean.com/itodorova/logistic-company:0.0.2 .
```

Test it runs:
```bash
docker run -p 8080:8080 itodorova/logistic-company
```


Push image:
```bash
docker push registry.digitalocean.com/itodorova/logistic-company --all-tags  
```

Automatically deploys (tag: `latest`) on Digital Ocean's App Platform.