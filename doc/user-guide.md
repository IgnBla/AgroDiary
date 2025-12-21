# Руководство пользователя AgroDiary

Версия приложения: 1.5.1  
Связанные документы: [Оглавление](README.md), [Спецификация](specification.md), [Модель данных](data-model.md)

## 1. Назначение
AgroDiary — локальный дневник хозяйства: животные, сотрудники, задачи, корма, продукты, журнал записей и журнал действий.

## 2. Требования
- Android 8.0+ (minSdk 26).
- Память для локальной БД Room и фото (URI сохраняются в галерее/камере).

## 3. Установка
1) Скопируйте APK на устройство. 2) Откройте APK и подтвердите установку. 3) Разрешите установку из неизвестных источников (если потребуется).

## 4. Аутентификация
- Регистрация: логин (>=3), пароль (>=4), подтверждение, отображаемое имя, название хозяйства (опц.). После регистрации выполняется автологин.
- Вход: введите логин и пароль, нажмите «Войти». Ошибки выводятся во всплывающем сообщении.
- Выход: раздел «Настройки» → «Выйти» → подтвердить.

## 5. Навигация
- Нижняя панель: Главная, Животные, Журнал.
- Карточки на Главной ведут к Задачам, Кормам, Продуктам, Сотрудникам, Журналу.
- Журнал действий открывается кнопкой «История» в верхней панели раздела Журнал.

## 6. Разделы и сценарии
### 6.1 Главная
- Видит статистику (животные, задачи, низкие остатки кормов), срочные задачи (<=3 дня), предупреждения по складу, последние записи журнала.
- Клик по карточке или задаче ведёт в соответствующий раздел.

### 6.2 Животные
- Список: поиск по имени, фильтр по типу.
- Создать: кнопка «+». Заполните имя и тип (обязательно); порода, дата рождения, пол, вес, статус, примечания и фото — опционально. Сохранить.
- Редактировать: открыть карточку животного → иконка «карандаш» в AppBar.
- Удалить: иконка «корзина» в AppBar удаляет сразу без подтверждения.

### 6.3 Сотрудники
- Список: поиск по имени, фильтр по статусу.
- Создать: кнопка «+». Обязательные поля: имя, должность. Телефон/email/дата найма/зарплата/примечания/фото — опционально.
- Редактировать: открыть карточку → «карандаш» в AppBar.
- Удалить: иконка «корзина» в AppBar удаляет сразу без подтверждения.

### 6.4 Задачи
- Список: поиск по названию/описанию, фильтр по статусу.
- Создать: «+». Укажите название (обязательно), описание (опц.), приоритет, статус, срок выполнения.
- Редактировать: карточка задачи → «карандаш».
- Удалить: иконка «корзина» требует подтверждения.

### 6.5 Корма
- Список: фильтр по категории.
- Создать: «+». Поля: имя*, категория, остаток*, единица измерения, минимальный остаток, примечания.
- Редактировать: открыть позицию → «карандаш».
- Удалить: иконка «корзина» с подтверждением.

### 6.6 Продукты
- Список: фильтр по категории.
- Создать: «+». Поля: имя*, категория, количество*, единица измерения, цена за единицу (опц.), примечания.
- Редактировать: карточка → «карандаш».
- Удалить: иконка «корзина» с подтверждением.

### 6.7 Журнал записей
- Список: фильтр по типу записи; быстрые кнопки к Кормам и Продуктам.
- Создать: «+». Поля: дата, тип*, описание*, связанное животное (опц.), сотрудник (опц.), примечания.
- Редактировать: карточка → «карандаш».
- Удалить: иконка «корзина» с подтверждением.

### 6.8 Журнал действий
- Открыть: кнопка «История» в AppBar журнала.
- Содержимое: события создания/обновления/удаления животных и сотрудников (тип, детали, время).

### 6.9 Настройки
- Смена темы: системная/светлая/тёмная.
- Обновление аватара: выбрать фото в профиле.
- Выход из аккаунта: кнопка «Выйти» с подтверждением.

### 6.10 Отчеты
- Доступ: маршрут `reports` (кнопка в UI отсутствует).
- Показатели: всего животных; задачи (выполнено/всего); низкий остаток кормов; стоимость продуктов.

