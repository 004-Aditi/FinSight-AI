import { useEffect, useState } from "react";
import {
  createBudget,
  getBudgets,
  getBudgetStatus,
  updateBudget,
  deleteBudget,
} from "../api/budgets";
import MonthYearFilter from "../components/MonthYearFilter";
import Layout from "../components/Layout";
import { toast } from "react-toastify";

export default function Budgets() {
  const currentDate = new Date();

  const [budgets, setBudgets] = useState([]);
  const [statuses, setStatuses] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [search, setSearch] = useState("");

  const [filter, setFilter] = useState({
    month: currentDate.getMonth() + 1,
    year: currentDate.getFullYear(),
  });

  const [form, setForm] = useState({
    category: "",
    amount: "",
    month: currentDate.getMonth() + 1,
    year: currentDate.getFullYear(),
  });

  const fetchBudgets = async () => {
    const budgetRes = await getBudgets(filter.month, filter.year);
    const statusRes = await getBudgetStatus(filter.month, filter.year);

    setBudgets(budgetRes.data.data);
    setStatuses(statusRes.data.data);
  };

  useEffect(() => {
    fetchBudgets();
  }, [filter.month, filter.year]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
      ...form,
      amount: Number(form.amount),
      month: Number(form.month),
      year: Number(form.year),
    };

    try {
      if (editingId) {
        await updateBudget(editingId, payload);
        toast.success("Budget updated successfully");
        setEditingId(null);
      } else {
        await createBudget(payload);
        toast.success("Budget saved successfully");
      }

      setForm({
        category: "",
        amount: "",
        month: currentDate.getMonth() + 1,
        year: currentDate.getFullYear(),
      });

      setFilter({
        month: Number(payload.month),
        year: Number(payload.year),
      });
    } catch (err) {
      toast.error(err.response?.data?.error || "Failed to save budget");
    }
  };

  const handleEdit = (budget) => {
    setEditingId(budget.id);

    setForm({
      category: budget.category,
      amount: budget.amount,
      month: budget.month,
      year: budget.year,
    });
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
    "Are you sure you want to delete this budget?"
  );

  if (!confirmed) return;
    try {
      await deleteBudget(id);
      toast.success("Budget deleted successfully");
      fetchBudgets();
    } catch (err) {
      toast.error(err.response?.data?.error || "Delete failed");
    }
  };

  const cancelEdit = () => {
    setEditingId(null);

    setForm({
      category: "",
      amount: "",
      month: currentDate.getMonth() + 1,
      year: currentDate.getFullYear(),
    });
  };

  const statusColor = (status) => {
    if (status === "SAFE") return "bg-green-100 text-green-700";
    if (status === "WARNING") return "bg-yellow-100 text-yellow-700";
    return "bg-red-100 text-red-700";
  };

  const filteredBudgets = budgets.filter((budget) =>
    budget.category.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <Layout>
      <h1 className="text-3xl font-bold mb-6">Budgets</h1>

      <form
        onSubmit={handleSubmit}
        className="bg-white p-6 rounded-xl shadow mb-8 grid grid-cols-1 md:grid-cols-4 gap-4"
      >
        <input
          className="border p-3 rounded"
          placeholder="Category e.g. Food or ALL"
          value={form.category}
          onChange={(e) => setForm({ ...form, category: e.target.value })}
        />

        <input
          className="border p-3 rounded"
          placeholder="Amount"
          value={form.amount}
          onChange={(e) => setForm({ ...form, amount: e.target.value })}
        />

        <input
          className="border p-3 rounded"
          placeholder="Month"
          value={form.month}
          onChange={(e) => setForm({ ...form, month: e.target.value })}
        />

        <input
          className="border p-3 rounded"
          placeholder="Year"
          value={form.year}
          onChange={(e) => setForm({ ...form, year: e.target.value })}
        />

        <button className="bg-purple-600 text-white p-3 rounded md:col-span-4 hover:bg-purple-700">
          {editingId ? "Update Budget" : "Save Budget"}
        </button>

        {editingId && (
          <button
            type="button"
            onClick={cancelEdit}
            className="border border-gray-300 p-3 rounded md:col-span-4 hover:bg-gray-100"
          >
            Cancel Edit
          </button>
        )}
      </form>

      <div className="bg-white rounded-xl shadow p-6 mb-8">
        <h2 className="text-xl font-bold mb-4">Budget Status</h2>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {statuses.map((item) => (
            <div key={item.category} className="border rounded-xl p-4">
              <div className="flex justify-between items-center mb-3">
                <h3 className="font-bold">{item.category}</h3>

                <span
                  className={`px-3 py-1 rounded-full text-sm ${statusColor(
                    item.status
                  )}`}
                >
                  {item.status}
                </span>
              </div>

              <p>Budget: ₹{item.budgetAmount}</p>
              <p>Spent: ₹{item.spentAmount}</p>
              <p>Remaining: ₹{item.remainingAmount}</p>

              <div className="w-full bg-gray-200 h-3 rounded-full mt-3">
                <div
                  className="bg-purple-600 h-3 rounded-full"
                  style={{
                    width: `${Math.min(item.usagePercentage, 100)}%`,
                  }}
                />
              </div>

              <p className="text-sm text-gray-500 mt-2">
                {item.usagePercentage}% used
              </p>
            </div>
          ))}

          {statuses.length === 0 && (
            <p className="text-gray-500">No budgets found.</p>
          )}
        </div>
      </div>

      <div className="bg-white rounded-xl shadow overflow-hidden">
        <h2 className="text-xl font-bold p-6">All Budgets</h2>

        <div className="bg-white p-4 border-t border-b flex flex-col md:flex-row md:items-center gap-4">
  <input
    className="border p-3 rounded w-full md:w-1/3"
    placeholder="Search budget by category"
    value={search}
    onChange={(e) => setSearch(e.target.value)}
  />

  <div className="w-full md:flex-1">
    <MonthYearFilter
      month={filter.month}
      year={filter.year}
      setMonth={(value) => setFilter({ ...filter, month: value })}
      setYear={(value) => setFilter({ ...filter, year: value })}
    />
  </div>
</div>
        

        <table className="w-full text-left">
          <thead className="bg-gray-200">
            <tr>
              <th className="p-4">Category</th>
              <th className="p-4">Amount</th>
              <th className="p-4">Month</th>
              <th className="p-4">Year</th>
              <th className="p-4">Action</th>
            </tr>
          </thead>

          <tbody>
            {filteredBudgets.map((budget) => (
              <tr key={budget.id} className="border-t">
                <td className="p-4">{budget.category}</td>
                <td className="p-4">₹{budget.amount}</td>
                <td className="p-4">{budget.month}</td>
                <td className="p-4">{budget.year}</td>

                <td className="p-4">
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleEdit(budget)}
                      className="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600"
                    >
                      Edit
                    </button>

                    <button
                      onClick={() => handleDelete(budget.id)}
                      className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}

            {filteredBudgets.length === 0 && (
              <tr>
                <td className="p-4 text-center text-gray-500" colSpan="5">
                  No budgets added yet
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </Layout>
  );
}