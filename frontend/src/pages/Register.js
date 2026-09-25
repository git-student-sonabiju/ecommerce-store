import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import API from '../api/axiosConfig.js';

const Register = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const response = await API.post('/auth/register', {
        username,
        email,
        password,
        firstName,
        lastName
      });

      navigate('/login', { replace: true });
    } catch (err) {
      setError(err.response?.data?.error || err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="auth-page">
      <div className="auth-layout">
        <aside className="auth-aside">
          <div><span className="page-kicker">A BETTER WAY TO SHOP</span><h2>Make yourself at home.</h2><p>Create an account to save your cart and find your orders whenever you need them.</p></div>
          <div className="auth-aside-art" aria-hidden="true">♡</div>
        </aside>
        <div className="auth-form-panel">
          <span className="page-kicker">JOIN THE STORE</span>
          <h1>Create an account</h1>
          <p className="auth-form-subtitle">A few details and you’re ready to browse.</p>
          {error && <div className="alert alert-danger" role="alert">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="row g-3">
              <div className="col-sm-6 mb-3">
                <label className="form-label" htmlFor="register-first-name">First name</label>
                <input id="register-first-name" type="text" className="form-control" autoComplete="given-name" value={firstName} onChange={(e) => setFirstName(e.target.value)} required />
              </div>
              <div className="col-sm-6 mb-3">
                <label className="form-label" htmlFor="register-last-name">Last name</label>
                <input id="register-last-name" type="text" className="form-control" autoComplete="family-name" value={lastName} onChange={(e) => setLastName(e.target.value)} required />
              </div>
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="register-username">Username</label>
              <input id="register-username" type="text" className="form-control" autoComplete="username" value={username} onChange={(e) => setUsername(e.target.value)} required />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="register-email">Email</label>
              <input id="register-email" type="email" className="form-control" autoComplete="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="register-password">Password</label>
              <input id="register-password" type="password" className="form-control" autoComplete="new-password" value={password} onChange={(e) => setPassword(e.target.value)} required />
            </div>
            <button type="submit" className="btn btn-primary w-100 mt-1" disabled={loading}>
              {loading ? 'Creating account...' : 'Create account'}
            </button>
          </form>
          <p className="auth-form-footer">Already have an account? <Link to="/login">Sign in</Link></p>
        </div>
      </div>
    </section>
  );
};

export default Register;
