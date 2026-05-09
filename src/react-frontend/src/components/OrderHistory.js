/**
 * OrderHistory Component
 * 
 * Purpose: Displays user's past orders with status tracking
 * Allows viewing order details and reordering
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 5, 2026
 */

import React, { useState, useEffect } from 'react';
import './OrderHistory.css';

const OrderHistory = ({ userId }) => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [expandedOrder, setExpandedOrder] = useState(null);
    const [error, setError] = useState(null);

    // Fetch orders on component mount
    useEffect(() => {
        fetchOrders();
    }, [userId]);

    // Fetch user's orders
    const fetchOrders = async () => {
        setLoading(true);
        setError(null);
        
        try {
            // TODO: Replace with actual API call
            // const response = await orderAPI.getByUser(userId);
            // setOrders(response);
            
            // Simulated data for demonstration
            await new Promise(resolve => setTimeout(resolve, 1000));
            
            const mockOrders = [
                {
                    id: '1',
                    restaurantName: 'Pizza Palace',
                    orderDate: '2026-04-04T18:30:00',
                    totalPrice: 45.99,
                    status: 'DELIVERED',
                    items: [
                        { foodName: 'Margherita Pizza', quantity: 1, price: 15.99 },
                        { foodName: 'Caesar Salad', quantity: 2, price: 12.00 }
                    ],
                    deliveryAddress: '123 Main St, Toronto',
                    contactNumber: '4165551234',
                    paymentMethod: 'CREDIT_CARD'
                },
                {
                    id: '2',
                    restaurantName: 'Burger House',
                    orderDate: '2026-04-03T12:15:00',
                    totalPrice: 32.50,
                    status: 'OUT_FOR_DELIVERY',
                    items: [
                        { foodName: 'Classic Burger', quantity: 2, price: 12.99 },
                        { foodName: 'French Fries', quantity: 1, price: 4.99 }
                    ],
                    deliveryAddress: '123 Main St, Toronto',
                    contactNumber: '4165551234',
                    paymentMethod: 'CASH'
                },
                {
                    id: '3',
                    restaurantName: 'Sushi World',
                    orderDate: '2026-04-01T19:45:00',
                    totalPrice: 78.25,
                    status: 'CANCELLED',
                    items: [
                        { foodName: 'California Roll', quantity: 3, price: 14.99 },
                        { foodName: 'Miso Soup', quantity: 2, price: 5.99 }
                    ],
                    deliveryAddress: '123 Main St, Toronto',
                    contactNumber: '4165551234',
                    paymentMethod: 'DEBIT_CARD'
                }
            ];

            setOrders(mockOrders);
        } catch (err) {
            console.error('Error fetching orders:', err);
            setError('Failed to load order history');
        } finally {
            setLoading(false);
        }
    };

    // Toggle order details expansion
    const toggleOrderDetails = (orderId) => {
        setExpandedOrder(expandedOrder === orderId ? null : orderId);
    };

    // Get status badge styling
    const getStatusStyle = (status) => {
        const styles = {
            PENDING: { bg: '#fef3c7', color: '#92400e', icon: '🕐' },
            CONFIRMED: { bg: '#dbeafe', color: '#1e40af', icon: '✅' },
            PREPARING: { bg: '#e0e7ff', color: '#4338ca', icon: '👨‍🍳' },
            OUT_FOR_DELIVERY: { bg: '#fce7f3', color: '#9f1239', icon: '🚚' },
            DELIVERED: { bg: '#d1fae5', color: '#065f46', icon: '✨' },
            CANCELLED: { bg: '#fee2e2', color: '#991b1b', icon: '❌' }
        };
        return styles[status] || styles.PENDING;
    };

    // Format date
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    // Handle reorder
    const handleReorder = (order) => {
        // TODO: Add items back to cart and navigate to restaurant
        console.log('Reordering:', order);
        alert('Reorder functionality coming soon!');
    };

    // Render loading state
    if (loading) {
        return (
            <div className="order-history-container">
                <div className="loading-state">
                    <div className="spinner"></div>
                    <p>Loading your orders...</p>
                </div>
            </div>
        );
    }

    // Render error state
    if (error) {
        return (
            <div className="order-history-container">
                <div className="error-state">
                    <span className="error-icon">⚠️</span>
                    <h3>Oops!</h3>
                    <p>{error}</p>
                    <button onClick={fetchOrders} className="retry-btn">
                        Try Again
                    </button>
                </div>
            </div>
        );
    }

    // Render empty state
    if (orders.length === 0) {
        return (
            <div className="order-history-container">
                <div className="empty-state">
                    <span className="empty-icon">📦</span>
                    <h3>No Orders Yet</h3>
                    <p>When you place orders, they'll show up here</p>
                </div>
            </div>
        );
    }

    // Render orders list
    return (
        <div className="order-history-container">
            <div className="order-history-header">
                <h2>Order History</h2>
                <p className="order-count">{orders.length} order{orders.length !== 1 ? 's' : ''}</p>
            </div>

            <div className="orders-list">
                {orders.map(order => {
                    const statusStyle = getStatusStyle(order.status);
                    const isExpanded = expandedOrder === order.id;

                    return (
                        <div key={order.id} className="order-card">
                            <div className="order-header" onClick={() => toggleOrderDetails(order.id)}>
                                <div className="order-main-info">
                                    <h3>{order.restaurantName}</h3>
                                    <p className="order-date">{formatDate(order.orderDate)}</p>
                                </div>
                                <div className="order-summary">
                                    <span 
                                        className="status-badge" 
                                        style={{ 
                                            background: statusStyle.bg, 
                                            color: statusStyle.color 
                                        }}
                                    >
                                        <span className="status-icon">{statusStyle.icon}</span>
                                        {order.status.replace(/_/g, ' ')}
                                    </span>
                                    <p className="order-total">${order.totalPrice.toFixed(2)}</p>
                                    <button className="expand-btn">
                                        {isExpanded ? '▲' : '▼'}
                                    </button>
                                </div>
                            </div>

                            {isExpanded && (
                                <div className="order-details">
                                    <div className="order-items">
                                        <h4>Items Ordered</h4>
                                        {order.items.map((item, index) => (
                                            <div key={index} className="order-item">
                                                <span className="item-detail">
                                                    {item.foodName} × {item.quantity}
                                                </span>
                                                <span className="item-price">
                                                    ${(item.price * item.quantity).toFixed(2)}
                                                </span>
                                            </div>
                                        ))}
                                    </div>

                                    <div className="order-info">
                                        <div className="info-row">
                                            <span className="info-label">📍 Delivery Address:</span>
                                            <span className="info-value">{order.deliveryAddress}</span>
                                        </div>
                                        <div className="info-row">
                                            <span className="info-label">📞 Contact:</span>
                                            <span className="info-value">{order.contactNumber}</span>
                                        </div>
                                        <div className="info-row">
                                            <span className="info-label">💳 Payment:</span>
                                            <span className="info-value">
                                                {order.paymentMethod.replace(/_/g, ' ')}
                                            </span>
                                        </div>
                                    </div>

                                    <div className="order-actions">
                                        {order.status === 'DELIVERED' && (
                                            <button 
                                                className="reorder-btn"
                                                onClick={() => handleReorder(order)}
                                            >
                                                🔄 Reorder
                                            </button>
                                        )}
                                        {(order.status === 'PENDING' || order.status === 'CONFIRMED') && (
                                            <button 
                                                className="cancel-order-btn"
                                                onClick={() => alert('Cancel order functionality coming soon!')}
                                            >
                                                ❌ Cancel Order
                                            </button>
                                        )}
                                        <button 
                                            className="help-btn"
                                            onClick={() => alert('Help & Support coming soon!')}
                                        >
                                            💬 Need Help?
                                        </button>
                                    </div>
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default OrderHistory;
