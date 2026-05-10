export default function SummaryCard({ title, value, color }) {
  return (
    <div className={`p-6 rounded-xl shadow-lg text-white ${color}`}>
      <h3 className="text-lg">{title}</h3>
      <p className="text-2xl font-bold mt-2">₹{value}</p>
    </div>
  );
}