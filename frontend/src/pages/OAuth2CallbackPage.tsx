import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { isTokenExpired } from '../context/AuthContext';
import LoadingSpinner from '../components/ui/LoadingSpinner';

/**
 * Handles the redirect from the backend after a successful OAuth2 login.
 * The backend redirects here with ?token=<jwt>.
 * Stores the token and username in localStorage (same keys as password login),
 * then navigates to the dashboard.
 */
export default function OAuth2CallbackPage() {
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');

    if (!token || isTokenExpired(token)) {
      navigate('/login', { replace: true });
      return;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const username = payload.sub as string;

      if (!username) {
        navigate('/login', { replace: true });
        return;
      }

      localStorage.setItem('token', token);
      localStorage.setItem('username', username);

      // Full page navigation so AuthProvider re-reads localStorage on mount
      window.location.replace('/dashboard');
    } catch {
      navigate('/login', { replace: true });
    }
  }, [navigate]);

  return (
    <div className="min-h-screen bg-gray-950 flex items-center justify-center">
      <LoadingSpinner />
    </div>
  );
}
