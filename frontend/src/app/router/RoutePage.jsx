import { Suspense } from "react";

export function RoutePage({ children }) {
  return (
    <Suspense
      fallback={
        <div className="state" role="status">
          Đang tải trang…
        </div>
      }
    >
      {children}
    </Suspense>
  );
}
