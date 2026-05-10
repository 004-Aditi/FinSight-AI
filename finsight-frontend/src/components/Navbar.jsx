import { useNavigate } from "react-router-dom";

export default function Navbar() {
  const navigate = useNavigate();

  const logout = () => {
  localStorage.removeItem("token");
  navigate("/");
};

  return (
    <div className="bg-white shadow px-6 py-4 flex justify-between items-center">
      <h1 className="text-xl font-bold text-blue-600">
        FinSight AI
      </h1>


<button
  onClick={() => navigate("/expenses")}
  className="text-gray-700 hover:text-blue-600"
>
  Expenses
</button>
<button
  onClick={() => navigate("/incomes")}
  className="text-gray-700 hover:text-blue-600"
>
  Income
</button>

<button
  onClick={() => navigate("/budgets")}
  className="text-gray-700 hover:text-blue-600"
>
  Budgets
</button>
<button
  onClick={() => navigate("/insights")}
  className="text-gray-700 hover:text-blue-600"
>
  Insights
</button>
  <button
  onClick={() => navigate("/dashboard")}
  className="text-gray-700 hover:text-blue-600"
>
  Dashboard
</button>

 <button
        onClick={logout}
        className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
      >
        Logout
      </button>
    </div>
  );
}