import { useEffect, useState } from "react";
import { createIncome, deleteIncome, getIncomes, updateIncome } from "../api/income";
import Layout from "../components/Layout";
import { getCategories } from "../api/categories";
import CreateCategoryModal from "../components/CreateCategoryModal";
import { toast } from "react-toastify";
import { exportToCsv } from "../utils/exportCsv";
export default function Incomes() {
 const [search, setSearch] = useState("");
const [startDate, setStartDate] = useState("");
const [endDate, setEndDate] = useState("");
    const [incomes, setIncomes] = useState([]);
 const [categories, setCategories] = useState([]);
const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({
    amount: "",
    source: "",
    description: "",
    incomeDate: "",
  });
const [editingId, setEditingId] = useState(null);
  const fetchIncomes = async () => {
    const res = await getIncomes();
    setIncomes(res.data.data);
  };

  const fetchCategories = async () => {
  const res = await getCategories("INCOME");
  setCategories(res.data.data);
};

  useEffect(() => {
    fetchIncomes();
    fetchCategories();
  }, []);

 const handleSubmit = async (e) => {
  e.preventDefault();

  const payload = {
    ...form,
    amount: Number(form.amount),
  };

  try {
    if (editingId) {
      await updateIncome(editingId, payload);
      toast.success("Income updated successfully");
      setEditingId(null);
    } else {
      await createIncome(payload);
      toast.success("Income added successfully");
    }

    setForm({
      amount: "",
      source: "",
      description: "",
      incomeDate: "",
    });

    fetchIncomes();
  } catch (err) {
    toast.error(err.response?.data?.error || "Failed to save income");
  }
};
const handleEdit = (income) => {
  setEditingId(income.id);

  setForm({
    amount: income.amount,
    source: income.source || "",
    description: income.description || "",
    incomeDate: income.incomeDate,
  });
};

const cancelEdit = () => {
  setEditingId(null);

  setForm({
    amount: "",
    source: "",
    description: "",
    incomeDate: "",
  });
};
 const handleDelete = async (id) => {
    const confirmed = window.confirm(
    "Are you sure you want to delete this income?"
  );

  if (!confirmed) return;

  try {
    await deleteIncome(id);

    toast.success("Income deleted successfully");

    fetchIncomes();

  } catch (err) {
    toast.error(
      err.response?.data?.error || "Delete failed"
    );
  }
};
const filteredIncomes = incomes.filter((income) => {
  const matchesSearch =
    income.source?.toLowerCase().includes(search.toLowerCase()) ||
    income.description?.toLowerCase().includes(search.toLowerCase());

  const incomeDate = new Date(income.incomeDate);

  const matchesStartDate =
    !startDate || incomeDate >= new Date(startDate);

  const matchesEndDate =
    !endDate || incomeDate <= new Date(endDate);

  return matchesSearch && matchesStartDate && matchesEndDate;
});
  return (
    <Layout>
        <div>
        <h1 className="text-3xl font-bold mb-6">Income</h1>

        <form
          onSubmit={handleSubmit}
          className="bg-white p-6 rounded-xl shadow mb-8 grid grid-cols-1 md:grid-cols-2 gap-4"
        >
          <input
            className="border p-3 rounded"
            placeholder="Amount"
            value={form.amount}
            onChange={(e) => setForm({ ...form, amount: e.target.value })}
          />

          <div className="flex gap-2">
  <select
    className="border p-3 rounded w-full"
    value={form.source}
    onChange={(e) => setForm({ ...form, source: e.target.value })}
  >
    <option value="">Select Source</option>

    {categories.map((cat) => (
      <option key={cat.id} value={cat.name}>
        {cat.name}
      </option>
    ))}
  </select>

  <button
    type="button"
    onClick={() => setShowModal(true)}
    className="bg-green-500 text-white px-4 rounded"
  >
    +
  </button>
</div>
          <input
            className="border p-3 rounded"
            placeholder="Description"
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
          />

          <input
            type="date"
            className="border p-3 rounded"
            value={form.incomeDate}
            onChange={(e) => setForm({ ...form, incomeDate: e.target.value })}
          />

          <button className="bg-green-600 text-white p-3 rounded md:col-span-2 hover:bg-green-700">
  {editingId ? "Update Income" : "Add Income"}
</button>

{editingId && (
  <button
    type="button"
    onClick={cancelEdit}
    className="border border-gray-300 p-3 rounded md:col-span-2 hover:bg-gray-100"
  >
    Cancel Edit
  </button>
)}
        </form>
<div className="bg-white p-4 rounded-xl shadow mb-4 grid grid-cols-1 md:grid-cols-3 gap-4">
  <input
    className="border p-3 rounded"
    placeholder="Search by source or description"
    value={search}
    onChange={(e) => setSearch(e.target.value)}
  />

  <input
    type="date"
    className="border p-3 rounded"
    value={startDate}
    onChange={(e) => setStartDate(e.target.value)}
  />

  <input
    type="date"
    className="border p-3 rounded"
    value={endDate}
    onChange={(e) => setEndDate(e.target.value)}
  />
</div>
<button
  onClick={() => exportToCsv("incomes.csv", filteredIncomes)}
  className="bg-green-600 text-white px-4 py-3 rounded hover:bg-green-700 mb-4"
>
  Export Income CSV
</button>
        <div className="bg-white rounded-xl shadow overflow-hidden">
          <table className="w-full text-left">
            <thead className="bg-gray-200">
              <tr>
                <th className="p-4">Date</th>
                <th className="p-4">Source</th>
                <th className="p-4">Description</th>
                <th className="p-4">Amount</th>
                <th className="p-4">Action</th>
              </tr>
            </thead>

            <tbody>
              {filteredIncomes.map((income) => (
                <tr key={income.id} className="border-t">
                  <td className="p-4">{income.incomeDate}</td>
                  <td className="p-4">{income.source}</td>
                  <td className="p-4">{income.description}</td>
                  <td className="p-4 font-semibold text-green-600">
                    ₹{income.amount}
                  </td>
                  <td className="p-4">
  <div className="flex gap-2">

    <button
      onClick={() => handleEdit(income)}
      className="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600"
    >
      Edit
    </button>

    <button
      onClick={() => handleDelete(income.id)}
      className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
    >
      Delete
    </button>

  </div>
</td>
                </tr>
              ))}

              {filteredIncomes.length === 0 && (
                <tr>
                  <td className="p-4 text-center text-gray-500" colSpan="5">
                    No income records found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
  <CreateCategoryModal
    type="INCOME"
    onClose={() => setShowModal(false)}
    onCreated={fetchCategories}
  />
)}
    </Layout>
  );
}