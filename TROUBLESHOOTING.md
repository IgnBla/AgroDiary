# Устранение ошибок сборки (Troubleshooting)

Если вы столкнулись с ошибкой `Execution failed for JdkImageTransform` или другими проблемами сборки в Android Studio на Windows, выполните следующие шаги.

## Ошибка JdkImageTransform

Эта ошибка часто возникает из-за блокировки файлов процессом Gradle Daemon в Windows.

### Решение 1: Очистка кэша (Самое эффективное)

1. Закройте Android Studio.
2. Перейдите в корневую папку проекта `C:\AgroDiary`.
3. Запустите командную строку (CMD или PowerShell).
4. Выполните команду для остановки всех процессов Gradle:
   ```powershell
   ./gradlew --stop
   ```
5. Выполните чистую сборку без демона:
   ```powershell
   ./gradlew clean assembleDebug --no-daemon
   ```
6. После успешной сборки откройте Android Studio и попробуйте запустить приложение.

### Решение 2: Invalidate Caches в Android Studio

1. В Android Studio перейдите в меню **File** > **Invalidate Caches...**
2. Отметьте галочками "Clear file system cache and Local History" и "Clear VCS Log caches and indexes".
3. Нажмите **Invalidate and Restart**.

### Решение 3: Удаление папки .gradle

Если ничего не помогает:
1. Закройте Android Studio.
2. Удалите скрытую папку `.gradle` в корне вашего проекта (`C:\AgroDiary\AgroDiary\.gradle`).
3. Также можно очистить глобальный кэш Gradle: `C:\Users\<Имя_Пользователя>\.gradle\caches`.
4. Откройте проект заново, Gradle загрузит зависимости заново.

## Настройки проекта

В файл `gradle.properties` были добавлены настройки для повышения стабильности:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
org.gradle.daemon=false
```
Это увеличивает память для сборки и отключает демон по умолчанию, что помогает избежать ошибок блокировки файлов.
