import { AppProvider } from "@/app/providers/AppProvider";
import { AppRoutes } from "@/app/router/AppRoutes";

export default function App() {
  return (
    <AppProvider>
      <AppRoutes />
    </AppProvider>
  );
}
