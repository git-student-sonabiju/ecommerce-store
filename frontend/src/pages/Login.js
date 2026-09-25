import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import API from '../api/axiosConfig.js';

const Login = () => {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const response = await API.post('/auth/login', {
        usernameOrEmail,
        password
      });

      const data = response.data;
      // Store token in localStorage
      localStorage.setItem('token', data.token);
      window.dispatchEvent(new Event('auth-changed'));
      // Redirect to home or previous page
      navigate('/', { replace: true });
    } catch (err) {
      setError(err.response?.data?.error || err.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="auth-page">
      <div className="auth-layout">
        <aside className="auth-aside">
          <div><span className="page-kicker">WELCOME BACK</span><h2>Your good finds are just around the corner.</h2><p>Sign in to manage your cart and keep up with your orders.</p></div>
          <div className="auth-aside-art" aria-hidden="true">✦</div>
        </aside>
        <div className="auth-form-panel">
          <span className="page-kicker">YOUR SHOPPING SPACE</span>
          <h1>Sign in</h1>
          <p className="auth-form-subtitle">Enter your account details to continue.</p>
          {error && <div className="alert alert-danger" role="alert">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label" htmlFor="login-username">Username or email</label>
              <input id="login-username" type="text" className="form-control" autoComplete="username" value={usernameOrEmail} onChange={(e) => setUsernameOrEmail(e.target.value)} required />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="login-password">Password</label>
              <input id="login-password" type="password" className="form-control" autoComplete="current-password" value={password} onChange={(e) => setPassword(e.target.value)} required />
            </div>
            <button type="submit" className="btn btn-primary w-100 mt-2" disabled={loading}>
              {loading ? 'Signing in...' : 'Sign in'}
            </button>
          </form>
          <p className="auth-form-footer">New here? <Link to="/register">Create an account</Link></p>
        </div>
      </div>
    </section>
  );
};

export default Login;
