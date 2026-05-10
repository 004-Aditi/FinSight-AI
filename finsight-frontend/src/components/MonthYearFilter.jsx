export default function MonthYearFilter({
  month,
  year,
  setMonth,
  setYear,
}) {
  return (
    <div className="bg-white p-4 rounded-xl shadow mb-4 grid grid-cols-1 md:grid-cols-2 gap-4">

      <input
        type="number"
        placeholder="Month"
        value={month}
        onChange={(e) => setMonth(Number(e.target.value))}
        className="border p-3 rounded"
      />

      <input
        type="number"
        placeholder="Year"
        value={year}
        onChange={(e) => setYear(Number(e.target.value))}
        className="border p-3 rounded"
      />

    </div>
  );
}