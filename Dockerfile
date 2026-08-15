# Build stage
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Copy Gradle files
COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY gradle.properties .
COPY build.gradle.kts .
COPY gradle/libs.versions.toml gradle/libs.versions.toml

# Copy all source code
COPY core core
COPY bot bot
COPY feat feat

# Build fat JAR
RUN ./gradlew :bot:discord:fatJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the fat JAR from builder
COPY --from=builder /app/bot/discord/build/libs/discord-bot-all.jar /app/discord-bot.jar

# Create /res directory for config volume
RUN mkdir -p /app/res
COPY res/config.json /app/res/config.json

# Run the bot
CMD ["java", "-Xms128m", "-Xmx350m", "-jar", "discord-bot.jar"]