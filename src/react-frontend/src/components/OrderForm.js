/**
 * OrderForm Component
 * 
 * Purpose: Order placement form with delivery details and payment selection
 * Validates user input and submits order to API Gateway
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 5, 2026
 */

import React, { useState } from 'react';
import { useCart } from '../context/CartContext';
import { orderAPI } from '../services/api';
import './OrderForm.css';

const OrderForm = ({ onOrderComplete, onCancel }) => {
    const { cartItems, getSubtotal, getTax, getDeliveryFee, getTotal, clearCart, getCartRestaurantId } = useCart();

    // Form state
    const [formData, setFormData] = useState({
        // Customer information
        customerName: '',
        customerEmail: '',
        customerPhone: '',
        // Delivery information
        deliveryAddress: '',
        deliveryCity: '',
        deliveryPostalCode: '',
        contactNumber: '',
        specialInstructions: '',
        paymentMethod: 'CASH'
    });

    // Validation errors
    const [errors, setErrors] = useState({});

    // Form submission state
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Handle input change
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        
        // Clear error for this field when user starts typing
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    // Validate form
    const validateForm = () => {
        const newErrors = {};

        // Customer name validation
        if (!formData.customerName.trim()) {
            newErrors.customerName = 'Name is required';
        } else if (formData.customerName.trim().length < 2) {
            newErrors.customerName = 'Name must be at least 2 characters';
        }

        // Email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!formData.customerEmail.trim()) {
            newErrors.customerEmail = 'Email is required';
        } else if (!emailRegex.test(formData.customerEmail)) {
            newErrors.customerEmail = 'Invalid email format';
        }

        // Customer phone validation
        const customerPhoneRegex = /^\d{10}$/;
        const cleanedCustomerPhone = formData.customerPhone.replace(/\D/g, '');
        if (!formData.customerPhone.trim()) {
            newErrors.customerPhone = 'Phone number is required';
        } else if (!customerPhoneRegex.test(cleanedCustomerPhone)) {
            newErrors.customerPhone = 'Please enter 10 digits';
        }

        // Delivery address validation
        if (!formData.deliveryAddress.trim()) {
            newErrors.deliveryAddress = 'Delivery address is required';
        } else if (formData.deliveryAddress.trim().length < 10) {
            newErrors.deliveryAddress = 'Please enter a complete address';
        }

        // City validation
        if (!formData.deliveryCity.trim()) {
            newErrors.deliveryCity = 'City is required';
        }

        // Postal code validation (Canadian format: A1A 1A1)
        const postalCodeRegex = /^[A-Za-z]\d[A-Za-z][ -]?\d[A-Za-z]\d$/;
        if (!formData.deliveryPostalCode.trim()) {
            newErrors.deliveryPostalCode = 'Postal code is required';
        } else if (!postalCodeRegex.test(formData.deliveryPostalCode.trim())) {
            newErrors.deliveryPostalCode = 'Invalid format (e.g., A1A 1A1)';
        }

        // Contact number validation (10 digits)
        const phoneRegex = /^\d{10}$/;
        const cleanedPhone = formData.contactNumber.replace(/\D/g, '');
        if (!formData.contactNumber.trim()) {
            newErrors.contactNumber = 'Contact number is required';
        } else if (!phoneRegex.test(cleanedPhone)) {
            newErrors.contactNumber = 'Please enter 10 digits';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        if (cartItems.length === 0) {
            alert('Your cart is empty');
            return;
        }

        setIsSubmitting(true);

        try {
            // Prepare order items
            const orderItems = cartItems.map(item => ({
                foodId: item.foodId,
                foodName: item.foodName,
                quantity: item.quantity,
                price: item.price,
                subtotal: item.price * item.quantity,
                specialRequest: ''
            }));

            console.log('📦 Cart Items:', cartItems);
            console.log('📝 Mapped Order Items:', orderItems);

            // Prepare combined checkout data
            const checkoutData = {
                // Customer information
                customerName: formData.customerName.trim(),
                customerEmail: formData.customerEmail.trim().toLowerCase(),
                customerPhone: formData.customerPhone.replace(/\D/g, ''),
                // Order information
                restaurantId: getCartRestaurantId(),
                items: orderItems,
                deliveryAddress: formData.deliveryAddress.trim(),
                deliveryCity: formData.deliveryCity.trim(),
                deliveryPostalCode: formData.deliveryPostalCode.trim().toUpperCase(),
                contactNumber: formData.contactNumber.replace(/\D/g, ''),
                specialInstructions: formData.specialInstructions.trim(),
                paymentMethod: formData.paymentMethod,
                deliveryFee: getDeliveryFee(),
                taxAmount: getTax(),
                discountAmount: 0
            };

            console.log('🚀 Submitting guest checkout:', checkoutData);
            console.log('📍 Restaurant ID:', checkoutData.restaurantId);
            console.log('📦 Items count:', checkoutData.items.length);

            // Validate before sending
            if (!checkoutData.restaurantId) {
                throw new Error('Restaurant ID is missing. Please add items from a single restaurant.');
            }

            if (checkoutData.items.length === 0) {
                throw new Error('No items in order');
            }

            // Call guest checkout API
            const response = await orderAPI.guestCheckout(checkoutData);
            
            console.log('✅ Order created:', response.data);

            // Clear cart and notify parent
            clearCart();
            
            if (onOrderComplete) {
                onOrderComplete(response.data);
            }

            alert(`Order placed successfully! 🎉\nOrder ID: ${response.data.id}`);

        } catch (error) {
            console.error('Error placing order:', error);
            console.error('Error details:', error.response?.data);
            
            // Show detailed error message
            const errorMessage = error.response?.data?.message 
                || error.response?.data?.error
                || error.message 
                || 'Failed to place order';
            
            alert(`❌ Order failed:\n${errorMessage}\n\nPlease check console for details.`);
        } finally {
            setIsSubmitting(false);
        }
    };

    // Format phone number as user types
    const formatPhoneNumber = (value) => {
        const cleaned = value.replace(/\D/g, '');
        const match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
        if (match) {
            return `(${match[1]}) ${match[2]}-${match[3]}`;
        }
        return value;
    };

    // Format postal code as user types
    const formatPostalCode = (value) => {
        const cleaned = value.replace(/[^A-Za-z0-9]/g, '').toUpperCase();
        if (cleaned.length <= 3) {
            return cleaned;
        }
        return `${cleaned.slice(0, 3)} ${cleaned.slice(3, 6)}`;
    };

    return (
        <div className="order-form-overlay">
            <div className="order-form-container">
                <div className="order-form-header">
                    <h2>Complete Your Order</h2>
                    <button className="close-btn" onClick={onCancel}>✕</button>
                </div>

                <form onSubmit={handleSubmit} className="order-form">
                    {/* Order Summary */}
                    <div className="order-summary">
                        <h3>Order Summary</h3>
                        <div className="summary-items">
                            {cartItems.map(item => (
                                <div key={item.foodId} className="summary-item">
                                    <span>{item.foodName} × {item.quantity}</span>
                                    <span>${(item.price * item.quantity).toFixed(2)}</span>
                                </div>
                            ))}
                        </div>
                        <div className="summary-totals">
                            <div className="summary-line">
                                <span>Subtotal:</span>
                                <span>${getSubtotal().toFixed(2)}</span>
                            </div>
                            <div className="summary-line">
                                <span>Tax (13% HST):</span>
                                <span>${getTax().toFixed(2)}</span>
                            </div>
                            <div className="summary-line">
                                <span>Delivery Fee:</span>
                                <span>${getDeliveryFee().toFixed(2)}</span>
                            </div>
                            <div className="summary-line total">
                                <span>Total:</span>
                                <span>${getTotal().toFixed(2)}</span>
                            </div>
                        </div>
                    </div>

                    {/* Customer Information */}
                    <div className="form-section">
                        <h3>Customer Information</h3>

                        <div className="form-group">
                            <label htmlFor="customerName">
                                Full Name <span className="required">*</span>
                            </label>
                            <input
                                type="text"
                                id="customerName"
                                name="customerName"
                                value={formData.customerName}
                                onChange={handleChange}
                                placeholder="John Doe"
                                maxLength="100"
                                className={errors.customerName ? 'error' : ''}
                            />
                            {errors.customerName && (
                                <span className="error-message">{errors.customerName}</span>
                            )}
                        </div>

                        <div className="form-group">
                            <label htmlFor="customerEmail">
                                Email <span className="required">*</span>
                            </label>
                            <input
                                type="email"
                                id="customerEmail"
                                name="customerEmail"
                                value={formData.customerEmail}
                                onChange={handleChange}
                                placeholder="john@example.com"
                                maxLength="100"
                                className={errors.customerEmail ? 'error' : ''}
                            />
                            {errors.customerEmail && (
                                <span className="error-message">{errors.customerEmail}</span>
                            )}
                        </div>

                        <div className="form-group">
                            <label htmlFor="customerPhone">
                                Phone Number <span className="required">*</span>
                            </label>
                            <input
                                type="tel"
                                id="customerPhone"
                                name="customerPhone"
                                value={formData.customerPhone}
                                onChange={(e) => {
                                    const formatted = formatPhoneNumber(e.target.value);
                                    handleChange({ target: { name: 'customerPhone', value: formatted } });
                                }}
                                placeholder="(416) 555-1234"
                                maxLength="14"
                                className={errors.customerPhone ? 'error' : ''}
                            />
                            {errors.customerPhone && (
                                <span className="error-message">{errors.customerPhone}</span>
                            )}
                        </div>
                    </div>

                    {/* Delivery Information */}
                    <div className="form-section">
                        <h3>Delivery Information</h3>

                        <div className="form-group">
                            <label htmlFor="deliveryAddress">
                                Street Address <span className="required">*</span>
                            </label>
                            <input
                                type="text"
                                id="deliveryAddress"
                                name="deliveryAddress"
                                value={formData.deliveryAddress}
                                onChange={handleChange}
                                placeholder="123 Main Street, Apt 4B"
                                maxLength="200"
                                className={errors.deliveryAddress ? 'error' : ''}
                            />
                            {errors.deliveryAddress && (
                                <span className="error-message">{errors.deliveryAddress}</span>
                            )}
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="deliveryCity">
                                    City <span className="required">*</span>
                                </label>
                                <input
                                    type="text"
                                    id="deliveryCity"
                                    name="deliveryCity"
                                    value={formData.deliveryCity}
                                    onChange={handleChange}
                                    placeholder="Toronto"
                                    maxLength="100"
                                    className={errors.deliveryCity ? 'error' : ''}
                                />
                                {errors.deliveryCity && (
                                    <span className="error-message">{errors.deliveryCity}</span>
                                )}
                            </div>

                            <div className="form-group">
                                <label htmlFor="deliveryPostalCode">
                                    Postal Code <span className="required">*</span>
                                </label>
                                <input
                                    type="text"
                                    id="deliveryPostalCode"
                                    name="deliveryPostalCode"
                                    value={formData.deliveryPostalCode}
                                    onChange={(e) => {
                                        const formatted = formatPostalCode(e.target.value);
                                        handleChange({ target: { name: 'deliveryPostalCode', value: formatted } });
                                    }}
                                    placeholder="A1A 1A1"
                                    maxLength="7"
                                    className={errors.deliveryPostalCode ? 'error' : ''}
                                />
                                {errors.deliveryPostalCode && (
                                    <span className="error-message">{errors.deliveryPostalCode}</span>
                                )}
                            </div>
                        </div>

                        <div className="form-group">
                            <label htmlFor="contactNumber">
                                Contact Number <span className="required">*</span>
                            </label>
                            <input
                                type="tel"
                                id="contactNumber"
                                name="contactNumber"
                                value={formData.contactNumber}
                                onChange={(e) => {
                                    const formatted = formatPhoneNumber(e.target.value);
                                    handleChange({ target: { name: 'contactNumber', value: formatted } });
                                }}
                                placeholder="(416) 555-1234"
                                maxLength="14"
                                className={errors.contactNumber ? 'error' : ''}
                            />
                            {errors.contactNumber && (
                                <span className="error-message">{errors.contactNumber}</span>
                            )}
                        </div>

                        <div className="form-group">
                            <label htmlFor="specialInstructions">
                                Special Instructions (Optional)
                            </label>
                            <textarea
                                id="specialInstructions"
                                name="specialInstructions"
                                value={formData.specialInstructions}
                                onChange={handleChange}
                                placeholder="Delivery instructions, dietary restrictions, etc."
                                rows="3"
                                maxLength="500"
                            />
                            <small className="char-count">
                                {formData.specialInstructions.length}/500 characters
                            </small>
                        </div>
                    </div>

                    {/* Payment Method */}
                    <div className="form-section">
                        <h3>Payment Method</h3>
                        <div className="payment-methods">
                            <label className={`payment-option ${formData.paymentMethod === 'CASH' ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="CASH"
                                    checked={formData.paymentMethod === 'CASH'}
                                    onChange={handleChange}
                                />
                                <span className="payment-icon">💵</span>
                                <span>Cash on Delivery</span>
                            </label>

                            <label className={`payment-option ${formData.paymentMethod === 'CREDIT_CARD' ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="CREDIT_CARD"
                                    checked={formData.paymentMethod === 'CREDIT_CARD'}
                                    onChange={handleChange}
                                />
                                <span className="payment-icon">💳</span>
                                <span>Credit Card</span>
                            </label>

                            <label className={`payment-option ${formData.paymentMethod === 'DEBIT_CARD' ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="DEBIT_CARD"
                                    checked={formData.paymentMethod === 'DEBIT_CARD'}
                                    onChange={handleChange}
                                />
                                <span className="payment-icon">💳</span>
                                <span>Debit Card</span>
                            </label>

                            <label className={`payment-option ${formData.paymentMethod === 'UPI' ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="UPI"
                                    checked={formData.paymentMethod === 'UPI'}
                                    onChange={handleChange}
                                />
                                <span className="payment-icon">📱</span>
                                <span>UPI / Mobile Payment</span>
                            </label>

                            <label className={`payment-option ${formData.paymentMethod === 'ONLINE' ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="ONLINE"
                                    checked={formData.paymentMethod === 'ONLINE'}
                                    onChange={handleChange}
                                />
                                <span className="payment-icon">🌐</span>
                                <span>Online Payment</span>
                            </label>
                        </div>
                    </div>

                    {/* Form Actions */}
                    <div className="form-actions">
                        <button
                            type="button"
                            className="cancel-btn"
                            onClick={onCancel}
                            disabled={isSubmitting}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="submit-btn"
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? 'Placing Order...' : `Place Order - $${getTotal().toFixed(2)}`}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default OrderForm;
