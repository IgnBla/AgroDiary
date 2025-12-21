# Архитектура AgroDiary

Версия приложения: 1.5.1  
См. также: [Функциональные требования](functional-requirements.md), [Модель данных](data-model.md)

## Стек
- Kotlin, Android, Jetpack Compose (Material 3).
- MVVM: ViewModel + UI state (StateFlow/Flow).
- Room (SQLite) + конвертеры для enum.
- Hilt DI.
- Navigation Compose.
- EncryptedSharedPreferences для сессии/учётных данных.

## Слои и ответственность
- UI (Compose): экраны, списки, формы; только отображение и вызовы ViewModel.
- ViewModel: бизнес-логика UI, валидация, вызовы репозиториев, управление состоянием (успех/ошибки).
- Repository: абстракция над DAO; доп. логика (журнал действий, обновление отметок времени, апгрейд хеша паролей).
- Data: Room DAO, сущности, enum-конвертеры.

## Навигация (routes)
- Auth: login, register.
- Main: home, animals, journal, tasks, feed, products, staff, settings, reports.
- Details/CRUD: animal/{id}, staff/{id}, task/{id}, feed/{id}, product/{id}, journal/{id}; edit/add маршруты; activity log (journal/activity).

## Потоки данных
1. UI инициирует действие (сохранить/удалить/поиск) -> ViewModel.
2. ViewModel вызывает репозиторий, обновляет uiState (loading/error/success).
3. Repository обращается к DAO; при изменениях животные/сотрудники создаёт записи ActivityLogEntity.
4. DAO пишет/читает Room; Flow отражает изменения в UI.

## Хранение и сессия
- Room хранит доменные сущности.
- EncryptedSharedPreferences хранит current_user_id и хеши паролей/солей.
- Фото хранятся как URI (галерея/камера) без копирования.

## Безопасность (кратко)
- PBKDF2 хеш пароля + соль, апгрейд со старого SHA-256.
- Сессия в зашифрованных prefs.
- Нет сетевых взаимодействий.

## Ошибки и уведомления
- Ошибки операций показываются через Snackbar (uiState.error).
- Успешные операции — uiState.successMessage, там же автопереходы (например, после удаления животного/сотрудника — navigateBack).

## Журнал действий
- Репозитории Animals/Staff пишут ActivityLogEntity (type, details, entityId, entityType, createdAt).
- ActivityLogScreen читает Flow из ActivityLogRepository и отображает хронологию.

## Ограничения реализации
- Транзакции кормов/продуктов в UI отсутствуют.
- Привязка задач к сотрудникам/животным не реализована в UI (только поля в модели/DAO).
- Экран Reports не имеет явной кнопки перехода в текущем UI.
