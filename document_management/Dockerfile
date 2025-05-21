# Etapa 1: Construcción del proyecto
# Usamos una imagen que contiene Maven y OpenJDK 17 para compilar nuestra aplicación
# El "AS build" nombra esta etapa para poder referenciarla después
FROM maven:3-openjdk-17 AS build

# Establece el directorio de trabajo dentro del contenedor
# Todos los comandos siguientes se ejecutarán desde esta ubicación
WORKDIR /app

# Copia los archivos necesarios para compilar
# Primero copiamos solo el pom.xml para aprovechar la caché de Docker con las dependencias
COPY pom.xml .
# Luego copiamos el código fuente de la aplicación
COPY src ./src

# Compila la aplicación sin ejecutar pruebas
# El comando "mvn clean package" limpia, compila y empaqueta la aplicación en un archivo JAR
# "-DskipTests" evita la ejecución de pruebas para acelerar el proceso de construcción
RUN mvn clean package -DskipTests

# Etapa 2: Contenedor para ejecutar el JAR
# Usamos una imagen más ligera que solo contiene el JDK necesario para ejecutar la aplicación
# Esta imagen "slim" reduce el tamaño final del contenedor
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo
# Nuevamente definimos /app como directorio de trabajo para esta segunda etapa
WORKDIR /app

# Copia el JAR desde la etapa de construcción
# "--from=build" indica que el archivo viene de la etapa anterior llamada "build"
# Copiamos el JAR generado y lo renombramos a "app.jar" para simplificar
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto de la aplicación
# Indica que el contenedor escuchará en el puerto 8080 (puerto por defecto de Spring Boot)
# Nota: Esto es solo documentación, aún necesitas mapear el puerto al ejecutar el contenedor
EXPOSE 8080

# Comando para ejecutar la aplicación
# Define el comando que se ejecutará cuando el contenedor se inicie
# Ejecuta la aplicación Java contenida en el archivo JAR
ENTRYPOINT ["java", "-jar", "app.jar"]