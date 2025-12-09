# E-Commerce Application

A full-featured e-commerce REST API built with Spring Boot 3.5.8, providing comprehensive functionality for customers and sellers to manage products, orders, carts, and more.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Docker Deployment](#docker-deployment)
- [Environment Variables](#environment-variables)

## ✨ Features

### Customer Features

- User authentication and authorization with JWT
- Product browsing and search
- Shopping cart management
- Wishlist functionality
- Order placement and tracking
- Customer profile management
- Address management

### Seller Features

- Seller profile management
- Product management (CRUD operations)
- Order management and fulfillment
- Inventory tracking

### Security & Performance

- JWT-based authentication
- Role-based access control (RBAC)
- AOP-based logging and performance monitoring
- Authorization aspect for endpoint security
- Cart validation and stock management aspects
- CORS configuration for cross-origin requests

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.5.8
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security with JWT
- **Validation**: Spring Validation
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **AOP**: Spring AOP for cross-cutting concerns
- **Containerization**: Docker

### Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Validation
- Spring Boot Starter AOP
- PostgreSQL Driver
- Lombok
- JWT (jjwt 0.11.5)
- SpringDoc OpenAPI 2.8.14

## 📦 Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher installed
- **Maven 3.6+** installed
- **PostgreSQL** database
- **Docker** (optional, for containerized deployment)

## 🚀 Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/Ahmed1092002/ecommerce_spring.git
   cd ecommerce_spring
   ```

2. **Configure the database**

   - Create a PostgreSQL database
   - Update database credentials in `application.properties` or set environment variables

3. **Build the project**
   ```bash
   mvn clean install
   ```

## ⚙️ Configuration

The application uses environment variables for configuration. See the `application.properties` file for available options:

### Database Configuration

```properties
spring.datasource.url=${JDBC_DATABASE_URL:jdbc:postgresql://localhost:5432/ecommerce}
spring.datasource.username=${JDBC_DATABASE_USERNAME:postgres}
spring.datasource.password=${JDBC_DATABASE_PASSWORD:password}
```

### JWT Configuration

```properties
jwt.secret=${JWT_SECRET:your-secret-key}
jwt.expiration=${JWT_EXPIRATION:43200000}
```

### Server Configuration

```properties
server.port=${PORT:8080}
```

## 🏃 Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using Java

```bash
java -jar target/ecommerce-0.0.1-SNAPSHOT.jar
```

### Using Maven Wrapper (Windows)

```bash
.\mvnw.cmd spring-boot:run
```

### Using Maven Wrapper (Linux/Mac)

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080` (or the port specified in the `PORT` environment variable).

## 📚 API Documentation

Once the application is running, access the interactive API documentation at:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs`

### Available Endpoints

#### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login

#### Products

- `GET /api/products` - Get all products (public)
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create product (seller only)
- `PUT /api/products/{id}` - Update product (seller only)
- `DELETE /api/products/{id}` - Delete product (seller only)

#### Cart

- `GET /api/cart` - Get user's cart
- `POST /api/cart` - Add item to cart
- `PUT /api/cart/{id}` - Update cart item
- `DELETE /api/cart/{id}` - Remove item from cart

#### Orders

- `GET /api/orders` - Get user's orders
- `POST /api/orders` - Place order
- `GET /api/orders/{id}` - Get order details

#### Wishlist

- `GET /api/wishlist` - Get user's wishlist
- `POST /api/wishlist` - Add item to wishlist
- `DELETE /api/wishlist/{id}` - Remove from wishlist

#### Profile Management

- `GET /api/customer/profile` - Get customer profile
- `PUT /api/customer/profile` - Update customer profile
- `GET /api/seller/profile` - Get seller profile
- `PUT /api/seller/profile` - Update seller profile

#### Address Management

- `GET /api/addresses` - Get user addresses
- `POST /api/addresses` - Add new address
- `PUT /api/addresses/{id}` - Update address
- `DELETE /api/addresses/{id}` - Delete address

#### Seller Orders

- `GET /api/seller/orders` - Get seller's orders
- `PUT /api/seller/orders/{id}` - Update order status

## 📁 Project Structure

```
ecommerce/
├── src/
│   ├── main/
│   │   ├── java/com/example/test_ecommerce/ecommerce/
│   │   │   ├── aspect/              # AOP aspects
│   │   │   │   ├── AuthorizationAspect.java
│   │   │   │   ├── CartValidationAspect.java
│   │   │   │   ├── LoggingAspect.java
│   │   │   │   ├── PerformanceAspect.java
│   │   │   │   └── StockManagementAspect.java
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── enums/               # Enumerations
│   │   │   ├── exceptions/          # Custom exceptions
│   │   │   ├── repository/          # Data repositories
│   │   │   ├── security/            # Security configuration & JWT
│   │   │   ├── services/            # Business logic
│   │   │   ├── utils/               # Utility classes
│   │   │   └── EcommerceApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/                  # Database scripts
│   └── test/                        # Test files
├── target/                          # Build output
├── DockerFile                       # Docker configuration
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## 🐳 Docker Deployment

### Build Docker Image

```bash
mvn clean package
docker build -t ecommerce-app .
```

### Run Docker Container

```bash
docker run -p 8080:8080 \
  -e JDBC_DATABASE_URL=jdbc:postgresql://your-db-host:5432/ecommerce \
  -e JDBC_DATABASE_USERNAME=your-username \
  -e JDBC_DATABASE_PASSWORD=your-password \
  -e JWT_SECRET=your-jwt-secret \
  ecommerce-app
```

### Docker Compose (Recommended)

Create a `docker-compose.yml` file:

```yaml
version: "3.8"
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: ecommerce
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      JDBC_DATABASE_URL: jdbc:postgresql://db:5432/ecommerce
      JDBC_DATABASE_USERNAME: postgres
      JDBC_DATABASE_PASSWORD: password
      JWT_SECRET: your-secret-key-here
    depends_on:
      - db

volumes:
  postgres-data:
```

Run with:

```bash
docker-compose up
```

## 🔐 Environment Variables

| Variable                 | Description                         | Default                                      |
| ------------------------ | ----------------------------------- | -------------------------------------------- |
| `JDBC_DATABASE_URL`      | PostgreSQL connection URL           | `jdbc:postgresql://localhost:5432/ecommerce` |
| `JDBC_DATABASE_USERNAME` | Database username                   | `postgres`                                   |
| `JDBC_DATABASE_PASSWORD` | Database password                   | -                                            |
| `JWT_SECRET`             | Secret key for JWT token generation | -                                            |
| `JWT_EXPIRATION`         | JWT token expiration time (ms)      | `43200000` (12 hours)                        |
| `PORT`                   | Application port                    | `8080`                                       |

## 🧪 Testing

Run tests with:

```bash
mvn test
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.

## 👤 Author

**Ahmed**

- GitHub: [@Ahmed1092002](https://github.com/Ahmed1092002)

## 📞 Support

For support, please open an issue in the GitHub repository.

## 🔮 Future Enhancements

- [ ] Payment gateway integration
- [ ] Email notifications
- [ ] Product reviews and ratings
- [ ] Advanced search and filtering
- [ ] Product recommendations
- [ ] Admin dashboard
- [ ] Multi-language support
- [ ] Mobile app integration

---

**Note**: Make sure to change default credentials and JWT secrets before deploying to production!
