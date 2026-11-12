/** @type {import('tailwindcss').Config} */
export default {
  darkMode: "class",
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        primary: "#1F2F8C", // Deep Sea Blue
        "primary-dark": "#002840",
        secondary: "#F7931E", // Gold
        "secondary-dark": "#FDF8F3",
        "accent-red": "#C8102E", // Chinese Red
        surface: "#FDFBF7", // Rice paper white
        "gold-light": "#FFF5C2",
        white: "#FFFFFF",
        "accent-yellow": "#FFD700",
        /* 👇 THÊM MÀU CHỮ RIÊNG */

        "text-main": "#0D121B",
        "text-muted": "#6B7280",
        "text-invert": "#FFFFFF",
      },
      fontFamily: {
        display: ["Lexend", "Noto Sans SC", "sans-serif"],
        body: ["Lexend", "Noto Sans SC", "sans-serif"],
      },
      backgroundImage: {
        "chinese-pattern":
          "url('https://www.transparenttextures.com/patterns/cubes.png')",
        "cloud-pattern":
          "url('https://www.transparenttextures.com/patterns/chinese-cloud.png')",
      },
    },
  },
  plugins: [],
};
