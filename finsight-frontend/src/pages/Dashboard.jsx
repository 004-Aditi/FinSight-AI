import { useEffect, useState } from "react";

import {
  getSummary,
  getDailyExpenses,
  getCategoryBreakdown,
} from "../api/dashboard";

import MonthYearFilter from "../components/MonthYearFilter";
import Layout from "../components/Layout";
import SummaryCard from "../components/SummaryCard";
import ExpenseLineChart from "../components/ExpenseLineChart";
import CategoryPieChart from "../components/CategoryPieChart";

import { exportToCsv } from "../utils/exportCsv";
import { getExpenses } from "../api/expenses";
import { getIncomes } from "../api/income";
import { getBudgetStatus } from "../api/budgets";
import { getRecurringTransactions } from "../api/recurring";

export default function Dashboard() {
  const [summary, setSummary] = useState(null);
  const [dailyExpenses, setDailyExpenses] = useState([]);
  const [categoryData, setCategoryData] = useState([]);

  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());

  const [recentExpenses, setRecentExpenses] = useState([]);
  const [recentIncomes, setRecentIncomes] = useState([]);
  const [budgetWarnings, setBudgetWarnings] = useState([]);
  const [recurringItems, setRecurringItems] = useState([]);

  const fetchData = async () => {
    const summaryRes = await getSummary(month, year);
    setSummary(summaryRes.data.data);

    const dailyRes = await getDailyExpenses(month, year);
    setDailyExpenses(dailyRes.data.data);

    const categoryRes = await getCategoryBreakdown(month, year);
    setCategoryData(categoryRes.data.data);

    const expensesRes = await getExpenses();
    setRecentExpenses(expensesRes.data.data.slice(0, 4));

    const incomesRes = await getIncomes();
    setRecentIncomes(incomesRes.data.data.slice(0, 4));

    const budgetRes = await getBudgetStatus(month, year);
    setBudgetWarnings(
      budgetRes.data.data
        .filter(
          (item) => item.status === "WARNING" || item.status === "EXCEEDED"
        )
        .slice(0, 4)
    );

    const recurringRes = await getRecurringTransactions();
    setRecurringItems(
      recurringRes.data.data.filter((item) => item.active).slice(0, 4)
    );
  };

  useEffect(() => {
    fetchData();
  }, [month, year]);

  if (!summary) {
    return <p className="text-center mt-10">Loading...</p>;
  }

  return (
    <Layout>
      <div className="flex items-center justify-between gap-4 mb-5">
        <h1 className="text-3xl font-bold">Financial Dashboard</h1>

        <button
          onClick={() =>
            exportToCsv("dashboard-summary.csv", [
              {
                month,
                year,
                totalIncome: summary.totalIncome,
                totalExpense: summary.totalExpense,
                balance: summary.balance,
                savingsRate: summary.savingsRate,
                topExpenseCategory: summary.topExpenseCategory,
              },
            ])
          }
          className="bg-green-600 text-white px-4 py-3 rounded hover:bg-green-700"
        >
          Export Summary CSV
        </button>
      </div>

      <MonthYearFilter
        month={month}
        year={year}
        setMonth={setMonth}
        setYear={setYear}
      />

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <SummaryCard
          title="Income"
          value={summary.totalIncome}
          color="bg-green-500"
        />

        <SummaryCard
          title="Expense"
          value={summary.totalExpense}
          color="bg-red-500"
        />

        <SummaryCard
          title="Balance"
          value={summary.balance}
          color="bg-blue-500"
        />

        <SummaryCard
          title="Savings"
          value={summary.savingsRate + "%"}
          color="bg-purple-500"
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <ExpenseLineChart data={dailyExpenses} />
        <CategoryPieChart data={categoryData} />
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-4 gap-4 mt-6">
        <div className="bg-white rounded-xl shadow p-4">
          <h2 className="text-lg font-bold mb-3">Recent Expenses</h2>

          {recentExpenses.length === 0 ? (
            <p className="text-gray-500 text-sm">No recent expenses.</p>
          ) : (
            <div className="space-y-2">
              {recentExpenses.map((expense) => (
                <div
                  key={expense.id}
                  className="flex justify-between border-b pb-2"
                >
                  <div>
                    <p className="font-semibold text-sm">{expense.category}</p>
                    <p className="text-xs text-gray-500">
                      {expense.merchant || "No merchant"} •{" "}
                      {expense.expenseDate}
                    </p>
                  </div>

                  <p className="font-bold text-red-600 text-sm">
                    ₹{expense.amount}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="bg-white rounded-xl shadow p-4">
          <h2 className="text-lg font-bold mb-3">Recent Income</h2>

          {recentIncomes.length === 0 ? (
            <p className="text-gray-500 text-sm">No recent income.</p>
          ) : (
            <div className="space-y-2">
              {recentIncomes.map((income) => (
                <div
                  key={income.id}
                  className="flex justify-between border-b pb-2"
                >
                  <div>
                    <p className="font-semibold text-sm">{income.source}</p>
                    <p className="text-xs text-gray-500">
                      {income.description || "No description"} •{" "}
                      {income.incomeDate}
                    </p>
                  </div>

                  <p className="font-bold text-green-600 text-sm">
                    ₹{income.amount}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="bg-white rounded-xl shadow p-4">
          <h2 className="text-lg font-bold mb-3">Budget Warnings</h2>

          {budgetWarnings.length === 0 ? (
            <p className="text-gray-500 text-sm">No budget warnings.</p>
          ) : (
            <div className="space-y-2">
              {budgetWarnings.map((item) => (
                <div key={item.category} className="border rounded-lg p-2">
                  <div className="flex justify-between gap-2">
                    <p className="font-semibold text-sm">{item.category}</p>

                    <span
                      className={`px-2 py-1 rounded-full text-xs ${
                        item.status === "EXCEEDED"
                          ? "bg-red-100 text-red-700"
                          : "bg-yellow-100 text-yellow-700"
                      }`}
                    >
                      {item.status}
                    </span>
                  </div>

                  <p className="text-xs text-gray-500 mt-1">
                    ₹{item.spentAmount} / ₹{item.budgetAmount}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="bg-white rounded-xl shadow p-4">
          <h2 className="text-lg font-bold mb-3">Recurring Items</h2>

          {recurringItems.length === 0 ? (
            <p className="text-gray-500 text-sm">No active recurring items.</p>
          ) : (
            <div className="space-y-2">
              {recurringItems.map((item) => (
                <div
                  key={item.id}
                  className="flex justify-between border-b pb-2"
                >
                  <div>
                    <p className="font-semibold text-sm">
                      {item.categoryOrSource}
                    </p>
                    <p className="text-xs text-gray-500">
                      {item.type} • Day {item.dayOfMonth}
                    </p>
                  </div>

                  <p className="font-bold text-sm">₹{item.amount}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}