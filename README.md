# DomuLink

A digital rental platform for landlords and tenants in Nigeria.

## Overview

DomuLink is a comprehensive platform designed to streamline the rental process in Nigeria. It connects landlords with potential tenants, facilitates property listings, manages rental payments, and provides a secure environment for all rental-related transactions.

## Features

- User authentication and authorization with JWT
- Property listing and management
- Secure payment processing via PayStack
- File uploads with Cloudinary integration
- Email notifications
- Role-based access control (Admin, Landlord, Tenant)
- API documentation with Swagger UI

## Technologies Used

- **Java 17**
- **Spring Boot 3.4.4**
  - Spring Security
  - Spring Data JPA
  - Spring Web
  - Spring Mail
  - Spring Validation
  - Spring Data Redis
- **MySQL** - Database
- **Redis** - Caching
- **JWT** - Authentication
- **Cloudinary** - File storage
- **PayStack** - Payment processing
- **Swagger/OpenAPI** - API documentation
- **Lombok** - Reducing boilerplate code
- **Maven** - Dependency management and build

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Redis server
- Maven 3.6 or higher

## Setup and Installation

1. **Clone the repository**
   ```
   git clone https://github.com/yourusername/domulink.git
   cd domulink
   ```

2. **Configure environment variables**
   
   Create a `.env` file in the project root with the following variables:
   ```
   JWT_SECRET_KEY=your_jwt_secret_key
   
   CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
   CLOUDINARY_API_KEY=your_cloudinary_api_key
   CLOUDINARY_API_SECRET=your_cloudinary_api_secret
   
   PAYSTACK_SECRET_KEY=your_paystack_secret_key
   
   SPRING_MAIL_USERNAME=your_email@gmail.com
   SPRING_MAIL_PASSWORD=your_email_app_password
   ```

3. **Configure database**
   
   Update the database configuration in `application-dev.properties` if needed:
   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/domulink
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

4. **Build the project**
   ```
   mvn clean install
   ```

5. **Run the application**
   ```
   mvn spring-boot:run
   ```

6. **Access the application**
   
   The application will be available at `http://localhost:8080`
   
   API documentation (Swagger UI) will be available at `http://localhost:8080/swagger-ui.html`

## API Endpoints

### Authentication
- `POST /api/auth/**` - Authentication endpoints (register, login, etc.)

### Properties
- `GET /api/properties/**` - Property-related endpoints (requires authentication)

### Payments
- `POST /api/v1/pay/paystack` - Initialize payment with PayStack
- `POST /api/v1/pay/webhook` - PayStack webhook endpoint

### Admin
- `GET /admin_only/**` - Admin-only endpoints

## Configuration

The application uses different property files for different environments:
- `application-dev.properties` - Development environment
- `application-prod.properties` - Production environment

Key configurations include:
- JWT settings
- Database connection
- Redis connection
- Cloudinary settings
- PayStack API keys
- Email settings

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact 

Harkinsam247@gmail.com