import { message } from "@/shared/utils/errors";
import { useCallback, useEffect, useRef, useState } from "react";
import { api } from "@/shared/api/http";

export function useData(path) {
  const [state, setState] = useState({
    path,
    data: null,
    error: "",
    loading: !!path,
  });
  const serial = useRef(0);
  const reload = useCallback(async () => {
    const id = ++serial.current;
    if (!path) {
      setState({ path, data: null, error: "", loading: false });
      return;
    }
    setState((s) => ({
      path,
      data: s.path === path ? s.data : null,
      error: "",
      loading: true,
    }));
    try {
      const r = await api.get(path);
      if (id === serial.current)
        setState({ path, data: r.data, error: "", loading: false });
      return r.data;
    } catch (e) {
      if (id === serial.current)
        setState((s) => ({ ...s, error: message(e), loading: false }));
    }
  }, [path]);
  useEffect(() => {
    const requestSerial = serial;
    reload();
    return () => {
      requestSerial.current++;
    };
  }, [reload]);
  const setData = useCallback(
    (data) => {
      serial.current++;
      setState({ path, data, error: "", loading: false });
    },
    [path],
  );
  return {
    ...(state.path === path
      ? state
      : { data: null, error: "", loading: !!path }),
    reload,
    setData,
  };
}
