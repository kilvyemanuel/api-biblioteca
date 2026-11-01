# Front-end da Biblioteca

Aplicação web em React + Vite para o sistema de biblioteca.

## Stack

- React
- TypeScript
- Vite
- Tailwind CSS
- TanStack Router / Query

## Como rodar localmente

```bash
cd front-end
npm install
npm run dev
```

A aplicação fica disponível em http://localhost:5173 por padrão.

## Scripts úteis

```bash
npm run build
npm run lint
npm test -- --run
```

## Observações

- O projeto usa os endpoints da API backend em `src/lib/api.ts`.
- O endereço padrão da API pode ser ajustado em `src/routes/configuracoes.tsx`.
