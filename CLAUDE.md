# CLAUDE.md — AgroDiary Android App

> **Мобильное приложение «Журнал фермы» для Android**  
> Простое, понятное приложение для учёта животных, персонала, задач, кормов и товаров

---

## 📋 О ПРОЕКТЕ

**AgroDiary** — Android-приложение для ведения дневника фермы с тремя основными модулями:

1. **🐄 Животные** — учёт поголовья, карточки животных, история событий
2. **👷 Персонал и задачи** — управление сотрудниками и их задачами
3. **📊 Журнал/Отчётность** — записи событий + запасы кормов + учёт товаров

### Технологический стек

```
Язык:         Kotlin
UI:           Jetpack Compose (Material 3)
Архитектура:  MVVM + Clean Architecture (упрощённая)
База данных:  Room (SQLite)
DI:           Hilt
Навигация:    Navigation Compose
Min SDK:      26 (Android 8.0)
Compile SDK:  34
```

---

## 🤖 АГЕНТЫ ПРОЕКТА

> Агенты берутся из репозитория VoltAgent: https://github.com/VoltAgent/awesome-claude-code-subagents  
> После скачивания размести их в: `.claude/agents/`

### Команды для установки агентов

```bash
# Клонируй репозиторий с агентами
git clone https://github.com/VoltAgent/awesome-claude-code-subagents.git temp-agents

# Создай папку для агентов
mkdir -p .claude/agents

# Скопируй нужных агентов
cp temp-agents/categories/01-core-development/mobile-developer.md .claude/agents/
cp temp-agents/categories/02-language-specialists/kotlin-specialist.md .claude/agents/
cp temp-agents/categories/01-core-development/ui-designer.md .claude/agents/
cp temp-agents/categories/03-infrastructure/database-administrator.md .claude/agents/
cp temp-agents/categories/04-quality-security/code-reviewer.md .claude/agents/
cp temp-agents/categories/04-quality-security/qa-expert.md .claude/agents/
cp temp-agents/categories/06-developer-experience/documentation-engineer.md .claude/agents/
cp temp-agents/categories/08-business-product/project-manager.md .claude/agents/

# Удали временную папку
rm -rf temp-agents
```

### Список используемых агентов

| Агент | Назначение | Когда использовать |
|-------|------------|-------------------|
| `mobile-developer` | Архитектура мобильного приложения, навигация | Общая структура, модули, навигация |
| `kotlin-specialist` | Kotlin код, корутины, паттерны | Написание кода, ViewModels, Repository |
| `ui-designer` | UI/UX компоненты, Material Design | Все экраны и компоненты UI |
| `database-administrator` | Room, миграции, оптимизация запросов | Entities, DAOs, миграции БД |
| `code-reviewer` | Проверка качества кода | Ревью после каждого этапа |
| `qa-expert` | Тестирование | Написание и выполнение тестов |
| `documentation-engineer` | README, комментарии | Документация проекта |
| `project-manager` | Управление задачами | Координация работы |

---

## 📁 СТРУКТУРА ПРОЕКТА(если потребуется ориентируй под пустой шаблон Empty Activity)

