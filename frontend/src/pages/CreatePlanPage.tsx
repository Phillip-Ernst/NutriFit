import Card from '../components/ui/Card';
import WorkoutPlanForm from '../components/workoutPlans/WorkoutPlanForm';

export default function CreatePlanPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Create Workout Plan</h1>
        <p className="text-gray-400 mt-1">Build a reusable workout template with multiple days.</p>
      </div>
      <Card>
        <WorkoutPlanForm />
      </Card>
    </div>
  );
}
