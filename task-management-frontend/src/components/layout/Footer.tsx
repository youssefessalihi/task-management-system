import { Github, Linkedin, Mail } from 'lucide-react';

const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-gradient-to-r from-gray-800 to-gray-900 border-t-2 border-gray-700 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="text-center md:text-left">
            <p className="text-sm text-gray-300">
              © {currentYear} Task Management. Developed by{' '}
              <span className="font-semibold text-white">Youssef Essalihi</span>
            </p>
            <p className="text-xs text-gray-400 mt-1">
              Full-Stack Internship Project - Spring Boot & React
            </p>
          </div>

          <div className="flex items-center gap-4">
            <a
              href="https://github.com/youssefessalihi"
              target="_blank"
              rel="noopener noreferrer"
              className="text-gray-400 hover:text-white transition-all p-2 hover:bg-gray-700 rounded-lg"
              title="GitHub Profile"
            >
              <Github size={20} />
            </a>

            <a
              href="https://linkedin.com/in/youssefessalihi"
              target="_blank"
              rel="noopener noreferrer"
              className="text-gray-400 hover:text-white transition-all p-2 hover:bg-gray-700 rounded-lg"
              title="LinkedIn Profile"
            >
              <Linkedin size={20} />
            </a>

            <a
              href="mailto:youssefessalihi@hotmail.com"
              className="text-gray-400 hover:text-white transition-all p-2 hover:bg-gray-700 rounded-lg"
              title="Email"
            >
              <Mail size={20} />
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
