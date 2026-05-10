import api from "./axios";

export const createBudget = (data) => {
  return api.post("/budgets", data);
};

export const getBudgets = (month, year) => {
  return api.get(`/budgets?month=${month}&year=${year}`);
};

export const getBudgetStatus = (month, year) => {
  return api.get(`/budgets/status?month=${month}&year=${year}`);
};

export const updateBudget = (id, data) => {
  return api.put(`/budgets/${id}`, data);
};

export const deleteBudget = (id) => {
  return api.delete(`/budgets/${id}`);
};