# Dockerfile para a API GasUp
FROM openjdk:25-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o Maven wrapper
COPY mvnw .
COPY .mvn .mvn

# Copia o pom.xml e baixa as dependências (cache para builds mais rápidos)
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

# Copia o código fonte
COPY src src

# Compila o projeto (pula os testes para acelerar)
RUN ./mvnw package -DskipTests

# Expõe a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação
CMD ["java", "-jar", "target/*.jar"]