# Модель данных AgroDiary

Связанные документы: [Оглавление](README.md), [Руководство пользователя](user-guide.md), [Спецификация](specification.md)

## 1. Таблицы

### 1.1 users (UserEntity)
| Поле | Тип | Обяз. | Описание |
| --- | --- | --- | --- |
| id | Long | да | Первичный ключ |
| username | String | да | Логин (уникальный) |
| passwordHash | String | да | Хеш пароля |
| passwordSalt | String | нет | Соль (PBKDF2) |
| displayName | String | да | Отображаемое имя |
| farmName | String | нет | Название хозяйства |
| email | String | нет | Email |
| phone | String | нет | Телефон |
| photoUri | String | нет | URI фото |
| isActive | Boolean | да | Активен |
| lastLoginAt | Long | нет | Последний вход |
| createdAt | Long | да | Создан |
| updatedAt | Long | да | Обновлен |

Сессия: EncryptedSharedPreferences (ключ `current_user_id`).

### 1.2 animals (AnimalEntity)
| Поле | Тип | Обяз. | Описание |
| --- | --- | --- | --- |
| id | Long | да | PK |
| name | String | да | Имя животного |
| type | AnimalType | да | Тип |
| breed | String | нет | Порода |
| birthDate | Long | нет | Дата рождения |
| gender | String | нет | Пол |
| weight | Float | нет | Вес |
| status | AnimalStatus | да | Статус |
| notes | String | нет | Примечания |
| photoUri | String | нет | Фото (URI) |
| createdAt | Long | да | Создан |
| updatedAt | Long | да | Обновлен |

### 1.3 staff (StaffEntity)
| Поле | Тип | Обяз. | Описание |
| --- | --- | --- | --- |
| id | Long | да | PK |
| name | String | да | ФИО |
| position | String | нет | Должность (в UI обязательна) |
| phone | String | нет | Телефон |
| email | String | нет | Email |
| hireDate | Long | нет | Дата найма |
| salary | Double | нет | Зарплата |
| status | StaffStatus | да | Статус |
| photoUri | String | нет | Фото |
| notes | String | нет | Примечания |
| createdAt | Long | да | Создан |
| updatedAt | Long | да | Обновлен |

### 1.4 tasks (TaskEntity)
| Поле | Тип | Обяз. | Описание |
| --- | --- | --- | --- |
| id | Long | да | PK |
| title | String | да | Название |
| description | String | нет | Описание |
| assignedStaffId | Long | нет | FK staff (SET_NULL) |
| animalId | Long | нет | FK animals (SET_NULL) |
| priority | TaskPriority | да | Приоритет |
| status | TaskStatus | да | Статус |
| dueDate | Long | нет | Срок |
| completedDate | Long | нет | Завершено |
| createdAt | Long | да | Создан |
| updatedAt | Long | да | Обновлен |

### 1.5 feed_stocks (FeedStockEntity)
| Поле | Тип | Обяз. | Описание |
| --- | --- | --- | --- |
| id | Long | да | PK |
| name | String | да | Наименование |
| category | FeedCategory | да | Категория |
| currentQuantity | Double | да | Текущий остаток |
| unit | MeasureUnit | да | Единица |
| minQuantity | Double | да | Минимальный остаток |
| pricePerUnit | Double | нет | Цена |
| notes | String | нет | Примечания |
| lastUpdated | Long | да | Обновлено |

### 1.6 feed_transactions (FeedTransactionEntity)
| Поле | Тип | Обяз. | Описание |
| --- | --- | --- | --- |
| id | Long | да | PK |
| feedStockId | Long | да | FK feed_stocks (CASCADE) |
| type | TransactionType | да | Тип операции |
| quantity | Double | да | Количество |
| date | Long | да | Дата |
| pricePerUnit | Double | нет | Цена |
| notes | String | нет | Примечания |
| createdAt | Long | да | Создан |