```
AgroDiary/
├── app/
│   ├── src/main/
│   │   ├── java/com/agrodiary/
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── dao/              # Data Access Objects
│   │   │   │   │   ├── entity/           # Room Entity классы
│   │   │   │   │   ├── converter/        # TypeConverters
│   │   │   │   │   └── AppDatabase.kt
│   │   │   │   └── repository/           # Репозитории
│   │   │   ├── domain/
│   │   │   │   ├── model/                # Domain модели
│   │   │   │   └── usecase/              # Use Cases (опционально)
│   │   │   ├── ui/
│   │   │   │   ├── theme/                # Material 3 тема
│   │   │   │   ├── components/           # Переиспользуемые компоненты
│   │   │   │   ├── navigation/           # Навигация
│   │   │   │   ├── home/                 # Главный экран
│   │   │   │   ├── animals/              # Модуль Животные
│   │   │   │   ├── staff/                # Модуль Персонал
│   │   │   │   ├── tasks/                # Модуль Задачи
│   │   │   │   ├── journal/              # Модуль Журнал
│   │   │   │   ├── feed/                 # Модуль Корма
│   │   │   │   └── products/             # Модуль Товары
│   │   │   ├── di/                       # Hilt модули
│   │   │   └── AgroDiaryApp.kt
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── .claude/
│   └── agents/                           # Субагенты
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## ✅ ПЛАН РАЗРАБОТКИ С ЧЕКБОКСАМИ

### ЭТАП 0: Подготовка окружения ✅
**Агенты:** `project-manager` + `mobile-developer`

- [x] 0.1 Проверить шаблон Empty Activity в папке проекте.
- [ ] 0.2 Скачать и установить агентов из VoltAgent в `.claude/agents/`
- [x] 0.3 Открыть шаблон Empty Activity.
- [x] 0.4 Инициализировать Git репозиторий
- [x] 0.5 Создать `.gitignore` файл
- [x] 0.6 Создать базовую структуру папок по шаблону

---

### ЭТАП 1: Настройка зависимостей и конфигурации
**Агенты:** `kotlin-specialist` + `mobile-developer`

- [ ] 1.1 Настроить `build.gradle.kts` (project-level)
  - [ ] Добавить Kotlin версию
  - [ ] Добавить KSP plugin
  - [ ] Добавить Hilt plugin
- [ ] 1.2 Настроить `build.gradle.kts` (app-level)
  - [ ] Jetpack Compose BOM (2024.02.00)
  - [ ] Material 3
  - [ ] Navigation Compose (2.7.7)
  - [ ] Room (2.6.1): runtime, ktx, compiler
  - [ ] Hilt (2.50): android, compiler, navigation-compose
  - [ ] Lifecycle: viewmodel-compose, runtime-compose
  - [ ] Coil для изображений (2.5.0)
  - [ ] Testing: JUnit, MockK, Compose UI Testing
- [ ] 1.3 Настроить `settings.gradle.kts`
- [ ] 1.4 Синхронизировать Gradle
- [ ] 1.5 Создать `AgroDiaryApp.kt` с аннотацией `@HiltAndroidApp`
- [ ] 1.6 Обновить `AndroidManifest.xml` с Application class

---

### ЭТАП 2: Тема и базовые UI компоненты
**Агенты:** `ui-designer` + `kotlin-specialist`

- [ ] 2.1 Создать цветовую схему (`ui/theme/Color.kt`)
  - [ ] Primary: Зелёный (#2E7D32) — природа, рост
  - [ ] Secondary: Коричневый (#5D4037) — земля
  - [ ] Tertiary: Золотисто-жёлтый (#FFA726) — урожай
  - [ ] Background: Светло-бежевый (#FFF8E1)
  - [ ] Surface: Белый (#FFFFFF)
  - [ ] Error: Красный (#D32F2F)
- [ ] 2.2 Создать типографику (`ui/theme/Type.kt`)
- [ ] 2.3 Создать формы (`ui/theme/Shape.kt`)
- [ ] 2.4 Создать главную тему (`ui/theme/Theme.kt`)
  - [ ] Светлая тема
  - [ ] Тёмная тема
- [ ] 2.5 Создать базовые переиспользуемые компоненты
  - [ ] `AgroDiaryCard.kt` — карточка с тенью
  - [ ] `AgroDiaryButton.kt` — кнопки разных типов
  - [ ] `AgroDiaryTextField.kt` — поля ввода
  - [ ] `AgroDiaryTopBar.kt` — верхняя панель
  - [ ] `SearchBar.kt` — поиск
  - [ ] `FilterChips.kt` — фильтры
  - [ ] `EmptyStateView.kt` — пустое состояние
  - [ ] `LoadingView.kt` — индикатор загрузки
  - [ ] `ErrorView.kt` — экран ошибки
  - [ ] `ConfirmDialog.kt` — диалог подтверждения
  - [ ] `DatePickerField.kt` — выбор даты
  - [ ] `DropdownField.kt` — выпадающий список
- [ ] 2.6 Протестировать компоненты в @Preview

---

### ЭТАП 3: База данных (Room)
**Агенты:** `database-administrator` + `kotlin-specialist`

#### 3.1 Создать Entity классы

- [ ] 3.1.1 `AnimalEntity.kt`
```kotlin
// Поля: id, name, type (корова/свинья/курица/другое), 
// breed, birthDate, gender, weight, status (активное/продано/умерло),
// notes, photoUri, createdAt, updatedAt
```
- [ ] 3.1.2 `StaffEntity.kt`
```kotlin
// Поля: id, name, position, phone, email, 
// hireDate, salary, status (активен/уволен), 
// photoUri, notes, createdAt, updatedAt
```
- [ ] 3.1.3 `TaskEntity.kt`
```kotlin
// Поля: id, title, description, assignedStaffId (FK), 
// animalId (FK, nullable), priority (LOW/MEDIUM/HIGH/URGENT),
// status (NEW/IN_PROGRESS/COMPLETED/CANCELLED), 
// dueDate, completedDate, createdAt, updatedAt
```
- [ ] 3.1.4 `JournalEntryEntity.kt`
```kotlin
// Поля: id, date, entryType (FEEDING/HEALTH_CHECK/VACCINATION/
// BREEDING/WEIGHT_MEASURE/PURCHASE/SALE/TASK_COMPLETED/OTHER),
// description, relatedAnimalId (FK, nullable), 
// relatedStaffId (FK, nullable), amount, notes, createdAt
```
- [ ] 3.1.5 `FeedStockEntity.kt`
```kotlin
// Поля: id, name, category (СЕНО/ЗЕРНО/КОМБИКОРМ/ДРУГОЕ),
// currentQuantity, unit (КГ/МЕШОК/ТОННА/ЛИТР),
// minQuantity (предупреждение), lastUpdated, notes
```
- [ ] 3.1.6 `FeedTransactionEntity.kt`
```kotlin
// Поля: id, feedStockId (FK), type (INCOME/EXPENSE),
// quantity, date, pricePerUnit, notes, createdAt
```
- [ ] 3.1.7 `ProductEntity.kt`
```kotlin
// Поля: id, name, category (МОЛОКО/МЯСО/ЯЙЦА/ШЕРСТЬ/ДРУГОЕ),
// currentQuantity, unit, pricePerUnit, 
// productionDate, expirationDate, notes, lastUpdated
```
- [ ] 3.1.8 `ProductTransactionEntity.kt`
```kotlin
// Поля: id, productId (FK), type (PRODUCED/SOLD/CONSUMED/SPOILED),
// quantity, date, pricePerUnit, totalPrice, 
// buyerName (nullable), notes, createdAt
```

#### 3.2 Создать Enum классы

- [ ] 3.2.1 `AnimalType.kt` (КОРОВА, СВИНЬЯ, КУРИЦА, ОВЦА, КОЗА, ДРУГОЕ)
- [ ] 3.2.2 `AnimalStatus.kt` (АКТИВНОЕ, ПРОДАНО, УМЕРЛО)
- [ ] 3.2.3 `StaffStatus.kt` (АКТИВЕН, УВОЛЕН, В_ОТПУСКЕ)
- [ ] 3.2.4 `TaskPriority.kt` (LOW, MEDIUM, HIGH, URGENT)
- [ ] 3.2.5 `TaskStatus.kt` (NEW, IN_PROGRESS, COMPLETED, CANCELLED)
- [ ] 3.2.6 `JournalEntryType.kt` (все типы записей)
- [ ] 3.2.7 `FeedCategory.kt` (СЕНО, ЗЕРНО, КОМБИКОРМ, ДРУГОЕ)
- [ ] 3.2.8 `ProductCategory.kt` (МОЛОКО, МЯСО, ЯЙЦА, ШЕРСТЬ, ДРУГОЕ)
- [ ] 3.2.9 `TransactionType.kt` (INCOME, EXPENSE, PRODUCED, SOLD, CONSUMED, SPOILED)

#### 3.3 Создать TypeConverters

- [ ] 3.3.1 `Converters.kt` (для Date, Enum классов)

#### 3.4 Создать DAO интерфейсы

- [ ] 3.4.1 `AnimalDao.kt` (CRUD + getAll + getByType + search)
- [ ] 3.4.2 `StaffDao.kt` (CRUD + getAll + getActive + search)
- [ ] 3.4.3 `TaskDao.kt` (CRUD + getByStatus + getByStaff + getOverdue)
- [ ] 3.4.4 `JournalDao.kt` (CRUD + getByDateRange + getByType + getByAnimal)
- [ ] 3.4.5 `FeedStockDao.kt` (CRUD + getAll + getLowStock)
- [ ] 3.4.6 `FeedTransactionDao.kt` (CRUD + getByFeed + getByDateRange)
- [ ] 3.4.7 `ProductDao.kt` (CRUD + getAll + getByCategory)
- [ ] 3.4.8 `ProductTransactionDao.kt` (CRUD + getByProduct + getByDateRange)

#### 3.5 Создать Database

- [ ] 3.5.1 `AppDatabase.kt` с версией 1
- [ ] 3.5.2 Добавить все Entity и DAO
- [ ] 3.5.3 Добавить TypeConverters
- [ ] 3.5.4 (Опционально) Добавить prepopulate для тестовых данных

#### 3.6 Создать Hilt модули

- [ ] 3.6.1 `DatabaseModule.kt` — предоставляет Database и DAOs
- [ ] 3.6.2 Протестировать создание БД

---

### ЭТАП 4: Репозитории
**Агенты:** `kotlin-specialist` + `mobile-developer`

- [ ] 4.1 Создать `AnimalRepository.kt` (+ интерфейс)
- [ ] 4.2 Создать `StaffRepository.kt` (+ интерфейс)
- [ ] 4.3 Создать `TaskRepository.kt` (+ интерфейс)
- [ ] 4.4 Создать `JournalRepository.kt` (+ интерфейс)
- [ ] 4.5 Создать `FeedStockRepository.kt` (+ интерфейс)
- [ ] 4.6 Создать `FeedTransactionRepository.kt` (+ интерфейс)
- [ ] 4.7 Создать `ProductRepository.kt` (+ интерфейс)
- [ ] 4.8 Создать `ProductTransactionRepository.kt` (+ интерфейс)
- [ ] 4.9 Создать `RepositoryModule.kt` для Hilt

---

### ЭТАП 5: Навигация
**Агенты:** `mobile-developer` + `ui-designer`

- [ ] 5.1 Создать `Screen.kt` — sealed class с маршрутами
- [ ] 5.2 Создать `BottomNavItem.kt` — элементы навигации (3 вкладки)
- [ ] 5.3 Создать `NavGraph.kt` — граф навигации
- [ ] 5.4 Создать `AgroDiaryBottomNavBar.kt` — нижняя панель
- [ ] 5.5 Интегрировать навигацию в `MainActivity.kt`
- [ ] 5.6 Проверить переходы между экранами

---

### ЭТАП 6: Главный экран (Dashboard)
**Агенты:** `ui-designer` + `kotlin-specialist`

- [ ] 6.1 Создать `HomeViewModel.kt`
  - [ ] Быстрая статистика (количество животных, активные задачи)
  - [ ] Последние 5 записей журнала
  - [ ] Срочные задачи (до 3 дней)
  - [ ] Предупреждения о низких запасах
- [ ] 6.2 Создать `HomeScreen.kt`
  - [ ] Приветствие с датой
  - [ ] Карточки статистики (4 карточки)
  - [ ] Список срочных задач
  - [ ] Список предупреждений о запасах
  - [ ] Кнопки быстрого доступа
- [ ] 6.3 Создать UI компоненты
  - [ ] `StatCard.kt` — карточка статистики
  - [ ] `QuickTaskCard.kt` — срочная задача
  - [ ] `WarningCard.kt` — предупреждение
- [ ] 6.4 Протестировать экран

---

### ЭТАП 7: Модуль «Животные» 🐄
**Агенты:** `kotlin-specialist` + `ui-designer` + `database-administrator`

#### 7.1 ViewModel

- [ ] 7.1.1 Создать `AnimalsViewModel.kt`
- [ ] 7.1.2 Загрузка списка животных (StateFlow)
- [ ] 7.1.3 Добавление животного
- [ ] 7.1.4 Редактирование животного
- [ ] 7.1.5 Удаление животного
- [ ] 7.1.6 Фильтрация по типу
- [ ] 7.1.7 Поиск по имени
- [ ] 7.1.8 Получение животного по ID

#### 7.2 Экраны

- [ ] 7.2.1 `AnimalsListScreen.kt`
  - [ ] Список животных (LazyColumn)
  - [ ] Поиск
  - [ ] Фильтр по типу (Chips)
  - [ ] FAB для добавления
  - [ ] Пустое состояние
- [ ] 7.2.2 `AnimalDetailScreen.kt`
  - [ ] Информация о животном
  - [ ] Фото (если есть)
  - [ ] История записей журнала
  - [ ] Кнопки редактирования/удаления
- [ ] 7.2.3 `AddEditAnimalScreen.kt`
  - [ ] Форма с полями
  - [ ] Выбор типа (Dropdown)
  - [ ] Выбор фото (опционально)
  - [ ] Валидация полей
  - [ ] Сохранение

#### 7.3 Компоненты

- [ ] 7.3.1 `AnimalCard.kt` — карточка в списке
- [ ] 7.3.2 `AnimalTypeChip.kt` — фильтр по типу

#### 7.4 Интеграция

- [ ] 7.4.1 Подключить навигацию модуля
- [ ] 7.4.2 Протестировать CRUD операции
- [ ] 7.4.3 Проверить поиск и фильтрацию

---

### ЭТАП 8: Модуль «Персонал» 👷
**Агенты:** `kotlin-specialist` + `ui-designer`

#### 8.1 ViewModel

- [ ] 8.1.1 Создать `StaffViewModel.kt`
- [ ] 8.1.2 Загрузка списка сотрудников
- [ ] 8.1.3 CRUD операции
- [ ] 8.1.4 Поиск
- [ ] 8.1.5 Фильтр по статусу

#### 8.2 Экраны

- [ ] 8.2.1 `StaffListScreen.kt`
- [ ] 8.2.2 `StaffDetailScreen.kt` (с задачами сотрудника)
- [ ] 8.2.3 `AddEditStaffScreen.kt`

#### 8.3 Компоненты

- [ ] 8.3.1 `StaffCard.kt`
- [ ] 8.3.2 `StaffStatusChip.kt`

#### 8.4 Интеграция

- [ ] 8.4.1 Подключить навигацию
- [ ] 8.4.2 Протестировать функционал

---

### ЭТАП 9: Модуль «Задачи» 📋
**Агенты:** `kotlin-specialist` + `ui-designer`

#### 9.1 ViewModel

- [ ] 9.1.1 Создать `TasksViewModel.kt`
- [ ] 9.1.2 Загрузка задач
- [ ] 9.1.3 CRUD операции
- [ ] 9.1.4 Изменение статуса
- [ ] 9.1.5 Фильтр по статусу/приоритету
- [ ] 9.1.6 Поиск

#### 9.2 Экраны

- [ ] 9.2.1 `TasksListScreen.kt`
  - [ ] Группировка по статусу
  - [ ] Фильтры
  - [ ] Поиск
- [ ] 9.2.2 `TaskDetailScreen.kt`
- [ ] 9.2.3 `AddEditTaskScreen.kt`
  - [ ] Выбор исполнителя
  - [ ] Выбор приоритета
  - [ ] Выбор даты
  - [ ] Привязка к животному (опционально)

#### 9.3 Компоненты

- [ ] 9.3.1 `TaskCard.kt` (с цветовым индикатором приоритета)
- [ ] 9.3.2 `TaskStatusChip.kt`
- [ ] 9.3.3 `TaskPriorityChip.kt`

#### 9.4 Интеграция

- [ ] 9.4.1 Подключить навигацию
- [ ] 9.4.2 Протестировать функционал

---

### ЭТАП 10: Модуль «Журнал» 📖
**Агенты:** `kotlin-specialist` + `ui-designer`

#### 10.1 ViewModel

- [ ] 10.1.1 Создать `JournalViewModel.kt`
- [ ] 10.1.2 Загрузка записей
- [ ] 10.1.3 CRUD операции
- [ ] 10.1.4 Фильтр по дате
- [ ] 10.1.5 Фильтр по типу
- [ ] 10.1.6 Фильтр по животному

#### 10.2 Экраны

- [ ] 10.2.1 `JournalListScreen.kt`
  - [ ] Календарь для выбора даты
  - [ ] Список записей по дате
  - [ ] Фильтры
- [ ] 10.2.2 `AddJournalEntryScreen.kt`
  - [ ] Быстрое добавление записи
  - [ ] Выбор типа
  - [ ] Привязка к животному/сотруднику

#### 10.3 Компоненты

- [ ] 10.3.1 `JournalEntryCard.kt`
- [ ] 10.3.2 `DateFilterBar.kt`
- [ ] 10.3.3 `EntryTypeChip.kt`

#### 10.4 Интеграция

- [ ] 10.4.1 Подключить навигацию
- [ ] 10.4.2 Протестировать функционал

---

### ЭТАП 11: Модуль «Запас кормов» 🌾
**Агенты:** `kotlin-specialist` + `ui-designer`

#### 11.1 ViewModels

- [ ] 11.1.1 Создать `FeedStockViewModel.kt`
- [ ] 11.1.2 Загрузка запасов
- [ ] 11.1.3 CRUD операции
- [ ] 11.1.4 Проверка низкого запаса
- [ ] 11.1.5 Создать `FeedTransactionViewModel.kt`
- [ ] 11.1.6 Операции прихода/расхода

#### 11.2 Экраны

- [ ] 11.2.1 `FeedStockListScreen.kt`
  - [ ] Список кормов с остатками
  - [ ] Индикатор уровня (ProgressBar)
  - [ ] Предупреждения о низком запасе
- [ ] 11.2.2 `FeedDetailScreen.kt`
  - [ ] История операций
  - [ ] График расхода (опционально)
- [ ] 11.2.3 `AddEditFeedScreen.kt`
- [ ] 11.2.4 `FeedTransactionScreen.kt` (приход/расход)

#### 11.3 Компоненты

- [ ] 11.3.1 `FeedStockCard.kt` (с индикатором уровня)
- [ ] 11.3.2 `LowStockWarning.kt`
- [ ] 11.3.3 `FeedTransactionCard.kt`

#### 11.4 Интеграция

- [ ] 11.4.1 Подключить навигацию
- [ ] 11.4.2 Протестировать функционал

---

### ЭТАП 12: Модуль «Учёт товаров» 🧺
**Агенты:** `kotlin-specialist` + `ui-designer`

#### 12.1 ViewModels

- [ ] 12.1.1 Создать `ProductViewModel.kt`
- [ ] 12.1.2 Загрузка товаров
- [ ] 12.1.3 CRUD операции
- [ ] 12.1.4 Группировка по категориям
- [ ] 12.1.5 Создать `ProductTransactionViewModel.kt`
- [ ] 12.1.6 Операции с товарами

#### 12.2 Экраны

- [ ] 12.2.1 `ProductsListScreen.kt`
  - [ ] Группировка по категориям
  - [ ] Список товаров
- [ ] 12.2.2 `ProductDetailScreen.kt`
  - [ ] История операций
  - [ ] Статистика продаж
- [ ] 12.2.3 `AddEditProductScreen.kt`
- [ ] 12.2.4 `ProductTransactionScreen.kt`

#### 12.3 Компоненты

- [ ] 12.3.1 `ProductCard.kt`
- [ ] 12.3.2 `CategoryHeader.kt`
- [ ] 12.3.3 `ProductTransactionCard.kt`

#### 12.4 Интеграция

- [ ] 12.4.1 Подключить навигацию
- [ ] 12.4.2 Протестировать функционал

---

### ЭТАП 13: Модуль «Отчёты и статистика» 📊
**Агенты:** `kotlin-specialist` + `ui-designer`

#### 13.1 ViewModel

- [ ] 13.1.1 Создать `ReportsViewModel.kt`
- [ ] 13.1.2 Статистика по животным
- [ ] 13.1.3 Статистика по задачам
- [ ] 13.1.4 Статистика по запасам
- [ ] 13.1.5 Финансовая статистика (продажи товаров)

#### 13.2 Экран

- [ ] 13.2.1 `ReportsScreen.kt`
  - [ ] Карточки сводки
  - [ ] Простые графики (опционально)
  - [ ] Экспорт в текст (опционально)

#### 13.3 Компоненты

- [ ] 13.3.1 `ReportStatCard.kt`
- [ ] 13.3.2 `SimpleChart.kt` (опционально)

#### 13.4 Интеграция

- [ ] 13.4.1 Подключить навигацию
- [ ] 13.4.2 Протестировать функционал

---

### ЭТАП 14: Интеграция и полировка
**Агенты:** `mobile-developer` + `ui-designer` + `code-reviewer`

- [ ] 14.1 Интегрировать все модули в единую навигацию
- [ ] 14.2 Обновить главный экран (Dashboard) с реальными данными
- [ ] 14.3 Реализовать глобальный поиск (опционально)
- [ ] 14.4 Добавить настройки приложения
  - [ ] Тема (светлая/тёмная)
  - [ ] Уведомления
  - [ ] Резервное копирование БД (опционально)
- [ ] 14.5 Оптимизировать производительность
  - [ ] LazyColumn pagination (если нужно)
  - [ ] Кэширование
- [ ] 14.6 Проверить все переходы и навигацию
- [ ] 14.7 Проверить обработку ошибок
- [ ] 14.8 Провести код-ревью всех модулей

---

### ЭТАП 15: Тестирование
**Агенты:** `qa-expert` + `kotlin-specialist`

#### 15.1 Unit тесты

- [ ] 15.1.1 Тесты для всех Repository
- [ ] 15.1.2 Тесты для всех ViewModel
- [ ] 15.1.3 Тесты для DAO

#### 15.2 UI тесты

- [ ] 15.2.1 Тесты навигации
- [ ] 15.2.2 Тесты основных экранов
- [ ] 15.2.3 Тесты CRUD операций

#### 15.3 Ручное тестирование

- [ ] 15.3.1 Тестирование всех функций вручную
- [ ] 15.3.2 Проверка на разных размерах экрана
- [ ] 15.3.3 Проверка тёмной темы
- [ ] 15.3.4 Проверка работы с фото (если реализовано)

#### 15.4 Исправление багов

- [ ] 15.4.1 Исправить все найденные баги
- [ ] 15.4.2 Повторное тестирование

---

### ЭТАП 16: Документация
**Агенты:** `documentation-engineer`

- [ ] 16.1 Написать README.md
  - [ ] Описание проекта
  - [ ] Установка и настройка
  - [ ] Структура проекта
  - [ ] Скриншоты
- [ ] 16.2 Добавить KDoc комментарии к основным классам
- [ ] 16.3 Создать CHANGELOG.md
- [ ] 16.4 Создать инструкцию по сборке

---

### ЭТАП 17: Финализация и релиз
**Агенты:** `mobile-developer` + `project-manager`

- [ ] 17.1 Создать иконку приложения
- [ ] 17.2 Создать splash screen (опционально)
- [ ] 17.3 Настроить ProGuard/R8 для release
- [ ] 17.4 Настроить подпись APK
- [ ] 17.5 Собрать release APK
- [ ] 17.6 Провести финальное тестирование release сборки
- [ ] 17.7 Создать release в Git с тегом версии v1.0.0
- [ ] 17.8 (Опционально) Подготовить к публикации в Google Play

---

## 📦 ЗАВИСИМОСТИ (build.gradle.kts)

### Project-level build.gradle.kts

```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
}
```

### App-level build.gradle.kts

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.agrodiary"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.agrodiary"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // Hilt DI
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    
    // Coil для изображений
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

---

## 📝 ПРИМЕРЫ КОДА

### Entity пример

```kotlin
@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: AnimalType,
    val breed: String?,
    val birthDate: Long?,
    val gender: String?,
    val weight: Float?,
    val status: AnimalStatus,
    val notes: String?,
    val photoUri: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### DAO пример

