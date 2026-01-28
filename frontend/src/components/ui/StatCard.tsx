interface StatCardProps {
  label: string;
  value: number | string;
  unit?: string;
  color?: string;
}

export default function StatCard({ label, value, unit, color = 'border-emerald-500' }: StatCardProps) {
  return (
    <div className={`bg-gray-900 border-l-4 ${color} rounded-lg p-4`}>
      <p className="text-2xl font-bold text-white">
        {value}
        {unit && <span className="text-sm font-normal text-gray-400 ml-1">{unit}</span>}
      </p>
      <p className="text-sm text-gray-400 mt-1">{label}</p>
    </div>
  );
}
