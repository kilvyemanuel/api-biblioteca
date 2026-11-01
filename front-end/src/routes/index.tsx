import { createFileRoute, Link } from "@tanstack/react-router";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "sonner";
import { Plus } from "lucide-react";
import { api, asList, type Autor, type Categoria, type LivroResumo, type Page } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { Empty, ErrorBox, PageHeader, Pager, selectCls } from "@/components/biblioteca";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { LivroForm } from "@/components/LivroForm";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "Catálogo de livros · Biblioteca" },
      { name: "description", content: "Busque livros por título, autor, categoria e disponibilidade." },
      { property: "og:title", content: "Catálogo de livros · Biblioteca" },
      { property: "og:description", content: "Busque livros por título, autor, categoria e disponibilidade." },
    ],
  }),
  component: Catalogo,
});

function Catalogo() {
  const { pode } = useAuth();
  const qc = useQueryClient();
  const [f, setF] = useState({ titulo: "", autorId: "", categoriaId: "", disponivel: "", sort: "titulo,asc", page: 0 });
  const [novo, setNovo] = useState(false);
  const set = (k: string, v: string) => setF((s) => ({ ...s, [k]: v, page: 0 }));

  const livros = useQuery({
    queryKey: ["livros", f],
    queryFn: () => api<Page<LivroResumo>>("/livros", { query: { ...f, size: 12 } }),
  });
  const cats = useQuery({ queryKey: ["categorias"], queryFn: () => api<Categoria[] | Page<Categoria>>("/categorias") });
  const autores = useQuery({ queryKey: ["autores", "all"], queryFn: () => api<Page<Autor>>("/autores", { query: { size: 200 } }) });

  const criar = useMutation({
    mutationFn: (body: unknown) => api("/livros", { method: "POST", body }),
    onSuccess: () => { toast.success("Livro cadastrado"); setNovo(false); qc.invalidateQueries({ queryKey: ["livros"] }); },
    onError: (e) => toast.error(e.message),
  });

  return (
    <>
      <PageHeader title="Catálogo" subtitle="Explore o acervo da biblioteca."
        actions={pode("BIBLIOTECARIO") && <Button onClick={() => setNovo(true)}><Plus className="h-4 w-4" /> Novo livro</Button>} />
      <div className="mb-6 grid gap-3 rounded-lg border border-border bg-card p-4 md:grid-cols-5">
        <Input placeholder="Buscar por título…" value={f.titulo} onChange={(e) => set("titulo", e.target.value)} className="md:col-span-2" />
        <select className={selectCls} value={f.categoriaId} onChange={(e) => set("categoriaId", e.target.value)}>
          <option value="">Todas as categorias</option>
          {asList(cats.data).map((c) => <option key={c.id} value={c.id}>{c.nome}</option>)}
        </select>
        <select className={selectCls} value={f.autorId} onChange={(e) => set("autorId", e.target.value)}>
          <option value="">Todos os autores</option>
          {asList(autores.data).map((a) => <option key={a.id} value={a.id}>{a.nome}</option>)}
        </select>
        <div className="flex gap-2">
          <select className={selectCls + " flex-1"} value={f.disponivel} onChange={(e) => set("disponivel", e.target.value)}>
            <option value="">Qualquer</option><option value="true">Disponíveis</option><option value="false">Indisponíveis</option>
          </select>
          <select className={selectCls} value={f.sort} onChange={(e) => set("sort", e.target.value)} title="Ordenação">
            <option value="titulo,asc">A–Z</option><option value="titulo,desc">Z–A</option><option value="id,desc">Recentes</option>
          </select>
        </div>
      </div>
      <ErrorBox error={livros.error} />
      {livros.isLoading ? <p className="text-muted-foreground">Carregando…</p> :
        asList(livros.data).length === 0 ? <Empty>Nenhum livro encontrado.</Empty> : (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {asList(livros.data).map((l) => (
              <Link key={l.id} to="/livros/$id" params={{ id: String(l.id) }}
                className="group rounded-lg border border-border bg-card p-5 transition hover:-translate-y-0.5 hover:border-primary/40 hover:shadow-md">
                <div className="mb-3 flex items-start justify-between gap-2">
                  <Badge variant="outline">{l.categoria}</Badge>
                  <Badge variant={l.exemplaresDisponiveis > 0 ? "default" : "secondary"}>
                    {l.exemplaresDisponiveis > 0 ? `${l.exemplaresDisponiveis} disponível(is)` : "Indisponível"}
                  </Badge>
                </div>
                <h3 className="font-display text-xl leading-tight group-hover:text-primary">{l.titulo}</h3>
                <p className="mt-1 text-sm text-muted-foreground">{l.autores?.join(", ")}</p>
                <p className="mt-3 text-xs text-muted-foreground">ISBN {l.isbn}</p>
              </Link>
            ))}
          </div>
        )}
      <Pager page={livros.data} onChange={(p) => setF((s) => ({ ...s, page: p }))} />
      <LivroForm open={novo} onOpenChange={setNovo} onSubmit={(b) => criar.mutate(b)} loading={criar.isPending} />
    </>
  );
}
