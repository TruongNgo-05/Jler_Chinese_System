import { useNotifications } from "@/shared/hooks/useNotifications";
import { useSession } from "@/features/auth/hooks/useSession";
import { AppContext } from "@/shared/context/AppContext";
import { Toast } from "@/shared/components/Toast";

export function AppProvider({ children }) {
  const { toast, setToast, notify } = useNotifications();
  const session = useSession(notify);
  return (
    <AppContext.Provider value={{ ...session, notify }}>
      {children}
      <Toast toast={toast} close={() => setToast(null)} />
    </AppContext.Provider>
  );
}
