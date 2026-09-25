import React from 'react';
import { Link } from 'react-router-dom';
import './Home.css';

const Home = () => (
  <div className="store-home">
    <section className="home-hero">
      <div className="hero-copy">
        <span className="hero-eyebrow"><span className="hero-dot" /> YOUR NEXT FAVORITE THING IS HERE</span>
        <h1>Good finds.<br /><span>Great feeling.</span></h1>
        <p>Fresh tech, everyday essentials, and little upgrades that make a big difference. Find something you’ll love.</p>
        <div className="hero-actions">
          <Link to="/products" className="home-primary-button">Explore the shop <span aria-hidden="true">→</span></Link>
          <Link to="/orders" className="home-secondary-link">Track an order</Link>
        </div>
        <div className="hero-proof"><span className="proof-stars">★★★★★</span><span>Thoughtful picks, easy shopping</span></div>
      </div>
      <div className="hero-art" aria-label="Featured products: laptop, headphones, and smart watch" role="img">
        <div className="hero-orbit orbit-one" />
        <div className="hero-orbit orbit-two" />
        <div className="hero-product product-laptop"><span>▰</span><small>WORK & PLAY</small></div>
        <div className="hero-product product-headphones"><span>◖◗</span><small>FIND YOUR FLOW</small></div>
        <div className="hero-product product-watch"><span>◷</span></div>
        <div className="hero-note"><span>✦</span> A little upgrade goes a long way</div>
      </div>
    </section>

    <section className="home-benefits" aria-label="Shopping benefits">
      <div><span className="benefit-icon">✦</span><p><strong>Picked with care</strong><small>Quality finds for everyday life</small></p></div>
      <div><span className="benefit-icon">↗</span><p><strong>Easy, secure checkout</strong><small>A smooth way to shop</small></p></div>
      <div><span className="benefit-icon">♡</span><p><strong>Here when you need us</strong><small>Shopping should feel simple</small></p></div>
    </section>

  </div>
);

export default Home;
