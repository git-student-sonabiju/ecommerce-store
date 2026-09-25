import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import API from '../api/axiosConfig';

const formatOrderDate = (value) => {
  if (!value) return '';
  // LocalDateTime responses have no zone suffix; these values are stored as UTC.
  const utcValue = /(?:Z|[+-]\d{2}:?\d{2})$/i.test(value) ? value : `${value}Z`;
  const date = new Date(utcValue);
  if (Number.isNaN(date.getTime())) return value;
  const parts = new Intl.DateTimeFormat('en-GB', {
    timeZone: 'Asia/Kolkata',
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hourCycle: 'h23',
  }).formatToParts(date).reduce((result, part) => {
    result[part.type] = part.value;
    return result;
  }, {});
  return `${parts.day}-${parts.month}-${parts.year}, ${parts.hour}:${parts.minute}`;
};

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cancelingOrderId, setCancelingOrderId] = useState(null);
  const [cancelError, setCancelError] = useState(null);

  const cancelOrder = async (orderId) => {
    if (!window.confirm('Cancel this order?')) return;
    setCancelingOrderId(orderId);
    setCancelError(null);
    try {
      const response = await API.post(`/orders/${orderId}/cancel`);
      setOrders((currentOrders) => currentOrders.map((order) => (
        order.id === orderId ? response.data : order
      )));
    } catch (err) {
      setCancelError(err.response?.data?.error || err.response?.data?.message || 'Could not cancel this order.');
    } finally {
      setCancelingOrderId(null);
    }
  };

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await API.get('/orders/mine');
        setOrders(response.data);
      } catch (err) {
        setError(err.response?.data?.error || err.response?.data?.message || 'Failed to load orders. Please sign in and try again.');
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, []);

  if (loading) return <div className="empty-state">Loading your orders...</div>;
  if (error) return <div className="alert alert-danger" role="alert">{error} <Link to="/login">Sign in</Link></div>;

  return (
    <section className="orders-page">
      <header className="page-heading">
        <div>
          <span className="page-kicker">THE STORY SO FAR</span>
          <h1>My orders</h1>
          <p className="page-subtitle">Your purchases, all together in one place.</p>
        </div>
        <Link to="/products" className="btn btn-outline-primary d-none d-sm-inline-flex">Shop more <span className="ms-2">→</span></Link>
      </header>
      {cancelError && <div className="alert alert-danger" role="alert">{cancelError}</div>}
      {orders.length === 0 ? (
        <div className="empty-state">
          <span className="empty-state-icon">✦</span>
          <h2 className="h4">Your next order starts here</h2>
          <p className="text-muted mb-3">When you place an order, you’ll find its details here.</p>
          <Link to="/products" className="btn btn-primary">Browse products</Link>
        </div>
      ) : (
        <div className="orders-list">
          {orders.map((order) => (
            <article className="order-card" key={order.id}>
              <div className="order-card-header">
                <div><span className="order-id">Order #{order.orderNumber ?? order.id}</span><div className="order-date">{formatOrderDate(order.orderDate)} IST</div></div>
                <div className="order-meta">
                  <span className="order-status">{order.status}</span>
                  <span className="order-total">${Number(order.totalAmount || 0).toFixed(2)}</span>
                  {(order.status === 'PLACED' || order.status === 'CONFIRMED') && (
                    <button
                      type="button"
                      className="btn btn-sm btn-outline-danger order-cancel-button"
                      onClick={() => cancelOrder(order.id)}
                      disabled={cancelingOrderId === order.id}
                    >
                      {cancelingOrderId === order.id ? 'Cancelling...' : 'Cancel order'}
                    </button>
                  )}
                </div>
              </div>
              <ul className="list-group list-group-flush order-items">
                {(order.items || []).map((item) => (
                  <li className="list-group-item d-flex justify-content-between" key={item.id}>
                    <span><span className="order-item-name">{item.productName || `Product #${item.productId}`}</span><span className="order-item-quantity"> × {item.quantity}</span></span>
                    <span className="order-item-price">${(Number(item.price || 0) * item.quantity).toFixed(2)}</span>
                  </li>
                ))}
              </ul>
            </article>
          ))}
        </div>
      )}
    </section>
  );
};

export default Orders;
