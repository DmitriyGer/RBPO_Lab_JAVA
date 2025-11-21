# Лаба №4 – Базовая безопасность API

## Требования к паролю

- Минимум 8 символов
- Минимум одна заглавная буква
- Минимум одна цифра
- Минимум один спецсимвол (!@#$%^&\* и т.д.)

Все запросы также продублированы в Postman в [JSON файле](./Postman/Laba_4_Airline.postman_collection.json)

- Запуск проекта: `mvn spring-boot:run`

## CSRF и базовая аутентификация

1. Получить CSRF-токен и сохранить куки:

```bash
curl -s -c cookies.txt http://localhost:8080/api/auth/csrf | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])"
```

2. Все последующие запросы выполняйте с флагами `-b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9"` плюс, при необходимости, `-u login:password` для Basic Auth.

## Регистрация и аутентификация

### Регистрация нового пользователя (доступно без аутентификации)

Регистрация USER:

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "password": "User123!@#",
    "role": "USER"
  }'
```

Регистрация ADMIN:

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin1",
    "password": "Admin123!@#",
    "role": "ADMIN"
  }'
```

Пример слабого пароля (ошибка):

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123",
    "role": "USER"
  }'
```

### Просмотр зарегистрированных пользователей (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/users \
  -u admin1:'Admin123!@#'
```

Попытка просмотра USER (ошибка 403):

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/users \
  -u user1:'User123!@#'
```

## Сценарий 1: Работа с самолетами (Aircraft)

### Шаг 1.1: Посмотрим все самолеты (USER или ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/aircrafts \
  -u user1:'User123!@#'
```

### Шаг 1.2: Посмотрим конкретный самолет (USER или ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/aircrafts/1 \
  -u user1:'User123!@#'
```

### Шаг 1.3: Создадим новый самолет (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/aircrafts \
  -u admin1:'Admin123!@#' \
  -H "Content-Type: application/json" \
  -d '{
    "model": "Embraer E190",
    "manufacturer": "Embraer",
    "registrationNumber": "EM-19001",
    "capacity": 114,
    "available": true
  }'
```

Попытка создания самолета USER (ошибка 403):

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/aircrafts \
  -u user1:User123!@# \
  -H "Content-Type: application/json" \
  -d '{
    "model": "Airbus A320",
    "manufacturer": "Airbus",
    "registrationNumber": "VP-NEW02",
    "capacity": 180,
    "available": true
  }'
```

### Шаг 1.4: Обновим самолет (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X PUT http://localhost:8080/api/aircrafts/1 \
  -u admin1:'Admin123!@#' \
  -H "Content-Type: application/json" \
  -d '{
    "model": "Boeing 737-800 MAX",
    "manufacturer": "Boeing",
    "registrationNumber": "RA-73001",
    "capacity": 189,
    "available": true
  }'
```

### Шаг 1.5: Удалим самолет (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X DELETE http://localhost:8080/api/aircrafts/5 \
  -u admin1:'Admin123!@#'
```

## Сценарий 2: Работа с аэропортами (Airport)

### Шаг 2.1: Смотрим все аэропорты (USER или ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/airports \
  -u user1:'User123!@#'
```

### Шаг 2.2: Создаем новый аэропорт (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/airports \
  -u admin1:'Admin123!@#' \
  -H "Content-Type: application/json" \
  -d '{
        "id": 2,
        "code": "LED",
        "name": "Pulkovo Airport",
        "city": "Saint Petersburg",
        "country": "Russia"
    }'
```

### Шаг 2.3: Найдем аэропорты по городу (USER или ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET "http://localhost:8080/api/airports/by-city?city=Moscow" \
  -u user1:'User123!@#'
```

### Шаг 2.4: Обновим аэропорт (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X PUT http://localhost:8080/api/airports/1 \
  -u admin1:'Admin123!@#' \
  -H "Content-Type: application/json" \
  -d '{
        "id": 1,
        "code": "SVO",
        "name": "Sheremetyevo International Airport",
        "city": "Moscow",
        "country": "Russia"
  }'
```

### Шаг 2.5: Удалим аэропорт (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X DELETE http://localhost:8080/api/airports/5 \
  -u admin1:'Admin123!@#'
```

## Сценарий 3: Управление пассажирами (Passenger)

### Шаг 3.1: Регистрируем нового пассажира (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/passengers \
  -u admin1:'Admin123!@#' \
  -H "Content-Type: application/json" \
  -d '{
  "firstName": "Иван",
  "lastName": "Петров",
  "email": "ivan.petrov@email.com",
  "phoneNumber": "+7-900-222-3344",
  "passportNumber": "1234567891"
}'
```

### Шаг 3.2: Посмотрим всех пассажиров (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/passengers \
  -u admin1:'Admin123!@#'
```

Попытка просмотра USER (ошибка 403):

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/passengers \
  -u user1:'User123!@#'
```

### Шаг 3.3: Обновим пассажира (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X PUT http://localhost:8080/api/passengers/1 \
  -u admin1:'Admin123!@#' \
  -H "Content-Type: application/json" \
  -d '{
  "firstName": "Иван",
  "lastName": "Иванов",
  "email": "ivan.ivanov@email.com",
  "phoneNumber": "+7-900-111-2233",
  "passportNumber": "1234567890"
}'
```

