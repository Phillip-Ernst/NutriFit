import { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

const links = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/meals/log', label: 'Log Meal' },
  { to: '/meals/history', label: 'History' },
  { to: '/workouts', label: 'Workouts' },
];

export default function Navbar() {
  const { username, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <nav className="bg-gray-900 border-b border-gray-800 sticky top-0 z-40">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <NavLink to="/dashboard" className="text-emerald-400 font-bold text-xl tracking-tight">
            NutriFit
          </NavLink>

          {/* Desktop nav */}
          <div className="hidden md:flex items-center gap-1">
            {links.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                className={({ isActive }) =>
                  `px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                    isActive
                      ? 'text-emerald-400 bg-gray-800'
                      : 'text-gray-300 hover:text-white hover:bg-gray-800'
                  }`
                }
              >
                {link.label}
              </NavLink>
            ))}
          </div>

          <div className="hidden md:flex items-center gap-4">
            <NavLink
              to="/profile"
              className={({ isActive }) =>
                `text-sm font-medium transition-colors ${
                  isActive ? 'text-emerald-400' : 'text-gray-300 hover:text-white'
                }`
              }
            >
              {username}
            </NavLink>
            <button
              onClick={logout}
              className="text-sm text-gray-400 hover:text-white transition-colors"
            >
              Logout
            </button>
          </div>

          {/* Mobile hamburger */}
          <button
            onClick={() => setMenuOpen(!menuOpen)}
            className="md:hidden text-gray-400 hover:text-white"
          >
            <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              {menuOpen ? (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              ) : (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              )}
            </svg>
          </button>
        </div>
      </div>

      {/* Mobile menu */}
      {menuOpen && (
        <div className="md:hidden border-t border-gray-800 px-4 pb-4 pt-2 space-y-1">
          {links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              onClick={() => setMenuOpen(false)}
              className={({ isActive }) =>
                `block px-3 py-2 rounded-md text-sm font-medium ${
                  isActive
                    ? 'text-emerald-400 bg-gray-800'
                    : 'text-gray-300 hover:text-white hover:bg-gray-800'
                }`
              }
            >
              {link.label}
            </NavLink>
          ))}
          <div className="border-t border-gray-800 pt-2 mt-2 flex items-center justify-between">
            <NavLink
              to="/profile"
              onClick={() => setMenuOpen(false)}
              className="text-sm text-gray-300 hover:text-white"
            >
              {username}
            </NavLink>
            <button onClick={logout} className="text-sm text-gray-400 hover:text-white">
              Logout
            </button>
          </div>
        </div>
      )}
    </nav>
  );
}
