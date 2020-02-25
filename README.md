# ComponentFactory

## Overview

Приложение для продажи комплектующих, производимых заводом. Основной круг потребителей: пивоварни, бары и пивные магазины.

## Сущности
Ниже перечисленны сущности в предметной области проекта и их поля.

### Company (Компания)
Представитель некоторой компании который хочет оставить заявку на покупку необходимых ему комплектующих.

Поля:
- Название
- Email
- ФИО
- Номер телефона
- Информация о компании

Связи:
- появляются в момент регистрации на домене "Завода" (Компания, Завод)
- появляются в рамках заказа (Счет-фактура, Компания, Завод)
- заканчиваются в момент отгрузки заказа(Счет-фактура, Компания, Завод)

### Order (Заказ)
Список товаров которые "Компания" покупает у "Завода".

Поля:
- Название компании
- Список товаров и их количество
- Номер счета
- Итоговая сумма
- Дата заявки
- Дата отгрузки
- Статус

Связи:
- появляются в рамках заказа (счет-фактура, Компания, Завод)
- заканчиваются в момент отгрузки заказа (счет-фактура, Компания, Завод)

### Product (Товар)
Единица продукции производимая фабрикой

Поля:
- Название;
- Тип товара;
- Себестоимость;
- категория;
Связи:
- появляются в рамках заказа (Счет-фактура)

### Factory (Завод)

Представитель завода.

Поля:
- Должность
- Email
- ФИО
- Номер телефона

Связи:
- появляются в рамках обработки заказа (Счет-фактура, Завод),
- заканчиваются в рамках отправки заказа к отгрузке(Счет-фактура, Завод)
- внутренние связи между представителями завода, для выполнения своих функций

## User Stories

## US-1
В первую очередь начнем с работы "Компании" с системой.
Регистрация на сайте "Завода".

### JTEP-1 Как "Компания" я хочу зарегистрироваться в системе, и, если такого пользователя не найдено, регистрируюсь

Request:

`POST /componentFactory/sign-up`
```json
{
   "company" : "ООО"Аливария"",
  "email" : "vasya@email.com",
  "password" : "qwerty",
  "fullName" : "Пупкин Василий Иванович",
  "phone" : "+375445333880", 
  "info" : "Пивоварня №1 в СНГ" 
}
```

Response:

`201 CREATED`

### JTEP-2 Как "Компания", будучи зарегистрированным пользователем, я хочу войти в систему, и, если такой пользователь существует и пароль совпадает, войти в систему

Request:

`POST /componentFactory/auth/sign-in`
```json
{
  "email" : "vasya@email.com",
  "password" : "qwerty"
}
```

Response:

`200 OK`

```json
{
    "token":  "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InZhc3lhQGVtYWlsLmNvbSIsImlhdCI6MTU4MTAwMDE5OCwiZXhwIjoxNTgxMDg2NTk4fQ.X-LwrENDF6t1i8w2guPGXW-nODArpn-WkT81iycxUrN7lpjJQxEuCJKgp15aCWoLEArbOqVQuZmtjTd_Tn7DLw"
}
```

### JTEP-3 Как "Компания" я хочу посмотреть список товаров, и в результате получаю список товаров

Request:

`GET /componentFactory/products`

`Headers: Authorization=Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InZhc3lhQGVtYWlsLmNvbSIsImlhdCI6MTU4MTAwMDE5OCwiZXhwIjoxNTgxMDg2NTk4fQ.X-LwrENDF6t1i8w2guPGXW-nODArpn-WkT81iycxUrN7lpjJQxEuCJKgp15aCWoLEArbOqVQuZmtjTd_Tn7DLw` 


Response:

`200 OK`
```json
[
   {
      "id":1,
      "name":"Бутылка",
      "type":"0.5",
      "primeCost":60,
      "category":"Тара для хранения"
   },
   {
      "id":2,
      "name":"Воздушный фильтр",
      "type":"0.1",
      "primeCost":40,
      "category":"Фильтрация и сорбирование"
   },
   {
      "id":3,
      "name":"Кран",
      "type":"45-120",
      "primeCost":12000,
      "category":"Устройства для розлива"
   }
]
```

### JTEP-4 Как "Компания" я хочу сделать заказ на 10000 бутылок и 4 воздушных фильтра для пивоварни , и в результате получаю счет-фактуру