## 7. Подсказки и ограничения
- Привязка задач к сотрудникам/животным не поддерживается в UI (поля в модели есть).
- Транзакции по кормам/продуктам не представлены в интерфейсе.
- Данные остаются на устройстве; удаление приложения удаляет данные.

## ?????? ?? ?????????
- ?????????: `../app/src/main/java/com/agrodiary/ui/navigation/NavGraph.kt`
- ??????????????: `../app/src/main/java/com/agrodiary/ui/auth/LoginScreen.kt`, `../app/src/main/java/com/agrodiary/ui/auth/RegisterScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/AuthRepository.kt`
- ???????: `../app/src/main/java/com/agrodiary/ui/home/HomeScreen.kt`
- ????????: `../app/src/main/java/com/agrodiary/ui/animals/AnimalsListScreen.kt`, `../app/src/main/java/com/agrodiary/ui/animals/AnimalDetailScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/AnimalRepository.kt`
- ??????????: `../app/src/main/java/com/agrodiary/ui/staff/StaffListScreen.kt`, `../app/src/main/java/com/agrodiary/ui/staff/StaffDetailScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/StaffRepository.kt`
- ??????: `../app/src/main/java/com/agrodiary/ui/tasks/TasksListScreen.kt`, `../app/src/main/java/com/agrodiary/ui/tasks/TaskDetailScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/TaskRepository.kt`
- ?????: `../app/src/main/java/com/agrodiary/ui/feed/FeedStockListScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/FeedStockRepository.kt`
- ????????: `../app/src/main/java/com/agrodiary/ui/products/ProductsListScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/ProductRepository.kt`
- ?????? ???????: `../app/src/main/java/com/agrodiary/ui/journal/JournalListScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/JournalRepository.kt`
- ?????? ????????: `../app/src/main/java/com/agrodiary/ui/activitylog/ActivityLogScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/ActivityLogRepository.kt`
- ?????????: `../app/src/main/java/com/agrodiary/ui/settings/SettingsScreen.kt`
- ??????: `../app/src/main/java/com/agrodiary/ui/reports/ReportsScreen.kt`
- ??????: `../app/build.gradle.kts`

## Ссылки на исходники
- Навигация: `../app/src/main/java/com/agrodiary/ui/navigation/NavGraph.kt`
- Аутентификация: `../app/src/main/java/com/agrodiary/ui/auth/LoginScreen.kt`, `../app/src/main/java/com/agrodiary/ui/auth/RegisterScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/AuthRepository.kt`
- Главная: `../app/src/main/java/com/agrodiary/ui/home/HomeScreen.kt`
- Животные: `../app/src/main/java/com/agrodiary/ui/animals/AnimalsListScreen.kt`, `../app/src/main/java/com/agrodiary/ui/animals/AnimalDetailScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/AnimalRepository.kt`
- Сотрудники: `../app/src/main/java/com/agrodiary/ui/staff/StaffListScreen.kt`, `../app/src/main/java/com/agrodiary/ui/staff/StaffDetailScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/StaffRepository.kt`
- Задачи: `../app/src/main/java/com/agrodiary/ui/tasks/TasksListScreen.kt`, `../app/src/main/java/com/agrodiary/ui/tasks/TaskDetailScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/TaskRepository.kt`
- Корма: `../app/src/main/java/com/agrodiary/ui/feed/FeedStockListScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/FeedStockRepository.kt`
- Продукты: `../app/src/main/java/com/agrodiary/ui/products/ProductsListScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/ProductRepository.kt`
- Журнал записей: `../app/src/main/java/com/agrodiary/ui/journal/JournalListScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/JournalRepository.kt`
- Журнал действий: `../app/src/main/java/com/agrodiary/ui/activitylog/ActivityLogScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/ActivityLogRepository.kt`
- Настройки: `../app/src/main/java/com/agrodiary/ui/settings/SettingsScreen.kt`
- Отчеты: `../app/src/main/java/com/agrodiary/ui/reports/ReportsScreen.kt`
- Сборка: `../app/build.gradle.kts`
