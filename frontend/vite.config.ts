import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  server: {
    port: 3001,
    proxy: {
      // Route each service directly — no gateway needed for dev
      '/api/v1/auth':         { target: 'http://localhost:8081', changeOrigin: true },
      '/api/v1/wallets':      { target: 'http://localhost:8084', changeOrigin: true },
      '/api/v1/payments':     { target: 'http://localhost:8088', changeOrigin: true },
      '/api/v1/loans':        { target: 'http://localhost:8086', changeOrigin: true },
      '/api/v1/savings':      { target: 'http://localhost:8087', changeOrigin: true },
      '/api/v1/cooperatives': { target: 'http://localhost:8089', changeOrigin: true },
      '/api/v1/users':        { target: 'http://localhost:8082', changeOrigin: true },
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
  },
})
