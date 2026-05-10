import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout";
import { getProfile } from "../api/auth";
import { toast } from "react-toastify";

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const navigate = useNavigate();

  const fetchProfile = async () => {
    try {
      const res = await getProfile();
      setProfile(res.data.data);
    } catch {
      toast.error("Failed to load profile");
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const logout = () => {
    localStorage.removeItem("token");
    toast.success("Logged out successfully");
    navigate("/");
  };

  if (!profile) {
    return (
      <Layout>
        <p>Loading profile...</p>
      </Layout>
    );
  }

  return (
    <Layout>
      <h1 className="text-3xl font-bold mb-6">Profile</h1>

      <div className="bg-white rounded-xl shadow p-6 max-w-xl">
        <div className="w-20 h-20 bg-blue-600 text-white rounded-full flex items-center justify-center text-3xl font-bold mb-6">
          {profile.name?.charAt(0).toUpperCase()}
        </div>

        <div className="space-y-4">
          <div>
            <p className="text-gray-500 text-sm">Name</p>
            <p className="text-lg font-semibold">{profile.name}</p>
          </div>

          <div>
            <p className="text-gray-500 text-sm">Email</p>
            <p className="text-lg font-semibold">{profile.email}</p>
          </div>

          <div>
            <p className="text-gray-500 text-sm">User ID</p>
            <p className="text-sm text-gray-700 break-all">{profile.id}</p>
          </div>
        </div>

        <button
          onClick={logout}
          className="mt-6 bg-red-500 text-white px-5 py-3 rounded-lg hover:bg-red-600"
        >
          Logout
        </button>
      </div>
    </Layout>
  );
}