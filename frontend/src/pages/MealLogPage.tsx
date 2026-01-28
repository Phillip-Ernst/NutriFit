import MealForm from '../components/meals/MealForm';
import Card from '../components/ui/Card';

export default function MealLogPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Log a Meal</h1>
        <p className="text-gray-400 mt-1">Add the foods you ate and their nutritional info.</p>
      </div>
      <Card>
        <MealForm />
      </Card>
    </div>
  );
}
