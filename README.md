# Сервис авиаперевозок

Сущности: `Flight`, `Aircraft`, `Airport`, `Booking`, `Passenger`.

## Как запустить / остановить

```bash
mvn spring-boot:run
pkill -f "spring-boot:run"
```

## Тестирование API

### Создание аэропортов

```bash
curl -sX POST http://localhost:8080/api/airports -H 'Content-Type: application/json' -d '{"code":"SVO","name":"Sheremetyevo","city":"Moscow","country":"Russia"}'
curl -sX POST http://localhost:8080/api/airports -H 'Content-Type: application/json' -d '{"code":"LED","name":"Pulkovo","city":"Saint Petersburg","country":"Russia"}'
curl -sX POST http://localhost:8080/api/airports -H 'Content-Type: application/json' -d '{"code":"KZN","name":"Kazan","city":"Kazan","country":"Russia"}'
```

### Создание самолетов

```bash
curl -sX POST http://localhost:8080/api/aircrafts -H 'Content-Type: application/json' -d '{"model":"Boeing 737","manufacturer":"Boeing","registrationNumber":"RA-73001","capacity":2,"available":true}'
curl -sX POST http://localhost:8080/api/aircrafts -H 'Content-Type: application/json' -d '{"model":"Airbus A320","manufacturer":"Airbus","registrationNumber":"RA-32001","capacity":160,"available":true}'
```

### Создание пассажиров

```bash
curl -sX POST http://localhost:8080/api/passengers -H 'Content-Type: application/json' -d '{"firstName":"Иван","lastName":"Петров","email":"ivan.petrov@mail.ru","passportNumber":"1234 567890","phoneNumber":"+7-900-123-45-67"}'
curl -sX POST http://localhost:8080/api/passengers -H 'Content-Type: application/json' -d '{"firstName":"Мария","lastName":"Сидорова","email":"maria.sidorova@gmail.com","passportNumber":"2345 678901","phoneNumber":"+7-916-234-56-78"}'
curl -sX POST http://localhost:8080/api/passengers -H 'Content-Type: application/json' -d '{"firstName":"Алексей","lastName":"Иванов","email":"alex.ivanov@mail.ru","passportNumber":"3456 789012","phoneNumber":"+7-905-987-65-43"}'
```

### Создание рейса

```bash
curl -sX POST http://localhost:8080/api/flights -H 'Content-Type: application/json' -d '{"flightNumber":"SU1234","aircraftId":1,"departureAirportId":1,"arrivalAirportId":2,"departureTime":"2024-12-20T10:00:00+03:00","arrivalTime":"2024-12-20T12:30:00+03:00","status":"SCHEDULED"}'
```

### Просмотр всех данных

```bash
curl -s http://localhost:8080/api/airports | jq
curl -s http://localhost:8080/api/aircrafts | jq
curl -s http://localhost:8080/api/passengers | jq
curl -s http://localhost:8080/api/flights | jq
curl -s http://localhost:8080/api/bookings | jq
```

### Бронирование билетов

```bash
curl -sX POST http://localhost:8080/api/bookings -H 'Content-Type: application/json' -d '{"flightId":1,"passengerId":1,"seatNumber":"1A","price":8500.00}' | jq
curl -sX POST http://localhost:8080/api/bookings -H 'Content-Type: application/json' -d '{"flightId":1,"passengerId":2,"seatNumber":"1B","price":8500.00}' | jq
```

### Проверка ошибок

```bash
# Дубликат места
curl -sX POST http://localhost:8080/api/bookings -H 'Content-Type: application/json' -d '{"flightId":1,"passengerId":2,"seatNumber":"1A","price":8500.00}'
# Превышение вместимости
curl -sX POST http://localhost:8080/api/bookings -H 'Content-Type: application/json' -d '{"flightId":1,"passengerId":3,"seatNumber":"1C","price":8500.00}'
# Неверный формат номера места
curl -sX POST http://localhost:8080/api/bookings -H 'Content-Type: application/json' -d '{"flightId":1,"passengerId":1,"seatNumber":"INVALID","price":8500.00}'
# Попытка бронирования отмененного рейса
curl -sX POST http://localhost:8080/api/flights/1/cancel
curl -sX POST http://localhost:8080/api/bookings -H 'Content-Type: application/json' -d '{"flightId":1,"passengerId":3,"seatNumber":"2A","price":8500.00}'
```

### Изменение статуса рейса

```bash
curl -sX PUT 'http://localhost:8080/api/flights/1/status?status=BOARDING'
curl -sX PUT 'http://localhost:8080/api/flights/1/status?status=DEPARTED'
```

### Отмена рейса с автоматической отменой бронирований

```bash
curl -sX POST http://localhost:8080/api/flights/1/cancel
```

### Просмотр бронирований

```bash
curl -s http://localhost:8080/api/bookings | jq
```

### Удаление записей

```bash
curl -sX DELETE http://localhost:8080/api/passengers/3
curl -sX DELETE http://localhost:8080/api/aircrafts/2
curl -sX DELETE http://localhost:8080/api/airports/3
```
