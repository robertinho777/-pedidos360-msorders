# Pedidos360 - Microservicio de Pedidos (Orders)

Microservicio principal en **Spring Boot 3** que implementa la máquina de estados finita de pedidos, control transaccional de estados y descuento automático de stock en integración con el Catálogo.

---

## 🏛️ Funcionalidades

- **Máquina de Estados de Pedidos**:
  ```text
  CREADO ───┬───> ACEPTADO ───> EN_PREPARACION ───> DESPACHADO ───> ENTREGADO
            └───> CANCELADO
  ```
- **Validación Estricta**: Lanzamiento de `InvalidTransitionException` (HTTP 409) ante cualquier salto de estado no autorizado.
- **Integración con Catálogo**: Descuento atómico de existencias al pasar de `CREADO` a `ACEPTADO`.
- **Persistencia en Oracle Autonomous Database (ATP)**: Conexión segura mutual TLS con Oracle Wallet.

---

## ⚙️ Variables de Entorno

| Variable | Descripción | Valor por Defecto |
| :--- | :--- | :--- |
| `SERVER_PORT` | Puerto de escucha | `8081` |
| `CATALOG_SERVICE_URL` | URL del microservicio de Catálogo | `http://localhost:8082` |
| `DB_TNS_NAME` | Nombre del servicio TNS en la Wallet | `pedidos360orders_tp` |
| `TNS_ADMIN` | Ruta a la carpeta de la Wallet | `C:/Wallet360/Wallet_pedidos360orders` |
| `DB_USER` | Usuario de base de datos Oracle | `PEDIDOS_ORDERS` |
| `DB_PASSWORD` | Contraseña del usuario Oracle | `17deAcuario#07` |

---

## 🚀 Compilación y Ejecución

```bash
# Compilar JAR
./mvnw clean package -DskipTests

# Ejecutar con perfil Oracle en Linux / AWS
java -jar target/msorders-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=oracle \
  --spring.datasource.url="jdbc:oracle:thin:@pedidos360orders_tp?TNS_ADMIN=/home/ec2-user/wallet"
```
