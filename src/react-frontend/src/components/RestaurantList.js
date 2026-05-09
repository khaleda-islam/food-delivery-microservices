/**
 * RestaurantList Component
 * 
 * Purpose: Displays a list of available restaurants fetched from the API
 * Architecture: React → API Gateway → Food Delivery Service → MongoDB
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 5, 2026
 */

import React, { useState, useEffect } from 'react';
import { restaurantAPI } from '../services/api';
import RestaurantCard from './RestaurantCard';
import './RestaurantList.css';

/**
 * RestaurantList Component
 * 
 * Fetches and displays all restaurants from the backend.
 * Includes loading states, error handling, and filtering options.
 * 
 * @param {Object} props - Component props
 * @param {Function} props.onRestaurantClick - Callback when restaurant is clicked
 */
function RestaurantList({ onRestaurantClick }) {
    // State management
    const [restaurants, setRestaurants] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [filter, setFilter] = useState({ city: '', cuisineType: '', minRating: 0 });

    /**
     * Fetch restaurants on component mount
     * Makes API call directly to API Gateway
     */
    useEffect(() => {
        fetchRestaurants();
    }, []);

    /**
     * Fetch all restaurants from API
     */
    const fetchRestaurants = async () => {
        try {
            setLoading(true);
            setError(null);
            
            console.log('📡 Fetching restaurants from /api/restaurants');
            console.log('🔄 Request flow: React → API Gateway (8082) → Food Service (8083)');
            
            const response = await restaurantAPI.getAll();
            
            console.log('✅ Successfully fetched restaurants:', response.data.length);
            setRestaurants(response.data);
            setLoading(false);
        } catch (err) {
            console.error('❌ Error fetching restaurants:', err);
            setError(err.response?.data?.message || err.message || 'Failed to fetch restaurants');
            setLoading(false);
        }
    };

    /**
     * Filter restaurants based on selected criteria
     */
    const filteredRestaurants = restaurants.filter(restaurant => {
        const matchesCity = !filter.city || 
            restaurant.city.toLowerCase().includes(filter.city.toLowerCase());
        
        const matchesCuisine = !filter.cuisineType || 
            restaurant.cuisineType.toLowerCase().includes(filter.cuisineType.toLowerCase());
        
        const matchesRating = restaurant.rating >= filter.minRating;
        
        return matchesCity && matchesCuisine && matchesRating && restaurant.isActive;
    });

    /**
     * Clear all filters
     */
    const clearFilters = () => {
        setFilter({ city: '', cuisineType: '', minRating: 0 });
    };

    /**
     * Render loading state
     */
    if (loading) {
        return (
            <div className="restaurant-list-loading">
                <div className="loading-spinner"></div>
                <p>Loading restaurants...</p>
                <small>Connecting to API Gateway on port 8082</small>
            </div>
        );
    }

    /**
     * Render error state
     */
    if (error) {
        return (
            <div className="restaurant-list-error">
                <h2>⚠️ Error Loading Restaurants</h2>
                <p>{error}</p>
                <div className="error-help">
                    <p><strong>Troubleshooting:</strong></p>
                    <ul>
                        <li>✓ Ensure API Gateway is running on port 8082</li>
                        <li>✓ Ensure Food Delivery Service is running on port 8083</li>
                        <li>✓ Check Eureka Dashboard: <a href="http://localhost:8761" target="_blank" rel="noopener noreferrer">http://localhost:8761</a></li>
                        <li>✓ Verify CORS is enabled in API Gateway</li>
                    </ul>
                </div>
                <button onClick={fetchRestaurants} className="retry-button">
                    🔄 Retry
                </button>
            </div>
        );
    }

    /**
     * Render empty state
     */
    if (restaurants.length === 0) {
        return (
            <div className="restaurant-list-empty">
                <h2>No Restaurants Found</h2>
                <p>There are no restaurants available at the moment.</p>
                <button onClick={fetchRestaurants} className="retry-button">
                    🔄 Refresh
                </button>
            </div>
        );
    }

    /**
     * Render restaurant list with filters
     */
    return (
        <div className="restaurant-list-container">
            {/* Header */}
            <div className="restaurant-list-header">
                <h1>🍽️ Available Restaurants</h1>
                <p className="subtitle">
                    Discover delicious food from {restaurants.length} restaurants
                </p>
            </div>

            {/* Filters */}
            <div className="restaurant-filters">
                <div className="filter-group">
                    <label htmlFor="city-filter">
                        <span className="filter-icon">📍</span> City
                    </label>
                    <input
                        id="city-filter"
                        type="text"
                        placeholder="Filter by city..."
                        value={filter.city}
                        onChange={(e) => setFilter({ ...filter, city: e.target.value })}
                        className="filter-input"
                    />
                </div>

                <div className="filter-group">
                    <label htmlFor="cuisine-filter">
                        <span className="filter-icon">🍕</span> Cuisine
                    </label>
                    <input
                        id="cuisine-filter"
                        type="text"
                        placeholder="Filter by cuisine..."
                        value={filter.cuisineType}
                        onChange={(e) => setFilter({ ...filter, cuisineType: e.target.value })}
                        className="filter-input"
                    />
                </div>

                <div className="filter-group">
                    <label htmlFor="rating-filter">
                        <span className="filter-icon">⭐</span> Min Rating: {filter.minRating}
                    </label>
                    <input
                        id="rating-filter"
                        type="range"
                        min="0"
                        max="5"
                        step="0.5"
                        value={filter.minRating}
                        onChange={(e) => setFilter({ ...filter, minRating: parseFloat(e.target.value) })}
                        className="filter-range"
                    />
                </div>

                {(filter.city || filter.cuisineType || filter.minRating > 0) && (
                    <button onClick={clearFilters} className="clear-filters-button">
                        ✖ Clear Filters
                    </button>
                )}
            </div>

            {/* Results count */}
            <div className="results-info">
                <p>
                    Showing <strong>{filteredRestaurants.length}</strong> of <strong>{restaurants.length}</strong> restaurants
                </p>
            </div>

            {/* Restaurant grid */}
            {filteredRestaurants.length > 0 ? (
                <div className="restaurant-grid">
                    {filteredRestaurants.map(restaurant => (
                        <RestaurantCard 
                            key={restaurant.id} 
                            restaurant={restaurant}
                            onClick={onRestaurantClick}
                        />
                    ))}
                </div>
            ) : (
                <div className="no-results">
                    <p>No restaurants match your filters.</p>
                    <button onClick={clearFilters} className="retry-button">
                        Clear Filters
                    </button>
                </div>
            )}

            {/* Footer info */}
            <div className="restaurant-list-footer">
                <p className="architecture-info">
                    <small>
                        🔄 Architecture: React (Port 5000) → BFF Server (Port 3000) → 
                        API Gateway (Port 8082) → Food Service (Port 8083) → MongoDB Atlas
                    </small>
                </p>
            </div>
        </div>
    );
}

export default RestaurantList;
