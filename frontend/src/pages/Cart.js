import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import API from '../api/axiosConfig';

const Cart = () => {
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [removingItemId, setRemovingItemId] = useState(null);
  const [actionError, setActionError] = useState(null);
  const [quantities, setQuantities] = useState({});
  const [savingCart, setSavingCart] = useState(false);
  const [checkingOut, setCheckingOut] = useState(false);
  const [orderMessage, setOrderMessage] = useState(null);

  const loadItems = (items) => {
    setCartItems(items);
    setQuantities(Object.fromEntries(items.map((item) => [item.id, item.quantity])));
  };

  const updateCart = async () => {
    setSavingCart(true);
    setActionError(null);
    try {
      let latestCart = null;
      for (const item of cartItems) {
        const quantity = Number(quantities[item.id]);
        if (!Number.isInteger(quantity) || quantity < 1) {
          throw new Error('Each quantity must be a whole number of at least 1.');
        }
        if (quantity !== item.quantity) {
          const response = await API.put(`/cart/items/${item.id}`, { productId: item.productId, quantity });
          latestCart = response.data;
        }
      }
      if (latestCart?.items) loadItems(latestCart.items);
      setOrderMessage({ type: 'success', text: 'Cart updated.' });
    } catch (err) {
      setActionError(err.response?.data?.error || err.response?.data?.message || err.message || 'Failed to update cart.');
    } finally {
      setSavingCart(false);
    }
  };

  const checkout = async () => {
    setCheckingOut(true);
    setActionError(null);
    setOrderMessage(null);
    try {
      const items = cartItems.map((item) => {
        const quantity = Number(quantities[item.id] ?? item.quantity);
        if (!Number.isInteger(quantity) || quantity < 1) {
          throw new Error('Each quantity must be a whole number of at least 1.');
        }
        return { productId: item.productId, quantity };
      });
      const response = await API.post('/orders', { items });
      const orderNumber = response.data.orderNumber ?? response.data.id;
      setOrderMessage({ type: 'success', text: `Order #${orderNumber} placed successfully.` });
      try {
        await API.delete('/cart');
        loadItems([]);
      } catch (clearError) {
        setOrderMessage({ type: 'warning', text: `Order #${orderNumber} was placed, but the cart could not be cleared. Remove its items before ordering again.` });
      }
    } catch (err) {
      setActionError(err.response?.data?.error || err.response?.data?.message || 'Could not complete checkout.');
    } finally {
      setCheckingOut(false);
    }
  };

  const removeItem = async (itemId) => {
    setRemovingItemId(itemId);
    setActionError(null);
    try {
      await API.delete(`/cart/items/${itemId}`);
      loadItems(cartItems.filter((item) => item.id !== itemId));
    } catch (err) {
      setActionError(err.response?.data?.error || err.response?.data?.message || 'Failed to remove item from cart.');
    } finally {
      setRemovingItemId(null);
    }
  };

  useEffect(() => {
    const fetchCart = async () => {
      try {
        setLoading(true);
        const response = await API.get('/cart');
        // The backend returns a CartDto object { id, user, items: [...] }
        // We only need the items array for our state
        if (response.data && response.data.items) {
          loadItems(response.data.items);
        } else {
          loadItems([]);
        }
        setLoading(false);
      } catch (err) {
        setError(err.response?.data?.error || err.message || 'Failed to fetch cart');
        setLoading(false);
      }
    };

    fetchCart();
  }, []);

  if (loading) return <div className="text-center py-5">Loading cart...</div>;
  if (error) return <div className="text-center py-5 text-danger">Error loading cart: {error}</div>;

  return (
    <section className="cart-page">
      <header className="page-heading">
        <div>
          <span className="page-kicker">YOUR PICKS, ALL IN ONE PLACE</span>
          <h1>Shopping cart</h1>
          <p className="page-subtitle">Review your items and adjust quantities before checkout.</p>
        </div>
        <Link to="/products" className="btn btn-outline-primary d-none d-sm-inline-flex">← Keep browsing</Link>
      </header>
      {actionError && <div className="alert alert-danger" role="alert">{actionError}</div>}
      {orderMessage && <div className={`alert alert-${orderMessage.type}`} role="status">{orderMessage.text}</div>}
      {cartItems.length === 0 ? (
        <div className="empty-state">
          <span className="empty-state-icon">♡</span>
          <h2 className="h4">Your cart is taking a breather</h2>
          <p className="text-muted mb-3">Find something good and it’ll show up here.</p>
          <Link to="/products" className="btn btn-primary">Explore products</Link>
        </div>
      ) : (
        <div className="cart-panel">
          <div className="cart-table-wrap"><table className="table cart-table">
            <thead>
              <tr>
                <th>Product</th>
                <th>Price</th>
                <th>Quantity</th>
                <th>Total</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {cartItems.map(item => (
                <tr key={item.id}>
                  <td><span className="cart-product-name">{item.productName || item.product?.name || 'Unknown Product'}</span><br /><span className="cart-product-meta">Ready for your next order</span></td>
                  <td>${item.price ?? item.product?.price ?? '0.00'}</td>
                  <td>
                    <input
                      aria-label={`Quantity for ${item.productName || item.product?.name || 'product'}`}
                      type="number"
                      min="1"
                      className="form-control cart-quantity"
                      value={quantities[item.id] ?? item.quantity}
                      onChange={(event) => setQuantities((values) => ({ ...values, [item.id]: event.target.value }))}
                    />
                  </td>
                  <td className="fw-bold">${(Number(item.price ?? item.product?.price ?? 0) * Number(quantities[item.id] ?? item.quantity)).toFixed(2)}</td>
                  <td>
                    <button
                      className="btn btn-sm btn-outline-danger"
                      onClick={() => removeItem(item.id)}
                      disabled={removingItemId === item.id}
                    >
                      {removingItemId === item.id ? 'Removing...' : 'Remove'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table></div>
          <div className="cart-summary">
            <div className="cart-actions">
              <button className="btn btn-outline-secondary" onClick={updateCart} disabled={savingCart || checkingOut}>
                {savingCart ? 'Updating...' : 'Update Cart'}
              </button>
            </div>
            <div className="text-end">
              <span className="cart-total-label">Order total</span>
              <span className="cart-total-value">${cartItems.reduce((acc, item) => acc + (Number(item.price ?? item.product?.price ?? 0) * Number(quantities[item.id] ?? item.quantity)), 0).toFixed(2)}</span>
              <button className="btn btn-primary ms-3" onClick={checkout} disabled={checkingOut || savingCart}>
                {checkingOut ? 'Placing Order...' : 'Proceed to Checkout'}
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  );
};

export default Cart;
