import { useState, useCallback, useEffect } from "react";

export function useNotifications() {
  const [toast, setToast] = useState(null);
  const notify = useCallback(
    (text, error = false) => setToast({ text, error }),
    [],
  );
  useEffect(() => {
    if (!toast) return;
    const timer = setTimeout(() => setToast(null), 5000);
    return () => clearTimeout(timer);
  }, [toast]);
  return { toast, setToast, notify };
}
