# ----------------------------------------------------------------------
# STAGE 1: BUILDER (Compilação)
# ----------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia e baixa as dependências (para aproveitar o cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte e compila
COPY src ./src
RUN mvn package -DskipTests

# ----------------------------------------------------------------------
# STAGE 2: FINAL (Runtime)
# ----------------------------------------------------------------------
# Usa apenas o JRE (Runtime) para uma imagem final leve
FROM eclipse-temurin:21-jre-alpine

# Define o volume de trabalho
WORKDIR /app

# Expõe a porta que o Spring Boot usa
EXPOSE 8080

# Copia o JAR compilado da etapa 'builder'
COPY --from=builder /app/target/*.jar app.jar

# Comando para rodar a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]