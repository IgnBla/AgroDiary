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
- Статистика: животные, задачи, низкие остатки кормов; срочные задачи (<=3 дня); последние записи журнала.
- Быстрые карточки переходов к основным модулям.

### 6.2 Животные
- Список: поиск по имени, фильтр по типу.
- Создать: кнопка «+». Имя и тип обязательны; порода/дата рождения/пол/вес/статус/примечания/фото — опционально.
- Редактировать: карточка → «карандаш» в AppBar.
- Удалить: «корзина» в AppBar, без подтверждения.

### 6.3 Сотрудники
- Список: поиск по имени, фильтр по статусу.
- Создать: «+». Обязательные поля: имя, должность. Остальное — опционально.
- Редактировать: карточка → «карандаш».
- Удалить: «корзина» без подтверждения.

### 6.4 Задачи
- Список: поиск/фильтр по статусу.
- Создать: «+». Название обязательно, описание/приоритет/срок — по желанию.
- Редактировать: карточка → «карандаш».
- Удалить: «корзина» с подтверждением.

### 6.5 Корма
- Список: фильтр по категории.
- Создать: «+». Поля: имя*, категория, остаток*, ед. измерения, минимальный остаток, примечания.
- Редактировать: карточка → «карандаш».
- Удалить: «корзина» с подтверждением.

### 6.6 Продукты
- Список: фильтр по категории.
- Создать: «+». Поля: имя*, категория, количество*, ед. измерения, цена (опц.), примечания.
- Редактировать: карточка → «карандаш».
- Удалить: «корзина» с подтверждением.

### 6.7 Журнал записей
- Список: фильтр по типу; быстрые кнопки к Кормам и Продуктам.
- Создать: «+». Поля: дата, тип*, описание*, животное (опц.), сотрудник (опц.), примечания.
- Редактировать: карточка → «карандаш».
- Удалить: «корзина» с подтверждением.

### 6.8 Журнал действий
- Кнопка «История» в AppBar журнала; события создания/обновления/удаления животных и сотрудников.

### 6.9 Настройки
- Смена темы: системная/светлая/тёмная.
- Обновление аватара: выбрать фото.
- Выход: кнопка «Выйти» с подтверждением.

### 6.10 Отчеты
- Маршрут `reports` (кнопки в UI нет). Показывает животных, задачи (выполнено/всего), низкий остаток кормов, стоимость продуктов.

## 7. Подсказки и ограничения
- Привязка задач к сотрудникам/животным пока не реализована в UI (поля в модели есть).
- Транзакции по кормам/продуктам в интерфейсе отсутствуют.
- Данные локальные; удаление приложения удаляет базу и prefs.

## Ссылки на исходники (относительно корня репозитория)
- Навигация: `../app/src/main/java/com/agrodiary/ui/navigation/NavGraph.kt`
- Аутентификация: `../app/src/main/java/com/agrodiary/ui/auth/LoginScreen.kt`, `../app/src/main/java/com/agrodiary/ui/auth/RegisterScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/AuthRepository.kt`
- Главная: `../app/src/main/java/com/agrodiary/ui/home/HomeScreen.kt`, `../app/src/main/java/com/agrodiary/ui/home/HomeViewModel.kt`
- Животные: `../app/src/main/java/com/agrodiary/ui/animals/AnimalsListScreen.kt`, `../app/src/main/java/com/agrodiary/ui/animals/AnimalDetailScreen.kt`, `../app/src/main/java/com/agrodiary/ui/animals/AddEditAnimalScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/AnimalRepository.kt`
- Сотрудники: `../app/src/main/java/com/agrodiary/ui/staff/StaffListScreen.kt`, `../app/src/main/java/com/agrodiary/ui/staff/StaffDetailScreen.kt`, `../app/src/main/java/com/agrodiary/ui/staff/AddEditStaffScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/StaffRepository.kt`
- Задачи: `../app/src/main/java/com/agrodiary/ui/tasks/TasksListScreen.kt`, `../app/src/main/java/com/agrodiary/ui/tasks/TaskDetailScreen.kt`, `../app/src/main/java/com/agrodiary/ui/tasks/AddEditTaskScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/TaskRepository.kt`
- Корма: `../app/src/main/java/com/agrodiary/ui/feed/FeedStockListScreen.kt`, `../app/src/main/java/com/agrodiary/ui/feed/FeedDetailScreen.kt`, `../app/src/main/java/com/agrodiary/ui/feed/AddEditFeedScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/FeedStockRepository.kt`
- Продукты: `../app/src/main/java/com/agrodiary/ui/products/ProductsListScreen.kt`, `../app/src/main/java/com/agrodiary/ui/products/ProductDetailScreen.kt`, `../app/src/main/java/com/agrodiary/ui/products/AddEditProductScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/ProductRepository.kt`
- Журнал записей: `../app/src/main/java/com/agrodiary/ui/journal/JournalListScreen.kt`, `../app/src/main/java/com/agrodiary/ui/journal/JournalDetailScreen.kt`, `../app/src/main/java/com/agrodiary/ui/journal/AddEditJournalEntryScreen.kt`, `../app/src/main/java/com/agrodiary/data/repository/JournalRepository.kt`
- Журнал действий: `../app/src/main/java/com/agrodiary/ui/activitylog/ActivityLogScreen.kt`, `../app/src/main/java/com/agrodiary/ui/activitylog/ActivityLogViewModel.kt`, `../app/src/main/java/com/agrodiary/data/repository/ActivityLogRepository.kt`
- Настройки: `../app/src/main/java/com/agrodiary/ui/settings/SettingsScreen.kt`
- Отчеты: `../app/src/main/java/com/agrodiary/ui/reports/ReportsScreen.kt`, `../app/src/main/java/com/agrodiary/ui/reports/ReportsViewModel.kt`
- Сборка: `../app/build.gradle.kts`
