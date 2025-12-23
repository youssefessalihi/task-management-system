import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useAuth } from '../context/AuthContext';
import { Mail, User, UserPlus, Eye, EyeOff, Lock, Check, X, AlertCircle } from 'lucide-react';
import Logo from '../components/ui/Logo';
import type { RegisterRequest } from '../types';

const Register = () => {
  const { register: registerUser, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [apiError, setApiError] = useState<string>('');
  const { register, handleSubmit, watch, formState: { errors, isSubmitted, isSubmitting } } = useForm<RegisterRequest>({
    mode: 'onSubmit',
  });

  const password = watch('password') || '';

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/dashboard', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const getPasswordStrength = (pwd: string) => {
    const checks = {
      length: pwd.length >= 8,
      uppercase: /[A-Z]/.test(pwd),
      lowercase: /[a-z]/.test(pwd),
      number: /[0-9]/.test(pwd),
      special: /[!@#$%^&*(),.?":{}|<>]/.test(pwd),
    };
    const strength = Object.values(checks).filter(Boolean).length;
    return { strength, checks };
  };

  const { strength, checks } = password ? getPasswordStrength(password) : { strength: 0, checks: {} };

  const validatePassword = (value: string) => {
    if (value.length < 8) return 'Password must be at least 8 characters';
    if (!/[A-Z]/.test(value)) return 'Password must contain at least one uppercase letter';
    if (!/[a-z]/.test(value)) return 'Password must contain at least one lowercase letter';
    if (!/[0-9]/.test(value)) return 'Password must contain at least one number';
    if (!/[!@#$%^&*(),.?":{}|<>]/.test(value)) return 'Password must contain at least one special character';
    return true;
  };

  const onSubmit = async (data: RegisterRequest) => {
    if (loading) return;
    
    setLoading(true);
    setApiError('');
    
    try {
      await registerUser(data);
    } catch (error: any) {
      const status = error.response?.status;
      const message = error.response?.data?.message || '';
      
      if (status === 409 || message?.includes('already exists')) {
        setApiError('Email already in use. Please use a different email or login.');
      } else if (status === 400) {
        setApiError(message || 'Invalid information. Please check all fields.');
      } else if (message) {
        setApiError(message);
      } else {
        setApiError('Registration failed. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-indigo-50 px-4 py-12">
      <div className="max-w-md w-full">
        <div className="bg-white rounded-2xl shadow-xl p-8 border border-gray-100">
          <div className="flex justify-center mb-8">
            <Logo size="lg" />
          </div>

          <div className="text-center mb-8">
            <h2 className="text-2xl font-bold text-gray-900 mb-2">
              Create Account
            </h2>
            <p className="text-gray-600">Join Task Management System to manage your projects</p>
          </div>

          {apiError && (
            <div className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded-r-lg flex items-start gap-3">
              <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
              <div className="flex-1">
                <p className="text-sm font-medium text-red-800">{apiError}</p>
                {apiError.includes('already in use') && (
                  <Link 
                    to="/login" 
                    className="text-sm text-red-600 hover:text-red-700 font-semibold underline mt-1 inline-block"
                  >
                    Sign in instead
                  </Link>
                )}
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Full Name
              </label>
              <div className="relative">
                <User className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
                <input
                  {...register('name', { required: 'Name is required' })}
                  type="text"
                  className={`w-full pl-11 pr-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all ${
                    errors.name && isSubmitted && !isSubmitting ? 'border-red-500' : 'border-gray-300'
                  }`}
                  placeholder="Youssef Essalihi"
                  onChange={() => setApiError('')}
                />
              </div>
              {errors.name && isSubmitted && !isSubmitting && (
                <p className="mt-1 text-sm text-red-600 flex items-center gap-1">
                  <X size={14} />
                  {errors.name.message}
                </p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Email Address
              </label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
                <input
                  {...register('email', {
                    required: 'Email is required',
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: 'Invalid email address',
                    },
                  })}
                  type="email"
                  className={`w-full pl-11 pr-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all ${
                    errors.email && isSubmitted && !isSubmitting ? 'border-red-500' : 'border-gray-300'
                  }`}
                  placeholder="you@example.com"
                  onChange={() => setApiError('')}
                />
              </div>
              {errors.email && isSubmitted && !isSubmitting && (
                <p className="mt-1 text-sm text-red-600 flex items-center gap-1">
                  <X size={14} />
                  {errors.email.message}
                </p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Password
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
                <input
                  {...register('password', {
                    required: 'Password is required',
                    validate: validatePassword,
                  })}
                  type={showPassword ? 'text' : 'password'}
                  className={`w-full pl-11 pr-12 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all ${
                    errors.password && isSubmitted && !isSubmitting ? 'border-red-500' : 'border-gray-300'
                  }`}
                  placeholder="Create a strong password"
                  onChange={() => setApiError('')}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
                  tabIndex={-1}
                >
                  {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
              </div>
              {errors.password && isSubmitted && !isSubmitting && (
                <p className="mt-1 text-sm text-red-600 flex items-center gap-1">
                  <X size={14} />
                  {errors.password.message}
                </p>
              )}

              {password && (
                <div className="mt-3 space-y-2">
                  <div className="flex items-center gap-2">
                    <div className="flex-1 h-2 bg-gray-200 rounded-full overflow-hidden">
                      <div
                        className={`h-full transition-all duration-300 ${
                          strength <= 1 ? 'bg-red-500' : 
                          strength === 2 ? 'bg-orange-500' : 
                          strength === 3 ? 'bg-yellow-500' : 
                          strength === 4 ? 'bg-blue-500' : 
                          'bg-green-500'
                        }`}
                        style={{ width: `${(strength / 5) * 100}%` }}
                      />
                    </div>
                    <span className={`text-xs font-medium ${
                      strength <= 1 ? 'text-red-600' : 
                      strength === 2 ? 'text-orange-600' : 
                      strength === 3 ? 'text-yellow-600' : 
                      strength === 4 ? 'text-blue-600' : 
                      'text-green-600'
                    }`}>
                      {strength <= 1 ? 'Weak' : 
                       strength === 2 ? 'Fair' : 
                       strength === 3 ? 'Good' : 
                       strength === 4 ? 'Strong' : 
                       'Excellent'}
                    </span>
                  </div>

                  <div className="grid grid-cols-2 gap-2 text-xs">
                    <div className={`flex items-center gap-1 ${checks.length ? 'text-green-600' : 'text-gray-400'}`}>
                      {checks.length ? <Check size={12} /> : <X size={12} />}
                      <span>8+ characters</span>
                    </div>
                    <div className={`flex items-center gap-1 ${checks.uppercase ? 'text-green-600' : 'text-gray-400'}`}>
                      {checks.uppercase ? <Check size={12} /> : <X size={12} />}
                      <span>Uppercase</span>
                    </div>
                    <div className={`flex items-center gap-1 ${checks.lowercase ? 'text-green-600' : 'text-gray-400'}`}>
                      {checks.lowercase ? <Check size={12} /> : <X size={12} />}
                      <span>Lowercase</span>
                    </div>
                    <div className={`flex items-center gap-1 ${checks.number ? 'text-green-600' : 'text-gray-400'}`}>
                      {checks.number ? <Check size={12} /> : <X size={12} />}
                      <span>Number</span>
                    </div>
                    <div className={`flex items-center gap-1 col-span-2 ${checks.special ? 'text-green-600' : 'text-gray-400'}`}>
                      {checks.special ? <Check size={12} /> : <X size={12} />}
                      <span>Special character</span>
                    </div>
                  </div>
                </div>
              )}
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-gradient-to-r from-blue-600 to-blue-700 text-white py-3 rounded-lg font-semibold hover:from-blue-700 hover:to-blue-800 disabled:opacity-50 transition-all shadow-md hover:shadow-lg flex items-center justify-center gap-2"
            >
              {loading ? (
                <>
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                  <span>Creating account...</span>
                </>
              ) : (
                <>
                  <UserPlus size={20} />
                  <span>Create Account</span>
                </>
              )}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              Already have an account?{' '}
              <Link to="/login" className="text-blue-600 font-semibold hover:text-blue-700 transition-colors">
                Sign in instead
              </Link>
            </p>
          </div>
        </div>

        <p className="text-center text-sm text-gray-500 mt-6">
          Developed by Youssef Essalihi - Internship Project
        </p>
      </div>
    </div>
  );
};

export default Register;
