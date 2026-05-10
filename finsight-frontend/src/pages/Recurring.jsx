import { useEffect, useState } from "react";
import Layout from "../components/Layout";
import { toast } from "react-toastify";
import {
  createRecurringTransaction,
  deleteRecurringTransaction,
  generateRecurringTransaction,
  getRecurringTransactions,
  updateRecurringTransaction,
} from "../api/recurring";

export default function Recurring() {
  const currentDate = new Date();

  const [items, setItems] = useState([]);
  const [editingId, setEditingId] = useState(null);

  const [generateFor, setGenerateFor] = useState({
    month: currentDate.getMonth() + 1,
    year: currentDate.getFullYear(),
  });

  const [form, setForm] = useState({
    type: "EXPENSE",
    amount: "",
    categoryOrSource: "",
    description: "",
    merchant: "",
    paymentMode: "",
    dayOfMonth: 1,
    active: true,
  });

  const fetchItems = async () => {
    try {
      const res = await getRecurringTransactions();
      setItems(res.data.data);
    } catch {
      toast.error("Failed to fetch recurring transactions");
    }
  };

  useEffect(() => {
    fetchItems();
  }, []);

  const resetForm = () => {
    setEditingId(null);

    setForm({
      type: "EXPENSE",
      amount: "",
      categoryOrSource: "",
      description: "",
      merchant: "",
      paymentMode: "",
      dayOfMonth: 1,
      active: true,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
      ...form,
      amount: Number(form.amount),
      dayOfMonth: Number(form.dayOfMonth),
      active: Boolean(form.active),
    };

    try {
      if (editingId) {
        await updateRecurringTransaction(editingId, payload);
        toast.success("Recurring transaction updated");
      } else {
        await createRecurringTransaction(payload);
        toast.success("Recurring transaction created");
      }

      resetForm();
      fetchItems();
    } catch (err) {
      toast.error(
        err.response?.data?.error || "Failed to save recurring transaction"
      );
    }
  };

  const handleEdit = (item) => {
    setEditingId(item.id);

    setForm({
      type: item.type,
      amount: item.amount,
      categoryOrSource: item.categoryOrSource,
      description: item.description || "",
      merchant: item.merchant || "",
      paymentMode: item.paymentMode || "",
      dayOfMonth: item.dayOfMonth,
      active: item.active,
    });
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this recurring transaction?"
    );

    if (!confirmed) return;

    try {
      await deleteRecurringTransaction(id);
      toast.success("Recurring transaction deleted");
      fetchItems();
    } catch (err) {
      toast.error(err.response?.data?.error || "Delete failed");
    }
  };

  const handleGenerate = async (id) => {
    try {
      await generateRecurringTransaction(
        id,
        generateFor.month,
        generateFor.year
      );

      toast.success("Transaction generated successfully");
    } catch (err) {
      toast.error(err.response?.data?.error || "Failed to generate transaction");
    }
  };

  return (
    <Layout>
      <h1 className="text-3xl font-bold mb-6">Recurring Transactions</h1>

      <form
        onSubmit={handleSubmit}
        className="bg-white p-6 rounded-xl shadow mb-8 grid grid-cols-1 md:grid-cols-4 gap-4"
      >
        <select
          className="border p-3 rounded"
          value={form.type}
          onChange={(e) => setForm({ ...form, type: e.target.value })}
        >
          <option value="EXPENSE">Expense</option>
          <option value="INCOME">Income</option>
        </select>

        <input
          className="border p-3 rounded"
          placeholder="Amount"
          value={form.amount}
          onChange={(e) => setForm({ ...form, amount: e.target.value })}
        />

        <input
          className="border p-3 rounded"
          placeholder="Category / Source"
          value={form.categoryOrSource}
          onChange={(e) =>
            setForm({ ...form, categoryOrSource: e.target.value })
          }
        />

        <input
          className="border p-3 rounded"
          placeholder="Day of month 1-28"
          value={form.dayOfMonth}
          onChange={(e) => setForm({ ...form, dayOfMonth: e.target.value })}
        />

        <input
          className="border p-3 rounded"
          placeholder="Description"
          value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
        />

        <input
          className="border p-3 rounded"
          placeholder="Merchant"
          value={form.merchant}
          onChange={(e) => setForm({ ...form, merchant: e.target.value })}
        />

        <input
          className="border p-3 rounded"
          placeholder="Payment Mode"
          value={form.paymentMode}
          onChange={(e) => setForm({ ...form, paymentMode: e.target.value })}
        />

        <label className="flex items-center gap-2 bg-gray-50 border p-3 rounded">
          <input
            type="checkbox"
            checked={form.active}
            onChange={(e) => setForm({ ...form, active: e.target.checked })}
          />
          Active
        </label>

        <button className="bg-blue-600 text-white p-3 rounded md:col-span-4 hover:bg-blue-700">
          {editingId ? "Update Recurring" : "Create Recurring"}
        </button>

        {editingId && (
          <button
            type="button"
            onClick={resetForm}
            className="border border-gray-300 p-3 rounded md:col-span-4 hover:bg-gray-100"
          >
            Cancel Edit
          </button>
        )}
      </form>

      <div className="bg-white p-4 rounded-xl shadow mb-4 grid grid-cols-1 md:grid-cols-3 gap-4">
        <input
          className="border p-3 rounded"
          placeholder="Generate Month"
          value={generateFor.month}
          onChange={(e) =>
            setGenerateFor({
              ...generateFor,
              month: Number(e.target.value),
            })
          }
        />

        <input
          className="border p-3 rounded"
          placeholder="Generate Year"
          value={generateFor.year}
          onChange={(e) =>
            setGenerateFor({
              ...generateFor,
              year: Number(e.target.value),
            })
          }
        />

        <p className="text-gray-500 flex items-center">
          Select month/year, then click Generate on any item.
        </p>
      </div>

      <div className="bg-white rounded-xl shadow overflow-hidden">
        <h2 className="text-xl font-bold p-6">All Recurring Transactions</h2>

        <table className="w-full text-left">
          <thead className="bg-gray-200">
            <tr>
              <th className="p-4">Type</th>
              <th className="p-4">Category / Source</th>
              <th className="p-4">Amount</th>
              <th className="p-4">Day</th>
              <th className="p-4">Active</th>
              <th className="p-4">Action</th>
            </tr>
          </thead>

          <tbody>
            {items.map((item) => (
              <tr key={item.id} className="border-t">
                <td className="p-4">
                  <span
                    className={`px-3 py-1 rounded-full text-sm ${
                      item.type === "EXPENSE"
                        ? "bg-red-100 text-red-700"
                        : "bg-green-100 text-green-700"
                    }`}
                  >
                    {item.type}
                  </span>
                </td>

                <td className="p-4">{item.categoryOrSource}</td>
                <td className="p-4 font-semibold">₹{item.amount}</td>
                <td className="p-4">{item.dayOfMonth}</td>

                <td className="p-4">
                  {item.active ? (
                    <span className="bg-green-100 text-green-700 px-3 py-1 rounded-full text-sm">
                      Active
                    </span>
                  ) : (
                    <span className="bg-gray-100 text-gray-700 px-3 py-1 rounded-full text-sm">
                      Inactive
                    </span>
                  )}
                </td>

                <td className="p-4">
                  <div className="flex gap-2 flex-wrap">
                    <button
                      onClick={() => handleGenerate(item.id)}
                      className="bg-purple-600 text-white px-3 py-1 rounded hover:bg-purple-700"
                    >
                      Generate
                    </button>

                    <button
                      onClick={() => handleEdit(item)}
                      className="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600"
                    >
                      Edit
                    </button>

                    <button
                      onClick={() => handleDelete(item.id)}
                      className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}

            {items.length === 0 && (
              <tr>
                <td className="p-4 text-center text-gray-500" colSpan="6">
                  No recurring transactions found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </Layout>
  );
}