```kotlin
@Dao
interface AnimalDao {
    @Query("SELECT * FROM animals ORDER BY name ASC")
    fun getAllAnimals(): Flow<List<AnimalEntity>>
    
    @Query("SELECT * FROM animals WHERE type = :type ORDER BY name ASC")
    fun getAnimalsByType(type: AnimalType): Flow<List<AnimalEntity>>
    
    @Query("SELECT * FROM animals WHERE id = :id")
    suspend fun getAnimalById(id: Long): AnimalEntity?
    
    @Query("SELECT * FROM animals WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchAnimals(query: String): Flow<List<AnimalEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimal(animal: AnimalEntity): Long
    
    @Update
    suspend fun updateAnimal(animal: AnimalEntity)
    
    @Delete
    suspend fun deleteAnimal(animal: AnimalEntity)
}
```

### Repository пример

```kotlin
class AnimalRepository @Inject constructor(
    private val animalDao: AnimalDao
) {
    fun getAllAnimals(): Flow<List<AnimalEntity>> = animalDao.getAllAnimals()
    
    fun getAnimalsByType(type: AnimalType): Flow<List<AnimalEntity>> = 
        animalDao.getAnimalsByType(type)
    
    suspend fun getAnimalById(id: Long): AnimalEntity? = animalDao.getAnimalById(id)
    
    fun searchAnimals(query: String): Flow<List<AnimalEntity>> = 
        animalDao.searchAnimals(query)
    
    suspend fun insertAnimal(animal: AnimalEntity): Long = animalDao.insertAnimal(animal)
    
    suspend fun updateAnimal(animal: AnimalEntity) = animalDao.updateAnimal(animal)
    
    suspend fun deleteAnimal(animal: AnimalEntity) = animalDao.deleteAnimal(animal)
}
```

