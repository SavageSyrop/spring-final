# spring-final

## Стек
- Java 17
- Sping Boot (Cloud, Security, Web, Eureka, Data JPA)
- H2 - база данных
- Slf4j - логирование

## Микросервисы
- eureka-server - управление маршрутизацией запросов по динамическим портам микросервисов
- api-gateway - единая точка доступа к системе
- hotel-service - данные об отелях, комнатах, подтверждение бронирований
- booking-service - авторизация, регистрация пользователей и управление бронированием

## Особенности
- Авторизация через JWT на API Gateway - внутренние микросервисы получают авторизацию в виде base64 encoded username/roles (класс InternalAuthData)
- Логирование с Correlation ID
- Предзаполнение H2 БД данными из классов FillDatabase
- Порт hotel-service и booking-service динамический, управлением запросами занимается eureka-server
- ControllerAdviceExceptionHandler для единой обработки ошибок
- Выполнено в соответствии с ТЗ и критериями оценки

## Запуск
- Склонируйте репозиторий, откройте через Idea
- Запустите Application eureka-server, дождитесь старта
- Запустите оставшиеся Application микросервисов, убедитесь, что они подключились к eureka-server через логи spring

## Тестирование
- После запуска системы можно начать отправлять запросы через Postman
- Зарегистрируйте пользователя через /api/users/register
- Авторизируйтесь через /api/users/login, в ответ придет json с accessToken и tokenType, при последующих запросах к системе стоит указывать его в header Authorization (например, Bearer sdf125ajghiHAUYSFGu==)
- Продолжайте тестирование по эндпоинтам ниже, не забывая об авторизации
- Можно также запустить UnitTests, посмотреть что они завершаются успешно

## Endpoint приложения

Gateway (8880):
- Регистрация и авторизация
  - POST `/api/users/register`
  - POST `/api/users/login` 
- Бронирования
  - GET `/api/bookings` — получить все бронирования текущего пользователя
  - POST `/api/bookings` - создать бронирование номера
  - POST `/api/bookings/cancel?bookingId=value` - отменить бронирование
  - GET `/api/bookings/suggestions?hotelId=value` — получить наиболее снимаемые комнаты в отеле
- Пользователи (только админ):
  - GET, DELETE, PUT `/api/users?id=value`- получить, удалить, обновить любого пользователя по id
  - GET `/api/users` - получить всех пользователей
- Отели и комнаты
  - GET `/api/hotels`, GET `/api/hotels/hotel?hotelId=value` - информация об отелях
  - POST `/api/hotels`, PUT `/api/hotels?id=value`, DELETE `/api/hotels?id=value` - добавить отель, изменить данные отеля, удалить отель (только админ)
  - POST `/api/rooms`, PUT `/api/rooms?id=value`, DELETE `/api/rooms?id=value`- добавить комнату, изменить данные комнаты, удалить комнату (только админ)
  - POST `/api/rooms/hold` — забронировать комнату по id
  - POST `/api/rooms/release` — освободить комнату по id бронирования
  