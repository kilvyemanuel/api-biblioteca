import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "sonner";
import { ArrowLeft, Pencil, Trash2 } from "lucide-react";
import { api, rotuloUsuario, type Exemplar, type LivroDetalhe, type Reserva, type StatusExemplar } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { ErrorBox, StatusBadge, selectCls } from "@/components/biblioteca";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { LivroForm } from "@/components/LivroForm";

export const Route = createFileRoute("/livros/$id")({
  head: () => ({
    meta: [
      { title: "Detalhes do livro · Biblioteca" },
      { name: "description", content: "Autores, categoria, sinopse e exemplares do livro." },
      { property: "og:title", content: "Detalhes do livro · Biblioteca" },
      { property: "og:description", content: "Autores, categoria, sinopse e exemplares do livro." },
    ],
  }),
  component: LivroPage,
});

function LivroPage() {
  const { id } = Route.useParams();
  const { usuario, pode } = useAuth();
  const qc = useQueryClient();
  const nav = useNavigate();
  const [editar, setEditar] = useState(false);
  const [codigo, setCodigo] = useState("");
  const isB = pode("BIBLIOTECARIO");

  const livro = useQuery({ queryKey: ["livro", id], queryFn: () => api<LivroDetalhe>(`/livros/${id}`) });
  const exemplares = useQuery({ queryKey: ["exemplares", id], queryFn: () => api<Exemplar[]>(`/livros/${id}/exemplares`), enabled: isB });
  const reservas = useQuery({ queryKey: ["reservas-livro", id], queryFn: () => api<Reserva[]>("/reservas", { query: { livroId: id } }), enabled: isB });

  const refresh = () => { qc.invalidateQueries({ queryKey: ["livro", id] }); qc.invalidateQueries({ queryKey: ["exemplares", id] }); qc.invalidateQueries({ queryKey: ["livros"] }); };
  const m = (fn: () => Promise<unknown>, ok: string, after?: () => void) => ({
    mutationFn: fn, onSuccess: () => { toast.success(ok); refresh(); after?.(); }, onError: (e: Error) => toast.error(e.message),
  });
  const reservar = useMutation(m(() => api("/reservas", { method: "POST", body: { livroId: Number(id) } }), "Reserva criada", () => qc.invalidateQueries({ queryKey: ["minhas-reservas"] })));
  const salvar = useMutation({ mutationFn: (b: unknown) => api(`/livros/${id}`, { method: "PUT", body: b }), onSuccess: () => { toast.success("Livro atualizado"); setEditar(false); refresh(); }, onError: (e: Error) => toast.error(e.message) });
  const excluir = useMutation(m(() => api(`/livros/${id}`, { method: "DELETE" }), "Livro excluído", () => nav({ to: "/" })));
  const addEx = useMutation(m(() => api(`/livros/${id}/exemplares`, { method: "POST", body: { codigo } }), "Exemplar adicionado", () => setCodigo("")));
  const status = useMutation({ mutationFn: ({ exId, s }: { exId: number; s: StatusExemplar }) => api(`/exemplares/${exId}/status`, { method: "PATCH", body: { status: s } }), onSuccess: () => { toast.success("Status alterado"); refresh(); }, onError: (e: Error) => toast.error(e.message) });
  const delEx = useMutation({ mutationFn: (exId: number) => api(`/exemplares/${exId}`, { method: "DELETE" }), onSuccess: () => { toast.success("Exemplar excluído"); refresh(); }, onError: (e: Error) => toast.error(e.message) });

  if (livro.isLoading) return <p className="text-muted-foreground">Carregando…</p>;
  if (livro.error) return <ErrorBox error={livro.error} />;
  const l = livro.data!;
  const cat = typeof l.categoria === "string" ? { nome: l.categoria, id: undefined } : l.categoria;
  const autores = l.autores.map((a) => (typeof a === "string" ? { nome: a, id: undefined } : a));
  const disponiveis = l.exemplaresDisponiveis ?? 0;

  return (
    <div className="space-y-8">
      <Link to="/" className="inline-flex items-center gap-1 text-sm text-muted-foreground hover:text-foreground"><ArrowLeft className="h-4 w-4" /> Catálogo</Link>
      <div className="grid gap-8 md:grid-cols-[1fr_280px]">
        <div>
          <Badge variant="outline">{cat?.nome}</Badge>
          <h1 className="mt-3 font-display text-5xl leading-tight">{l.titulo}</h1>
          <p className="mt-2 text-lg text-muted-foreground">{autores.map((a) => a.nome).join(", ")}</p>
          <p className="mt-6 max-w-2xl leading-relaxed">{l.sinopse || <span className="text-muted-foreground">Sem sinopse.</span>}</p>
          <dl className="mt-6 flex gap-8 text-sm">
            <div><dt className="text-muted-foreground">ISBN</dt><dd>{l.isbn}</dd></div>
            <div><dt className="text-muted-foreground">Ano</dt><dd>{l.anoPublicacao ?? "—"}</dd></div>
          </dl>
        </div>
        <aside className="h-fit space-y-3 rounded-lg border border-border bg-card p-5">
          <p className="font-display text-3xl text-primary">{disponiveis}<span className="text-base text-muted-foreground"> / {l.totalExemplares ?? "?"} disponíveis</span></p>
          {usuario && disponiveis === 0 && (
            <Button className="w-full" onClick={() => reservar.mutate()} disabled={reservar.isPending}>Reservar</Button>
          )}
          {usuario && disponiveis > 0 && <p className="text-sm text-muted-foreground">Disponível: solicite o empréstimo no balcão.</p>}
          {!usuario && <p className="text-sm text-muted-foreground"><Link to="/login" className="text-primary underline">Entre</Link> para reservar.</p>}
          {isB && (
            <div className="flex gap-2 border-t border-border pt-3">
              <Button variant="outline" size="sm" onClick={() => setEditar(true)}><Pencil className="h-4 w-4" /> Editar</Button>
              <Button variant="destructive" size="sm" onClick={() => confirm("Excluir este livro?") && excluir.mutate()}><Trash2 className="h-4 w-4" /> Excluir</Button>
            </div>
          )}
        </aside>
      </div>

      {isB && (
        <section className="grid gap-6 lg:grid-cols-2">
          <div className="rounded-lg border border-border bg-card p-5">
            <h2 className="mb-3 font-display text-2xl">Exemplares</h2>
            <form className="mb-3 flex gap-2" onSubmit={(e) => { e.preventDefault(); addEx.mutate(); }}>
              <Input placeholder="Código do exemplar" value={codigo} maxLength={30} required onChange={(e) => setCodigo(e.target.value)} />
              <Button type="submit" disabled={addEx.isPending}>Adicionar</Button>
            </form>
            <ErrorBox error={exemplares.error} />
            <Table>
              <TableHeader><TableRow><TableHead>Código</TableHead><TableHead>Status</TableHead><TableHead /></TableRow></TableHeader>
              <TableBody>
                {(exemplares.data ?? []).map((ex) => (
                  <TableRow key={ex.id}>
                    <TableCell className="font-mono text-xs">{ex.codigo}</TableCell>
                    <TableCell>
                      {ex.status === "EMPRESTADO" ? <StatusBadge s={ex.status} /> : (
                        <select className={selectCls} value={ex.status} onChange={(e) => status.mutate({ exId: ex.id, s: e.target.value as StatusExemplar })}>
                          {["DISPONIVEL", "MANUTENCAO", "PERDIDO"].map((s) => <option key={s}>{s}</option>)}
                        </select>
                      )}
                    </TableCell>
                    <TableCell className="text-right">
                      <Button variant="ghost" size="icon" onClick={() => confirm("Excluir exemplar?") && delEx.mutate(ex.id)}><Trash2 className="h-4 w-4" /></Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
          <div className="rounded-lg border border-border bg-card p-5">
            <h2 className="mb-3 font-display text-2xl">Reservas deste livro</h2>
            <ErrorBox error={reservas.error} />
            <Table>
              <TableHeader><TableRow><TableHead>Usuário</TableHead><TableHead>Criada</TableHead><TableHead>Status</TableHead></TableRow></TableHeader>
              <TableBody>
                {(reservas.data ?? []).map((r) => (
                  <TableRow key={r.id}>
                    <TableCell>{rotuloUsuario(r.usuarioNome, r.usuarioId)}</TableCell>
                    <TableCell>{(r.criadaEm ?? r.criadoEm)?.slice(0, 10)}</TableCell>
                    <TableCell><StatusBadge s={r.status} /></TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        </section>
      )}

      <LivroForm open={editar} onOpenChange={setEditar} loading={salvar.isPending} onSubmit={(b) => salvar.mutate(b)}
        inicial={{ titulo: l.titulo, isbn: l.isbn, anoPublicacao: l.anoPublicacao, sinopse: l.sinopse, categoriaId: cat?.id,
          autoresIds: autores.map((a) => a.id).filter((x): x is number => typeof x === "number") }} />
    </div>
  );
}
