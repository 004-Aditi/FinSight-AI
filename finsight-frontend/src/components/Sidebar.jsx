import { NavLink, useNavigate } from "react-router-dom";

export default function Sidebar() {

  const navigate = useNavigate();

  const logout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  const linkClass = ({ isActive }) =>
    `block px-4 py-3 rounded-lg transition ${
      isActive
        ? "bg-blue-600 text-white"
        : "text-gray-700 hover:bg-gray-200"
    }`;

  return (
    <div className="w-64 h-screen bg-white shadow-lg p-5 flex flex-col">

      <h1 className="text-2xl font-bold text-blue-600 mb-10">
        FinSight AI
      </h1>

      <div className="flex flex-col gap-2 flex-1">

        <NavLink to="/dashboard" className={linkClass}>
          Dashboard
        </NavLink>

        <NavLink to="/expenses" className={linkClass}>
          Expenses
        </NavLink>

        <NavLink to="/incomes" className={linkClass}>
          Income
        </NavLink>

        <NavLink to="/budgets" className={linkClass}>
          Budgets
        </NavLink>

        <NavLink to="/insights" className={linkClass}>
          Insights
          </NavLink>
          <NavLink to="/recurring" className={linkClass}>
  Recurring

        </NavLink>
        <NavLink to="/categories" className={linkClass}>
  Categories
</NavLink>
    <NavLink to="/profile" className={linkClass}>
  Profile
</NavLink>

      </div>

      <button
        onClick={logout}
        className="mt-6 bg-red-500 text-white py-3 rounded-lg hover:bg-red-600"
      >
        Logout
      </button>

    </div>
  );
}