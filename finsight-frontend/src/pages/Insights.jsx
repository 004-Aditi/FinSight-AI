import { useEffect, useState } from "react";
import Layout from "../components/Layout";
import { getMonthlyInsights } from "../api/insights";
import MonthYearFilter from "../components/MonthYearFilter";
export default function Insights() {
  const [insights, setInsights] = useState([]);
const [month, setMonth] = useState(
  new Date().getMonth() + 1
);

const [year, setYear] = useState(
  new Date().getFullYear()
);
  const [loading, setLoading] = useState(false);

  const fetchInsights = async () => {
    setLoading(true);
    try {
      const res = await getMonthlyInsights(month, year);
      setInsights(res.data.data.insights);
    } catch {
      alert("Failed to fetch insights");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInsights();
  }, []);

  return (
   <Layout>
        <h1 className="text-3xl font-bold mb-6">Smart Insights</h1>
<div className="flex items-center gap-4 w-full mb-6">
  
  <div className="flex-1">
    <MonthYearFilter
      month={month}
      year={year}
      setMonth={setMonth}
      setYear={setYear}
    />
  </div>

  <button
    onClick={fetchInsights}
    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 whitespace-nowrap"
  >
    Generate Insights
  </button>

</div>

        {loading ? (
          <p>Generating insights...</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {insights.map((item, index) => (
              <div key={index} className="bg-white p-5 rounded-xl shadow">
                <p className="text-gray-700">{item}</p>
              </div>
            ))}

            {insights.length === 0 && (
              <p className="text-gray-500">No insights available.</p>
            )}
          </div>
        )}
      </Layout>
  );
}