import { useAuth } from '../hooks/useAuth';
import { useMyMeals } from '../hooks/useMeals';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';

export default function ProfilePage() {
  const { username, logout } = useAuth();
  const { data: meals } = useMyMeals();

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-white">Profile</h1>

      <Card className="space-y-4">
        <div className="flex items-center gap-4">
          <div className="w-14 h-14 rounded-full bg-emerald-500/20 flex items-center justify-center">
            <span className="text-2xl font-bold text-emerald-400">
              {username?.charAt(0).toUpperCase()}
            </span>
          </div>
          <div>
            <p className="text-lg font-semibold text-white">{username}</p>
            <p className="text-sm text-gray-400">
              {meals ? `${meals.length} meal${meals.length !== 1 ? 's' : ''} logged` : ''}
            </p>
          </div>
        </div>

        <div className="border-t border-gray-800 pt-4">
          <Button variant="danger" onClick={logout}>
            Logout
          </Button>
        </div>
      </Card>
    </div>
  );
}
