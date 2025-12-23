import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { LogOut, User } from 'lucide-react';
import Logo from '../ui/Logo';

const Navbar = () => {
  const { user, logout } = useAuth();

  return (
    <nav className="bg-gradient-to-r from-gray-50 to-gray-100 shadow-md border-b-2 border-gray-200 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <Link to="/dashboard" className="hover:opacity-80 transition-opacity">
              <Logo size="sm" />
            </Link>
          </div>

          <div className="flex items-center gap-4">
            <div className="hidden sm:flex items-center gap-3 px-4 py-2 bg-white rounded-lg border border-gray-200 shadow-sm">
<div className="w-9 h-9 bg-gradient-to-br from-gray-800 to-gray-900 rounded-lg flex items-center justify-center shadow-md border border-gray-700">                <User size={18} className="text-white" />
              </div>
              <div className="flex flex-col">
                <span className="text-sm font-semibold text-gray-900 leading-tight">
                  {user?.name}
                </span>
                <span className="text-xs text-gray-500">
                  {user?.email}
                </span>
              </div>
            </div>
            
            <button
              onClick={logout}
className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-100 bg-gradient-to-r from-gray-800 to-gray-900 rounded-lg hover:from-gray-700 hover:to-gray-800 transition-all shadow-md hover:shadow-lg border border-gray-700">              <LogOut size={16} />
              <span className="hidden sm:inline">Logout</span>
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
