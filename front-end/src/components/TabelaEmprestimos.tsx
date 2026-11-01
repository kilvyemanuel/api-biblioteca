import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { api, fmtData, fmtMoeda, nomeDe, rotuloUsuario, type Emprestimo } from "@/lib/api";
import { Empty, StatusBadge } from "@/components/biblioteca";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

export function TabelaEmprestimos({ rows, gestao }: { rows: Emprestimo[]; gestao?: boolean }) {
  const qc = useQueryClient();
  const acao = useMutation({
    mutationFn: ({ id, a }: { id: number; a: string }) => api(`/emprestimos/${id}/${a}`, { method: "POST" }),
    onSuccess: (_d, v) => {
      toast.success({ devolucao: "Devolução registrada", renovacao: "Empréstimo renovado (+7 dias)", "pagamento-multa": "Multa paga" }[v.a]);
      qc.invalidateQueries({ queryKey: ["emprestimos"] }); qc.invalidateQueries({ queryKey: ["livros"] });
    },
    onError: (e) => toast.error(e.message),
  });
  if (rows.length === 0) return <Empty>Nenhum empréstimo encontrado.</Empty>;
  return (
    <div className="overflow-x-auto rounded-lg border border-border bg-card">
      <Table>
        <TableHeader><TableRow>
          <TableHead>#</TableHead><TableHead>Livro</TableHead>{gestao && <TableHead>Usuário</TableHead>}
          <TableHead>Emprestado</TableHead><TableHead>Prevista</TableHead><TableHead>Devolvido</TableHead>
          <TableHead>Status</TableHead><TableHead>Multa</TableHead><TableHead />
        </TableRow></TableHeader>
        <TableBody>
          {rows.map((e) => (
            <TableRow key={e.id}>
              <TableCell className="text-muted-foreground">{e.id}</TableCell>
              <TableCell className="font-medium">{e.tituloLivro ?? e.livroTitulo ?? nomeDe(e.livro, "titulo")}
                <div className="font-mono text-xs text-muted-foreground">{e.codigoExemplar ?? e.exemplarCodigo ?? nomeDe(e.exemplar, "codigo")}</div></TableCell>
              {gestao && <TableCell>{rotuloUsuario(e.usuarioNome ?? nomeDe(e.usuario, "nome"), e.usuarioId)}</TableCell>}
              <TableCell>{fmtData(e.dataEmprestimo)}</TableCell>
              <TableCell>{fmtData(e.dataPrevistaDevolucao)}{e.renovacoes > 0 && <span className="ml-1 text-xs text-muted-foreground">(renovado)</span>}</TableCell>
              <TableCell>{fmtData(e.dataDevolucao)}</TableCell>
              <TableCell><StatusBadge s={e.atrasado ? "ATRASADO" : e.status} /></TableCell>
              <TableCell>{e.multa > 0 ? <span className={e.multaPaga ? "text-muted-foreground line-through" : "font-semibold text-destructive"}>{fmtMoeda(e.multa)}</span> : "—"}</TableCell>
              <TableCell className="space-x-1 whitespace-nowrap text-right">
                {e.status === "ATIVO" && !e.atrasado && e.renovacoes < 1 &&
                  <Button size="sm" variant="outline" onClick={() => acao.mutate({ id: e.id, a: "renovacao" })}>Renovar</Button>}
                {gestao && e.status === "ATIVO" &&
                  <Button size="sm" onClick={() => acao.mutate({ id: e.id, a: "devolucao" })}>Devolver</Button>}
                {gestao && e.multa > 0 && !e.multaPaga &&
                  <Button size="sm" variant="secondary" onClick={() => acao.mutate({ id: e.id, a: "pagamento-multa" })}>Pagar multa</Button>}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
