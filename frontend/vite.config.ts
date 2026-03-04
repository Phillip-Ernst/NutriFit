import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    proxy: {
      // nutrition-service — must come before the /api catch-all
      '/api/meals': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      // main backend (auth, fitness, profile, OAuth2)
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    }
  }
})
