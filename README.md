# Link Shortener

A feature-rich Spring Boot REST API application that shortens URLs and provides detailed analytics on link usage with geolocation tracking and user behavior insights.

## Features

### Core Functionality
- **URL Shortening**: Convert long URLs into short, memorable 8-character alphanumeric codes
- **Smart Redirects**: Redirect short URLs to their original destinations while tracking clicks
- **Security**: Spring Security integration with user authentication for protected endpoints

### Analytics & Insights
- **Basic Statistics**:
  - Total click count
  - Daily/weekly click distribution (last 7 days or 4 weeks)
  - Top 3 countries, cities, referrers, and devices
- **Advanced Analytics**:
  - Returning users count (IP-based tracking)
  - Average time between clicks
  - Hourly and daily click distribution patterns

### Data Collection
- **Geolocation Tracking**: Track click sources by country and city using MaxMind GeoIP2
- **User Agent Detection**: Parse and store browser, OS, and device information
- **Referrer Tracking**: Monitor traffic sources
- **IP-based User Recognition**: Identify returning users

### Performance
- **Redis Caching**: Optimized analytics computations with 1-hour TTL
- **Lazy Loading**: GeoIP database downloaded from S3 on first use
- **Multi-environment Support**: Separate configurations for dev, prod, and test

## Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.4.5
- **Database**: PostgreSQL (production), H2 (testing)
- **Cache**: Redis
- **Build Tool**: Maven 3.9.4
- **Containerization**: Docker with multi-stage builds

### Key Dependencies
- Spring Web, Data JPA, Security, Actuator
- MaxMind GeoIP2 (v4.3.0) - Geolocation services
- UA-Parser (v1.6.1) - User agent parsing
- Lombok - Boilerplate reduction
- dotenv-java (v3.2.0) - Environment configuration

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.9.4 or higher
- PostgreSQL database
- Redis server
- (Optional) MaxMind GeoLite2 City database

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd link-shortener
   ```

2. **Set up environment variables**

   Create a `.env` file in the root directory:
   ```properties
   # Development
   SPRING_PROFILES_ACTIVE=dev
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/linkshortener
   SPRING_DATASOURCE_USERNAME=your_username
   SPRING_DATASOURCE_PASSWORD=your_password
   SPRING_DATA_REDIS_HOST=localhost
   SPRING_DATA_REDIS_PORT=6379
   APP_BASE_URL=http://localhost:8080
   MAX_MIND_CITY_DB=<s3-url-to-maxmind-db>
   ```

3. **Build the project**
   ```bash
   mvn clean package -DskipTests
   ```

4. **Run the application**
   ```bash
   java -jar target/link-shortener-*.jar
   ```

   The application will start on `http://localhost:8080`

### Using Docker

**Multi-stage build (recommended):**
```bash
docker build -t link-shortener .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e PGHOST=your_host \
  -e PGPORT=5432 \
  -e PGDATABASE=linkshortener \
  -e PGUSER=your_user \
  -e PGPASSWORD=your_password \
  -e REDIS_URL=redis://redis:6379 \
  link-shortener
```

**Single-stage build:**
```bash
docker build -f Dockerfile.single-stage -t link-shortener .
```

## Configuration

### Environment Variables

