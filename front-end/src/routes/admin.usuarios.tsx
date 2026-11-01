import { createFileRoute } from "@tanstack/react-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "sonner";
import { api, asList, type Page, type Papel, type Usuario } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { ErrorBox, Guard, PageHeader, Pager, selectCls } from "@/components/biblioteca";
import { Switch } from "@/components/ui/switch";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

export const Route = createFileRoute("/admin/usuarios")({
  head: () => ({
    meta: [
      { title: "Usuários · Biblioteca" },
      { name: "description", content: "Gerencie papéis e contas dos usuários." },
      { property: "og:title", content: "Usuários · Biblioteca" },
      { property: "og:description", content: "Gerencie papéis e contas dos usuários." },
    ],
  }),
  component: () => <Guard min="ADMIN"><Usuarios /></Guard>,
});

function Usuarios() {
  const { usuario: eu } = useAuth();
  const qc = useQueryClient();
  const [f, setF] = useState({ papel: "", ativo: "", page: 0 });
  const q = useQuery({ queryKey: ["admin-usuarios", f], queryFn: () => api<Page<Usuario>>("/admin/usuarios", { query: { ...f, size: 20 } }) });
  const opts = { onSuccess: () => { toast.success("Usuário atualizado"); qc.invalidateQueries({ queryKey: ["admin-usuarios"] }); }, onError: (e: Error) => toast.error(e.message) };
  const papel = useMutation({ mutationFn: ({ id, p }: { id: number; p: Papel }) => api(`/admin/usuarios/${id}/papel`, { method: "PATCH", body: { papel: p } }), ...opts });
  const ativo = useMutation({ mutationFn: ({ id, a }: { id: number; a: boolean }) => api(`/admin/usuarios/${id}/ativo`, { method: "PATCH", body: { ativo: a } }), ...opts });

  return (
    <>
      <PageHeader title="Usuários" subtitle="Altere papéis e ative ou desative contas."
        actions={<div className="flex gap-2">
          <select className={selectCls} value={f.papel} onChange={(e) => setF({ ...f, papel: e.target.value, page: 0 })}>
            <option value="">Todos os papéis</option><option>MEMBRO</option><option>BIBLIOTECARIO</option><option>ADMIN</option>
          </select>
          <select className={selectCls} value={f.ativo} onChange={(e) => setF({ ...f, ativo: e.target.value, page: 0 })}>
            <option value="">Ativos e inativos</option><option value="true">Ativos</option><option value="false">Inativos</option>
          </select>
        </div>} />
      <ErrorBox error={q.error} />
      <div className="rounded-lg border border-border bg-card">
        <Table>
          <TableHeader><TableRow><TableHead>#</TableHead><TableHead>Nome</TableHead><TableHead>E-mail</TableHead><TableHead>Papel</TableHead><TableHead>Ativo</TableHead></TableRow></TableHeader>
          <TableBody>
            {asList(q.data).map((u) => {
              const self = u.id === eu?.id;
              return (
                <TableRow key={u.id}>
                  <TableCell className="text-muted-foreground">{u.id}</TableCell>
                  <TableCell className="font-medium">{u.nome}{self && <span className="ml-2 text-xs text-muted-foreground">(você)</span>}</TableCell>
                  <TableCell>{u.email}</TableCell>
                  <TableCell>
                    <select className={selectCls} disabled={self} value={u.papel} onChange={(e) => papel.mutate({ id: u.id, p: e.target.value as Papel })}>
                      <option>MEMBRO</option><option>BIBLIOTECARIO</option><option>ADMIN</option>
                    </select>
                  </TableCell>
                  <TableCell><Switch disabled={self} checked={u.ativo} onCheckedChange={(a) => ativo.mutate({ id: u.id, a })} /></TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </div>
      <Pager page={q.data} onChange={(p) => setF({ ...f, page: p })} />
    </>
  );
}
