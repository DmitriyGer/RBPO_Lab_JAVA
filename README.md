# Лаба №6 – JСоздание цепочки сертификатов и настройка HTTPS

## Требования к паролю

- Минимум 8 символов
- Минимум одна заглавная буква
- Минимум одна цифра
- Минимум один спецсимвол (!@#$%^&\* и т.д.)

## Запуск

```bash
mvn spring-boot:run
```

### 1) Регистрация пользователей (без JWT)

Регистрация USER:

```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"User123!@#","role":"USER"}'
```

Регистрация ADMIN:

```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin1","password":"Admin123!@#","role":"ADMIN"}'
```

Пример слабого пароля (ожидаем 400):

```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123","role":"USER"}'
```

### 2) Логин и получение JWT (USER и ADMIN)

Логин USER:

```bash
TOKENS_USER=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"User123!@#"}')

ACCESS_USER=$(echo "$TOKENS_USER" | jq -r '.accessToken')
REFRESH_USER=$(echo "$TOKENS_USER" | jq -r '.refreshToken')
```

Логин ADMIN:

```bash
TOKENS_ADMIN=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin1","password":"Admin123!@#"}')

ACCESS_ADMIN=$(echo "$TOKENS_ADMIN" | jq -r '.accessToken')
REFRESH_ADMIN=$(echo "$TOKENS_ADMIN" | jq -r '.refreshToken')
```

### Быстрый сценарий проверки refresh-сессий

1. Сохраните текущий refresh пользователя перед обновлением:

```bash
OLD_REFRESH_USER="$REFRESH_USER"
```

2. Обновите пару токенов (возвращает новую сессию и новый refresh):

```bash
TOKENS_USER=$(curl -s -X POST "http://localhost:8080/api/auth/refresh" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_USER\"}")

ACCESS_USER=$(echo "$TOKENS_USER" | jq -r '.accessToken')
REFRESH_USER=$(echo "$TOKENS_USER" | jq -r '.refreshToken')
SESSION_ID_NEW=$(echo "$TOKENS_USER" | jq -r '.sessionId')
```

3. Попробуйте повторно обновить по старому refresh (ожидаем 401/403):

```bash
curl -i -X POST "http://localhost:8080/api/auth/refresh" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$OLD_REFRESH_USER\"}"
```

4. Проверка статусов сессий в БД (пример через psql):

```bash
psql "postgresql://${POSTGRES_USER:-admin}:${POSTGRES_PASSWORD:-admin}@localhost:${POSTGRES_PORT:-50000}/${POSTGRES_DB:-admin_bd}" \
  -c "select id, user_id, status, expires_at, revoked_at from user_sessions order by id desc limit 5;"
```

5. Запрос к защищенному ресурсу с новым access:

```bash
curl -H "Authorization: Bearer $ACCESS_USER" "http://localhost:8080/api/flights"
```

### 3) Обновление пары токенов (refresh)

Обновить токены USER:

```bash
TOKENS_USER=$(curl -s -X POST "http://localhost:8080/api/auth/refresh" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_USER\"}")

ACCESS_USER=$(echo "$TOKENS_USER" | jq -r '.accessToken')
REFRESH_USER=$(echo "$TOKENS_USER" | jq -r '.refreshToken')
```

### 4) Пользователи (ADMIN)

Просмотр зарегистрированных пользователей:

```bash
curl -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/users"
```

### 5) Самолёты (Aircrafts)

Посмотрим все самолёты (USER):

```bash
curl -H "Authorization: Bearer $ACCESS_USER" \
  "http://localhost:8080/api/aircrafts"
```

Посмотрим конкретный самолёт (USER):

```bash
curl -H "Authorization: Bearer $ACCESS_USER" \
  "http://localhost:8080/api/aircrafts/1"
```

Создадим новый самолёт (ADMIN):

```bash
curl -X POST "http://localhost:8080/api/aircrafts" \
  -H "Authorization: Bearer $ACCESS_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "Embraer E190",
    "manufacturer": "Embraer",
    "registrationNumber": "EM-19001",
    "capacity": 114,
    "available": true
  }'
```

Обновить самолёт (ADMIN):

```bash
curl -X PUT "http://localhost:8080/api/aircrafts/1" \
  -H "Authorization: Bearer $ACCESS_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "Boeing 737-800 MAX",
    "manufacturer": "Boeing",
    "registrationNumber": "RA-73001",
    "capacity": 189,
    "available": true
  }'
```

Удалить самолёт (ADMIN):

```bash
curl -X DELETE "http://localhost:8080/api/aircrafts/5" \
  -H "Authorization: Bearer $ACCESS_ADMIN"
```

### 6) Аэропорты (Airports)

Посмотреть все аэропорты (USER):

```bash
curl -H "Authorization: Bearer $ACCESS_USER" \
  "http://localhost:8080/api/airports"
```

Создать новый аэропорт (ADMIN):

```bash
curl -X POST "http://localhost:8080/api/airports" \
  -H "Authorization: Bearer $ACCESS_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "LED",
    "name": "Пулково",
    "city": "Санкт-Петербург",
    "country": "Россия"
  }'
```

Найти аэропорты по городу (USER):

```bash
curl -G -H "Authorization: Bearer $ACCESS_USER" \
  --data-urlencode "city=Moscow" \
  "http://localhost:8080/api/airports/by-city"
```

Обновить аэропорт (ADMIN):

```bash
curl -X PUT "http://localhost:8080/api/airports/1" \
  -H "Authorization: Bearer $ACCESS_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "code": "SVO",
    "name": "Sheremetyevo International Airport",
    "city": "Moscow",
    "country": "Russia"
  }'
```

Удалить аэропорт (ADMIN):

```bash
curl -X DELETE "http://localhost:8080/api/airports/4" \
  -H "Authorization: Bearer $ACCESS_ADMIN"
```

### 7) Пассажиры (Passengers) — только ADMIN

Зарегистрировать нового пассажира:

```bash
curl -X POST "http://localhost:8080/api/passengers" \
  -H "Authorization: Bearer $ACCESS_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "test",
    "lastName": "tests",
    "email": "test.tests@email.com",
    "phoneNumber": "+7-900-222-2244",
    "passportNumber": "1234567878"
  }'
```

Посмотреть всех пассажиров:

```bash
curl -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/passengers"
```

Обновить пассажира:

```bash
curl -X PUT "http://localhost:8080/api/passengers/4" \
  -H "Authorization: Bearer $ACCESS_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Иван",
    "lastName": "Герасимов",
    "email": "dmitriy.gerasimov@email.com",
    "passportNumber": "1111234567",
    "phoneNumber": "+7-999-222-3344"
  }'
```

Удалить пассажира:

```bash
curl -X DELETE "http://localhost:8080/api/passengers/5" \
  -H "Authorization: Bearer $ACCESS_ADMIN"
```

### 8) Рейсы (Flights)

Создать новый рейс (ADMIN):

```bash
curl -X POST "http://localhost:8080/api/flights" \
  -H "Authorization: Bearer $ACCESS_ADMIN" \
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

Найти рейсы между городами (USER):

```bash
curl -G -H "Authorization: Bearer $ACCESS_USER" \
  --data-urlencode "from=Saint Petersburg" \
  --data-urlencode "to=Kazan" \
  --data-urlencode "date=2025-11-10" \
  "http://localhost:8080/api/flights/search"
```

Посмотреть все рейсы (USER):

```bash
curl -H "Authorization: Bearer $ACCESS_USER" \
  "http://localhost:8080/api/flights"
```

Обновить рейс (ADMIN):

```bash
curl -X PUT "http://localhost:8080/api/flights/1" \
  -H "Authorization: Bearer $ACCESS_ADMIN" \
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

Изменить статус рейса (ADMIN):

```bash
curl -X PUT -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/flights/1/status?status=BOARDING"
```

Отменить рейс (ADMIN):

```bash
curl -X POST -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/flights/1/cancel"
```

Удалить рейс (ADMIN):

```bash
curl -X DELETE -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/flights/5"
```

### 9) Бронирования (Bookings)

Создать бронирование (USER):

```bash
curl -X POST "http://localhost:8080/api/bookings" \
  -H "Authorization: Bearer $ACCESS_USER" \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": 1,
    "flightId": 1,
    "seatNumber": "12A",
    "price": 5000.0
  }'
```

Смотрим бронирования пассажира (USER):

```bash
curl -H "Authorization: Bearer $ACCESS_USER" \
  "http://localhost:8080/api/bookings/passenger/1"
```

Смотрим все бронирования (ADMIN):

```bash
curl -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/bookings"
```

Отменить бронирование (USER):

```bash
curl -X PUT -H "Authorization: Bearer $ACCESS_USER" \
  "http://localhost:8080/api/bookings/1/cancel"
```

Удалить бронирование (ADMIN):

```bash
curl -X DELETE -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/bookings/1"
```

### 10) Бизнес-операции авиакомпании (Airline) — только ADMIN

Расчёт выручки рейса:

```bash
curl -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/airline/flights/1/revenue"
```

Статистика загруженности рейса:

```bash
curl -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/airline/flights/1/occupancy"
```

Популярные направления:

```bash
curl -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/airline/popular-routes"
```

Частые пассажиры:

```bash
curl -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/airline/frequent-passengers"
```

Рейсы на сегодня:

```bash
curl -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/airline/todays-flights"
```

Резервирование места:

```bash
curl -X POST "http://localhost:8080/api/airline/reservation" \
  -H "Authorization: Bearer $ACCESS_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "flightId": 1,
    "passengerId": 1,
    "seatNumber": "15B",
    "price": 7500.0
  }'
```

Отмена рейса с причиной:

```bash
curl -X POST "http://localhost:8080/api/airline/flights/1/cancel" \
  -H "Authorization: Bearer $ACCESS_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{"reason":"Плохие погодные условия"}'
```

Задержка рейса:

```bash
curl -X POST "http://localhost:8080/api/airline/flights/1/delay" \
  -H "Authorization: Bearer $ACCESS_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "newDepartureTime": "2024-02-15T14:30:00+03:00",
    "newArrivalTime": "2024-02-15T16:45:00+03:00"
  }'
```

Регистрация на рейс:

```bash
curl -X POST -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/airline/flights/1/check-in"
```

Отправление рейса:

```bash
curl -X POST -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/airline/flights/1/depart"
```

Список пассажиров рейса:

```bash
curl -H "Authorization: Bearer $ACCESS_ADMIN" \
  "http://localhost:8080/api/airline/flights/1/passengers"
```

## Тестирование без аутентификации (ожидаем 401)

```bash
curl -X GET "http://localhost:8080/api/aircrafts"
```

```bash
curl -X GET "http://localhost:8080/api/flights"
```
