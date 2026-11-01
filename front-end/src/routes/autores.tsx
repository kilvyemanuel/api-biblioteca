import { createFileRoute } from "@tanstack/react-router";
import { CrudSimples } from "@/components/CrudSimples";

export const Route = createFileRoute("/autores")({
  head: () => ({
    meta: [
      { title: "Autores · Biblioteca" },
      { name: "description", content: "Autores do acervo da biblioteca." },
      { property: "og:title", content: "Autores · Biblioteca" },
      { property: "og:description", content: "Autores do acervo da biblioteca." },
    ],
  }),
  component: () => (
    <CrudSimples titulo="Autores" subtitulo="Quem escreveu o nosso acervo." path="/autores" paginado
      campos={[{ key: "nome", label: "Nome", required: true }, { key: "nacionalidade", label: "Nacionalidade" }]} />
  ),
});