### 1.7 products (ProductEntity)
| Поле | Тип | Обяз. | Описание |
| --- | --- | --- | --- |
| id | Long | да | PK |
| name | String | да | Наименование |
| category | ProductCategory | да | Категория |
| currentQuantity | Double | да | Количество |
| unit | MeasureUnit | да | Единица |
| pricePerUnit | Double | нет | Цена |
| productionDate | Long | нет | Дата производства |
| expirationDate | Long | нет | Срок годности |
| notes | String | нет | Примечания |
| lastUpdated | Long | да | Обновлено |

### 1.8 product_transactions (ProductTransactionEntity)
| Поле | Тип | Обяз. | Описание |
| --- | --- | --- | --- |
| id | Long | да | PK |
| productId | Long | да | FK products (CASCADE) |
| type | TransactionType | да | Тип операции |
| quantity | Double | да | Количество |
| date | Long | да | Дата |
| pricePerUnit | Double | нет | Цена |
| totalPrice | Double | нет | Итоговая стоимость |
| buyerName | String | нет | Покупатель |
| notes | String | нет | Примечания |
| createdAt | Long | да | Создан |

### 1.9 journal_entries (JournalEntryEntity)
| Поле | Тип | Обяз. | Описание |
| --- | --- | --- | --- |
| id | Long | да | PK |
| date | Long | да | Дата записи |
| entryType | JournalEntryType | да | Тип |
| description | String | да | Описание |
| relatedAnimalId | Long | нет | FK animals (SET_NULL) |
| relatedStaffId | Long | нет | FK staff (SET_NULL) |
| amount | Double | нет | Сумма |
| notes | String | нет | Примечания |
| createdAt | Long | да | Создан |

### 1.10 activity_logs (ActivityLogEntity)
| Поле | Тип | Обяз. | Описание |
| --- | --- | --- | --- |
| id | Long | да | PK |
| type | ActivityLogType | да | Тип события |
| details | String | нет | Детали события |
| entityType | String | нет | Тип сущности (animal/staff) |
| entityId | Long | нет | ID сущности |
| createdAt | Long | да | Время создания |

## 2. Справочники (enum)
- AnimalType: COW (Корова), PIG (Свинья), CHICKEN (Курица), SHEEP (Овца), GOAT (Коза), HORSE (Лошадь), RABBIT (Кролик), OTHER (Другое)
- AnimalStatus: ACTIVE (Активен), SOLD (Продан), DEAD (Погиб), SICK (Болен)
- StaffStatus: ACTIVE (Активен), ON_VACATION (В отпуске), FIRED (Уволен)
- TaskPriority: LOW (Низкий), MEDIUM (Средний), HIGH (Высокий), URGENT (Срочный)
- TaskStatus: NEW (Новая), IN_PROGRESS (В работе), COMPLETED (Выполнена), CANCELLED (Отменена)
- FeedCategory: HAY (Сено), GRAIN (Зерно), COMPOUND_FEED (Комбикорм), SILAGE (Силос), VITAMINS (Витамины), OTHER (Другое)
- ProductCategory: MILK (Молоко), MEAT (Мясо), EGGS (Яйца), WOOL (Шерсть), CHEESE (Сыр), BUTTER (Масло), OTHER (Другое)
- MeasureUnit: KILOGRAM (кг), GRAM (г), LITER (л), PIECE (шт), BAG (меш), TON (т), DOZEN (дюж)
- JournalEntryType: FEEDING, HEALTH_CHECK, VACCINATION, BREEDING, WEIGHT_MEASURE, PURCHASE, SALE, TREATMENT, BIRTH, TASK_COMPLETED, OTHER
- TransactionType: INCOME, EXPENSE, PRODUCED, SOLD, CONSUMED, SPOILED
- ActivityLogType: ANIMAL_CREATED, ANIMAL_UPDATED, ANIMAL_DELETED, STAFF_CREATED, STAFF_UPDATED, STAFF_DELETED