### ViewModel пример

```kotlin
@HiltViewModel
class AnimalsViewModel @Inject constructor(
    private val repository: AnimalRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    private val _selectedType = MutableStateFlow<AnimalType?>(null)
    
    val animals: StateFlow<List<AnimalEntity>> = combine(
        _searchQuery,
        _selectedType
    ) { query, type ->
        when {
            query.isNotBlank() -> repository.searchAnimals(query)
            type != null -> repository.getAnimalsByType(type)
            else -> repository.getAllAnimals()
        }
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setSelectedType(type: AnimalType?) {
        _selectedType.value = type
    }
    
    fun addAnimal(animal: AnimalEntity) {
        viewModelScope.launch {
            repository.insertAnimal(animal)
        }
    }
    
    fun updateAnimal(animal: AnimalEntity) {
        viewModelScope.launch {
            repository.updateAnimal(animal)
        }
    }
    
    fun deleteAnimal(animal: AnimalEntity) {
        viewModelScope.launch {
            repository.deleteAnimal(animal)
        }
    }
}
```

### Compose Screen пример

```kotlin
@Composable
fun AnimalsListScreen(
    viewModel: AnimalsViewModel = hiltViewModel(),
    onAnimalClick: (Long) -> Unit,
    onAddClick: () -> Unit
) {
    val animals by viewModel.animals.collectAsState()
    
    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = "Животные",
                onBackClick = null
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Добавить животное")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Поиск
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                placeholder = "Поиск животных..."
            )
            
            // Фильтры по типу
            FilterChips(
                items = AnimalType.values().toList(),
                selectedItem = selectedType,
                onItemSelected = { viewModel.setSelectedType(it) }
            )
            
            // Список
            if (animals.isEmpty()) {
                EmptyStateView(
                    message = "Нет животных",
                    icon = Icons.Default.Pets
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(animals) { animal ->
                        AnimalCard(
                            animal = animal,
                            onClick = { onAnimalClick(animal.id) }
                        )
                    }
                }
            }
        }
    }
}
```

