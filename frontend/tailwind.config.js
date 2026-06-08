/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        game: {
          gold: '#F59E0B',
          'gold-light': '#FCD34D',
          dark: '#0F172A',
          panel: '#1E293B',
          border: '#334155',
        },
      },
      fontFamily: {
        game: ['"Cinzel"', 'serif'],
      },
    },
  },
  plugins: [],
}
