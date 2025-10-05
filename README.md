# Короткое введение в Java + Maven

Ссылка на задание: https://github.com/MatorinFedor/maven-demo

## 1) Что потребуется

- Среда разработки (рекомендуется [IntelliJ IDEA](https://www.jetbrains.com/idea/download/?section=windows))
- JDK версии 21 или выше (среда разработки установит сама)
- Maven

## 2) Создаём простой проект в IDE и компилируем его в консоли

**Шаг 0. Готовим окружение и `PATH`**  
Задача — чтобы `java`, `javac` и `mvn` были доступны в любой консоли.

- **Проверяем доступность инструментов:**

  ```bash
  java --version
  javac --version
  mvn -v
  ```

Если команды не находятся — нужно добавить в `PATH` путь к папке bin.

Примерный путь к Maven:  
C:\Program Files\JetBrains\\<ваша версия IDE>\plugins\maven\lib\maven3\bin

Примерный путь к JDK:  
C:\Users\<user>\.jdks\\<ваша версия JDK>\bin

**Шаг 1. Создаём простой Java-проект в IntelliJ IDEA (без Maven)**

IDE создаст минимальную структуру с папкой `src`.
Код:

```java
package com.example;

public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello from IDE!");
    }
}
```

**Шаг 2. Запускаем тот же код из консоли (ручная компиляция)**

1. **Откройте терминал в корне проекта** `hello` (где лежит папка `src`).

   - В IntelliJ: на снизу на левой панели значок терминала.
   - Либо откройте системный терминал в корневой папке проекта.

2. **Создаём папку для скомпилированных классов** (выходной каталог):

   - Windows (PowerShell/CMD):

     ```powershell
     mkdir out
     ```

3. **Компилируем исходник в байткод (`.class`)**:

   - Windows:

     ```powershell
     javac -d out src\Hello.java
     ```

   Что здесь происходит:

   - `javac` — компилятор Java.
   - `-d out` — сложить все скомпилированные `.class` в папку `out`, сохраняя структуру пакетов.

4. **Смотрим, что получилось**:

   В `out` появилcя файл `out/Hello.class`.

5. **Запускаем JVM на скомпилированных классах**:

   - macOS/Linux/Windows:
     ```bash
     java -cp out Hello
     ```

   Пояснения:

   - `java` — запуск байткода на JVM.
   - `-cp out` — `classpath`, где искать классы (наша папка `out`). -cp (или -classpath) говорит JVM/javac, где искать ваши классы.
   - `Hello` — **полное имя класса**, а не путь к файлу.

**Шаг 3. Собираем исполняемый JAR**

1. Находясь в корне проекта:

   ```bash
   jar --create --file out/hello.jar --main-class Hello -C out .
   ```

   Что делает команда:

   - `jar --create --file out/hello.jar` — создаёт архив JAR.
   - `--main-class Hello` — прописывает в манифест главный класс.
   - `-C out .` — взять все классы из каталога `out`.

2. Запуск:

   ```bash
   java -jar out/hello.jar
   ```

---

## 3) Maven

### 3.1 Создаём новый Maven-проект с «Hello» в IntelliJ IDEA (или используйте этот)

1. **File → New Project → Maven** (можно выбрать archetype `maven-archetype-quickstart`).
2. **SDK:** JDK 21, **GroupId:** `com.example`, **ArtifactId:** `hello-mvn`.

### 3.2 Ключевые **фазы** стандартного жизненного цикла

- `validate` — проверка корректности структуры проекта;
- `compile` — компиляция `src/main/java`;
- `test` — запуск юнит-тестов (`src/test/java`);
- `package` — упаковка артефакта (обычно JAR в `target/`);
- `verify` — доп. проверки качества (если настроены плагины);
- `install` — установка артефакта в локальный репозиторий `~/.m2`;
- `deploy` — публикация в удалённый репозиторий (CI/CD).

Ещё есть отдельный цикл `clean` (удаляет `target/`) и `site` (генерация сайта/отчётов).

### 3.3 Собираем JAR и запускаем проект из консоли

Собираем:

```bash
mvn clean package
```

Запускаем байткод классом (в quickstart-JAR нет `Main-Class` в манифесте):

```bash
java -cp target/hello-mvn-1.0-SNAPSHOT.jar ru.mfa.App
```

### 3.4 Добавляем `exec-maven-plugin` и запускаем

Чтобы не возиться с `-cp` и манифестами, нужно добавить в конец pox.xml плагин, упрощающий запуск:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>exec-maven-plugin</artifactId>
      <version>3.2.0</version>
      <configuration>
        <mainClass>ru.mfa.App</mainClass>
      </configuration>
    </plugin>
  </plugins>
</build>
```

Запуск:

```bash
mvn clean compile exec:java
# или
mvn clean package exec:java
# или после первых двух
mvn exec:java
```

> Maven скомпилирует проект и запустит `main` без указания classpath.

# Задание 1

- Создать Java проект с фреймворком Spring Boot версии 3.5.5 или выше;
- Опубликовать его на Github;
- Создать простые контроллеры, чтобы посмотреть, как с ними можно работать.

Инструмент для автоматического создания проекта на Spring Boot: [Spring Initializer](https://start.spring.io/)  
[Дополнительный обучающие материалы](https://spring.io/)

## Выполнение задания 1 (готово)

- Версия Spring Boot: 3.5.6 (>= 3.5.5)
- Сборщик: Maven
- Стартовый модуль: `ru.mfa.MavenDemoApplication`
- Контроллеры: `GET /api/hello`, `GET /api/greet/{name}`, `GET /api/ping`

### Как запустить (вариант 1 — из Maven)

```bash
cd Lab/maven-demo
mvn spring-boot:run
```

Примеры запросов:

```bash
curl http://localhost:8080/api/hello
curl http://localhost:8080/api/greet/Student
curl http://localhost:8080/api/ping
```

### Сборка JAR и запустить

```bash
cd Lab/maven-demo
mvn clean package
java -jar target/maven-demo-0.0.1-SNAPSHOT.jar
```

### Тесты

```bash
mvn -q -DskipTests=false test
```

# Задание 2

Ветка: Laba_2

- Выбрать тему из предложенных в [файле](./files/Варианты_РБПО_2025.xlsx) (либо предложить свою);
- Для каждой сущности реализовать методы контроллера для: Создания, Получения, Удаления и Изменения;
- Продумать, какие функции в дальнейшем будет предоставлять ваш сервис.

## Выполнение задания 2 (тема: каршеринг)

Сущности: `Car`, `User`, `Ride`, `Payment`.

Эндпоинты (CRUD):

- Cars: `GET/POST /api/cars`, `GET/PUT/DELETE /api/cars/{id}`
- Users: `GET/POST /api/users`, `GET/PUT/DELETE /api/users/{id}`
- Rides: `GET/POST /api/rides`, `GET/PUT/DELETE /api/rides/{id}`
- Payments: `GET/POST /api/payments`, `GET/PUT/DELETE /api/payments/{id}`

Доп. функция сервиса: завершение поездки с автоматическим расчётом платежа

- Finish ride: `POST /api/rides/{id}/finish?distanceKm=12.3` → возвращает `{ ride, payment }`.

Тесты:

- Создать пользователя:
  `curl -sX POST http://localhost:8080/api/users -H 'Content-Type: application/json' -d '{"name":"Ivan","email":"ivan@example.com","drivingLicense":"LIC123"}'`

- Создать второго пользователя:
  `curl -sX POST http://localhost:8080/api/users -H 'Content-Type: application/json' -d '{"name":"Olga","email":"olga@example.com","drivingLicense":"LIC456"}'`

- Создать третьего пользователя:
  `curl -sX POST http://localhost:8080/api/users -H 'Content-Type: application/json' -d '{"name":"Petr","email":"petr@example.com","drivingLicense":"LIC789"}'`

- Создать автомобиль:
  `curl -sX POST http://localhost:8080/api/cars -H 'Content-Type: application/json' -d '{"brand":"Kia","model":"Rio","licensePlate":"A123BC","available":true}'`

- Создать второй автомобиль:
  `curl -sX POST http://localhost:8080/api/cars -H 'Content-Type: application/json' -d '{"brand":"Hyundai","model":"Solaris","licensePlate":"B234CD","available":true}'`

- Создать третий автомобиль:
  `curl -sX POST http://localhost:8080/api/cars -H 'Content-Type: application/json' -d '{"brand":"VW","model":"Polo","licensePlate":"C345DE","available":true}'`

- Посмотреть всех пользователей:
  `curl -s http://localhost:8080/api/users | jq`

- Посмотреть все машины:
  `curl -s http://localhost:8080/api/cars | jq`

- Начать поездку (пример с userId=1 и carId=1):
  `curl -sX POST http://localhost:8080/api/rides -H 'Content-Type: application/json' -d '{"userId":1, "carId":1}' | jq`

- Завершить поездку с расстоянием 10.5 км:
  `curl -sX POST 'http://localhost:8080/api/rides/1/finish?distanceKm=10.5' | jq`

- Проверить список поездок:
  `curl -s http://localhost:8080/api/rides | jq`

- Проверить платежи:
  `curl -s http://localhost:8080/api/payments | jq`

- Удалить пользователя (пример id=3):
  `curl -sX DELETE http://localhost:8080/api/users/3`

- Удалить машину (пример id=2):
  `curl -sX DELETE http://localhost:8080/api/cars/2`
