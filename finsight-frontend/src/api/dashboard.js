import api from "./axios";

export const getSummary = async (month, year) => {
  return api.get(`/dashboard/summary?month=${month}&year=${year}`);
};

export const getDailyExpenses = async (month, year) => {
  return api.get(`/dashboard/daily-expenses?month=${month}&year=${year}`);
};

export const getCategoryBreakdown = async (month, year) => {
  return api.get(`/dashboard/category-breakdown?month=${month}&year=${year}`);
};

export const getIncomeVsExpense = async (month, year) => {
  return api.get(`/dashboard/income-vs-expense?month=${month}&year=${year}`);
};

