import Sidebar from "./Sidebar";

export default function Layout({ children }) {
  return (
    <div className="flex bg-gray-100">

      <Sidebar />

      <div className="flex-1 min-h-screen p-6 overflow-auto">
        {children}
      </div>

    </div>
  );
}