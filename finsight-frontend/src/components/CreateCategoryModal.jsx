import { useState } from "react";
import { createCategory } from "../api/categories";

export default function CreateCategoryModal({
  type,
  onClose,
  onCreated,
}) {
  const [form, setForm] = useState({
    name: "",
    color: "#3b82f6",
    icon: "circle",
  });

  const handleSubmit = async (e) => {
    e.preventDefault();

    await createCategory({
      ...form,
      type,
    });

    onCreated();
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

      <div className="bg-white p-6 rounded-xl w-96 shadow-2xl">

        <div className="flex justify-between items-center mb-4">

          <h2 className="text-2xl font-bold">
            Create Category
          </h2>

          <button
            onClick={onClose}
            className="text-gray-500 hover:text-black text-xl"
          >
            ✕
          </button>

        </div>

        <form onSubmit={handleSubmit}>

          <input
            className="w-full border p-3 rounded mb-4"
            placeholder="Category Name"
            value={form.name}
            onChange={(e) =>
              setForm({ ...form, name: e.target.value })
            }
          />

          <div className="mb-4">
            <label className="block mb-2 text-sm font-medium">
              Pick Color
            </label>

            <input
              type="color"
              className="w-full h-12"
              value={form.color}
              onChange={(e) =>
                setForm({ ...form, color: e.target.value })
              }
            />
          </div>

          <div className="flex gap-3">

            <button
              type="button"
              onClick={onClose}
              className="w-full border border-gray-300 p-3 rounded hover:bg-gray-100"
            >
              Cancel
            </button>

            <button
              className="w-full bg-blue-600 text-white p-3 rounded hover:bg-blue-700"
            >
              Create
            </button>

          </div>

        </form>
      </div>
    </div>
  );
}