import { Link } from 'react-router-dom';
import Button from '../components/ui/Button';

export default function NotFoundPage() {
  return (
    <div className="min-h-screen bg-gray-950 flex items-center justify-center px-4">
      <div className="text-center">
        <p className="text-7xl font-bold text-emerald-400">404</p>
        <h1 className="text-2xl font-bold text-white mt-4">Page not found</h1>
        <p className="text-gray-400 mt-2 mb-6">The page you're looking for doesn't exist.</p>
        <Link to="/">
          <Button>Go Home</Button>
        </Link>
      </div>
    </div>
  );
}
