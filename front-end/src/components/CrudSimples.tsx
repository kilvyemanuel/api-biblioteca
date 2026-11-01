import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "sonner";
import { Pencil, Plus, Trash2 } from "lucide-react";
import { api, asList, type Page } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { Empty, ErrorBox, PageHeader, Pager } from "@/components/biblioteca";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

type Row = { id: number } & Record<string, unknown>;

/** CRUD genérico para recursos simples (categorias, autores). */
export function CrudSimples({ titulo, subtitulo, path, campos, paginado, busca }: {
  titulo: string; subtitulo: string; path: string; paginado?: boolean; busca?: string;
  campos: { key: string; label: string; required?: boolean }[];
}) {
  const { pode } = useAuth();
  const qc = useQueryClient();
  const [q, setQ] = useState(""); const [page, setPage] = useState(0);
  const [edit, setEdit] = useState<Row | "novo" | null>(null);
  const [form, setForm] = useState<Record<string, string>>({});

  const lista = useQuery({
    queryKey: [path.slice(1), "list", q, page],
    queryFn: () => api<Row[] | Page<Row>>(path, { query: paginado ? { [busca ?? "nome"]: q, page, size: 20 } : {} }),
  });
  const done = (msg: string) => () => { toast.success(msg); setEdit(null); qc.invalidateQueries({ queryKey: [path.slice(1)] }); };
  const salvar = useMutation({
    mutationFn: () => edit === "novo" ? api(path, { method: "POST", body: form }) : api(`${path}/${(edit as Row).id}`, { method: "PUT", body: form }),
    onSuccess: done("Salvo"), onError: (e) => toast.error(e.message),
  });
  const excluir = useMutation({ mutationFn: (id: number) => api(`${path}/${id}`, { method: "DELETE" }), onSuccess: done("Excluído"), onError: (e) => toast.error(e.message) });

  const abrir = (r: Row | "novo") => {
    setForm(Object.fromEntries(campos.map((c) => [c.key, r === "novo" ? "" : String(r[c.key] ?? "")])));
    setEdit(r);
  };
  let rows = asList(lista.data);
  if (!paginado && q) rows = rows.filter((r) => String(r[campos[0]?.key ?? ""]).toLowerCase().includes(q.toLowerCase()));

  return (
    <>
      <PageHeader title={titulo} subtitle={subtitulo}
        actions={pode("BIBLIOTECARIO") && <Button onClick={() => abrir("novo")}><Plus className="h-4 w-4" /> Novo</Button>} />
      <Input className="mb-4 max-w-sm" placeholder="Buscar…" value={q} onChange={(e) => { setQ(e.target.value); setPage(0); }} />
      <ErrorBox error={lista.error} />
      {rows.length === 0 && !lista.isLoading ? <Empty>Nada por aqui ainda.</Empty> : (
        <div className="rounded-lg border border-border bg-card">
          <Table>
            <TableHeader><TableRow>{campos.map((c) => <TableHead key={c.key}>{c.label}</TableHead>)}{pode("BIBLIOTECARIO") && <TableHead />}</TableRow></TableHeader>
            <TableBody>
              {rows.map((r) => (
                <TableRow key={r.id}>
                  {campos.map((c, i) => <TableCell key={c.key} className={i === 0 ? "font-medium" : "text-muted-foreground"}>{String(r[c.key] ?? "—")}</TableCell>)}
                  {pode("BIBLIOTECARIO") && (
                    <TableCell className="text-right">
                      <Button variant="ghost" size="icon" onClick={() => abrir(r)}><Pencil className="h-4 w-4" /></Button>
                      <Button variant="ghost" size="icon" onClick={() => confirm("Excluir?") && excluir.mutate(r.id)}><Trash2 className="h-4 w-4" /></Button>
                    </TableCell>
                  )}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      )}
      {paginado && <Pager page={lista.data as Page<unknown>} onChange={setPage} />}
      <Dialog open={!!edit} onOpenChange={(o) => !o && setEdit(null)}>
        <DialogContent>
          <DialogHeader><DialogTitle>{edit === "novo" ? "Novo" : "Editar"}</DialogTitle></DialogHeader>
          <form className="grid gap-3" onSubmit={(e) => { e.preventDefault(); salvar.mutate(); }}>
            {campos.map((c) => (
              <div key={c.key}><Label>{c.label}</Label>
                <Input required={c.required} value={form[c.key] ?? ""} onChange={(e) => setForm({ ...form, [c.key]: e.target.value })} /></div>
            ))}
            <DialogFooter><Button type="submit" disabled={salvar.isPending}>Salvar</Button></DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </>
  );
}
