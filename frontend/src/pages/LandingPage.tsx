import { Link } from 'react-router-dom';

const features = [
  {
    title: 'Log Your Meals',
    description: 'Quickly log what you eat with detailed macro breakdowns for every food item.',
    icon: (
      <svg className="w-8 h-8 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 6v6l4 2m6-2a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
    ),
  },
  {
    title: 'Track Your Macros',
    description: 'See your protein, carbs, and fats at a glance with visual breakdowns and daily totals.',
    icon: (
      <svg className="w-8 h-8 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6m6 0h6m-6 0V9a2 2 0 012-2h2a2 2 0 012 2v10m6 0v-4a2 2 0 00-2-2h-2a2 2 0 00-2 2v4" />
      </svg>
    ),
  },
  {
    title: 'Monitor Progress',
    description: 'Review your meal history, spot trends, and stay consistent on your fitness journey.',
    icon: (
      <svg className="w-8 h-8 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
      </svg>
    ),
  },
];

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-gray-950">
      {/* Hero */}
      <div className="relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-emerald-900/20 to-gray-950" />
        <div className="relative max-w-5xl mx-auto px-4 py-24 sm:py-32 text-center">
          <h1 className="text-4xl sm:text-6xl font-bold text-white tracking-tight">
            Track Your Nutrition.
            <br />
            <span className="text-emerald-400">Crush Your Goals.</span>
          </h1>
          <p className="mt-6 text-lg sm:text-xl text-gray-400 max-w-2xl mx-auto">
            NutriFit helps you log meals, track macros, and stay on top of your fitness journey.
          </p>
          <div className="mt-10 flex flex-col sm:flex-row gap-4 justify-center">
            <Link
              to="/register"
              className="bg-emerald-500 hover:bg-emerald-600 text-white font-semibold px-8 py-3 rounded-lg text-lg transition-colors"
            >
              Get Started
            </Link>
            <Link
              to="/login"
              className="bg-gray-800 hover:bg-gray-700 text-gray-200 font-semibold px-8 py-3 rounded-lg text-lg transition-colors border border-gray-700"
            >
              Login
            </Link>
          </div>
        </div>
      </div>

      {/* Features */}
      <div className="max-w-5xl mx-auto px-4 py-16 sm:py-24">
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-8">
          {features.map((f) => (
            <div key={f.title} className="bg-gray-900 border border-gray-800 rounded-xl p-6">
              <div className="mb-4">{f.icon}</div>
              <h3 className="text-lg font-semibold text-white mb-2">{f.title}</h3>
              <p className="text-gray-400 text-sm">{f.description}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Footer */}
      <footer className="border-t border-gray-800 py-8 text-center text-sm text-gray-500">
        NutriFit &mdash; Built for fitness.
      </footer>
    </div>
  );
}
