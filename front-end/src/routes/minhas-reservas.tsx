import { createFileRoute, Link } from "@tanstack/react-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { api, asList, nomeDe, type Page, type Reserva } from "@/lib/api";
import { Empty, ErrorBox, Guard, PageHeader, StatusBadge } from "@/components/biblioteca";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

export const Route = createFileRoute("/minhas-reservas")({
  head: () => ({
    meta: [
      { title: "Minhas reservas · Biblioteca" },
      { name: "description", content: "Veja e cancele suas reservas de livros." },
      { property: "og:title", content: "Minhas reservas · Biblioteca" },
      { property: "og:description", content: "Veja e cancele suas reservas de livros." },
    ],
  }),
  component: () => <Guard min="MEMBRO"><Minhas /></Guard>,
});

function Minhas() {
  const qc = useQueryClient();
  const q = useQuery({ queryKey: ["minhas-reservas"], queryFn: () => api<Reserva[] | Page<Reserva>>("/reservas/minhas") });
  const cancelar = useMutation({
    mutationFn: (id: number) => api(`/reservas/${id}`, { method: "DELETE" }),
    onSuccess: () => { toast.success("Reserva cancelada"); qc.invalidateQueries({ queryKey: ["minhas-reservas"] }); },
    onError: (e) => toast.error(e.message),
  });
  const rows = asList(q.data);
  return (
    <>
      <PageHeader title="Minhas reservas" subtitle="Reserve livros sem exemplar disponível no catálogo." />
      <ErrorBox error={q.error} />
      {rows.length === 0 && !q.isLoading ? <Empty>Você não tem reservas. <Link to="/" className="text-primary underline">Ver catálogo</Link></Empty> : (
        <div className="rounded-lg border border-border bg-card">
          <Table>
            <TableHeader><TableRow><TableHead>Livro</TableHead><TableHead>Criada em</TableHead><TableHead>Status</TableHead><TableHead /></TableRow></TableHeader>
            <TableBody>
              {rows.map((r) => (
                <TableRow key={r.id}>
                  <TableCell className="font-medium">
                    {r.livroId ? <Link to="/livros/$id" params={{ id: String(r.livroId) }} className="hover:text-primary">{r.tituloLivro ?? r.livroTitulo ?? nomeDe(r.livro, "titulo")}</Link> : r.tituloLivro ?? r.livroTitulo ?? nomeDe(r.livro, "titulo")}
                  </TableCell>
                  <TableCell>{(r.criadaEm ?? r.criadoEm)?.slice(0, 10)}</TableCell>
                  <TableCell><StatusBadge s={r.status} /></TableCell>
                  <TableCell className="text-right">
                    {r.status === "ATIVA" && <Button size="sm" variant="outline" onClick={() => cancelar.mutate(r.id)}>Cancelar</Button>}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      )}
    </>
  );
}
