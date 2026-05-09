/**
 * MenuDisplay Component
 * 
 * Purpose: Displays restaurant menu with filtering and search capabilities
 * Architecture: React → API Gateway → Food Delivery Service → MongoDB
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 5, 2026
 */

import React, { useState, useEffect } from 'react';
import { foodAPI } from '../services/api';
import FoodItem from './FoodItem';
import './MenuDisplay.css';

/**
 * MenuDisplay Component
 * 
 * Fetches and displays menu items with filtering and search functionality.
 * Can display all foods or filter by restaurant ID.
 * Includes category filtering, search, dietary filters, and sorting.
 * 
 * @param {Object} props - Component props
 * @param {string} props.restaurantId - Optional restaurant ID to filter menu
 * @param {string} props.restaurantName - Optional restaurant name for display
 * @param {Function} props.onBackClick - Optional callback for back navigation
 */
function MenuDisplay({ restaurantId, restaurantName, onBackClick }) {
    // State management
    const [menuItems, setMenuItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    // Filter states
    const [searchQuery, setSearchQuery] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('all');
    const [showVegetarianOnly, setShowVegetarianOnly] = useState(false);
    const [showVeganOnly, setShowVeganOnly] = useState(false);
    const [showAvailableOnly, setShowAvailableOnly] = useState(true);
    const [sortBy, setSortBy] = useState('name'); // name, price-asc, price-desc

    /**
     * Fetch menu items on component mount or when restaurantId changes
     */
    useEffect(() => {
        fetchMenuItems();
    }, [restaurantId]);

    /**
     * Fetch menu items from API
     */
    const fetchMenuItems = async () => {
        try {
            setLoading(true);
            setError(null);
            
            console.log('📡 Fetching menu items...');
            console.log('🔄 Request flow: React → API Gateway (8082) → Food Service (8083)');
            
            let response;
            if (restaurantId) {
                // Fetch menu for specific restaurant
                console.log(`Fetching menu for restaurant: ${restaurantId}`);
                response = await foodAPI.getByRestaurant(restaurantId);
            } else {
                // Fetch all menu items
                console.log('Fetching all menu items');
                response = await foodAPI.getAll();
            }
            
            console.log('✅ Successfully fetched menu items:', response.data.length);
            
            // DEBUG: Log first item to check imageUrl format
            if (response.data.length > 0) {
                console.log('🖼️  Sample food item:', response.data[0]);
                console.log('🖼️  Image URL:', response.data[0].imageUrl);
            }
            
            setMenuItems(response.data);
            setLoading(false);
        } catch (err) {
            console.error('❌ Error fetching menu items:', err);
            setError(err.response?.data?.message || err.message || 'Failed to fetch menu items');
            setLoading(false);
        }
    };

    /**
     * Get unique categories from menu items
     */
    const getCategories = () => {
        const categories = [...new Set(menuItems.map(item => item.category))];
        return categories.sort();
    };

    /**
     * Filter and sort menu items based on current filter states
     */
    const getFilteredMenuItems = () => {
        let filtered = [...menuItems];

        // Search filter
        if (searchQuery.trim()) {
            const query = searchQuery.toLowerCase();
            filtered = filtered.filter(item =>
                item.name.toLowerCase().includes(query) ||
                item.description?.toLowerCase().includes(query) ||
                item.category.toLowerCase().includes(query)
            );
        }

        // Category filter
        if (selectedCategory !== 'all') {
            filtered = filtered.filter(item => item.category === selectedCategory);
        }

        // Dietary filters
        if (showVegetarianOnly) {
            filtered = filtered.filter(item => item.isVegetarian);
        }

        if (showVeganOnly) {
            filtered = filtered.filter(item => item.isVegan);
        }

        // Availability filter
        if (showAvailableOnly) {
            filtered = filtered.filter(item => item.isAvailable);
        }

        // Sorting
        filtered.sort((a, b) => {
            switch (sortBy) {
                case 'price-asc':
                    return a.price - b.price;
                case 'price-desc':
                    return b.price - a.price;
                case 'name':
                default:
                    return a.name.localeCompare(b.name);
            }
        });

        return filtered;
    };

    /**
     * Handle add to cart (placeholder for future implementation)
     */
    const handleAddToCart = (food) => {
        console.log('Adding to cart:', food);
        alert(`Added ${food.name} to cart! (Cart functionality coming soon)`);
    };

    /**
     * Clear all filters
     */
    const clearFilters = () => {
        setSearchQuery('');
        setSelectedCategory('all');
        setShowVegetarianOnly(false);
        setShowVeganOnly(false);
        setShowAvailableOnly(true);
        setSortBy('name');
    };

    const filteredItems = getFilteredMenuItems();
    const categories = getCategories();

    /**
     * Render loading state
     */
    if (loading) {
        return (
            <div className="menu-display-loading">
                <div className="loading-spinner"></div>
                <p>Loading menu...</p>
                <small>Fetching delicious items from API Gateway</small>
            </div>
        );
    }

    /**
     * Render error state
     */
    if (error) {
        return (
            <div className="menu-display-error">
                <h2>⚠️ Error Loading Menu</h2>
                <p>{error}</p>
                <div className="error-help">
                    <p><strong>Troubleshooting:</strong></p>
                    <ul>
                        <li>✓ Ensure API Gateway is running on port 8082</li>
                        <li>✓ Ensure Food Delivery Service is running on port 8083</li>
                        <li>✓ Verify CORS is enabled in API Gateway</li>
                    </ul>
                </div>
                <button onClick={fetchMenuItems} className="retry-button">
                    🔄 Retry
                </button>
            </div>
        );
    }

    /**
     * Render empty state
     */
    if (menuItems.length === 0) {
        return (
            <div className="menu-display-empty">
                <h2>No Menu Items Found</h2>
                <p>There are no menu items available at the moment.</p>
                <button onClick={fetchMenuItems} className="retry-button">
                    🔄 Refresh
                </button>
            </div>
        );
    }

    /**
     * Render menu display with filters
     */
    return (
        <div className="menu-display-container">
            {/* Header */}
            <div className="menu-display-header">
                <h1>🍽️ {restaurantName ? `${restaurantName} Menu` : 'Browse Menu'}</h1>
                <p className="subtitle">
                    Showing {filteredItems.length} of {menuItems.length} items
                </p>
            </div>

            {/* Search and Filters */}
            <div className="menu-filters-section">
                {/* Search Bar */}
                <div className="search-bar">
                    <span className="search-icon">🔍</span>
                    <input
                        type="text"
                        placeholder="Search menu items..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="search-input"
                    />
                    {searchQuery && (
                        <button
                            className="clear-search"
                            onClick={() => setSearchQuery('')}
                            title="Clear search"
                        >
                            ✕
                        </button>
                    )}
                </div>

                {/* Filter Controls */}
                <div className="filter-controls">
                    {/* Category Filter */}
                    <div className="filter-group">
                        <label htmlFor="category-select">Category:</label>
                        <select
                            id="category-select"
                            value={selectedCategory}
                            onChange={(e) => setSelectedCategory(e.target.value)}
                            className="filter-select"
                        >
                            <option value="all">All Categories</option>
                            {categories.map(category => (
                                <option key={category} value={category}>
                                    {category}
                                </option>
                            ))}
                        </select>
                    </div>

                    {/* Sort By */}
                    <div className="filter-group">
                        <label htmlFor="sort-select">Sort By:</label>
                        <select
                            id="sort-select"
                            value={sortBy}
                            onChange={(e) => setSortBy(e.target.value)}
                            className="filter-select"
                        >
                            <option value="name">Name (A-Z)</option>
                            <option value="price-asc">Price (Low to High)</option>
                            <option value="price-desc">Price (High to Low)</option>
                        </select>
                    </div>

                    {/* Dietary Filters */}
                    <div className="filter-group checkbox-group">
                        <label className="checkbox-label">
                            <input
                                type="checkbox"
                                checked={showVegetarianOnly}
                                onChange={(e) => setShowVegetarianOnly(e.target.checked)}
                            />
                            <span>🥬 Vegetarian Only</span>
                        </label>

                        <label className="checkbox-label">
                            <input
                                type="checkbox"
                                checked={showVeganOnly}
                                onChange={(e) => setShowVeganOnly(e.target.checked)}
                            />
                            <span>🌱 Vegan Only</span>
                        </label>

                        <label className="checkbox-label">
                            <input
                                type="checkbox"
                                checked={showAvailableOnly}
                                onChange={(e) => setShowAvailableOnly(e.target.checked)}
                            />
                            <span>✓ Available Only</span>
                        </label>
                    </div>

                    {/* Clear Filters Button */}
                    <button onClick={clearFilters} className="clear-filters-button">
                        Clear Filters
                    </button>
                </div>
            </div>

            {/* Menu Items Grid */}
            <div className="menu-items-grid">
                {filteredItems.length > 0 ? (
                    filteredItems.map(food => (
                        <FoodItem
                            key={food.id}
                            food={food}
                            onAddToCart={handleAddToCart}
                        />
                    ))
                ) : (
                    <div className="no-results">
                        <p>No items match your filters.</p>
                        <button onClick={clearFilters} className="retry-button">
                            Clear Filters
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}

export default MenuDisplay;
