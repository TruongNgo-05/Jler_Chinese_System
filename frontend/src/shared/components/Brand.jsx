import { Link } from "react-router-dom";
import logo from "@/assets/jler-logo.svg";

export function Brand({ to = "/" }) {
  return (
    <Link className="brand jler-brand" to={to} aria-label="JLer">
      <img src={logo} alt="JLer" />
    </Link>
  );
}
