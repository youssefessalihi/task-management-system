import { CheckSquare } from 'lucide-react';

interface LogoProps {
  size?: 'sm' | 'md' | 'lg';
  showText?: boolean;
}

const Logo = ({ size = 'md', showText = true }: LogoProps) => {
  const sizes = {
    sm: { icon: 24, text: 'text-lg' },
    md: { icon: 32, text: 'text-xl' },
    lg: { icon: 40, text: 'text-2xl' },
  };

  return (
    <div className="flex items-center gap-3">
      <div className="bg-gradient-to-br from-gray-800 to-gray-900 p-2 rounded-xl shadow-lg">
        <CheckSquare 
          size={sizes[size].icon} 
          className="text-white" 
          strokeWidth={2.5}
        />
      </div>
      {showText && (
        <div className="flex flex-col">
          <span className={`font-bold text-gray-900 leading-tight ${sizes[size].text}`}>
            Task Management
          </span>
          <span className="text-xs text-gray-500 font-medium">
            Project Management
          </span>
        </div>
      )}
    </div>
  );
};

export default Logo;
