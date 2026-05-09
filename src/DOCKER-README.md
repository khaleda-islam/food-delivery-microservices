# Docker Setup for Food Delivery System

This document explains how to run the entire Food Delivery System using Docker Compose.

## Prerequisites

- Docker Desktop installed and running
- At least 4GB of RAM available for Docker
- Ports 3000, 8082, 8083, and 8761 available on your machine

## Architecture

The system consists of four services:

1. **Eureka Server** (Port 8761) - Service Discovery
2. **API Gateway** (Port 8082) - API Gateway for routing requests
3. **Food Delivery Service** (Port 8083) - Core business logic microservice
4. **React Frontend** (Port 3000) - User interface

## Quick Start

### 1. Navigate to the project root directory

```bash
cd d:\Backup\GitHub\my-spring-projects\fds\src
```

### 2. Build and start all services

```bash
docker-compose up --build
```

This command will:
- Build Docker images for all services
- Start all containers in the correct order
- Wait for Eureka Server to be healthy before starting dependent services

### 3. Access the application

- **Frontend UI**: http://localhost:3000
- **API Gateway**: http://localhost:8082
- **Eureka Dashboard**: http://localhost:8761
- **Food Delivery Service**: http://localhost:8083

## Docker Commands

### Start services (after first build)
```bash
docker-compose up
```

### Start services in background (detached mode)
```bash
docker-compose up -d
```

### Stop all services
```bash
docker-compose down
```

### Stop and remove all volumes
```bash
docker-compose down -v
```

### Rebuild specific service
```bash
docker-compose build <service-name>
# Example: docker-compose build react-frontend
```

### View logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f react-frontend
docker-compose logs -f api-gateway
docker-compose logs -f food-delivery-service
docker-compose logs -f eureka-server
```

### Restart specific service
```bash
docker-compose restart <service-name>
# Example: docker-compose restart api-gateway
```

## Service Startup Order

The docker-compose.yml ensures proper startup sequence:

1. **Eureka Server** starts first with health check
2. **API Gateway** and **Food Delivery Service** wait for Eureka to be healthy
3. **React Frontend** starts after API Gateway is ready

## Networking

All services run on a custom bridge network called `food-delivery-network`. This allows:
- Services to communicate using container names (e.g., `eureka-server`, `api-gateway`)
- Isolation from other Docker applications
- DNS resolution between containers

## API Access from Frontend

The React frontend uses nginx reverse proxy to communicate with the API Gateway:
- Frontend makes requests to `/api/*`
- Nginx proxies these requests to `api-gateway:8082`
- This avoids CORS issues and works seamlessly in Docker

## Troubleshooting

### Services not starting
```bash
# Check service status
docker-compose ps

# Check logs for errors
docker-compose logs
```

### Port already in use
If ports are already in use, you can modify the port mappings in `docker-compose.yml`:
```yaml
ports:
  - "3001:3000"  # Change host port from 3000 to 3001
```

### Clear everything and rebuild
```bash
docker-compose down -v
docker system prune -f
docker-compose up --build
```

### Service not registering with Eureka
- Check Eureka dashboard at http://localhost:8761
- Verify service logs: `docker-compose logs <service-name>`
- Ensure health checks are passing: `docker-compose ps`

### React app can't connect to backend
- Verify API Gateway is running: `docker-compose ps`
- Check API Gateway logs: `docker-compose logs api-gateway`
- Test API directly: http://localhost:8082/api/restaurants

## Development Tips

### Local Development vs Docker

For local development (without Docker):
- Services connect to `localhost:8761` for Eureka
- React app connects to `http://localhost:8082` for API

When running in Docker:
- Services connect to `eureka-server:8761`
- React app uses nginx proxy `/api/` → `api-gateway:8082`

### Updating Code

After code changes:
```bash
# Rebuild and restart affected service
docker-compose build <service-name>
docker-compose up -d <service-name>
```

### Debugging

To run a container interactively:
```bash
docker-compose run --rm <service-name> sh
```

## Resource Usage

Each service's approximate resource usage:
- Eureka Server: ~512MB RAM
- API Gateway: ~512MB RAM
- Food Delivery Service: ~512MB RAM
- React Frontend: ~50MB RAM

Total: ~1.6GB RAM minimum

## Production Considerations

This Docker setup is suitable for development and testing. For production:

1. Remove `--build` flag and use pre-built images
2. Use environment-specific configuration files
3. Implement proper secret management (not hardcoded in docker-compose)
4. Add resource limits to containers
5. Use Docker Swarm or Kubernetes for orchestration
6. Implement proper logging and monitoring
7. Use HTTPS with proper certificates

## MongoDB Connection

The Food Delivery Service connects to MongoDB Atlas (cloud database). Ensure:
- MongoDB Atlas cluster is running
- Network access is configured to allow connections
- Credentials in `application.properties` are correct

## Support

For issues or questions, check:
- Service logs: `docker-compose logs <service-name>`
- Eureka Dashboard: http://localhost:8761
- Container status: `docker-compose ps`
