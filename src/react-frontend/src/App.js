/**
 * Online Food Delivery System - Main App Component
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Course: Online Food Delivery System
 * Date: April 5, 2026
 * 
 * Main application component for the Food Delivery System.
 * Task 5.2: Restaurant Listing Component - Integrated
 * Task 5.3: Menu Display Component - Integrated
 * Enhancement: Clickable restaurant cards with menu navigation
 */

import React, { useState } from 'react';
import './App.css';
import { RestaurantList, MenuDisplay, Cart, OrderForm } from './components';
import { CartProvider } from './context/CartContext';

function App() {
  // State for selected restaurant (null = show restaurant list, object = show restaurant menu)
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);
  
  // State for showing order form
  const [showOrderForm, setShowOrderForm] = useState(false);
  
  /**
   * Handle restaurant card click
   * @param {Object} restaurant - Restaurant object
   */
  const handleRestaurantClick = (restaurant) => {
    console.log('Restaurant selected:', restaurant.name);
    setSelectedRestaurant(restaurant);
  };
  
  /**
   * Handle back to restaurants list
   */
  const handleBackToRestaurants = () => {
    console.log('Returning to restaurant list');
    setSelectedRestaurant(null);
  };

  /**
   * Handle checkout button click from cart
   */
  const handleCheckout = () => {
    setShowOrderForm(true);
  };

  /**
   * Handle order form completion
   */
  const handleOrderComplete = (orderData) => {
    console.log('Order completed:', orderData);
    setShowOrderForm(false);
    // Optionally navigate to order history or show success page
  };

  /**
   * Handle order form cancellation
   */
  const handleOrderCancel = () => {
    setShowOrderForm(false);
  };

  return (
    <CartProvider>
      <div className="App">
      {/* Application Header */}
      <header className="App-header">
        <div className="header-content">
          <h1>🍕 Online Food Delivery System</h1>
        </div>
      </header>

      {/* Breadcrumb Navigation */}
      {selectedRestaurant && (
        <nav className="App-breadcrumb">
          <button onClick={handleBackToRestaurants} className="breadcrumb-back">
            ← Back to Restaurants
          </button>
          <span className="breadcrumb-divider">/</span>
          <span className="breadcrumb-current">{selectedRestaurant.name}</span>
        </nav>
      )}

      {/* Main Content */}
      <main className="App-main">
        {/* Conditional rendering based on selected restaurant */}
        {!selectedRestaurant ? (
          /* Show Restaurant List */
          <RestaurantList onRestaurantClick={handleRestaurantClick} />
        ) : (
          /* Show Restaurant Menu */
          <MenuDisplay 
            restaurantId={selectedRestaurant.id}
            restaurantName={selectedRestaurant.name}
            onBackClick={handleBackToRestaurants}
          />
        )}
      </main>

      {/* Application Footer */}
      <footer className="App-footer">
       
        <p>
          Architecture design by Khaleda Islam : React (Port 3000)  → API Gateway (Port 8082) → Food Service (Port 8083) → MongoDB Atlas
        </p>
        <p className="footer-links">
          <a href="http://localhost:8082/api/restaurants" target="_blank" rel="noopener noreferrer">
            API Gateway
          </a>
          {' | '}
          <a href="http://localhost:8761" target="_blank" rel="noopener noreferrer">
            Eureka Dashboard
          </a>
        </p>
      </footer>

      {/* Shopping Cart - Fixed Position */}
      <Cart onCheckout={handleCheckout} />

      {/* Order Form Modal */}
      {showOrderForm && (
        <OrderForm 
          onOrderComplete={handleOrderComplete}
          onCancel={handleOrderCancel}
        />
      )}
    </div>
    </CartProvider>
  );
}

export default App;