Request:

`POST /componentFactory/orders/new`

`Headers: Authorization=Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InZhc3lhQGVtYWlsLmNvbSIsImlhdCI6MTU4MTAwMDE5OCwiZXhwIjoxNTgxMDg2NTk4fQ.X-LwrENDF6t1i8w2guPGXW-nODArpn-WkT81iycxUrN7lpjJQxEuCJKgp15aCWoLEArbOqVQuZmtjTd_Tn7DLw` 

```json
{
   "productDetails" : 
  [{ 
    "id": 1,
     "quantity": 10000
  },{
  "id": 2,
       "quantity": 4
  }],
  "startDate" : "12.02.2020",
  "endDate" : "16.03.2020"
}
```

Response:

`200 OK`

```json
{
   "orderId" : 1,
   "company" :  " ООО\"Аливария\"",
    "email": "vasya@email.com",
    "startDate" : "12.03.2020",
    "endDate" : "12.03.2020",
    "cost": 756201
    }
```

### JTEP-5 Как "Компания" я смотрю счет-фактуру, и в результате подтверждаю заказ

Заявка сохраняется  в базе. 

Request:

`POST /componentFactory/orders/1`

`Headers: Authorization=Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InZhc3lhQGVtYWlsLmNvbSIsImlhdCI6MTU4MTAwMDE5OCwiZXhwIjoxNTgxMDg2NTk4fQ.X-LwrENDF6t1i8w2guPGXW-nODArpn-WkT81iycxUrN7lpjJQxEuCJKgp15aCWoLEArbOqVQuZmtjTd_Tn7DLw` 

```json
{
   "isConfirmed" : "true"
}
```

Response:

`200 OK`

## US-2
Теперь рассмотрим работу "Завода" с системой.
Начнем с человека ответственного за заказы. 
Данный пользователь уже зарегистрирован с соответствующими правами.

### JTEP-1 Как "Завод", будучи зарегистрированным пользователем, я хочу войти в систему, и, если такой пользователь существует и пароль совпадает, войти в систему

Request:

`POST /componentFactory/auth/sign-in`
```json
{
  "email" : "petya@email.com",
  "password" : "123qweasdzxc"
}
```

Response:

`200 OK`

```json
{
    "toke": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InBldHlhQGVtYWlsLmNvbSIsImlhdCI6MTU4MTAwMDE5OCwiZXhwIjoxNTgxMDg2NTk4fQ.Ay1NMwXVorXg71kUKPcl5kVrpsqAibUAkbXyEOvF_OU1Y_5WLulIAzntoBIAQEopEfmeh5nvqmemIagFgJfNgQ"
}
```

### JTEP-2 Как "Завод" я хочу посмотреть список заявок, и в результате получаю список заявок

Request:

`GET /componentFactory/orders`

`Headers: Authorization=Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InBldHlhQGVtYWlsLmNvbSIsImlhdCI6MTU4MTAwMDE5OCwiZXhwIjoxNTgxMDg2NTk4fQ.Ay1NMwXVorXg71kUKPcl5kVrpsqAibUAkbXyEOvF_OU1Y_5WLulIAzntoBIAQEopEfmeh5nvqmemIagFgJfNgQ` 


Response:

`200 OK`

```json
[
   {
      "id":1,
      "cost":756201,
      "startDate":"12.02.2020",
      "endDate":"12.03.2020",
      "status":"confirmed"
   },
   {
      "id":2,
      "cost":756201,
      "startDate":"12.02.2020",
      "endDate":"12.03.2020",
      "status":"confirmed"
   },
   {
      "id":3,
      "cost":756201,
      "startDate":"12.02.2020",
      "endDate":"12.03.2020",
      "status":"confirmed"
   }
]
```

### JTEP-3 Как "Завод" я хочу открыть заказ №1, и в результате вижу содержимое заказа

Request:

`GET /componentFactory/orders/1`

`Headers: Authorization=Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InBldHlhQGVtYWlsLmNvbSIsImlhdCI6MTU4MTAwMDE5OCwiZXhwIjoxNTgxMDg2NTk4fQ.Ay1NMwXVorXg71kUKPcl5kVrpsqAibUAkbXyEOvF_OU1Y_5WLulIAzntoBIAQEopEfmeh5nvqmemIagFgJfNgQ` 

