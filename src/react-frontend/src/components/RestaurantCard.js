/**
 * RestaurantCard Component
 * 
 * Purpose: Displays individual restaurant information as a card
 * Used by: RestaurantList component
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 5, 2026
 */

import React from 'react';
import './RestaurantCard.css';

/**
 * RestaurantCard Component
 * 
 * Displays a single restaurant's information in a card format.
 * Includes restaurant name, location, cuisine type, rating, and contact info.
 * Clickable to view restaurant menu.
 * 
 * @param {Object} props - Component props
 * @param {Object} props.restaurant - Restaurant object from API
 * @param {string} props.restaurant.id - Restaurant ID
 * @param {string} props.restaurant.name - Restaurant name
 * @param {string} props.restaurant.city - Restaurant city
 * @param {string} props.restaurant.cuisineType - Type of cuisine
 * @param {number} props.restaurant.rating - Rating (0.0 - 5.0)
 * @param {string} props.restaurant.phoneNumber - Phone number (optional)
 * @param {string} props.restaurant.address - Address (optional)
 * @param {boolean} props.restaurant.isActive - Active status
 * @param {Function} props.onClick - Callback when restaurant card is clicked
 */
function RestaurantCard({ restaurant, onClick }) {
    
    /**
     * Render star rating display
     * @param {number} rating - Restaurant rating
     * @returns {JSX.Element} Star rating component
     */
    const renderStars = (rating) => {
        const fullStars = Math.floor(rating);
        const hasHalfStar = rating % 1 >= 0.5;
        const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        
        return (
            <div className="star-rating">
                {/* Full stars */}
                {[...Array(fullStars)].map((_, i) => (
                    <span key={`full-${i}`} className="star full">★</span>
                ))}
                
                {/* Half star */}
                {hasHalfStar && <span className="star half">★</span>}
                
                {/* Empty stars */}
                {[...Array(emptyStars)].map((_, i) => (
                    <span key={`empty-${i}`} className="star empty">☆</span>
                ))}
                
                {/* Rating number */}
                <span className="rating-number">{rating.toFixed(1)}</span>
            </div>
        );
    };

    /**
     * Get cuisine emoji based on cuisine type
     * @param {string} cuisineType - Type of cuisine
     * @returns {string} Appropriate emoji
     */
    const getCuisineEmoji = (cuisineType) => {
        const type = cuisineType?.toLowerCase() || '';
        
        if (type.includes('italian')) return '🍝';
        if (type.includes('chinese')) return '🥡';
        if (type.includes('japanese') || type.includes('sushi')) return '🍱';
        if (type.includes('indian')) return '🍛';
        if (type.includes('mexican')) return '🌮';
        if (type.includes('american') || type.includes('burger')) return '🍔';
        if (type.includes('pizza')) return '🍕';
        if (type.includes('thai')) return '🍜';
        if (type.includes('french')) return '🥐';
        if (type.includes('mediterranean')) return '🥙';
        if (type.includes('seafood')) return '🦞';
        if (type.includes('vegan') || type.includes('vegetarian')) return '🥗';
        
        return '🍽️'; // Default
    };

    /**
     * Handle view menu click
     */
    const handleViewMenu = (e) => {
        e.stopPropagation(); // Prevent event bubbling if card is also clickable
        console.log(`📖 View menu for: ${restaurant.name} (ID: ${restaurant.id})`);
        if (onClick) {
            onClick(restaurant);
        }
    };
    
    /**
     * Handle card click
     */
    const handleCardClick = () => {
        console.log(`🏪 Restaurant card clicked: ${restaurant.name}`);
        if (onClick) {
            onClick(restaurant);
        }
    };

    return (
        <div className="restaurant-card" onClick={handleCardClick} role="button" tabIndex={0}>
            {/* Restaurant badge */}
            <div className="restaurant-badge">
                {getCuisineEmoji(restaurant.cuisineType)}
            </div>

            {/* Active status indicator */}
            {restaurant.isActive && (
                <div className="active-indicator" title="Currently accepting orders">
                    <span className="pulse-dot"></span>
                    Open
                </div>
            )}

            {/* Restaurant info */}
            <div className="restaurant-info">
                <h3 className="restaurant-name" title={restaurant.name}>
                    {restaurant.name}
                </h3>

                <div className="restaurant-cuisine">
                    <span className="cuisine-icon">{getCuisineEmoji(restaurant.cuisineType)}</span>
                    <span className="cuisine-text">{restaurant.cuisineType}</span>
                </div>

                <div className="restaurant-location">
                    <span className="location-icon">📍</span>
                    <span className="location-text">{restaurant.city}</span>
                </div>

                {/* Rating */}
                <div className="restaurant-rating">
                    {renderStars(restaurant.rating)}
                </div>

                {/* Optional: Address */}
                {restaurant.address && (
                    <div className="restaurant-address" title={restaurant.address}>
                        <span className="address-icon">🏠</span>
                        <span className="address-text">{restaurant.address}</span>
                    </div>
                )}

                {/* Optional: Phone number */}
                {restaurant.phoneNumber && (
                    <div className="restaurant-phone">
                        <span className="phone-icon">📞</span>
                        <a href={`tel:${restaurant.phoneNumber}`} className="phone-link">
                            {restaurant.phoneNumber}
                        </a>
                    </div>
                )}
            </div>

            {/* Action buttons */}
            <div className="restaurant-actions">
                <button 
                    className="btn-primary view-menu-btn"
                    onClick={handleViewMenu}
                    title={`View menu for ${restaurant.name}`}
                >
                    📖 View Menu
                </button>
            </div>

            {/* Debug info (remove in production) */}
            {process.env.NODE_ENV === 'development' && (
                <div className="debug-info">
                    <small title={restaurant.id}>ID: {restaurant.id.substring(0, 8)}...</small>
                </div>
            )}
        </div>
    );
}

export default RestaurantCard;
