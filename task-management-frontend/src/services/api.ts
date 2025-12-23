import axios from 'axios';
import type {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  Project,
  CreateProjectRequest,
  UpdateProjectRequest,
  Task,
  CreateTaskRequest,
  UpdateTaskRequest,
  ApiResponse,
} from '../types';

const BASE_URL = 'http://localhost:8080/api/v1';

const axiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const currentPath = window.location.pathname;
      if (currentPath !== '/login' && currentPath !== '/register') {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await axiosInstance.post<ApiResponse<AuthResponse>>('/auth/login', data);
    return response.data.data;
  },
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await axiosInstance.post<ApiResponse<AuthResponse>>('/auth/register', data);
    return response.data.data;
  },
};

export const projectsAPI = {
  getAll: async (): Promise<Project[]> => {
    const response = await axiosInstance.get<ApiResponse<Project[]>>('/projects');
    return response.data.data;
  },
  getById: async (id: number): Promise<Project> => {
    const response = await axiosInstance.get<ApiResponse<Project>>(`/projects/${id}`);
    return response.data.data;
  },
  create: async (data: CreateProjectRequest): Promise<Project> => {
    const response = await axiosInstance.post<ApiResponse<Project>>('/projects', data);
    return response.data.data;
  },
  update: async (id: number, data: UpdateProjectRequest): Promise<Project> => {
    const response = await axiosInstance.put<ApiResponse<Project>>(`/projects/${id}`, data);
    return response.data.data;
  },
  delete: async (id: number): Promise<void> => {
    await axiosInstance.delete(`/projects/${id}`);
  },
  getProgress: async (id: number): Promise<any> => {
    const response = await axiosInstance.get<ApiResponse<any>>(`/projects/${id}/progress`);
    return response.data.data;
  },
};

export const tasksAPI = {
  getAll: async (projectId: number): Promise<Task[]> => {
    const response = await axiosInstance.get<ApiResponse<Task[]>>(`/projects/${projectId}/tasks`);
    return response.data.data;
  },
  getById: async (projectId: number, taskId: number): Promise<Task> => {
    const response = await axiosInstance.get<ApiResponse<Task>>(`/projects/${projectId}/tasks/${taskId}`);
    return response.data.data;
  },
  create: async (projectId: number, data: CreateTaskRequest): Promise<Task> => {
    const response = await axiosInstance.post<ApiResponse<Task>>(`/projects/${projectId}/tasks`, data);
    return response.data.data;
  },
  update: async (projectId: number, taskId: number, data: UpdateTaskRequest): Promise<Task> => {
    const response = await axiosInstance.put<ApiResponse<Task>>(`/projects/${projectId}/tasks/${taskId}`, data);
    return response.data.data;
  },
  markComplete: async (projectId: number, taskId: number): Promise<Task> => {
    const response = await axiosInstance.patch<ApiResponse<Task>>(`/projects/${projectId}/tasks/${taskId}/complete`);
    return response.data.data;
  },
  delete: async (projectId: number, taskId: number): Promise<void> => {
    await axiosInstance.delete(`/projects/${projectId}/tasks/${taskId}`);
  },
};

export default axiosInstance;
