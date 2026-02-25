import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    proxy: {
      // nutrition-service — must come before the /api catch-all
      '/api/meals': 'http://localhost:8081',
      // main backend (auth, fitness, profile)
      '/api': 'http://localhost:8080'
    }
  }
})
