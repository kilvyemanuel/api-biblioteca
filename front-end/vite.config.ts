import { defineConfig } from "@lovable.dev/vite-tanstack-config";

export default defineConfig({
  // Se apagou o src/server.ts e src/start.ts nos passos anteriores, 
  // pode remover o bloco tanstackStart completamente. 
  // Se ainda os tem, mantenha-o.

  vite: {
    server: {
      host: true, // Permite que o Docker exponha a porta para fora do container
      port: 5173,
      watch: {
        usePolling: true, // Essencial para o hot-reload funcionar com volumes do Docker
      }
    }
  }
});