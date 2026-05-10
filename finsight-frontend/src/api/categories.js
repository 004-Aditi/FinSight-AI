import api from "./axios";

export const getCategories = (type = "") => {
  const query = type ? `?type=${type}` : "";
  return api.get(`/categories${query}`);
};

export const createCategory = (data) => {
  return api.post("/categories", data);
};

export const updateCategory = (id, data) => {
  return api.put(`/categories/${id}`, data);
};

export const deleteCategory = (id) => {
  return api.delete(`/categories/${id}`);
};