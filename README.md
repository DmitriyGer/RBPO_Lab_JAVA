# Лаба №3

Все запросы также продублированы в Postman в [JSON файле](./Postman/Laba_Airline.postman_collection.json)

### Шаг 1.1: Посмотрим все самолеты

```bash
curl -X GET http://localhost:8080/api/aircrafts
```

### Шаг 1.2: Посмотрим конкретный самолет

```bash
curl -X GET http://localhost:8080/api/aircrafts/1
```

### Шаг 1.3: Создадим новый самолет

```bash
curl -X POST http://localhost:8080/api/aircrafts \
  -H "Content-Type: application/json" \
  -d '{
    "model": "Airbus A320",
    "manufacturer": "Airbus",
    "registrationNumber": "VP-NEW01",
    "capacity": 180,
    "available": true
  }'
```

### Шаг 1.4: Обновим самолет

```bash
curl -X PUT http://localhost:8080/api/aircrafts/1 \
  -H "Content-Type: application/json" \
  -d '{
    "model": "Boeing 737-800 MAX",
    "manufacturer": "Boeing",
    "registrationNumber": "RA-73001",
    "capacity": 189,
    "available": true
  }'
```

## Сценарий 2: Работа с аэропортами (Airport)

### Шаг 2.1: Смотрим все аэропорты

```bash
curl -X GET http://localhost:8080/api/airports
```

### Шаг 2.2: Создаем новый аэропорт

```bash
curl -X POST http://localhost:8080/api/airports \
  -H "Content-Type: application/json" \
  -d '{
    "code": "LED",
    "name": "Пулково",
    "city": "Санкт-Петербург",
    "country": "Россия"
  }'
```

### Шаг 2.3: Найдем аэропорты по городу

```bash
curl -X GET "http://localhost:8080/api/airports/by-city?city=Москва"
```

## Сценарий 3: Управление пассажирами (Passenger)

### Шаг 3.1: Регистрируем нового пассажира

```bash
curl -X POST http://localhost:8080/api/passengers \
  -H "Content-Type: application/json" \
  -d '{
  "firstName": "Иван",
  "lastName": "Петров",
  "email": "ivan.petrov@email.com",
  "phoneNumber": "+7-900-222-3344",
  "passportNumber": "1234567891"
}'
```

### Шаг 3.2: Найдем пассажира по email

```bash
curl -X GET "http://localhost:8080/api/passengers/by-email?email=alexey.ivanov@email.com"
```

## Сценарий 4: Создание рейсов (Flight)

### Шаг 4.1: Создаем новый рейс

```bash
curl -X POST http://localhost:8080/api/flights \
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

### Шаг 4.2: Найдем рейсы между городами

```bash
curl -X GET "http://localhost:8080/api/flights/search?from=Saint Petersburg&to=Kazan&date=2025-11-10"
```

## Сценарий 5: Бронирование билетов (Booking)

### Шаг 5.1: Создаем бронирование

```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "passenger": {"id": 1},
    "flight": {"id": 1},
    "seatNumber": "12A",
    "bookingClass": "ECONOMY"
  }'
```

### Шаг 5.2: Смотрим бронирования пассажира

```bash
curl -X GET http://localhost:8080/api/bookings/passenger/1
```

## БИЗНЕС-ОПЕРАЦИИ

### Операция 1: Расчет выручки рейса

```bash
curl -X GET http://localhost:8080/api/airline/flights/1/revenue
```

### Операция 2: Статистика загруженности рейса

```bash
curl -X GET http://localhost:8080/api/airline/flights/1/occupancy
```

### Операция 3: Популярные направления

```bash
curl -X GET http://localhost:8080/api/airline/popular-routes
```

### Операция 4: Частые пассажиры

```bash
curl -X GET http://localhost:8080/api/airline/frequent-passengers
```

### Операция 5: Рейсы на сегодня

```bash
curl -X GET http://localhost:8080/api/airline/todays-flights
```
