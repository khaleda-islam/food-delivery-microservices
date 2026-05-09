/**
 * API Service Configuration
 * 
 * This file configures axios for making HTTP requests directly to API Gateway.
 * 
 * Architecture Flow:
 * React App → API Gateway (port 8082) → Microservices
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 5, 2026
 */

import axios from 'axios';

// Base axios instance
// For local development: connects directly to API Gateway at http://localhost:8082/api
// For Docker: uses /api/ proxy via nginx
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8082/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 second timeout
});

// Request interceptor for logging (optional)
api.interceptors.request.use(
  (config) => {
    console.log(`Making ${config.method?.toUpperCase()} request to ${config.url}`);
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    console.log(`Response from ${response.config.url}:`, response.status);
    return response;
  },
  (error) => {
    if (error.response) {
      // Server responded with error status
      console.error('Response error:', error.response.status, error.response.data);
    } else if (error.request) {
      // Request made but no response received
      console.error('No response received:', error.request);
      console.error('⚠️  Make sure API Gateway is running on port 8082');
      console.error('📡 React App → API Gateway (port 8082) → Microservices');
      console.error('🔧 Check: http://localhost:8082/api/restaurants');
    } else {
      // Something else happened
      console.error('Error:', error.message);
    }
    return Promise.reject(error);
  }
);

// API endpoints
export const restaurantAPI = {
  // Get all restaurants
  getAll: () => api.get('/restaurants'),
  
  // Get restaurant by ID
  getById: (id) => api.get(`/restaurants/${id}`),
  
  // Create new restaurant (admin)
  create: (restaurantData) => api.post('/restaurants', restaurantData),
  
  // Update restaurant (admin)
  update: (id, restaurantData) => api.put(`/restaurants/${id}`, restaurantData),
  
  // Delete restaurant (admin)
  delete: (id) => api.delete(`/restaurants/${id}`),
};

export const foodAPI = {
  // Get all foods
  getAll: () => api.get('/foods'),
  
  // Get food by ID
  getById: (id) => api.get(`/foods/${id}`),
  
  // Get foods by restaurant ID
  getByRestaurant: (restaurantId) => api.get(`/foods/restaurant/${restaurantId}`),
  
  // Create new food item (admin)
  create: (foodData) => api.post('/foods', foodData),
  
  // Update food item (admin)
  update: (id, foodData) => api.put(`/foods/${id}`, foodData),
  
  // Delete food item (admin)
  delete: (id) => api.delete(`/foods/${id}`),
};

export const orderAPI = {
  // Get all orders (admin)
  getAll: () => api.get('/orders'),
  
  // Get order by ID
  getById: (id) => api.get(`/orders/${id}`),
  
  // Get orders by user ID
  getByUser: (userId) => api.get(`/orders/user/${userId}`),
  
  // Create new order
  create: (orderData) => api.post('/orders', orderData),
  
  // Guest checkout - creates user and order
  guestCheckout: (checkoutData) => {
    console.log('Guest Checkout Data:', checkoutData);
    return api.post('/orders/guest-checkout', checkoutData);
  },
  
  // Update order status (admin)
  updateStatus: (id, status) => api.put(`/orders/${id}/status`, { status }),
};

export const userAPI = {
  // Get all users
  getAll: () => api.get('/users'),
  
  // Get user by ID
  getById: (id) => api.get(`/users/${id}`),
  
  // Get user by email
  getByEmail: (email) => api.get(`/users/email/${email}`),
  
  // Register new user
  register: (userData) => api.post('/users/register', userData),
};

export default api;
