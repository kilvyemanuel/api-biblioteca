import { createFileRoute } from "@tanstack/react-router";
import { CrudSimples } from "@/components/CrudSimples";

export const Route = createFileRoute("/categorias")({
  head: () => ({
    meta: [
      { title: "Categorias · Biblioteca" },
      { name: "description", content: "Categorias de livros da biblioteca." },
      { property: "og:title", content: "Categorias · Biblioteca" },
      { property: "og:description", content: "Categorias de livros da biblioteca." },
    ],
  }),
  component: () => (
    <CrudSimples titulo="Categorias" subtitulo="Como o acervo está organizado." path="/categorias"
      campos={[{ key: "nome", label: "Nome", required: true }, { key: "descricao", label: "Descrição" }]} />
  ),
});
