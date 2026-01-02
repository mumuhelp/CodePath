import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'https://volchari.ru',
        changeOrigin: true,
      },
      '/oauth2': {
        target: 'https://volchari.ru',
        changeOrigin: true,
      },
      '/login': {
        target: 'https://volchari.ru',
        changeOrigin: true,
      },
    },
  },
})
