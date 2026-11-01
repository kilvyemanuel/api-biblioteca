import { createFileRoute } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { api, asList, nomeDe, rotuloUsuario, type LivroResumo, type Page, type Reserva } from "@/lib/api";
import { Empty, ErrorBox, Guard, PageHeader, StatusBadge, selectCls } from "@/components/biblioteca";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

export const Route = createFileRoute("/reservas")({
  head: () => ({
    meta: [
      { title: "Reservas · Biblioteca" },
      { name: "description", content: "Fila de reservas por livro." },
      { property: "og:title", content: "Reservas · Biblioteca" },
      { property: "og:description", content: "Fila de reservas por livro." },
    ],
  }),
  component: () => <Guard min="BIBLIOTECARIO"><Reservas /></Guard>,
});

function Reservas() {
  const [livroId, setLivroId] = useState("");
  const livros = useQuery({ queryKey: ["livros", "todos-reservas"], queryFn: () => api<Page<LivroResumo>>("/livros", { query: { size: 200, sort: "titulo,asc" } }) });
  const q = useQuery({ queryKey: ["reservas", livroId], queryFn: () => api<Reserva[] | Page<Reserva>>("/reservas", { query: { livroId } }) });
  const rows = asList(q.data);
  return (
    <>
      <PageHeader title="Reservas" subtitle="Quem está esperando cada livro."
        actions={<select className={selectCls} value={livroId} onChange={(e) => setLivroId(e.target.value)}>
          <option value="">Todos os livros</option>
          {asList(livros.data).map((l) => <option key={l.id} value={l.id}>{l.titulo}</option>)}
        </select>} />
      <ErrorBox error={q.error} />
      {rows.length === 0 && !q.isLoading ? <Empty>Nenhuma reserva.</Empty> : (
        <div className="rounded-lg border border-border bg-card">
          <Table>
            <TableHeader><TableRow><TableHead>#</TableHead><TableHead>Livro</TableHead><TableHead>Usuário</TableHead><TableHead>Criada</TableHead><TableHead>Status</TableHead></TableRow></TableHeader>
            <TableBody>
              {rows.map((r) => (
                <TableRow key={r.id}>
                  <TableCell className="text-muted-foreground">{r.id}</TableCell>
                  <TableCell className="font-medium">{r.tituloLivro ?? r.livroTitulo ?? nomeDe(r.livro, "titulo")}</TableCell>
                  <TableCell>{rotuloUsuario(r.usuarioNome, r.usuarioId)}</TableCell>
                  <TableCell>{(r.criadaEm ?? r.criadoEm)?.slice(0, 10)}</TableCell>
                  <TableCell><StatusBadge s={r.status} /></TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      )}
    </>
  );
}
