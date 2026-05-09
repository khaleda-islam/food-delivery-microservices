/**
 * Cart Context - Global State Management for Shopping Cart
 * 
 * Purpose: Manages cart items, quantities, and totals across the application
 * Architecture: React Context API for state management
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 5, 2026
 */

import React, { createContext, useContext, useState, useEffect } from 'react';

// Create Cart Context
const CartContext = createContext();

/**
 * Custom hook to use cart context
 * @returns {Object} Cart context value
 */
export const useCart = () => {
    const context = useContext(CartContext);
    if (!context) {
        throw new Error('useCart must be used within a CartProvider');
    }
    return context;
};

/**
 * Cart Provider Component
 * Wraps application to provide cart functionality
 * 
 * @param {Object} props - Component props
 * @param {React.ReactNode} props.children - Child components
 */
export const CartProvider = ({ children }) => {
    // Cart state - array of cart items
    const [cartItems, setCartItems] = useState([]);
    
    // Load cart from localStorage on mount
    useEffect(() => {
        const savedCart = localStorage.getItem('foodDeliveryCart');
        if (savedCart) {
            try {
                setCartItems(JSON.parse(savedCart));
            } catch (error) {
                console.error('Error loading cart from localStorage:', error);
            }
        }
    }, []);
    
    // Save cart to localStorage whenever it changes
    useEffect(() => {
        localStorage.setItem('foodDeliveryCart', JSON.stringify(cartItems));
    }, [cartItems]);
    
    /**
     * Add item to cart or increase quantity if already exists
     * @param {Object} food - Food item to add
     * @param {number} quantity - Quantity to add (default: 1)
     */
    const addToCart = (food, quantity = 1) => {
        setCartItems(prevItems => {
            const existingItem = prevItems.find(item => item.foodId === food.id);
            
            if (existingItem) {
                // Item exists, increase quantity
                return prevItems.map(item =>
                    item.foodId === food.id
                        ? { ...item, quantity: item.quantity + quantity }
                        : item
                );
            } else {
                // New item, add to cart
                return [
                    ...prevItems,
                    {
                        foodId: food.id,
                        foodName: food.name,
                        price: food.price,
                        quantity: quantity,
                        restaurantId: food.restaurantId,
                        category: food.category,
                        imageUrl: food.imageUrl
                    }
                ];
            }
        });
        
        console.log(`✅ Added ${quantity}x ${food.name} to cart`);
    };
    
    /**
     * Remove item from cart completely
     * @param {string} foodId - Food ID to remove
     */
    const removeFromCart = (foodId) => {
        setCartItems(prevItems => prevItems.filter(item => item.foodId !== foodId));
        console.log(`🗑️ Removed item from cart`);
    };
    
    /**
     * Update item quantity in cart
     * @param {string} foodId - Food ID to update
     * @param {number} quantity - New quantity
     */
    const updateQuantity = (foodId, quantity) => {
        if (quantity <= 0) {
            removeFromCart(foodId);
            return;
        }
        
        setCartItems(prevItems =>
            prevItems.map(item =>
                item.foodId === foodId
                    ? { ...item, quantity: quantity }
                    : item
            )
        );
    };
    
    /**
     * Clear entire cart
     */
    const clearCart = () => {
        setCartItems([]);
        console.log('🧹 Cart cleared');
    };
    
    /**
     * Get total number of items in cart
     * @returns {number} Total item count
     */
    const getCartItemCount = () => {
        return cartItems.reduce((total, item) => total + item.quantity, 0);
    };
    
    /**
     * Get subtotal (sum of all item prices * quantities)
     * @returns {number} Subtotal amount
     */
    const getSubtotal = () => {
        return cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
    };
    
    /**
     * Calculate tax (13% HST for Ontario)
     * @returns {number} Tax amount
     */
    const getTax = () => {
        return getSubtotal() * 0.13;
    };
    
    /**
     * Calculate delivery fee
     * Free for orders over $30, otherwise $5
     * @returns {number} Delivery fee
     */
    const getDeliveryFee = () => {
        const subtotal = getSubtotal();
        return subtotal >= 30 ? 0 : 5;
    };
    
    /**
     * Get total amount (subtotal + tax + delivery)
     * @returns {number} Total amount
     */
    const getTotal = () => {
        return getSubtotal() + getTax() + getDeliveryFee();
    };
    
    /**
     * Check if cart contains items from multiple restaurants
     * @returns {boolean} True if multiple restaurants
     */
    const hasMultipleRestaurants = () => {
        if (cartItems.length === 0) return false;
        const restaurantIds = [...new Set(cartItems.map(item => item.restaurantId))];
        return restaurantIds.length > 1;
    };
    
    /**
     * Get restaurant ID of items in cart
     * @returns {string|null} Restaurant ID or null if empty/multiple
     */
    const getCartRestaurantId = () => {
        if (cartItems.length === 0) return null;
        const restaurantIds = [...new Set(cartItems.map(item => item.restaurantId))];
        return restaurantIds.length === 1 ? restaurantIds[0] : null;
    };
    
    // Context value
    const value = {
        cartItems,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        getCartItemCount,
        getSubtotal,
        getTax,
        getDeliveryFee,
        getTotal,
        hasMultipleRestaurants,
        getCartRestaurantId
    };
    
    return (
        <CartContext.Provider value={value}>
            {children}
        </CartContext.Provider>
    );
};

export default CartContext;
