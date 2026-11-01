import { createFileRoute } from "@tanstack/react-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "sonner";
import { api, asList, type Emprestimo, type LivroResumo, type Page, type Usuario } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { ErrorBox, Guard, PageHeader, Pager, selectCls } from "@/components/biblioteca";
import { TabelaEmprestimos } from "@/components/TabelaEmprestimos";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";

export const Route = createFileRoute("/emprestimos")({
  head: () => ({
    meta: [
      { title: "Balcão de empréstimos · Biblioteca" },
      { name: "description", content: "Registre empréstimos, devoluções e pagamentos de multa." },
      { property: "og:title", content: "Balcão de empréstimos · Biblioteca" },
      { property: "og:description", content: "Registre empréstimos, devoluções e pagamentos de multa." },
    ],
  }),
  component: () => <Guard min="BIBLIOTECARIO"><Balcao /></Guard>,
});

function Balcao() {
  const { pode } = useAuth();
  const qc = useQueryClient();
  const [f, setF] = useState({ status: "", usuarioId: "", atrasados: false, page: 0 });
  const [novo, setNovo] = useState({ usuarioId: "", livroId: "", busca: "" });

  const lista = useQuery({ queryKey: ["emprestimos", "todos", f], queryFn: () => api<Page<Emprestimo>>("/emprestimos", { query: { ...f, atrasados: f.atrasados || undefined, size: 20 } }) });
  const livros = useQuery({ queryKey: ["livros", "balcao", novo.busca], queryFn: () => api<Page<LivroResumo>>("/livros", { query: { titulo: novo.busca, disponivel: true, size: 30 } }) });
  const usuarios = useQuery({ queryKey: ["admin-usuarios", "balcao"], queryFn: () => api<Page<Usuario>>("/admin/usuarios", { query: { ativo: true, size: 200 } }), enabled: pode("ADMIN") });

  const emprestar = useMutation({
    mutationFn: () => api("/emprestimos", { method: "POST", body: { usuarioId: Number(novo.usuarioId), livroId: Number(novo.livroId) } }),
    onSuccess: () => { toast.success("Empréstimo registrado (14 dias)"); setNovo({ ...novo, livroId: "" }); qc.invalidateQueries({ queryKey: ["emprestimos"] }); qc.invalidateQueries({ queryKey: ["livros"] }); },
    onError: (e) => toast.error(e.message),
  });

  return (
    <>
      <PageHeader title="Balcão" subtitle="Registre empréstimos para membros e gerencie devoluções." />
      <form className="mb-8 grid gap-3 rounded-lg border border-border bg-card p-5 md:grid-cols-[1fr_1fr_1fr_auto] md:items-end"
        onSubmit={(e) => { e.preventDefault(); emprestar.mutate(); }}>
        <div><Label>Membro</Label>
          {pode("ADMIN") ? (
            <select required className={selectCls + " w-full"} value={novo.usuarioId} onChange={(e) => setNovo({ ...novo, usuarioId: e.target.value })}>
              <option value="">Selecione…</option>
              {asList(usuarios.data).map((u) => <option key={u.id} value={u.id}>(#{u.id}) {u.nome} ({u.email})</option>)}
            </select>
          ) : <Input required type="number" placeholder="Id do usuário" value={novo.usuarioId} onChange={(e) => setNovo({ ...novo, usuarioId: e.target.value })} />}
        </div>
        <div><Label>Buscar livro disponível</Label><Input placeholder="Título…" value={novo.busca} onChange={(e) => setNovo({ ...novo, busca: e.target.value })} /></div>
        <div><Label>Livro</Label>
          <select required className={selectCls + " w-full"} value={novo.livroId} onChange={(e) => setNovo({ ...novo, livroId: e.target.value })}>
            <option value="">Selecione…</option>
            {asList(livros.data).map((l) => <option key={l.id} value={l.id}>{l.titulo} ({l.exemplaresDisponiveis})</option>)}
          </select>
        </div>
        <Button type="submit" disabled={emprestar.isPending}>Emprestar</Button>
      </form>

      <div className="mb-4 flex flex-wrap items-center gap-3">
        <select className={selectCls} value={f.status} onChange={(e) => setF({ ...f, status: e.target.value, page: 0 })}>
          <option value="">Todos os status</option><option value="ATIVO">Ativos</option><option value="DEVOLVIDO">Devolvidos</option>
        </select>
        <Input className="w-40" type="number" placeholder="ID do usuário" value={f.usuarioId} onChange={(e) => setF({ ...f, usuarioId: e.target.value, page: 0 })} />
        <label className="flex items-center gap-2 text-sm"><Switch checked={f.atrasados} onCheckedChange={(c) => setF({ ...f, atrasados: c, page: 0 })} /> Só atrasados</label>
      </div>
      <ErrorBox error={lista.error} />
      <TabelaEmprestimos rows={asList(lista.data)} gestao />
      <Pager page={lista.data} onChange={(p) => setF({ ...f, page: p })} />
    </>
  );
}
