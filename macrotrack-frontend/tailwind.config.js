/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: "var(--color-purple)",
        black: "var(--color-black)",
        white: "var(--color-white)",
        "gray-dark": "var(--color-gray-dark)",
        "gray-mid": "var(--color-gray-mid)",
        "gray-light": "var(--color-gray-light)",
        "text-muted": "var(--color-text-muted)",
        "gray-border": "#E5E5E5",
      }
    },
  },
  plugins: [],
}
