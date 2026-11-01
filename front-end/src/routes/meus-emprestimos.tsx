import { createFileRoute } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { api, asList, type Emprestimo, type Page } from "@/lib/api";
import { ErrorBox, Guard, PageHeader, Pager, selectCls } from "@/components/biblioteca";
import { TabelaEmprestimos } from "@/components/TabelaEmprestimos";

export const Route = createFileRoute("/meus-emprestimos")({
  head: () => ({
    meta: [
      { title: "Meus empréstimos · Biblioteca" },
      { name: "description", content: "Acompanhe prazos, renove e veja multas." },
      { property: "og:title", content: "Meus empréstimos · Biblioteca" },
      { property: "og:description", content: "Acompanhe prazos, renove e veja multas." },
    ],
  }),
  component: () => <Guard min="MEMBRO"><Meus /></Guard>,
});

function Meus() {
  const [status, setStatus] = useState(""); const [page, setPage] = useState(0);
  const q = useQuery({ queryKey: ["emprestimos", "meus", status, page], queryFn: () => api<Page<Emprestimo>>("/emprestimos/meus", { query: { status, page, size: 20 } }) });
  return (
    <>
      <PageHeader title="Meus empréstimos" subtitle="Prazo de 14 dias, uma renovação de +7 dias. Multa de R$ 1,00 por dia de atraso."
        actions={<select className={selectCls} value={status} onChange={(e) => { setStatus(e.target.value); setPage(0); }}>
          <option value="">Todos</option><option value="ATIVO">Ativos</option><option value="DEVOLVIDO">Devolvidos</option></select>} />
      <ErrorBox error={q.error} />
      <TabelaEmprestimos rows={asList(q.data)} />
      <Pager page={q.data} onChange={setPage} />
    </>
  );
}
