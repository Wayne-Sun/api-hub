import axios from 'axios';
import type { BaseResponse } from '@/types/api';

const apiClient = axios.create({
  baseURL: '',  // Vite proxy handles /v1/*
  timeout: 30000,
});

// Response interceptor — unwrap BaseResponse
apiClient.interceptors.response.use(
  (response) => {
    const data = response.data as BaseResponse;
    if (data.code !== 200) {
      return Promise.reject(new Error(data.message || 'Request failed'));
    }
    return response;
  },
  (error) => {
    return Promise.reject(error);
  },
);

export default apiClient;