Response:

`200 OK`

```json
{
   "orderId":1,
   "users":{
      "company":" ООО\"Аливария\"",
      "email":"vasya@email.com"
   },
   "startDate":"10.02.2020",
   "endDate":"12.03.2020",
   "cost":756201,
   "status":"confirmed",
   "productDetails":[
      {
         "product":{
            "id":1,
            "name":"Бутылка",
            "primeCost":60,
            "type":"0.5",
            "category":"Тара для хранения"
         },
         "quantity":10000
      },
      {
         "product":{
            "id":2,
            "name":"Воздушный фильтр",
            "primeCost":40,
            "type":"0.1",
            "category":"Фильтрация и сорбирование"
         },
         "quantity":4
      }
   ]
}
```

## US-3
Продолжим рассматривать работу "Завода" с системой.
Теперь от начальника производства. 
Данный пользователь уже зарегистрирован с соответствующими правами.

### JTEP-1 Как "Завод", будучи зарегистрированным пользователем, я хочу войти в систему, и, если такой пользователь существует и пароль совпадает, войти в систему

Request:

`POST /componentFactory/auth/sign-in`
```json
{
  "email" : "dima@email.com",
  "password" : "cxzdsaewq321"
}
```

Response:

`200 OK`

```json
{
  "token" : "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImRpbWFAZW1haWwuY29tIiwiaWF0IjoxNTgxMDAwMTk4LCJleHAiOjE1ODEwODY1OTh9.Tq5maHNgMPAN9nZWTquXWV46TDKJocdU8b8uzvPZZhZN2wRHJR31W5LHQz87eApGcGmWda5PBEaMSNEYCPFs6w"
}
```


### JTEP-2 Как "Завод" я хочу открыть заказ , и в результате вижу содержимое заказа

Request:

`GET /componentFactory/orders/1`

`Headers: Authorization=Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImRpbWFAZW1haWwuY29tIiwiaWF0IjoxNTgxMDAwMTk4LCJleHAiOjE1ODEwODY1OTh9.Tq5maHNgMPAN9nZWTquXWV46TDKJocdU8b8uzvPZZhZN2wRHJR31W5LHQz87eApGcGmWda5PBEaMSNEYCPFs6w` 

Response:

`200 OK`

```json
{
   "orderId":1,
   "users":{
      "company":" ООО\"Аливария\"",
      "email":"vasya@email.com"
   },
   "startDate":"10.02.2020",
   "endDate":"12.03.2020",
   "cost":756201,
   "status":"confirmed",
   "productDetails":[
      {
         "product":{
            "id":1,
            "name":"Бутылка",
            "primeCost":60,
            "type":"0.5",
            "category":"Тара для хранения"
         },
         "quantity":10000
      },
      {
         "product":{
            "id":2,
            "name":"Воздушный фильтр",
            "primeCost":40,
            "type":"0.1",
            "category":"Фильтрация и сорбирование"
         },
         "quantity":4
      }
   ]
}
```

### JTEP-3 Как "Завод" я смотрю заказ, и в результате ставлю статус заказа в "работе"

Request:

`PUT /componentFactory/orders/{orderId}`

`Headers: Authorization=Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImRpbWFAZW1haWwuY29tIiwiaWF0IjoxNTgxMDAwMTk4LCJleHAiOjE1ODEwODY1OTh9.Tq5maHNgMPAN9nZWTquXWV46TDKJocdU8b8uzvPZZhZN2wRHJR31W5LHQz87eApGcGmWda5PBEaMSNEYCPFs6w` 

```json
{ 
"status": "work"
 }
```

Response:

`200 OK`

### JTEP-4 Как "Завод" подготовив заказ, в результате ставлю статус заказа в "закрыт"

Request:

`PUT /componentFactory/orders/{orderId}`

`Headers: Authorization=Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImRpbWFAZW1haWwuY29tIiwiaWF0IjoxNTgxMDAwMTk4LCJleHAiOjE1ODEwODY1OTh9.Tq5maHNgMPAN9nZWTquXWV46TDKJocdU8b8uzvPZZhZN2wRHJR31W5LHQz87eApGcGmWda5PBEaMSNEYCPFs6w` 

```json
{ "orderId": 1,
"status": "work" }
```

Response:

`200 OK`