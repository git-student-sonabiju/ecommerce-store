import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import API from '../api/axiosConfig';

const coffeeMachineImage = 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a6/Modern_coffee_machine_%28Unsplash_aw6UewokOIo%29.jpg/960px-Modern_coffee_machine_%28Unsplash_aw6UewokOIo%29.jpg';

const Products = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [addingProductId, setAddingProductId] = useState(null);
  const [cartMessage, setCartMessage] = useState(null);

  const addToCart = async (productId) => {
    setCartMessage(null);
    if (!localStorage.getItem('token')) {
      setCartMessage({ type: 'warning', text: 'Please sign in to add items to your cart.' });
      return;
    }

    setAddingProductId(productId);
    try {
      await API.post('/cart/items', { productId, quantity: 1 });
      setCartMessage({ type: 'success', text: 'Item added to your cart.' });
    } catch (err) {
      const status = err.response?.status;
      const message = status === 401
        ? 'Your session has expired. Please sign in again.'
        : err.response?.data?.error || err.response?.data?.message || 'Could not add this item to your cart.';
      setCartMessage({ type: 'danger', text: message });
    } finally {
      setAddingProductId(null);
    }
  };

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true);
        const response = await API.get('/products');
        setProducts(response.data);
        setLoading(false);
      } catch (err) {
        setError(err.response?.data?.error || err.message || 'Failed to fetch products');
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  if (loading) return <div className="text-center py-5">Loading products...</div>;
  if (error) return <div className="text-center py-5 text-danger">Error loading products: {error}</div>;

  return (
    <section className="products-page">
      <header className="page-heading">
        <div>
          <span className="page-kicker">MADE FOR EVERYDAY</span>
          <h1>Explore the shop</h1>
          <p className="page-subtitle">Thoughtful picks for work, play, and everything in between.</p>
        </div>
        <Link to="/cart" className="btn btn-outline-primary d-none d-sm-inline-flex">View cart <span className="ms-2">→</span></Link>
      </header>
      {cartMessage && (
        <div className={`alert alert-${cartMessage.type}`} role="status">
          {cartMessage.text}{' '}
          {cartMessage.type === 'warning' && <Link to="/login">Sign in</Link>}
        </div>
      )}
      {products.length === 0 ? (
        <div className="empty-state">
          <span className="empty-state-icon">✦</span>
          <h2 className="h4">Nothing on the shelves yet</h2>
          <p className="text-muted mb-0">Check back soon for new products.</p>
        </div>
      ) : (
        <div className="row row-cols-1 row-cols-sm-2 row-cols-lg-3 g-4 product-grid">
          {products.map(product => (
            <div key={product.id} className="col">
              <article className="card h-100 product-card">
                <div className="product-image-wrap">
                  <img
                    src={product.name === 'Coffee Machine' ? coffeeMachineImage : product.imageUrl?.trim() || '/product-placeholder.svg'}
                    onError={(event) => {
                      event.currentTarget.onerror = null;
                      event.currentTarget.src = '/product-placeholder.svg';
                    }}
                    className="card-img-top"
                    alt={product.name}
                  />
                </div>
                <div className="card-body d-flex flex-column">
                  <span className="product-category">{product.categoryName || 'Curated find'}</span>
                  <h5 className="card-title">{product.name}</h5>
                  {product.description && <p className="product-description">{product.description}</p>}
                  <p className="product-price">${Number(product.price).toFixed(2)}</p>
                  <div className="mt-auto">
                    <button
                      className="btn btn-outline-primary w-100"
                      onClick={() => addToCart(product.id)}
                      disabled={addingProductId === product.id}
                    >
                      {addingProductId === product.id ? 'Adding...' : 'Add to Cart'}
                    </button>
                  </div>
                </div>
              </article>
            </div>
          ))}
        </div>
      )}
    </section>
  );
};

export default Products;
