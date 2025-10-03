# Usa un'immagine base con Java 17
FROM eclipse-temurin:17-jdk

# Crea una directory per l'app
WORKDIR /app

# Copia il jar del microservizio (compilato)
COPY target/*.jar app.jar

# Espone la porta (facoltativo, aiuta nei compose/network)
EXPOSE 8081

# Comando di avvio
ENTRYPOINT ["java", "-jar", "app.jar"]

ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

