import { Routes, Route } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import AppLayout from '../components/layout/AppLayout';
import LandingPage from '../pages/LandingPage';
import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';
import DashboardPage from '../pages/DashboardPage';
import MealLogPage from '../pages/MealLogPage';
import MealHistoryPage from '../pages/MealHistoryPage';
import WorkoutsPage from '../pages/WorkoutsPage';
import WorkoutLogPage from '../pages/WorkoutLogPage';
import WorkoutHistoryPage from '../pages/WorkoutHistoryPage';
import WorkoutPlansPage from '../pages/WorkoutPlansPage';
import CreatePlanPage from '../pages/CreatePlanPage';
import PlanDetailPage from '../pages/PlanDetailPage';
import EditPlanPage from '../pages/EditPlanPage';
import ExecuteWorkoutPage from '../pages/ExecuteWorkoutPage';
import ProfilePage from '../pages/ProfilePage';
import NotFoundPage from '../pages/NotFoundPage';

export default function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/meals/log" element={<MealLogPage />} />
          <Route path="/meals/history" element={<MealHistoryPage />} />
          <Route path="/workouts" element={<WorkoutsPage />} />
          <Route path="/workouts/log" element={<WorkoutLogPage />} />
          <Route path="/workouts/history" element={<WorkoutHistoryPage />} />
          <Route path="/workouts/plans" element={<WorkoutPlansPage />} />
          <Route path="/workouts/plans/new" element={<CreatePlanPage />} />
          <Route path="/workouts/plans/:id" element={<PlanDetailPage />} />
          <Route path="/workouts/plans/:id/edit" element={<EditPlanPage />} />
          <Route path="/workouts/execute/:dayId" element={<ExecuteWorkoutPage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Route>
      </Route>

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
