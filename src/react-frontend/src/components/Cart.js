/**
 * Cart Component
 * 
 * Purpose: Displays shopping cart with items, quantities, and totals
 * Allows users to modify quantities and proceed to checkout
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 5, 2026
 */

import React, { useState } from 'react';
import { useCart } from '../context/CartContext';
import './Cart.css';

/**
 * Cart Component
 * 
 * Shows cart items, allows quantity adjustments, and provides checkout button
 * 
 * @param {Object} props - Component props
 * @param {Function} props.onCheckout - Callback when checkout is clicked
 */
function Cart({ onCheckout }) {
    const {
        cartItems,
        removeFromCart,
        updateQuantity,
        clearCart,
        getCartItemCount,
        getSubtotal,
        getTax,
        getDeliveryFee,
        getTotal
    } = useCart();
    
    const [isExpanded, setIsExpanded] = useState(false);
    
    /**
     * Format price with currency
     * @param {number} price - Price value
     * @returns {string} Formatted price
     */
    const formatPrice = (price) => {
        return `$${price.toFixed(2)}`;
    };
    
    /**
     * Handle quantity change
     * @param {string} foodId - Food ID
     * @param {number} newQuantity - New quantity
     */
    const handleQuantityChange = (foodId, newQuantity) => {
        const quantity = parseInt(newQuantity);
        if (!isNaN(quantity)) {
            updateQuantity(foodId, quantity);
        }
    };
    
    /**
     * Handle checkout button click
     */
    const handleCheckout = () => {
        if (onCheckout) {
            onCheckout();
        }
    };
    
    const itemCount = getCartItemCount();
    const subtotal = getSubtotal();
    const tax = getTax();
    const deliveryFee = getDeliveryFee();
    const total = getTotal();
    
    // Empty cart message
    if (cartItems.length === 0) {
        return (
            <div className="cart-empty">
                <div className="empty-icon">🛒</div>
                <h3>Your cart is empty</h3>
                <p>Add items from the menu to get started!</p>
            </div>
        );
    }
    
    return (
        <div className={`cart-container ${isExpanded ? 'expanded' : ''}`}>
            {/* Cart Header */}
            <div className="cart-header" onClick={() => setIsExpanded(!isExpanded)}>
                <h3>🛒 Your Cart</h3>
                <div className="cart-summary">
                    <span className="item-count">{itemCount} {itemCount === 1 ? 'item' : 'items'}</span>
                    <span className="cart-total">{formatPrice(total)}</span>
                </div>
                <button className="cart-toggle">
                    {isExpanded ? '▼' : '▲'}
                </button>
            </div>
            
            {/* Cart Items (expanded view) */}
            {isExpanded && (
                <div className="cart-content">
                    {/* Cart Items List */}
                    <div className="cart-items">
                        {cartItems.map(item => (
                            <div key={item.foodId} className="cart-item">
                                <div className="item-info">
                                    <h4 className="item-name">{item.foodName}</h4>
                                    <p className="item-price">{formatPrice(item.price)} each</p>
                                    {item.category && (
                                        <span className="item-category">{item.category}</span>
                                    )}
                                </div>
                                
                                <div className="item-actions">
                                    <div className="quantity-control">
                                        <button
                                            className="qty-btn"
                                            onClick={() => updateQuantity(item.foodId, item.quantity - 1)}
                                            title="Decrease quantity"
                                        >
                                            −
                                        </button>
                                        <input
                                            type="number"
                                            className="qty-input"
                                            value={item.quantity}
                                            onChange={(e) => handleQuantityChange(item.foodId, e.target.value)}
                                            min="1"
                                            max="99"
                                        />
                                        <button
                                            className="qty-btn"
                                            onClick={() => updateQuantity(item.foodId, item.quantity + 1)}
                                            title="Increase quantity"
                                        >
                                            +
                                        </button>
                                    </div>
                                    
                                    <div className="item-subtotal">
                                        {formatPrice(item.price * item.quantity)}
                                    </div>
                                    
                                    <button
                                        className="remove-btn"
                                        onClick={() => removeFromCart(item.foodId)}
                                        title="Remove from cart"
                                    >
                                        🗑️
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                    
                    {/* Cart Totals */}
                    <div className="cart-totals">
                        <div className="total-line">
                            <span>Subtotal:</span>
                            <span>{formatPrice(subtotal)}</span>
                        </div>
                        <div className="total-line">
                            <span>Tax (HST 13%):</span>
                            <span>{formatPrice(tax)}</span>
                        </div>
                        <div className="total-line">
                            <span>Delivery Fee:</span>
                            <span>{deliveryFee === 0 ? 'FREE' : formatPrice(deliveryFee)}</span>
                        </div>
                        {deliveryFee > 0 && subtotal < 30 && (
                            <div className="delivery-note">
                                💡 Add {formatPrice(30 - subtotal)} more for free delivery!
                            </div>
                        )}
                        <div className="total-line grand-total">
                            <span>Total:</span>
                            <span>{formatPrice(total)}</span>
                        </div>
                    </div>
                    
                    {/* Cart Actions */}
                    <div className="cart-actions">
                        <button
                            className="clear-cart-btn"
                            onClick={clearCart}
                            title="Clear entire cart"
                        >
                            Clear Cart
                        </button>
                        <button
                            className="checkout-btn"
                            onClick={handleCheckout}
                            title="Proceed to checkout"
                        >
                            Proceed to Checkout →
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Cart;
