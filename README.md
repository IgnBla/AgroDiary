# AgroDiary — дневник хозяйства для Android

![Version](https://img.shields.io/badge/version-1.5.1-brightgreen)

AgroDiary — мобильное приложение для учета хозяйства: животные, сотрудники, корма,
продукты, задачи и записи журнала. Поддерживает регистрацию и вход, журнал действий
и базовые отчеты.

## Возможности
- Животные: список, карточка, статус, редактирование.
- Сотрудники: список, карточка, статус, редактирование и удаление.
- Задачи: приоритет, срок, статус выполнения.
- Корма: склад, карточки, операции прихода/расхода.
- Продукты: склад, карточки, операции прихода/расхода.
- Журнал: записи по хозяйству и отдельный журнал действий.
- Отчеты: ключевые показатели по животным, задачам, складу и стоимости.
- Настройки: профиль пользователя, смена темы, выход.

## Технологии
- Kotlin
- Jetpack Compose (Material 3)
- MVVM + Clean Architecture
- Room (SQLite)
- Hilt
- Navigation Compose
- Coroutines & Flow
- AndroidX Security (EncryptedSharedPreferences) для локальной авторизации

## Требования
- Android Studio Hedgehog (или новее) / JDK 17
- Android 8.0+ (minSdk 26)

## Сборка
```bash
git clone https://github.com/OPnce32/AgroDiary.git
cd AgroDiary

# сборка
./gradlew build

# тесты
./gradlew test

# debug apk
./gradlew assembleDebug

# release apk
./gradlew assembleRelease
```

APK: `app/build/outputs/apk/release/app-release.apk`

## Структура проекта
- `app/src/main/java/com/agrodiary/data` — база данных, DAO, репозитории.
- `app/src/main/java/com/agrodiary/di` — Hilt модули.
- `app/src/main/java/com/agrodiary/ui` — экраны и компоненты (Compose).
- `app/src/main/java/com/agrodiary/common` — общие модели и утилиты.

## Лицензия
MIT