### Шаг 3.4: Удалим пассажира (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X DELETE http://localhost:8080/api/passengers/5 \
  -u admin1:'Admin123!@#'
```

## Сценарий 4: Создание рейсов (Flight)

### Шаг 4.1: Создаем новый рейс (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/flights \
  -u admin1:'Admin123!@#' \
  -H "Content-Type: application/json" \
  -d '{
  "flightNumber": "SU150",
  "aircraftId": 1,
  "departureAirportId": 1,
  "arrivalAirportId": 2,
  "departureTime": "2024-02-15T10:30:00+03:00",
  "arrivalTime": "2024-02-15T12:45:00+03:00"
}'
```

### Шаг 4.2: Найдем рейсы между городами (USER или ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET "http://localhost:8080/api/flights/search?from=Saint Petersburg&to=Kazan&date=2025-11-10" \
  -u user1:'User123!@#'
```

### Шаг 4.3: Посмотрим все рейсы (USER или ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/flights \
  -u user1:'User123!@#'
```

### Шаг 4.4: Обновим рейс (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X PUT http://localhost:8080/api/flights/1 \
  -u admin1:'Admin123!@#' \
  -H "Content-Type: application/json" \
  -d '{
  "flightNumber": "SU151",
  "aircraft": {"id": 1},
  "departureAirport": {"id": 1},
  "arrivalAirport": {"id": 2},
  "departureTime": "2024-02-15T11:30:00+03:00",
  "arrivalTime": "2024-02-15T13:45:00+03:00",
  "status": "SCHEDULED"
}'
```

### Шаг 4.5: Изменим статус рейса (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X PUT "http://localhost:8080/api/flights/1/status?status=BOARDING" \
  -u admin1:'Admin123!@#'
```

### Шаг 4.6: Отменим рейс (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/flights/1/cancel \
  -u admin1:'Admin123!@#'
```

### Шаг 4.7: Удалим рейс (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X DELETE http://localhost:8080/api/flights/5 \
  -u admin1:'Admin123!@#'
```

## Сценарий 5: Бронирование билетов (Booking)

### Шаг 5.1: Создаем бронирование (USER или ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/bookings \
  -u user1:'User123!@#' \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": 1,
    "flightId": 1,
    "seatNumber": "12A",
    "price": 5000.00
  }'
```

### Шаг 5.2: Смотрим бронирования пассажира (USER или ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/bookings/passenger/1 \
  -u user1:'User123!@#'
```

### Шаг 5.3: Смотрим все бронирования (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/bookings \
  -u admin1:'Admin123!@#'
```

Попытка просмотра всех бронирований USER (ошибка 403):

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/bookings \
  -u user1:'User123!@#'
```

### Шаг 5.4: Отменим бронирование (USER или ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X PUT http://localhost:8080/api/bookings/1/cancel \
  -u user1:'User123!@#'
```

### Шаг 5.5: Удалим бронирование (только ADMIN)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X DELETE http://localhost:8080/api/bookings/1 \
  -u admin1:'Admin123!@#'
```

## БИЗНЕС-ОПЕРАЦИИ (только ADMIN)

### Операция 1: Расчет выручки рейса

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/airline/flights/1/revenue \
  -u admin1:'Admin123!@#'
```

Попытка USER (ошибка 403):

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/airline/flights/1/revenue \
  -u user1:'User123!@#'
```

### Операция 2: Статистика загруженности рейса

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/airline/flights/1/occupancy \
  -u admin1:'Admin123!@#'
```

### Операция 3: Популярные направления

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/airline/popular-routes \
  -u admin1:'Admin123!@#'
```

### Операция 4: Частые пассажиры

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/airline/frequent-passengers \
  -u admin1:'Admin123!@#'
```

### Операция 5: Рейсы на сегодня

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/airline/todays-flights \
  -u admin1:'Admin123!@#'
```

### Операция 6: Резервирование места

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/airline/reservation \
  -u admin1:'Admin123!@#' \
  -H "Content-Type: application/json" \
  -d '{
    "flightId": 1,
    "passengerId": 1,
    "seatNumber": "15B",
    "price": 7500.00
  }'
```

### Операция 7: Отмена рейса с причиной

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/airline/flights/1/cancel \
  -u admin1:'Admin123!@#' \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Плохие погодные условия"
  }'
```

### Операция 8: Задержка рейса

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/airline/flights/1/delay \
  -u admin1:'Admin123!@#' \
  -H "Content-Type: application/json" \
  -d '{
    "newDepartureTime": "2024-02-15T14:30:00+03:00",
    "newArrivalTime": "2024-02-15T16:45:00+03:00"
  }'
```

### Операция 9: Регистрация на рейс

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/airline/flights/1/check-in \
  -u admin1:'Admin123!@#'
```

### Операция 10: Отправление рейса

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X POST http://localhost:8080/api/airline/flights/1/depart \
  -u admin1:'Admin123!@#'
```

### Операция 11: Список пассажиров рейса

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/airline/flights/1/passengers \
  -u admin1:'Admin123!@#'
```

## Тестирование без аутентификации (ошибка 401)

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/aircrafts
```

```bash
curl -b cookies.txt -H "X-XSRF-TOKEN: 20a535b7-ae43-4ea9-826c-fbaf085a7ef9" -X GET http://localhost:8080/api/flights
```
