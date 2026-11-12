import { useParams } from "react-router-dom";
import { Reports } from "@/features/admin/pages/ReportsPage";
import { Dashboard } from "@/features/admin/pages/DashboardPage";
import { AdminOrders } from "@/features/admin/pages/AdminOrdersPage";
import { Transactions } from "@/features/admin/pages/TransactionsPage";
import { Management } from "@/features/admin/pages/ManagementPage";

export function Admin() {
  const { kind = "dashboard" } = useParams();
  return kind === "reports" ? (
    <Reports />
  ) : kind === "dashboard" ? (
    <Dashboard />
  ) : kind === "orders" ? (
    <AdminOrders />
  ) : kind === "sepay-transactions" ? (
    <Transactions />
  ) : (
    <Management key={kind} kind={kind} />
  );
}
