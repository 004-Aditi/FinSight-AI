import { useEffect, useState } from "react";
import {
  createExpense,
  deleteExpense,
  getExpenses,
  updateExpense,
} from "../api/expenses";
import Layout from "../components/Layout";
import { getCategories } from "../api/categories";
import CreateCategoryModal from "../components/CreateCategoryModal";
import { toast } from "react-toastify";
import { exportToCsv } from "../utils/exportCsv";
export default function Expenses() {
  const [expenses, setExpenses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [search, setSearch] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");
    const [startDate, setStartDate] = useState("");
const [endDate, setEndDate] = useState("");
  const [form, setForm] = useState({
    amount: "",
    category: "",
    description: "",
    merchant: "",
    paymentMode: "",
    expenseDate: "",
  });

  const fetchExpenses = async () => {
    const res = await getExpenses();
    setExpenses(res.data.data);
  };

  const fetchCategories = async () => {
    const res = await getCategories("EXPENSE");
    setCategories(res.data.data);
  };

  useEffect(() => {
    fetchExpenses();
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
        await updateExpense(editingId, payload);
        toast.success("Expense updated successfully");
        setEditingId(null);
      } else {
        await createExpense(payload);
        toast.success("Expense added successfully");
      }

      setForm({
        amount: "",
        category: "",
        description: "",
        merchant: "",
        paymentMode: "",
        expenseDate: "",
      });

      fetchExpenses();
    } catch (err) {
      toast.error(err.response?.data?.error || "Something went wrong");
    }
  };

const handleDelete = async (id) => {

  const confirmed = window.confirm(
    "Are you sure you want to delete this expense?"
  );

  if (!confirmed) return;

  try {
    await deleteExpense(id);

    toast.success("Expense deleted successfully");

    fetchExpenses();

  } catch (err) {
    toast.error(
      err.response?.data?.error || "Delete failed"
    );
  }
};

  const handleEdit = (expense) => {
    setEditingId(expense.id);

    setForm({
      amount: expense.amount,
      category: expense.category,
      description: expense.description || "",
      merchant: expense.merchant || "",
      paymentMode: expense.paymentMode || "",
      expenseDate: expense.expenseDate,
    });
  };

  const filteredExpenses = expenses.filter((expense) => {
  const matchesSearch =
    expense.category.toLowerCase().includes(search.toLowerCase()) ||
    expense.merchant?.toLowerCase().includes(search.toLowerCase()) ||
    expense.description?.toLowerCase().includes(search.toLowerCase());

  const matchesCategory =
    !selectedCategory || expense.category === selectedCategory;

  const expenseDate = new Date(expense.expenseDate);

  const matchesStartDate =
    !startDate || expenseDate >= new Date(startDate);

  const matchesEndDate =
    !endDate || expenseDate <= new Date(endDate);

  return (
    matchesSearch &&
    matchesCategory &&
    matchesStartDate &&
    matchesEndDate
  );
});
  return (
    <Layout>
      <div>
        <h1 className="text-3xl font-bold mb-6">Expenses</h1>

        <form
          onSubmit={handleSubmit}
          className="bg-white p-6 rounded-xl shadow mb-8 grid grid-cols-1 md:grid-cols-3 gap-4"
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
              value={form.category}
              onChange={(e) => setForm({ ...form, category: e.target.value })}
            >
              <option value="">Select Category</option>

              {categories.map((cat) => (
                <option key={cat.id} value={cat.name}>
                  {cat.name}
                </option>
              ))}
            </select>

            <button
              type="button"
              onClick={() => setShowModal(true)}
              className="bg-blue-500 text-white px-4 rounded"
            >
              +
            </button>
          </div>

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
            onChange={(e) =>
              setForm({ ...form, paymentMode: e.target.value })
            }
          />

          <input
            className="border p-3 rounded"
            placeholder="Description"
            value={form.description}
            onChange={(e) =>
              setForm({ ...form, description: e.target.value })
            }
          />

          <input
            type="date"
            className="border p-3 rounded"
            value={form.expenseDate}
            onChange={(e) =>
              setForm({ ...form, expenseDate: e.target.value })
            }
          />

          <button className="bg-blue-600 text-white p-3 rounded md:col-span-3 hover:bg-blue-700">
            {editingId ? "Update Expense" : "Add Expense"}
          </button>
        </form>

       <div className="bg-white p-4 rounded-xl shadow mb-4 grid grid-cols-1 md:grid-cols-4 gap-4">
  <input
    className="border p-3 rounded"
    placeholder="Search by category, merchant or description"
    value={search}
    onChange={(e) => setSearch(e.target.value)}
  />

  <select
    className="border p-3 rounded"
    value={selectedCategory}
    onChange={(e) => setSelectedCategory(e.target.value)}
  >
    <option value="">All Categories</option>

    {categories.map((cat) => (
      <option key={cat.id} value={cat.name}>
        {cat.name}
      </option>
    ))}
  </select>

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
  onClick={() => exportToCsv("expenses.csv", filteredExpenses)}
  className="bg-green-600 text-white px-4 py-3 rounded hover:bg-green-700 mb-4"
>
  Export Expenses CSV
</button>
        <div className="bg-white rounded-xl shadow overflow-hidden">
          <table className="w-full text-left">
            <thead className="bg-gray-200">
              <tr>
                <th className="p-4">Date</th>
                <th className="p-4">Category</th>
                <th className="p-4">Merchant</th>
                <th className="p-4">Amount</th>
                <th className="p-4">Payment</th>
                <th className="p-4">Action</th>
              </tr>
            </thead>

            <tbody>
              {filteredExpenses.map((expense) => (
                <tr key={expense.id} className="border-t">
                  <td className="p-4">{expense.expenseDate}</td>
                  <td className="p-4">{expense.category}</td>
                  <td className="p-4">{expense.merchant}</td>
                  <td className="p-4 font-semibold">₹{expense.amount}</td>
                  <td className="p-4">{expense.paymentMode}</td>
                  <td className="p-4">
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleEdit(expense)}
                        className="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600"
                      >
                        Edit
                      </button>

                      <button
                        onClick={() => handleDelete(expense.id)}
                        className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}

              {filteredExpenses.length === 0 && (
                <tr>
                  <td className="p-4 text-center text-gray-500" colSpan="6">
                    No expenses found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <CreateCategoryModal
          type="EXPENSE"
          onClose={() => setShowModal(false)}
          onCreated={fetchCategories}
        />
      )}
    </Layout>
  );
}