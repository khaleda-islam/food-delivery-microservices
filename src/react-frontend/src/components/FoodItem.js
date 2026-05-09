/**
 * FoodItem Component
 * 
 * Purpose: Displays individual food/menu item information as a card
 * Used by: MenuDisplay component
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 5, 2026
 */

import React, { useState } from 'react';
import { useCart } from '../context/CartContext';
import './FoodItem.css';

/**
 * FoodItem Component
 * 
 * Displays a single food item's information in a card format.
 * Includes food name, description, price, category, dietary info, and availability.
 * 
 * @param {Object} props - Component props
 * @param {Object} props.food - Food object from API
 * @param {string} props.food.id - Food ID
 * @param {string} props.food.name - Food name
 * @param {string} props.food.description - Food description
 * @param {number} props.food.price - Price
 * @param {string} props.food.category - Food category
 * @param {string} props.food.restaurantId - Restaurant ID
 * @param {string} props.food.imageUrl - Image URL (optional)
 * @param {boolean} props.food.isAvailable - Availability status
 * @param {number} props.food.preparationTimeMinutes - Prep time (optional)
 * @param {boolean} props.food.isVegetarian - Vegetarian flag
 * @param {boolean} props.food.isVegan - Vegan flag
 * @param {Function} props.onAddToCart - Callback when add to cart is clicked
 */
function FoodItem({ food, onAddToCart }) {
    const { addToCart } = useCart();
    const [quantity, setQuantity] = useState(1);
    const [showQuantity, setShowQuantity] = useState(false);
    const [addedToCart, setAddedToCart] = useState(false);
    
    // DEBUG: Log food item to check imageUrl
    console.log(`🍔 Rendering food: ${food.name}, imageUrl: "${food.imageUrl}"`);
    
    /**
     * Get category emoji based on food category
     * @param {string} category - Food category
     * @returns {string} Appropriate emoji
     */
    const getCategoryEmoji = (category) => {
        const cat = category?.toLowerCase() || '';
        
        if (cat.includes('pizza')) return '🍕';
        if (cat.includes('sushi')) return '🍣';
        if (cat.includes('burger')) return '🍔';
        if (cat.includes('curry')) return '🍛';
        if (cat.includes('pasta')) return '🍝';
        if (cat.includes('salad')) return '🥗';
        if (cat.includes('bread') || cat.includes('naan')) return '🍞';
        if (cat.includes('dessert')) return '🍰';
        if (cat.includes('drink') || cat.includes('beverage')) return '🥤';
        if (cat.includes('soup')) return '🍲';
        if (cat.includes('sandwich')) return '🥪';
        if (cat.includes('taco') || cat.includes('mexican')) return '🌮';
        if (cat.includes('noodle')) return '🍜';
        if (cat.includes('rice')) return '🍚';
        
        return '🍽️'; // Default food emoji
    };

    /**
     * Format price with currency
     * @param {number} price - Price value
     * @returns {string} Formatted price string
     */
    const formatPrice = (price) => {
        return `$${price.toFixed(2)}`;
    };

    /**
     * Handle add to cart button click
     */
    const handleAddToCart = () => {
        if (!showQuantity) {
            setShowQuantity(true);
            return;
        }

        // Add item to cart
        addToCart(food, quantity);
        
        // Show feedback
        setAddedToCart(true);
        setTimeout(() => {
            setAddedToCart(false);
            setShowQuantity(false);
            setQuantity(1);
        }, 2000);

        // Call parent callback if provided
        if (onAddToCart) {
            onAddToCart(food);
        }
    };

    /**
     * Increase quantity
     */
    const increaseQuantity = () => {
        setQuantity(prev => Math.min(prev + 1, 10)); // Max 10
    };

    /**
     * Decrease quantity
     */
    const decreaseQuantity = () => {
        setQuantity(prev => Math.max(prev - 1, 1)); // Min 1
    };

    return (
        <div className={`food-item ${!food.isAvailable ? 'unavailable' : ''}`}>
            {/* Category Badge */}
            <div className="food-badge">{getCategoryEmoji(food.category)}</div>
            
            {/* Availability Indicator */}
            {food.isAvailable ? (
                <div className="available-indicator">
                    <span className="indicator-dot"></span>
                    Available
                </div>
            ) : (
                <div className="unavailable-indicator">
                    <span className="indicator-dot"></span>
                    Unavailable
                </div>
            )}

            {/* Food Image (if available) */}
            {food.imageUrl && (
                <div className="food-image">
                    <img src={food.imageUrl} alt={food.name} />
                </div>
            )}

            {/* Food Header */}
            <div className="food-header">
                <h3 className="food-name">{food.name}</h3>
                <div className="food-price">{formatPrice(food.price)}</div>
            </div>

            {/* Food Category */}
            <div className="food-category">
                <span className="category-tag">{food.category}</span>
            </div>

            {/* Food Description */}
            {food.description && (
                <p className="food-description">{food.description}</p>
            )}

            {/* Food Metadata */}
            <div className="food-metadata">
                {/* Preparation Time */}
                {food.preparationTimeMinutes && (
                    <div className="metadata-item">
                        <span className="metadata-icon">⏱️</span>
                        <span className="metadata-text">{food.preparationTimeMinutes} min</span>
                    </div>
                )}

                {/* Dietary Tags */}
                <div className="dietary-tags">
                    {food.isVegan && (
                        <span className="dietary-tag vegan" title="Vegan">
                            🌱 Vegan
                        </span>
                    )}
                    {food.isVegetarian && !food.isVegan && (
                        <span className="dietary-tag vegetarian" title="Vegetarian">
                            🥬 Vegetarian
                        </span>
                    )}
                </div>
            </div>

            {/* Add to Cart Button */}
            {addedToCart ? (
                <div className="added-feedback">
                    ✅ Added to Cart!
                </div>
            ) : showQuantity ? (
                <div className="cart-controls">
                    <div className="quantity-selector">
                        <button 
                            className="qty-btn" 
                            onClick={decreaseQuantity}
                            type="button"
                        >
                            −
                        </button>
                        <span className="qty-display">{quantity}</span>
                        <button 
                            className="qty-btn" 
                            onClick={increaseQuantity}
                            type="button"
                        >
                            +
                        </button>
                    </div>
                    <button 
                        className="confirm-add-button"
                        onClick={handleAddToCart}
                    >
                        Add {quantity} to Cart
                    </button>
                </div>
            ) : (
                <button 
                    className="add-to-cart-button"
                    onClick={handleAddToCart}
                    disabled={!food.isAvailable}
                >
                    {food.isAvailable ? '🛒 Add to Cart' : '❌ Not Available'}
                </button>
            )}
        </div>
    );
}

export default FoodItem;
