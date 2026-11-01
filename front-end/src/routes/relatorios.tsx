import { createFileRoute } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import { api, fmtData, fmtMoeda } from "@/lib/api";
import { Empty, ErrorBox, Guard, PageHeader } from "@/components/biblioteca";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

export const Route = createFileRoute("/relatorios")({
  head: () => ({
    meta: [
      { title: "Relatórios · Biblioteca" },
      { name: "description", content: "Livros mais emprestados e empréstimos atrasados." },
      { property: "og:title", content: "Relatórios · Biblioteca" },
      { property: "og:description", content: "Livros mais emprestados e empréstimos atrasados." },
    ],
  }),
  component: () => <Guard min="BIBLIOTECARIO"><Relatorios /></Guard>,
});

type Rank = { livroId?: number; titulo?: string; livroTitulo?: string; total?: number; quantidade?: number; emprestimos?: number };
type Atraso = { id?: number; emprestimoId?: number; titulo?: string; livroTitulo?: string; usuarioNome?: string; usuario?: string; dataPrevistaDevolucao?: string; diasAtraso?: number; multaEstimada?: number };

function Relatorios() {
  const top = useQuery({ queryKey: ["rel", "top"], queryFn: () => api<Rank[]>("/relatorios/livros-mais-emprestados", { query: { limite: 10 } }) });
  const atr = useQuery({ queryKey: ["rel", "atrasados"], queryFn: () => api<Atraso[]>("/relatorios/atrasados") });
  const max = Math.max(1, ...(top.data ?? []).map((r) => r.total ?? r.quantidade ?? r.emprestimos ?? 0));
  return (
    <>
      <PageHeader title="Relatórios" subtitle="Visão geral da circulação do acervo." />
      <div className="grid gap-6 lg:grid-cols-2">
        <section className="rounded-lg border border-border bg-card p-5">
          <h2 className="mb-4 font-display text-2xl">Mais emprestados</h2>
          <ErrorBox error={top.error} />
          {(top.data ?? []).length === 0 && !top.isLoading ? <Empty>Sem dados.</Empty> : (
            <ol className="space-y-3">
              {(top.data ?? []).map((r, i) => {
                const n = r.total ?? r.quantidade ?? r.emprestimos ?? 0;
                return (
                  <li key={i}>
                    <div className="flex justify-between text-sm"><span>{i + 1}. {r.titulo ?? r.livroTitulo}</span><span className="font-semibold">{n}</span></div>
                    <div className="mt-1 h-2 rounded-full bg-muted"><div className="h-2 rounded-full bg-primary" style={{ width: `${(n / max) * 100}%` }} /></div>
                  </li>
                );
              })}
            </ol>
          )}
        </section>
        <section className="rounded-lg border border-border bg-card p-5">
          <h2 className="mb-4 font-display text-2xl">Atrasados</h2>
          <ErrorBox error={atr.error} />
          {(atr.data ?? []).length === 0 && !atr.isLoading ? <Empty>Nenhum atraso. 🎉</Empty> : (
            <Table>
              <TableHeader><TableRow><TableHead>Livro</TableHead><TableHead>Usuário</TableHead><TableHead>Prevista</TableHead><TableHead>Dias</TableHead><TableHead>Multa</TableHead></TableRow></TableHeader>
              <TableBody>
                {(atr.data ?? []).map((a, i) => (
                  <TableRow key={a.id ?? a.emprestimoId ?? i}>
                    <TableCell>{a.titulo ?? a.livroTitulo}</TableCell><TableCell>{a.usuarioNome ?? a.usuario}</TableCell>
                    <TableCell>{fmtData(a.dataPrevistaDevolucao)}</TableCell><TableCell>{a.diasAtraso}</TableCell>
                    <TableCell className="text-destructive">{fmtMoeda(a.multaEstimada)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </section>
      </div>
    </>
  );
}