#### Development
- `SPRING_PROFILES_ACTIVE=dev`
- `SPRING_DATASOURCE_URL` - PostgreSQL JDBC URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `SPRING_DATA_REDIS_HOST` - Redis host (default: localhost)
- `SPRING_DATA_REDIS_PORT` - Redis port (default: 6379)
- `APP_BASE_URL` - Base URL for shortened links (default: http://localhost:8080)
- `MAX_MIND_CITY_DB` - S3 URL to MaxMind GeoLite2 database

#### Production (Railway-style)
- `SPRING_PROFILES_ACTIVE=prod`
- `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD` - PostgreSQL connection details
- `REDIS_URL` - Redis connection string
- `PORT` - Application port (default: 8080)
- `APP_BASE_URL` - Base URL for shortened links
- `MAX_MIND_CITY_DB` - S3 URL to MaxMind GeoLite2 database

### Application Profiles

The application supports three profiles:
- **dev**: Development environment with debug logging
- **prod**: Production environment with optimized settings
- **test**: Testing environment with H2 in-memory database

## API Documentation

### Public Endpoints (No Authentication Required)

#### 1. Shorten a URL
```http
POST /shorten
Content-Type: text/plain

https://www.example.com/very/long/url
```

**Response:**
```
http://localhost:8080/a1b2c3d4
```

**Features:**
- Auto-adds `https://` if protocol is missing
- Generates random 8-character codes using `0-9a-zA-Z-_@`
- Ensures unique short codes

#### 2. Redirect to Original URL
```http
GET /{short_code}
```

**Response:** HTTP 302 redirect to original URL

**Side Effects:**
- Records click event with metadata (IP, referrer, browser, OS, device, location)
- Increments click count
- Tracks returning users

### Protected Endpoints (Authentication Required)

#### 3. Get Basic Statistics
```http
GET /{shortCode}/stats?interval=daily
```

**Query Parameters:**
- `interval` (optional): `daily` (last 7 days) or `weekly` (last 4 weeks)

**Response:**
```json
{
  "shortCode": "a1b2c3d4",
  "totalClicks": 150,
  "createdAt": "2025-01-15T10:30:00",
  "clickDistribution": {
    "daily": {
      "2025-01-20": 25,
      "2025-01-19": 30,
      "2025-01-18": 20
    }
  },
  "topCountries": [
    {"country": "United States", "clicks": 80},
    {"country": "United Kingdom", "clicks": 40},
    {"country": "Canada", "clicks": 30}
  ],
  "topCities": [
    {"city": "New York", "clicks": 50},
    {"city": "London", "clicks": 40},
    {"city": "Toronto", "clicks": 30}
  ],
  "topReferrers": [
    {"referrer": "google.com", "clicks": 70},
    {"referrer": "twitter.com", "clicks": 45},
    {"referrer": "Direct", "clicks": 35}
  ],
  "topDevices": [
    {"device": "Desktop", "clicks": 90},
    {"device": "Mobile", "clicks": 50},
    {"device": "Tablet", "clicks": 10}
  ]
}
```

#### 4. Get Advanced Statistics
```http
GET /{shortCode}/stats/advanced
```

**Response:**
```json
{
  "shortCode": "a1b2c3d4",
  "returningUsersCount": 45,
  "averageTimeBetweenClicks": {
    "value": 2.5,
    "unit": "hours"
  },
  "clickDistribution": {
    "hourlyDistribution": {
      "00": 5,
      "01": 3,
      "10": 25,
      "14": 30
    },
    "dailyDistribution": {
      "MONDAY": 30,
      "TUESDAY": 25,
      "WEDNESDAY": 35
    }
  }
}
```

**Note:** Results are cached in Redis for 1 hour for optimal performance.

## Project Structure

```
link-shortener/
├── src/main/java/com/mordizze/linkshortener/
│   ├── LinkShortenerApplication.java        # Entry point
│   ├── link/                                 # Link management
│   │   ├── LinkController.java              # REST endpoints
│   │   ├── LinkRepo.java                    # JPA repository
│   │   ├── models/                          # Domain models
│   │   └── services/                        # Business logic
│   ├── stats/                               # Analytics
│   │   ├── StatsController.java             # Statistics endpoints
│   │   ├── ClickEventsRepo.java             # Analytics queries
│   │   ├── models/                          # Response DTOs
│   │   └── services/                        # Analytics services
│   ├── security/                            # Security configuration
│   ├── user/                                # User management
│   ├── configs/                             # Application configs
│   └── exceptions/                          # Error handling
├── src/main/resources/
│   ├── application.properties               # Main config
│   ├── application-dev.properties           # Dev config
│   ├── application-prod.properties          # Production config
│   └── application-test.properties          # Test config
├── Dockerfile                               # Multi-stage build
├── Dockerfile.single-stage                  # Single-stage build
├── pom.xml                                  # Maven config
└── system.properties                        # Runtime version
```

## Database Schema

### Link Entity
- Primary key: `linkId` (Long, auto-generated)
- `shortCode` (unique, 8 characters)
- `originalUrl` (unique)
- `clickCount` (Integer)
- `createdAt` (LocalDateTime)
- `returningUsers` (Set of IP addresses)

### ClickEvents Entity
- Primary key: `id` (Long, auto-generated)
- Foreign key: `link` (ManyToOne relationship)
- `ipAddress`, `referrer`, `browser`, `browserVersion`, `os`, `device`
- `country`, `city` (from GeoIP lookup)
- `clickedAt` (LocalDateTime)

## Architecture Patterns

- **Command/Query Pattern**: Standardized request-response processing
- **Repository Pattern**: Data access abstraction with Spring Data JPA
- **Service Layer**: Business logic separation
- **Cache-Aside Pattern**: Redis caching for expensive computations
- **Global Exception Handling**: Centralized error responses

## Security

- Public endpoints: `/shorten`, `/{short_code}/**`
- Protected endpoints: All statistics endpoints require authentication
- CSRF protection disabled (stateless API)
- Form login disabled
- HTTP Basic authentication disabled

## Performance Optimizations

- **Redis Caching**: 1-hour TTL for analytics computations
- **Lazy Loading**: GeoIP database downloaded only when needed
- **Database Indexing**: Unique indexes on `shortCode` and `originalUrl`
- **Connection Pooling**: HikariCP for database connections
- **Multi-stage Docker Builds**: Optimized image size

## Development

### Running Tests
```bash
mvn test
```

### Building without Tests
```bash
mvn clean package -DskipTests
```

### Accessing Actuator Endpoints
Health check and metrics are available at:
- `http://localhost:8080/actuator/health`
- `http://localhost:8080/actuator/info`

## Deployment

### Railway Deployment
The application is configured for Railway deployment with:
- PostgreSQL database support via environment variables
- Redis cache support
- Port binding to 0.0.0.0
- Production profile with optimized settings

### Docker Compose (Example)
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - PGHOST=postgres
      - PGPORT=5432
      - PGDATABASE=linkshortener
      - PGUSER=postgres
      - PGPASSWORD=password
      - REDIS_URL=redis://redis:6379
    depends_on:
      - postgres
      - redis

  postgres:
    image: postgres:15
    environment:
      - POSTGRES_DB=linkshortener
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

volumes:
  postgres_data:
```

## License

[Add your license information here]

## Contributing

[Add contribution guidelines here]

## Support

For issues and questions, please create an issue in the repository.
