import { useState } from "react";
import { signup } from "../api/auth";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
export default function Signup() {
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
  });

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await signup(form);
      toast.success("Account created successfully");;
      navigate("/");
    } catch {
      toast.error("Signup failed");;
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-green-400 to-blue-500">
      <div className="bg-white p-8 rounded-2xl shadow-2xl w-96">

        <h2 className="text-3xl font-bold text-center mb-2 text-gray-800">
          Create Account 🚀
        </h2>
        <p className="text-center text-gray-500 mb-6">
          Start managing your finances
        </p>

        <form onSubmit={handleSubmit}>
          <input
            className="w-full mb-4 p-3 border rounded-lg focus:ring-2 focus:ring-green-400"
            placeholder="Name"
            onChange={(e) =>
              setForm({ ...form, name: e.target.value })
            }
          />

          <input
            className="w-full mb-4 p-3 border rounded-lg focus:ring-2 focus:ring-green-400"
            placeholder="Email"
            onChange={(e) =>
              setForm({ ...form, email: e.target.value })
            }
          />

          <input
            type="password"
            className="w-full mb-4 p-3 border rounded-lg focus:ring-2 focus:ring-green-400"
            placeholder="Password"
            onChange={(e) =>
              setForm({ ...form, password: e.target.value })
            }
          />

          <button
            className="w-full bg-green-500 text-white p-3 rounded-lg hover:bg-green-600 transition font-semibold"
          >
            Signup
          </button>
        </form>

        <p className="text-sm text-center mt-6 text-gray-600">
          Already have an account?{" "}
          <span
            className="text-green-600 font-medium cursor-pointer hover:underline"
            onClick={() => navigate("/")}
          >
            Login
          </span>
        </p>
      </div>
    </div>
  );
}