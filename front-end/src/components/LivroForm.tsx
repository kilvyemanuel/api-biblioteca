import { useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { api, asList, type Autor, type Categoria, type Page } from "@/lib/api";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { selectCls } from "@/components/biblioteca";

export interface LivroBody { titulo: string; isbn: string; anoPublicacao?: number | undefined; sinopse?: string | undefined; categoriaId: number; autoresIds: number[] }

export function LivroForm({ open, onOpenChange, onSubmit, loading, inicial }: {
  open: boolean; onOpenChange: (o: boolean) => void; onSubmit: (b: LivroBody) => void; loading?: boolean; inicial?: { [K in keyof LivroBody]?: LivroBody[K] | undefined };
}) {
  const [v, setV] = useState({ titulo: "", isbn: "", ano: "", sinopse: "", categoriaId: "", autoresIds: [] as number[] });
  useEffect(() => {
    if (open) setV({
      titulo: inicial?.titulo ?? "", isbn: inicial?.isbn ?? "", ano: inicial?.anoPublicacao ? String(inicial.anoPublicacao) : "",
      sinopse: inicial?.sinopse ?? "", categoriaId: inicial?.categoriaId ? String(inicial.categoriaId) : "", autoresIds: inicial?.autoresIds ?? [],
    });
  }, [open, inicial]);
  const cats = useQuery({ queryKey: ["categorias"], queryFn: () => api<Categoria[] | Page<Categoria>>("/categorias"), enabled: open });
  const autores = useQuery({ queryKey: ["autores", "all"], queryFn: () => api<Page<Autor>>("/autores", { query: { size: 200 } }), enabled: open });
  const toggle = (id: number) => setV((s) => ({ ...s, autoresIds: s.autoresIds.includes(id) ? s.autoresIds.filter((x) => x !== id) : [...s.autoresIds, id] }));

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg">
        <DialogHeader><DialogTitle>{inicial ? "Editar livro" : "Novo livro"}</DialogTitle></DialogHeader>
        <form className="grid gap-3" onSubmit={(e) => {
          e.preventDefault();
          onSubmit({ titulo: v.titulo, isbn: v.isbn, anoPublicacao: v.ano ? Number(v.ano) : undefined, sinopse: v.sinopse || undefined, categoriaId: Number(v.categoriaId), autoresIds: v.autoresIds });
        }}>
          <div><Label>Título</Label><Input required maxLength={200} value={v.titulo} onChange={(e) => setV({ ...v, titulo: e.target.value })} /></div>
          <div className="grid grid-cols-2 gap-3">
            <div><Label>ISBN</Label><Input required maxLength={17} value={v.isbn} onChange={(e) => setV({ ...v, isbn: e.target.value })} /></div>
            <div><Label>Ano</Label><Input type="number" min={1} value={v.ano} onChange={(e) => setV({ ...v, ano: e.target.value })} /></div>
          </div>
          <div><Label>Categoria</Label>
            <select required className={selectCls + " w-full"} value={v.categoriaId} onChange={(e) => setV({ ...v, categoriaId: e.target.value })}>
              <option value="">Selecione…</option>
              {asList(cats.data).map((c) => <option key={c.id} value={c.id}>{c.nome}</option>)}
            </select>
          </div>
          <div><Label>Autores (pelo menos um)</Label>
            <div className="mt-1 flex max-h-36 flex-wrap gap-2 overflow-auto rounded-md border border-input p-2">
              {asList(autores.data).map((a) => (
                <button type="button" key={a.id} onClick={() => toggle(a.id)}
                  className={"rounded-full border px-3 py-1 text-xs transition " + (v.autoresIds.includes(a.id) ? "border-primary bg-primary text-primary-foreground" : "border-border hover:bg-secondary")}>
                  {a.nome}
                </button>
              ))}
              {asList(autores.data).length === 0 && <span className="text-xs text-muted-foreground">Cadastre autores primeiro.</span>}
            </div>
          </div>
          <div><Label>Sinopse</Label><Textarea rows={3} value={v.sinopse} onChange={(e) => setV({ ...v, sinopse: e.target.value })} /></div>
          <DialogFooter><Button type="submit" disabled={loading || v.autoresIds.length === 0}>Salvar</Button></DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
