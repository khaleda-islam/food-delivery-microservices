# Food Delivery Microservice Application
## Architecture Design Document

**Version:** 1.0  
**Date:** April 12, 2026  
**Author:** Development Team

---

## Table of Contents
1. [System Overview](#1-system-overview)
2. [Food Delivery Service](#2-food-delivery-service)
3. [API Gateway Service](#3-api-gateway-service)
4. [Eureka Server](#4-eureka-server)
5. [React Frontend Application](#5-react-frontend-application)
6. [Data Flow Architecture](#6-data-flow-architecture)
7. [Complete URL Reference](#7-complete-url-reference)
8. [Running the Application](#8-running-the-application)

---

## 1. System Overview

This is a **microservice-based food delivery application** built using modern reactive programming principles. The system consists of four main components:

```
┌─────────────────────────────────────────────────────────────────┐
│                    FOOD DELIVERY ECOSYSTEM                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐                                               │
│  │   EUREKA    │  ←─── Service Discovery & Registration       │
│  │   SERVER    │       Port: 8761                              │
│  └──────┬──────┘                                               │
│         │                                                       │
│    ┌────┴────┐                                                 │
│    │         │                                                 │
│  ┌─▼──┐   ┌─▼───────────┐      ┌──────────────┐              │
│  │FDS │   │ API-GATEWAY │◄─────┤ REACT-FRONTEND│              │
│  │8083│   │    8082     │      │     3000      │              │
│  └─┬──┘   └──────┬──────┘      └──────────────┘              │
│    │             │                                             │
│    │             │                                             │
│  ┌─▼─────────────▼──┐                                         │
│  │  MongoDB Atlas   │                                         │
│  │  (Cloud Database)│                                         │
│  └──────────────────┘                                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Component Breakdown
| Component | Port | Technology | Purpose |
|-----------|------|------------|---------|
| **Eureka Server** | 8761 | Spring Cloud Netflix | Service registry and discovery |
| **Food Delivery Service** | 8083 | Spring WebFlux + MongoDB | Core business logic & data storage |
| **API Gateway** | 8082 | Spring Cloud Gateway | Request routing & load balancing |
| **React Frontend** | 3000 | React 19.2 | User interface |

---

## 2. Food Delivery Service

### 2.1 Overview
The **Food Delivery Service** is the **core microservice** that handles all business logic and data persistence. It's built using **Spring Boot WebFlux** for reactive, non-blocking operations.

### 2.2 Architecture Design

```
┌────────────────────────────────────────────────────────────┐
│         FOOD DELIVERY SERVICE (Port 8083)                  │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ┌──────────────────────────────────────────────────┐    │
│  │           TWO APPLICATION LAYERS                  │    │
│  ├──────────────────────────────────────────────────┤    │
│  │                                                   │    │
│  │  1. REST API Layer     (/api/*)                  │    │
│  │     - JSON endpoints                              │    │
│  │     - Used by API Gateway                         │    │
│  │     - Reactive (Mono/Flux)                        │    │
│  │                                                   │    │
│  │  2. Thymeleaf Admin Layer  (/admin/*)            │    │
│  │     - HTML views                                  │    │
│  │     - Direct browser access                       │    │
│  │     - Server-side rendering                       │    │
│  │                                                   │    │
│  └──────────────────────────────────────────────────┘    │
│                           ▼                                │
│  ┌──────────────────────────────────────────────────┐    │
│  │              CONTROLLERS                          │    │
│  │  - RestaurantController  (/api/restaurants)      │    │
│  │  - FoodController        (/api/foods)            │    │
│  │  - UserController        (/api/users)            │    │
│  │  - OrderController       (/api/orders)           │    │
│  │  - Admin Controllers     (/admin/*)              │    │
│  └──────────────────────────────────────────────────┘    │
│                           ▼                                │
│  ┌──────────────────────────────────────────────────┐    │
│  │              SERVICES                             │    │
│  │  - RestaurantService                              │    │
│  │  - FoodService                                    │    │
│  │  - UserService                                    │    │
│  │  - OrderService                                   │    │
│  └──────────────────────────────────────────────────┘    │
│                           ▼                                │
│  ┌──────────────────────────────────────────────────┐    │
│  │         REACTIVE REPOSITORIES                     │    │
│  │  - ReactiveMongoRepository<Restaurant>            │    │
│  │  - ReactiveMongoRepository<Food>                  │    │
│  │  - ReactiveMongoRepository<User>                  │    │
│  │  - ReactiveMongoRepository<Order>                 │    │
│  └──────────────────────────────────────────────────┘    │
│                           ▼                                │
│  ┌──────────────────────────────────────────────────┐    │
│  │           MongoDB Atlas Connection                │    │
│  │  URI: mongodb+srv://adminUser:****@fds-cluster   │    │
│  │  Database: foodDeliveryDB                         │    │
│  │  Collections: restaurants, foods, users, orders   │    │
│  └──────────────────────────────────────────────────┘    │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

### 2.3 MongoDB Atlas Connection

**Configuration (application.properties):**
```properties
# MongoDB Atlas Connection
spring.data.mongodb.uri=mongodb+srv://adminUser:Canada%402023@fds-cluster.s1pt4r2.mongodb.net/foodDeliveryDB
spring.data.mongodb.database=foodDeliveryDB
spring.data.mongodb.auto-index-creation=true

# Reactive Configuration
spring.main.web-application-type=reactive
```

**Key Features:**
- ✅ **Cloud-Hosted:** MongoDB Atlas cluster (AWS US_WEST_2)
- ✅ **Reactive Driver:** Uses Spring Data Reactive MongoDB
- ✅ **Auto-Indexing:** Automatically creates indexes for performance
- ✅ **Connection Pooling:** Managed by Spring Boot
- ✅ **Retry Logic:** Built-in retry for transient failures

**Collections:**
| Collection | Purpose | Sample Document |
|------------|---------|-----------------|
| **restaurants** | Store restaurant info | `{name, address, cuisine, rating}` |
| **foods** | Menu items for restaurants | `{name, price, restaurantId, category}` |
| **users** | Customer accounts | `{email, name, address, phone}` |
| **orders** | Order transactions | `{userId, items[], totalPrice, status}` |

### 2.4 Standalone Operation with Thymeleaf

The Food Delivery Service can **operate independently** as a complete web application using its **Thymeleaf admin panel**.

**Why This Works:**
- ✅ Has its own controller layer (`/admin/*`)
- ✅ Serves HTML views directly (no frontend needed)
- ✅ Built-in CSS styling (admin-style.css)
- ✅ Full CRUD operations available
- ✅ Does **NOT** require API Gateway or React to function

**When to Use Standalone Mode:**
- 👨‍💼 Admin users managing data directly
- 🧪 Testing backend functionality
- 📊 Internal operations/monitoring
- 🔧 Development and debugging

**Access Methods:**

1. **Thymeleaf Admin Panel (Standalone):**
   ```
   http://localhost:8083/admin
   ```
   - Full browser-based UI
   - Create/Read/Update/Delete operations
   - No API Gateway needed

2. **REST API (Microservice Mode):**
   ```
   http://localhost:8083/api/restaurants
   ```
   - JSON responses
   - Used by API Gateway
   - Programmatic access

### 2.5 Eureka Registration

**Configuration:**
```properties
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
spring.application.name=food-delivery-service
```

**Registration Details:**
- **Service Name:** `FOOD-DELIVERY-SERVICE`
- **Instance ID:** `10.0.0.47:food-delivery-service:8083`
- **Health Check:** Automatic via Spring Boot Actuator
- **Heartbeat:** Every 30 seconds

---

## 3. API Gateway Service

### 3.1 Overview
The **API Gateway** is the **single entry point** for all client applications (React frontend). It routes requests to appropriate microservices using service discovery.

### 3.2 Architecture Design

```
┌─────────────────────────────────────────────────────────┐
│          API GATEWAY SERVICE (Port 8082)                │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌────────────────────────────────────────────┐        │
│  │   INCOMING REQUESTS FROM REACT FRONTEND    │        │
│  │   http://localhost:8082/api/*              │        │
│  └───────────────────┬────────────────────────┘        │
│                      │                                  │
│                      ▼                                  │
│  ┌─────────────────────────────────────────────────┐  │
│  │         GATEWAY CONTROLLERS                     │  │
│  │                                                  │  │
│  │  RestaurantController  (/api/restaurants)       │  │
│  │  FoodController        (/api/foods)             │  │
│  │  UserController        (/api/users)             │  │
│  │  OrderController       (/api/orders)            │  │
│  │                                                  │  │
│  │  @CrossOrigin(origins = "http://localhost:3000")│  │
│  └────────────────────┬────────────────────────────┘  │
│                       │                                 │
│                       ▼                                 │
│  ┌─────────────────────────────────────────────────┐  │
│  │         CLIENT SERVICE LAYER                    │  │
│  │  (Makes HTTP calls to FDS)                      │  │
│  │                                                  │  │
│  │  RestaurantClientService                        │  │
│  │  FoodClientService                              │  │
│  │  UserClientService                              │  │
│  │  OrderClientService                             │  │
│  └────────────────────┬────────────────────────────┘  │
│                       │                                 │
│                       ▼                                 │
│  ┌─────────────────────────────────────────────────┐  │
│  │     WEBCLIENT (@LoadBalanced)                   │  │
│  │                                                  │  │
│  │  Uses Eureka to resolve service names:          │  │
│  │  http://food-delivery-service/api/restaurants   │  │
│  │                             ▲                    │  │
│  │                             │                    │  │
│  │                    Service Discovery             │  │
│  └────────────────────┬────────────────────────────┘  │
│                       │                                 │
│                       ▼                                 │
│  ┌─────────────────────────────────────────────────┐  │
│  │          EUREKA CLIENT                          │  │
│  │  Queries Eureka for service locations           │  │
│  └─────────────────────────────────────────────────┘  │
│                       │                                 │
└───────────────────────┼─────────────────────────────────┘
                        │
                        ▼
         ┌──────────────────────────┐
         │  FOOD-DELIVERY-SERVICE   │
         │  (Port 8083)             │
         └──────────────────────────┘
```

### 3.3 How API Gateway Connects with Food Delivery Service

**Step-by-Step Flow:**

1. **React makes request:**
   ```javascript
   GET http://localhost:8082/api/restaurants
   ```

2. **API Gateway receives request** at `RestaurantController`

3. **Controller delegates to Client Service:**
   ```java
   @GetMapping
   public Flux<Restaurant> getAllRestaurants() {
       return restaurantClientService.getAllRestaurants();
   }
   ```

4. **Client Service uses Load-Balanced WebClient:**
   ```java
   return webClient
       .get()
       .uri("http://food-delivery-service/api/restaurants")
       .retrieve()
       .bodyToFlux(Restaurant.class);
   ```

5. **Eureka resolves service name** `food-delivery-service` → `http://10.0.0.47:8083`

6. **WebClient makes HTTP call:**
   ```
   GET http://10.0.0.47:8083/api/restaurants
   ```

7. **Food Delivery Service responds** with JSON data

8. **API Gateway forwards response** back to React

**Key Configuration:**

```java
@Bean
@LoadBalanced  // ← This enables Eureka service discovery
public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
}
```

### 3.4 API Gateway Endpoints

All endpoints follow the pattern: `http://localhost:8082/api/{resource}`

| HTTP Method | Endpoint | Proxies To | Purpose |
|-------------|----------|------------|---------|
| **Restaurants** |
| GET | `/api/restaurants` | `food-delivery-service/api/restaurants` | Get all restaurants |
| GET | `/api/restaurants/{id}` | `food-delivery-service/api/restaurants/{id}` | Get restaurant by ID |
| POST | `/api/restaurants` | `food-delivery-service/api/restaurants` | Create new restaurant |
| PUT | `/api/restaurants/{id}` | `food-delivery-service/api/restaurants/{id}` | Update restaurant |
| DELETE | `/api/restaurants/{id}` | `food-delivery-service/api/restaurants/{id}` | Delete restaurant |
| **Foods** |
| GET | `/api/foods` | `food-delivery-service/api/foods` | Get all food items |
| GET | `/api/foods/{id}` | `food-delivery-service/api/foods/{id}` | Get food item |
| GET | `/api/foods/restaurant/{id}` | `food-delivery-service/api/foods/restaurant/{id}` | Get menu by restaurant |
| POST | `/api/foods` | `food-delivery-service/api/foods` | Create food item |
| PUT | `/api/foods/{id}` | `food-delivery-service/api/foods/{id}` | Update food item |
| DELETE | `/api/foods/{id}` | `food-delivery-service/api/foods/{id}` | Delete food item |
| **Users** |
| GET | `/api/users` | `food-delivery-service/api/users` | Get all users |
| GET | `/api/users/{id}` | `food-delivery-service/api/users/{id}` | Get user profile |
| POST | `/api/users/register` | `food-delivery-service/api/users/register` | Register new user |
| PUT | `/api/users/{id}` | `food-delivery-service/api/users/{id}` | Update user profile |
| DELETE | `/api/users/{id}` | `food-delivery-service/api/users/{id}` | Delete user |
| **Orders** |
| GET | `/api/orders` | `food-delivery-service/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | `food-delivery-service/api/orders/{id}` | Get order details |
| GET | `/api/orders/user/{userId}` | `food-delivery-service/api/orders/user/{userId}` | Get user's orders |
| POST | `/api/orders` | `food-delivery-service/api/orders` | Place new order |
| PUT | `/api/orders/{id}/status` | `food-delivery-service/api/orders/{id}/status` | Update order status |
| DELETE | `/api/orders/{id}` | `food-delivery-service/api/orders/{id}` | Cancel order |

### 3.5 CORS Configuration

**Enabled for React development:**
```java
@CrossOrigin(origins = "http://localhost:3000")
```

**What this allows:**
- ✅ React frontend (port 3000) can call API Gateway (port 8082)
- ✅ Browser allows cross-origin requests
- ❌ **PRODUCTION:** Should restrict to actual frontend domain

---

## 4. Eureka Server

### 4.1 Overview
**Eureka Server** is Netflix's service discovery solution. It acts as a **phone book** for microservices.

### 4.2 How Eureka Works

```
┌──────────────────────────────────────────────────────┐
│              EUREKA SERVER (Port 8761)               │
├──────────────────────────────────────────────────────┤
│                                                      │
│  ┌────────────────────────────────────────────┐    │
│  │         SERVICE REGISTRY                    │    │
│  │                                             │    │
│  │  ┌────────────────────────────────────┐    │    │
│  │  │ Service: FOOD-DELIVERY-SERVICE     │    │    │
│  │  │ Instance: 10.0.0.47:8083          │    │    │
│  │  │ Status: UP                         │    │    │
│  │  │ Last Heartbeat: 2s ago             │    │    │
│  │  └────────────────────────────────────┘    │    │
│  │                                             │    │
│  │  ┌────────────────────────────────────┐    │    │
│  │  │ Service: API-GATEWAY-SERVICE       │    │    │
│  │  │ Instance: 10.0.0.47:8082          │    │    │
│  │  │ Status: UP                         │    │    │
│  │  │ Last Heartbeat: 3s ago             │    │    │
│  │  └────────────────────────────────────┘    │    │
│  │                                             │    │
│  └────────────────────────────────────────────┘    │
│                                                      │
└──────────────────────────────────────────────────────┘
         ▲                           ▲
         │                           │
         │ Register/Heartbeat        │ Query
         │                           │
         │                           │
    ┌────┴─────┐              ┌─────┴────┐
    │   FDS    │              │ API-GW   │
    │  (8083)  │              │ (8082)   │
    └──────────┘              └──────────┘
```

### 4.3 Service Registration Process

**When Food Delivery Service starts:**

1. **Service boots up** and reads Eureka config:
   ```properties
   eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
   spring.application.name=food-delivery-service
   ```

2. **Sends registration request** to Eureka:
   ```json
   {
     "instanceId": "10.0.0.47:food-delivery-service:8083",
     "app": "FOOD-DELIVERY-SERVICE",
     "ipAddr": "10.0.0.47",
     "port": 8083,
     "status": "UP"
   }
   ```

3. **Eureka stores registration** in its registry

4. **Service sends heartbeats** every 30 seconds:
   ```
   POST http://localhost:8761/eureka/apps/FOOD-DELIVERY-SERVICE
   ```

5. **If heartbeats stop**, Eureka marks service as `DOWN` after 90 seconds

### 4.4 Service Discovery Process

**When API Gateway needs to call Food Delivery Service:**

1. **API Gateway queries Eureka:**
   ```
   GET http://localhost:8761/eureka/apps/FOOD-DELIVERY-SERVICE
   ```

2. **Eureka responds** with instance info:
   ```json
   {
     "instances": [{
       "hostName": "10.0.0.47",
       "port": 8083,
       "status": "UP"
     }]
   }
   ```

3. **WebClient resolves** `food-delivery-service` → `http://10.0.0.47:8083`

4. **Makes actual HTTP call** to resolved URL

### 4.5 Benefits of Eureka

| Feature | Description | Benefit |
|---------|-------------|---------|
| **Dynamic Discovery** | Services find each other automatically | No hardcoded URLs |
| **Health Monitoring** | Heartbeat-based health checks | Auto-detect failures |
| **Load Balancing** | Client-side load balancing | Distribute traffic |
| **Scalability** | Multiple instances register with same name | Easy horizontal scaling |
| **Resilience** | Services can come and go | Self-healing architecture |

### 4.6 Eureka Dashboard

**Access:** `http://localhost:8761`

**What you can see:**
- ✅ All registered services
- ✅ Instance health status
- ✅ Number of instances per service
- ✅ Last heartbeat time
- ✅ Service metadata

**Configuration:**
```properties
# Eureka Server doesn't register itself
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

---

## 5. React Frontend Application

### 5.1 Overview
The **React Frontend** is a modern single-page application (SPA) that provides the user interface for customers to browse restaurants, view menus, and place orders.

### 5.2 Architecture Design

```
┌──────────────────────────────────────────────────────┐
│        REACT FRONTEND (Port 3000)                    │
├──────────────────────────────────────────────────────┤
│                                                      │
│  ┌────────────────────────────────────────────┐    │
│  │            REACT COMPONENTS                 │    │
│  │                                             │    │
│  │  App.js                                     │    │
│  │   ├─ RestaurantList.js                      │    │
│  │   │   └─ RestaurantCard.js                  │    │
│  │   │                                          │    │
│  │   ├─ MenuDisplay.js                         │    │
│  │   │   └─ FoodItem.js                        │    │
│  │   │                                          │    │
│  │   ├─ Cart.js                                │    │
│  │   │                                          │    │
│  │   ├─ OrderForm.js                           │    │
│  │   │                                          │    │
│  │   └─ OrderHistory.js                        │    │
│  │                                             │    │
│  └──────────────────┬──────────────────────────┘    │
│                     │                                │
│                     ▼                                │
│  ┌────────────────────────────────────────────┐    │
│  │         CONTEXT / STATE                     │    │
│  │  CartContext.js                             │    │
│  │  - Manages cart items                       │    │
│  │  - Calculate totals                         │    │
│  │  - Persist to localStorage                  │    │
│  └──────────────────┬──────────────────────────┘    │
│                     │                                │
│                     ▼                                │
│  ┌────────────────────────────────────────────┐    │
│  │         SERVICE LAYER                       │    │
│  │  services/api.js                            │    │
│  │                                             │    │
│  │  Axios Instance:                            │    │
│  │  baseURL: http://localhost:8082             │    │
│  │                                             │    │
│  │  Functions:                                 │    │
│  │  - fetchRestaurants()                       │    │
│  │  - fetchMenu(restaurantId)                  │    │
│  │  - registerUser(userData)                   │    │
│  │  - placeOrder(orderData)                    │    │
│  │  - fetchOrderHistory(userId)                │    │
│  └──────────────────┬──────────────────────────┘    │
│                     │                                │
└─────────────────────┼────────────────────────────────┘
                      │
                      │ HTTP Requests
                      │
                      ▼
         ┌────────────────────────┐
         │   API GATEWAY (8082)   │
         └────────────────────────┘
```

### 5.3 Component Breakdown

| Component | Purpose | API Calls |
|-----------|---------|-----------|
| **RestaurantList.js** | Display all restaurants | `GET /api/restaurants` |
| **RestaurantCard.js** | Individual restaurant card | - |
| **MenuDisplay.js** | Show restaurant menu | `GET /api/foods/restaurant/{id}` |
| **FoodItem.js** | Display individual menu item | - |
| **Cart.js** | Shopping cart management | - (local state) |
| **OrderForm.js** | Checkout and place order | `POST /api/orders` |
| **OrderHistory.js** | View past orders | `GET /api/orders/user/{userId}` |

### 5.4 How React Connects with API Gateway

**1. Axios Configuration (services/api.js):**

```javascript
import axios from 'axios';

// Base axios instance pointing to API Gateway
const api = axios.create({
  baseURL: 'http://localhost:8082',  // ← API Gateway URL
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export default api;
```

**2. Example API Call:**

```javascript
// In a React component
import api from '../services/api';

const fetchRestaurants = async () => {
  try {
    const response = await api.get('/api/restaurants');
    setRestaurants(response.data);
  } catch (error) {
    console.error('Error fetching restaurants:', error);
  }
};
```

**3. Complete Request Flow:**

```
USER ACTION (Browser)
       ↓
React Component (RestaurantList.js)
       ↓
Service Layer (api.js)
       ↓
Axios HTTP Request
       ↓
http://localhost:8082/api/restaurants  ← API Gateway
       ↓
API Gateway Controller
       ↓
RestaurantClientService
       ↓
WebClient (@LoadBalanced)
       ↓
Eureka Service Discovery
       ↓
http://10.0.0.47:8083/api/restaurants  ← Food Delivery Service
       ↓
RestaurantController
       ↓
RestaurantService
       ↓
ReactiveMongoRepository
       ↓
MongoDB Atlas
       ↓
Response (JSON)
       ↓
API Gateway (forwards response)
       ↓
React Component (updates state)
       ↓
UI Re-renders
```

### 5.5 Key React Features

**Technologies Used:**
- **React 19.2.4** - Latest React version
- **React Router DOM 7.14** - Client-side routing
- **Axios 1.14** - HTTP client
- **Context API** - State management for cart

**Functionality:**

1. **Browse Restaurants**
   - View all available restaurants
   - See ratings, cuisine type, address
   - Click to view menu

2. **View Menu**
   - Browse food items by restaurant
   - See prices, descriptions, categories
   - Add items to cart

3. **Shopping Cart**
   - Add/remove items
   - Update quantities
   - View total price
   - Persist cart in localStorage

4. **Place Orders**
   - Checkout with cart items
   - Enter delivery address
   - Submit order
   - Receive order confirmation

5. **Order History**
   - View past orders
   - See order status (Pending, Preparing, Delivered)
   - Track order details

**Development Server:**
```bash
npm start
# Runs on http://localhost:3000
```

---

## 6. Data Flow Architecture

### 6.1 End-to-End Request Flow

Let's trace a complete request from user action to database and back:

**Scenario: User views menu for a restaurant**

```
┌─────────────────────────────────────────────────────────────────┐
│                    COMPLETE REQUEST FLOW                        │
└─────────────────────────────────────────────────────────────────┘

1. USER ACTION
   User clicks "View Menu" button for "Pizza Palace"
   
   ↓

2. REACT FRONTEND (Port 3000)
   Component: MenuDisplay.js
   Action: useEffect(() => fetchMenu(restaurantId))
   
   ↓

3. AXIOS REQUEST
   GET http://localhost:8082/api/foods/restaurant/64abc123...
   
   ↓

4. API GATEWAY (Port 8082)
   Endpoint: FoodController.getMenuByRestaurant(restaurantId)
   Code: return foodClientService.getMenuByRestaurant(restaurantId);
   
   ↓

5. API GATEWAY CLIENT SERVICE
   Class: FoodClientService
   Code: webClient.get()
           .uri("http://food-delivery-service/api/foods/restaurant/{id}")
           .retrieve()
           .bodyToFlux(Food.class)
   
   ↓

6. EUREKA SERVICE DISCOVERY
   Query: Resolve "food-delivery-service"
   Response: http://10.0.0.47:8083
   
   ↓

7. HTTP CALL TO FOOD DELIVERY SERVICE
   GET http://10.0.0.47:8083/api/foods/restaurant/64abc123...
   
   ↓

8. FOOD DELIVERY SERVICE (Port 8083)
   Endpoint: FoodController.getMenuByRestaurant(restaurantId)
   Code: return foodService.findMenuByRestaurantId(restaurantId);
   
   ↓

9. SERVICE LAYER
   Class: FoodService
   Method: findMenuByRestaurantId(restaurantId)
   
   ↓

10. REPOSITORY LAYER
    Interface: ReactiveMongoRepository<Food, String>
    Query: db.foods.find({ "restaurantId": "64abc123..." })
    
    ↓

11. MONGODB ATLAS
    Database: foodDeliveryDB
    Collection: foods
    Operation: Query execution
    Returns: Flux<Food>
    
    ↓

12. RESPONSE BUBBLES BACK UP
    MongoDB → Repository → Service → Controller → JSON Response
    
    ↓

13. API GATEWAY RECEIVES RESPONSE
    Flux<Food> from Food Delivery Service
    Forwards to React
    
    ↓

14. REACT RECEIVES DATA
    Axios promise resolves
    setMenuItems(response.data)
    Component re-renders
    
    ↓

15. UI UPDATES
    User sees menu items displayed on screen
```

### 6.2 Order Placement Flow

```
USER CLICKS "PLACE ORDER"
       ↓
React: OrderForm.js
       ↓
POST http://localhost:8082/api/orders
Body: {
  userId: "user123",
  restaurantId: "rest456",
  items: [
    { foodId: "pizza789", quantity: 2 }
  ],
  deliveryAddress: "123 Main St"
}
       ↓
API Gateway: OrderController.placeOrder()
       ↓
Eureka resolves: food-delivery-service
       ↓
POST http://10.0.0.47:8083/api/orders
       ↓
Food Delivery Service: OrderController.placeOrder()
       ↓
OrderService.placeOrder()
  - Calculate total price
  - Set order status: "PENDING"
  - Set timestamps
       ↓
MongoDB Atlas: Insert into orders collection
       ↓
Response: { orderId: "order999", status: "PENDING", total: 29.99 }
       ↓
React: Display order confirmation
```

---

## 7. Complete URL Reference

### 7.1 Food Delivery Service URLs (Direct Access)

**Base URL:** `http://localhost:8083`

#### A. Thymeleaf Admin Panel (HTML Views)

| URL | Description |
|-----|-------------|
| `http://localhost:8083/admin` | Admin dashboard home |
| **Restaurants** |
| `http://localhost:8083/admin/restaurants` | List all restaurants |
| `http://localhost:8083/admin/restaurants/new` | Create new restaurant form |
| `http://localhost:8083/admin/restaurants/{id}` | View restaurant details |
| `http://localhost:8083/admin/restaurants/{id}/edit` | Edit restaurant form |
| **Foods** |
| `http://localhost:8083/admin/foods` | List all food items |
| `http://localhost:8083/admin/foods?restaurantId={id}` | Filter foods by restaurant |
| `http://localhost:8083/admin/foods/new` | Create new food item form |
| `http://localhost:8083/admin/foods/{id}` | View food item details |
| `http://localhost:8083/admin/foods/{id}/edit` | Edit food item form |
| **Customers** |
| `http://localhost:8083/admin/customers` | List all customers |
| `http://localhost:8083/admin/customers/new` | Register new customer form |
| `http://localhost:8083/admin/customers/{id}` | View customer profile |
| `http://localhost:8083/admin/customers/{id}/edit` | Edit customer form |
| **Orders** |
| `http://localhost:8083/admin/orders` | List all orders |
| `http://localhost:8083/admin/orders?userId={id}` | Filter orders by user |
| `http://localhost:8083/admin/orders?status={status}` | Filter orders by status |
| `http://localhost:8083/admin/orders/new` | Create new order form |
| `http://localhost:8083/admin/orders/{id}` | View order details |
| `http://localhost:8083/admin/orders/{id}/edit` | Edit order/update status |

#### B. REST API Endpoints (JSON Responses)

**Restaurants:**
```
GET    http://localhost:8083/api/restaurants           # Get all
GET    http://localhost:8083/api/restaurants/{id}      # Get by ID
POST   http://localhost:8083/api/restaurants           # Create
PUT    http://localhost:8083/api/restaurants/{id}      # Update
DELETE http://localhost:8083/api/restaurants/{id}      # Delete
```

**Foods:**
```
GET    http://localhost:8083/api/foods                         # Get all
GET    http://localhost:8083/api/foods/{id}                    # Get by ID
GET    http://localhost:8083/api/foods/restaurant/{id}         # Get menu
POST   http://localhost:8083/api/foods                         # Create
PUT    http://localhost:8083/api/foods/{id}                    # Update
DELETE http://localhost:8083/api/foods/{id}                    # Delete
```

**Users:**
```
GET    http://localhost:8083/api/users                 # Get all
GET    http://localhost:8083/api/users/{id}            # Get by ID
POST   http://localhost:8083/api/users/register        # Register
PUT    http://localhost:8083/api/users/{id}            # Update
DELETE http://localhost:8083/api/users/{id}            # Delete
```

**Orders:**
```
GET    http://localhost:8083/api/orders                      # Get all
GET    http://localhost:8083/api/orders/{id}                 # Get by ID
GET    http://localhost:8083/api/orders/user/{userId}        # User orders
POST   http://localhost:8083/api/orders                      # Place order
PUT    http://localhost:8083/api/orders/{id}/status?status={status} # Update status
DELETE http://localhost:8083/api/orders/{id}                 # Delete
```

### 7.2 API Gateway URLs (Public Endpoints)

**Base URL:** `http://localhost:8082`

All REST API endpoints are identical to Food Delivery Service, but accessed through:
```
http://localhost:8082/api/{resource}
```

**Example:**
```
# Instead of:
GET http://localhost:8083/api/restaurants

# React Frontend uses:
GET http://localhost:8082/api/restaurants
```

### 7.3 Eureka Server URLs

| URL | Description |
|-----|-------------|
| `http://localhost:8761` | Eureka dashboard |
| `http://localhost:8761/eureka/apps` | All registered services (XML) |
| `http://localhost:8761/eureka/apps/FOOD-DELIVERY-SERVICE` | Specific service info |

### 7.4 React Frontend URLs

| URL | Description |
|-----|-------------|
| `http://localhost:3000` | Home page / Restaurant list |
| `http://localhost:3000/menu/:restaurantId` | Restaurant menu |
| `http://localhost:3000/cart` | Shopping cart |
| `http://localhost:3000/checkout` | Order form |
| `http://localhost:3000/orders` | Order history |

---

## 8. Running the Application

### 8.1 Prerequisites

- **Java 21** installed
- **Node.js 18+** and npm installed
- **MongoDB Atlas** account (or connection string)
- **Maven** (included via Maven Wrapper)

### 8.2 Start Sequence

**IMPORTANT:** Start services in this order!

#### Step 1: Start Eureka Server

```powershell
cd d:\Backup\GitHub\sm-love\online-food-delivery-system\fds\src\eureka-server
.\mvnw.cmd spring-boot:run
```

✅ **Verify:** Visit `http://localhost:8761` - should see Eureka dashboard

#### Step 2: Start Food Delivery Service

```powershell
cd d:\Backup\GitHub\sm-love\online-food-delivery-system\fds\src\food-delivery-service
.\mvnw.cmd spring-boot:run
```

✅ **Verify:** 
- Check console for: `Food Delivery Service Started Successfully!`
- Visit Eureka dashboard - should see `FOOD-DELIVERY-SERVICE` registered

#### Step 3: Start API Gateway

```powershell
cd d:\Backup\GitHub\sm-love\online-food-delivery-system\fds\src\api-gateway-service
.\mvnw.cmd spring-boot:run
```

✅ **Verify:**
- Check Eureka dashboard - should see `API-GATEWAY-SERVICE` registered

#### Step 4: Start React Frontend

```powershell
cd d:\Backup\GitHub\sm-love\online-food-delivery-system\fds\src\react-frontend
npm install  # First time only
npm start
```

✅ **Verify:** Browser opens at `http://localhost:3000`

### 8.3 Testing the Application

#### Test 1: Thymeleaf Admin Panel (Standalone)

1. Visit: `http://localhost:8083/admin`
2. Click "Manage Restaurants"
3. Click "Add New Restaurant"
4. Fill form and submit
5. ✅ Should see restaurant in list

#### Test 2: REST API (Direct)

```powershell
# Get all restaurants
Invoke-WebRequest -Uri "http://localhost:8083/api/restaurants" -Method GET
```

#### Test 3: API Gateway Proxy

```powershell
# Get all restaurants through API Gateway
Invoke-WebRequest -Uri "http://localhost:8082/api/restaurants" -Method GET
```

#### Test 4: React Frontend (Full Stack)

1. Open `http://localhost:3000`
2. View restaurant list
3. Click a restaurant to see menu
4. Add items to cart
5. Proceed to checkout
6. ✅ Order should be created successfully

### 8.4 Troubleshooting

**Problem:** Eureka shows service as DOWN

**Solution:** 
- Check if service is actually running
- Verify `eureka.client.serviceUrl.defaultZone` in application.properties
- Wait 30 seconds for heartbeat

---

**Problem:** API Gateway can't find Food Delivery Service

**Solution:**
- Ensure Eureka Server is running first
- Check Food Delivery Service is registered in Eureka
- Verify service name matches: `food-delivery-service`

---

**Problem:** React can't connect to API Gateway

**Solution:**
- Verify API Gateway is running on port 8082
- Check CORS configuration in API Gateway controllers
- Check browser console for CORS errors

---

**Problem:** MongoDB connection fails

**Solution:**
- Verify MongoDB Atlas connection string
- Check username/password encoding (use `%40` for `@`)
- Ensure IP whitelist includes your IP
- Test connection using MongoDB Compass

---

## 9. Summary

This food delivery microservice application demonstrates:

✅ **Microservice Architecture** - Separation of concerns with dedicated services  
✅ **Service Discovery** - Dynamic service registration and lookup with Eureka  
✅ **API Gateway Pattern** - Single entry point for frontend clients  
✅ **Reactive Programming** - Non-blocking operations with Spring WebFlux  
✅ **Cloud Database** - MongoDB Atlas for scalable data storage  
✅ **Multiple UI Options** - Thymeleaf for admin, React for customers  
✅ **RESTful APIs** - Standard HTTP methods and JSON responses  
✅ **Modern Frontend** - React with hooks and context API  

**Key Takeaway:**  
The Food Delivery Service can operate **both as a standalone application** (using Thymeleaf admin panel) **and as a microservice** (serving JSON APIs to the API Gateway). This dual-mode operation provides flexibility for different use cases while maintaining a clean architecture.

---

**Document Version:** 1.0  
**Last Updated:** April 12, 2026  
**For Questions:** Contact Development Team
