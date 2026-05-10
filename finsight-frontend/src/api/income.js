import api from "./axios";

export const getIncomes = () => {
  return api.get("/incomes");
};

export const createIncome = (data) => {
  return api.post("/incomes", data);
};

export const deleteIncome = (id) => {
  return api.delete(`/incomes/${id}`);
};

export const updateIncome = (id, data) => {
  return api.put(`/incomes/${id}`, data);
};