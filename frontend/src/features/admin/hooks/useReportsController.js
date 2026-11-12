import { useState } from "react";
import { adminApi } from "@/features/admin/api/adminApi";
import { useData } from "@/shared/hooks/useData";

export function useReportsController() {
  const [range, setRange] = useState({ from: "", to: "" });
  const [path, setPath] = useState(adminApi.reportsPath);
  const d = useData(path);
  const report = d.data;
  return { setPath, range, setRange, d, report };
}
