#!/bin/bash
# Скрипт для ініціалізації Gradle Wrapper

echo "Ініціалізація Gradle Wrapper..."

# Завантажуємо wrapper JAR якщо його немає
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "Завантаження gradle-wrapper.jar..."
    mkdir -p gradle/wrapper
    curl -L -o gradle/wrapper/gradle-wrapper.jar \
        https://raw.githubusercontent.com/gradle/gradle/v8.2.0/gradle/wrapper/gradle-wrapper.jar
fi

# Робимо gradlew виконуваним
chmod +x gradlew

echo "Готово! Тепер можна запустити: ./gradlew assembleRelease"
