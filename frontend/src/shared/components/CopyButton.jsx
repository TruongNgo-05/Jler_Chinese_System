import { useState } from "react";

export function Copy({ value }) {
  const [copied, setCopied] = useState(false);
  const [failed, setFailed] = useState(false);
  async function copy() {
    try {
      await navigator.clipboard.writeText(value);
      setCopied(true);
      setFailed(false);
    } catch {
      setFailed(true);
    }
  }
  return (
    <>
      <button type="button" className="copy-button" onClick={copy}>
        {copied ? "Đã sao chép" : "Sao chép"}
      </button>
      {failed && <small>Hãy chọn và sao chép nội dung thủ công.</small>}
    </>
  );
}