---

## 🎯 СОВЕТЫ ДЛЯ НОВИЧКА

1. **Делай по одному этапу** — не переходи к следующему, пока не закончишь текущий
2. **Тестируй после каждого шага** — запускай приложение часто
3. **Используй Preview** — Jetpack Compose позволяет видеть UI без запуска
4. **Коммить часто** — делай маленькие коммиты после каждой завершённой задачи
5. **Читай ошибки внимательно** — Kotlin даёт понятные сообщения
6. **Используй агентов** — обращайся к конкретному агенту для конкретной задачи
7. **Не бойся спрашивать** — если что-то непонятно, спроси Claude

---

## 🚀 КОМАНДЫ ДЛЯ ЗАПУСКА

```bash
# Сборка проекта
./gradlew build

# Запуск unit-тестов
./gradlew test

# Запуск UI-тестов (с подключённым устройством/эмулятором)
./gradlew connectedAndroidTest

# Сборка debug APK
./gradlew assembleDebug

# Сборка release APK
./gradlew assembleRelease

# Проверка кода (lint)
./gradlew lint

# Форматирование кода (если настроен ktlint)
./gradlew ktlintFormat
```

---

## 📚 ПОЛЕЗНЫЕ РЕСУРСЫ

### Официальная документация

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Hilt DI](https://developer.android.com/training/dependency-injection/hilt-android)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Material 3 Design](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

### Репозитории с агентами

- [VoltAgent/awesome-claude-code-subagents](https://github.com/VoltAgent/awesome-claude-code-subagents) — 100+ production-ready агентов
- [hesreallyhim/awesome-claude-code](https://github.com/hesreallyhim/awesome-claude-code) — Команды, файлы, workflows

---

## 📌 ВАЖНЫЕ ЗАМЕЧАНИЯ

### Соглашения по именованию

```kotlin
// Классы: PascalCase
class AnimalEntity
class AnimalsViewModel

// Функции: camelCase
fun getAllAnimals()
fun addNewAnimal()

// Константы: SCREAMING_SNAKE_CASE
const val MAX_ANIMALS = 1000
const val DEFAULT_WEIGHT = 0f

// Пакеты: lowercase
com.agrodiary.data.local
```

### Архитектурные правила

- ViewModel не должен знать о View (Composable функциях)
- Repository абстрагирует источники данных (Room, API, и т.д.)
- Entity (Room) != Domain Model — используй маппинг когда нужно
- Все IO операции через Kotlin Coroutines
- UI состояние через StateFlow/State
- Compose: stateless компоненты где возможно

### Git workflow

```bash
# Ветки
main — стабильная версия
develop — разработка
feature/animals — модуль животные
feature/staff — модуль персонал

# Коммиты
feat: add animals list screen
fix: correct database migration
refactor: improve navigation structure
test: add repository tests
docs: update README
```

---

## ✅ КРИТЕРИИ ГОТОВНОСТИ ПРОЕКТА

Проект считается готовым когда:

- [ ] Все чекбоксы в плане отмечены
- [ ] Все модули работают без ошибок
- [ ] Unit-тесты проходят успешно
- [ ] UI-тесты проходят успешно
- [ ] Приложение работает на реальном устройстве/эмуляторе
- [ ] Код прошёл ревью (`code-reviewer`)
- [ ] Документация написана
- [ ] Release APK собран и протестирован

---

**Версия плана:** 1.0  
**Дата создания:** 2025-12-19  
**Создано для:** Claude Code  
**Основано на:** VoltAgent subagents + awesome-claude-code best practices  
**Лицензия:** MIT

---

*Этот план разработан специально для использования с Claude Code. Все агенты, команды и структура оптимизированы для максимальной эффективности работы.*
