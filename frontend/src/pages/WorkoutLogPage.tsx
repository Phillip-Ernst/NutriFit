import WorkoutForm from '../components/workouts/WorkoutForm';
import Card from '../components/ui/Card';

export default function WorkoutLogPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Log a Workout</h1>
        <p className="text-gray-400 mt-1">Add the exercises you performed.</p>
      </div>
      <Card>
        <WorkoutForm />
      </Card>
    </div>
  );
}
