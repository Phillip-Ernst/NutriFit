import Card from '../components/ui/Card';

export default function WorkoutsPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Workouts</h1>
        <p className="text-gray-400 mt-1">Track and manage your workout sessions.</p>
      </div>
      <Card className="text-center py-12">
        <div className="inline-block bg-emerald-500/10 rounded-full p-4 mb-4">
          <svg className="w-10 h-10 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M13 10V3L4 14h7v7l9-11h-7z" />
          </svg>
        </div>
        <p className="text-xl font-semibold text-white mb-2">Coming Soon</p>
        <p className="text-gray-400 max-w-sm mx-auto">
          Workout tracking is under development. You'll be able to log exercises, sets, reps, and monitor your training progress.
        </p>
      </Card>
    </div>
  );
}
