import { useEffect, useState } from "react";
import Layout from "../components/Layout";
import {
  createCategory,
  deleteCategory,
  getCategories,
  updateCategory,
} from "../api/categories";
import { toast } from "react-toastify";

export default function Categories() {
  const [categories, setCategories] = useState([]);
  const [typeFilter, setTypeFilter] = useState("");
  const [editingId, setEditingId] = useState(null);

  const [form, setForm] = useState({
    name: "",
    type: "EXPENSE",
    color: "#3b82f6",
    icon: "circle",
  });

  const fetchCategories = async () => {
    try {
      const res = await getCategories(typeFilter);
      setCategories(res.data.data);
    } catch {
      toast.error("Failed to fetch categories");
    }
  };

  useEffect(() => {
    fetchCategories();
  }, [typeFilter]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      if (editingId) {
        await updateCategory(editingId, form);
        toast.success("Category updated successfully");
        setEditingId(null);
      } else {
        await createCategory(form);
        toast.success("Category created successfully");
      }

      setForm({
        name: "",
        type: "EXPENSE",
        color: "#3b82f6",
        icon: "circle",
      });

      fetchCategories();
    } catch (err) {
      toast.error(err.response?.data?.error || "Failed to save category");
    }
  };

  const handleEdit = (category) => {
    if (category.isDefault) {
      toast.error("Default categories cannot be edited");
      return;
    }

    setEditingId(category.id);

    setForm({
      name: category.name,
      type: category.type,
      color: category.color || "#3b82f6",
      icon: category.icon || "circle",
    });
  };

  const handleDelete = async (category) => {
    if (category.isDefault) {
      toast.error("Default categories cannot be deleted");
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to delete ${category.name}?`
    );

    if (!confirmed) return;

    try {
      await deleteCategory(category.id);
      toast.success("Category deleted successfully");
      fetchCategories();
    } catch (err) {
      toast.error(err.response?.data?.error || "Delete failed");
    }
  };

  const cancelEdit = () => {
    setEditingId(null);

    setForm({
      name: "",
      type: "EXPENSE",
      color: "#3b82f6",
      icon: "circle",
    });
  };

  return (
    <Layout>
      <h1 className="text-3xl font-bold mb-6">Categories</h1>

      <form
        onSubmit={handleSubmit}
        className="bg-white p-6 rounded-xl shadow mb-8 grid grid-cols-1 md:grid-cols-4 gap-4"
      >
        <input
          className="border p-3 rounded"
          placeholder="Category name"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
        />

        <select
          className="border p-3 rounded"
          value={form.type}
          onChange={(e) => setForm({ ...form, type: e.target.value })}
        >
          <option value="EXPENSE">Expense</option>
          <option value="INCOME">Income</option>
        </select>

        <input
          type="color"
          className="border p-2 rounded h-12"
          value={form.color}
          onChange={(e) => setForm({ ...form, color: e.target.value })}
        />

        <input
          className="border p-3 rounded"
          placeholder="Icon e.g. food, salary"
          value={form.icon}
          onChange={(e) => setForm({ ...form, icon: e.target.value })}
        />

        <button className="bg-blue-600 text-white p-3 rounded md:col-span-4 hover:bg-blue-700">
          {editingId ? "Update Category" : "Create Category"}
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

      <div className="bg-white p-4 rounded-xl shadow mb-4">
        <select
          className="border p-3 rounded"
          value={typeFilter}
          onChange={(e) => setTypeFilter(e.target.value)}
        >
          <option value="">All Categories</option>
          <option value="EXPENSE">Expense Categories</option>
          <option value="INCOME">Income Categories</option>
        </select>
      </div>

      <div className="bg-white rounded-xl shadow overflow-hidden">
        <table className="w-full text-left">
          <thead className="bg-gray-200">
            <tr>
              <th className="p-4">Color</th>
              <th className="p-4">Name</th>
              <th className="p-4">Type</th>
              <th className="p-4">Icon</th>
              <th className="p-4">Default</th>
              <th className="p-4">Action</th>
            </tr>
          </thead>

          <tbody>
            {categories.map((category) => (
              <tr key={category.id} className="border-t">
                <td className="p-4">
                  <div
                    className="w-6 h-6 rounded-full border"
                    style={{ backgroundColor: category.color || "#ddd" }}
                  />
                </td>

                <td className="p-4 font-semibold">{category.name}</td>
                <td className="p-4">{category.type}</td>
                <td className="p-4">{category.icon}</td>

                <td className="p-4">
                  {category.isDefault ? (
                    <span className="bg-green-100 text-green-700 px-3 py-1 rounded-full text-sm">
                      Yes
                    </span>
                  ) : (
                    <span className="bg-gray-100 text-gray-700 px-3 py-1 rounded-full text-sm">
                      No
                    </span>
                  )}
                </td>

                <td className="p-4">
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleEdit(category)}
                      disabled={category.isDefault}
                      className={`px-3 py-1 rounded text-white ${
                        category.isDefault
                          ? "bg-gray-300 cursor-not-allowed"
                          : "bg-yellow-500 hover:bg-yellow-600"
                      }`}
                    >
                      Edit
                    </button>

                    <button
                      onClick={() => handleDelete(category)}
                      disabled={category.isDefault}
                      className={`px-3 py-1 rounded text-white ${
                        category.isDefault
                          ? "bg-gray-300 cursor-not-allowed"
                          : "bg-red-500 hover:bg-red-600"
                      }`}
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}

            {categories.length === 0 && (
              <tr>
                <td className="p-4 text-center text-gray-500" colSpan="6">
                  No categories found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </Layout>
  );
}