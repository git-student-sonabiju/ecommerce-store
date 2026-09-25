import axios from 'axios';

const API = axios.create({
    baseURL: 'http://localhost:8080/api',
});

// This interceptor automatically attaches the JWT token to every request
API.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default API;
