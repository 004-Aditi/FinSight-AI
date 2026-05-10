import api from "./axios";

export const getMonthlyInsights = (month, year) => {
  return api.get(`/insights/monthly?month=${month}&year=${year}`);
};