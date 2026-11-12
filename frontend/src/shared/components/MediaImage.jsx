import { useState } from "react";
import { media } from "@/shared/utils/media";

export function Image({ src, alt = "", className = "" }) {
  const [failed, setFailed] = useState(null);
  return src && failed !== src ? (
    <img
      className={className}
      src={media(src)}
      alt={alt}
      loading="lazy"
      onError={() => setFailed(src)}
    />
  ) : (
    <div className={`placeholder ${className}`} role="img" aria-label={alt}>
      学
    </div>
  );
}
