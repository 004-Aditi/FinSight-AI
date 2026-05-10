import api from "./axios";

export const getRecurringTransactions = () => {
  return api.get("/recurring");
};

export const createRecurringTransaction = (data) => {
  return api.post("/recurring", data);
};

export const updateRecurringTransaction = (id, data) => {
  return api.put(`/recurring/${id}`, data);
};

export const deleteRecurringTransaction = (id) => {
  return api.delete(`/recurring/${id}`);
};

export const generateRecurringTransaction = (id, month, year) => {
  return api.post(`/recurring/${id}/generate?month=${month}&year=${year}`);
};