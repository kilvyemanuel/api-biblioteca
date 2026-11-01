import { Link, useNavigate } from "@tanstack/react-router";
import { useEffect, type ReactNode } from "react";
import { BookOpen, LogOut, Settings } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { useAuth } from "@/lib/auth";
import type { Page, Papel } from "@/lib/api";

const links: { to: string; label: string; min?: Papel }[] = [
  { to: "/", label: "Catálogo" },
  { to: "/autores", label: "Autores" },
  { to: "/categorias", label: "Categorias" },
  { to: "/meus-emprestimos", label: "Meus empréstimos", min: "MEMBRO" },
  { to: "/minhas-reservas", label: "Minhas reservas", min: "MEMBRO" },
  { to: "/emprestimos", label: "Balcão", min: "BIBLIOTECARIO" },
  { to: "/reservas", label: "Reservas", min: "BIBLIOTECARIO" },
  { to: "/relatorios", label: "Relatórios", min: "BIBLIOTECARIO" },
  { to: "/admin/usuarios", label: "Usuários", min: "ADMIN" },
];

export function AppShell({ children }: { children: ReactNode }) {
  const { usuario, logout, pode } = useAuth();
  return (
    <div className="min-h-screen bg-background">
      <header className="border-b border-border bg-card/80 backdrop-blur sticky top-0 z-30">
        <div className="mx-auto flex max-w-7xl flex-wrap items-center gap-x-6 gap-y-2 px-4 py-3">
          <Link to="/" className="flex items-center gap-2 font-display text-xl text-primary">
            <BookOpen className="h-6 w-6" /> Biblioteca
          </Link>
          <nav className="flex flex-1 flex-wrap gap-1 text-sm">
            {links.filter((l) => !l.min || pode(l.min)).map((l) => (
              <Link key={l.to} to={l.to} activeOptions={{ exact: l.to === "/" }}
                className="rounded-md px-2.5 py-1.5 text-muted-foreground transition-colors hover:bg-secondary hover:text-foreground"
                activeProps={{ className: "bg-secondary text-foreground font-medium" }}>
                {l.label}
              </Link>
            ))}
          </nav>
          <div className="flex items-center gap-2">
            <Link to="/configuracoes" title="Configurar API"><Button variant="ghost" size="icon"><Settings className="h-4 w-4" /></Button></Link>
            {usuario ? (
              <>
                <span className="text-sm">{usuario.nome}</span>
                <Badge variant="secondary">{usuario.papel}</Badge>
                <Button variant="ghost" size="icon" onClick={logout} title="Sair"><LogOut className="h-4 w-4" /></Button>
              </>
            ) : (
              <>
                <Link to="/login"><Button size="sm" variant="outline">Entrar</Button></Link>
                <Link to="/registro"><Button size="sm">Criar conta</Button></Link>
              </>
            )}
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-7xl px-4 py-8">{children}</main>
    </div>
  );
}

export function PageHeader({ title, subtitle, actions }: { title: string; subtitle?: string; actions?: ReactNode }) {
  return (
    <div className="mb-6 flex flex-wrap items-end justify-between gap-4">
      <div>
        <h1 className="font-display text-4xl text-foreground">{title}</h1>
        {subtitle && <p className="mt-1 text-muted-foreground">{subtitle}</p>}
      </div>
      {actions}
    </div>
  );
}

export function Guard({ min, children }: { min: Papel; children: ReactNode }) {
  const { pronto, usuario, pode } = useAuth();
  const nav = useNavigate();
  useEffect(() => { if (pronto && !usuario) nav({ to: "/login" }); }, [pronto, usuario, nav]);
  if (!pronto) return <p className="text-muted-foreground">Carregando…</p>;
  if (!usuario) return null;
  if (!pode(min)) return <Empty>Seu perfil ({usuario.papel}) não tem acesso a esta página.</Empty>;
  return <>{children}</>;
}

export function Empty({ children }: { children: ReactNode }) {
  return <div className="rounded-lg border border-dashed border-border p-10 text-center text-muted-foreground">{children}</div>;
}

export function ErrorBox({ error }: { error: unknown }) {
  if (!error) return null;
  return <div className="mb-4 rounded-md border border-destructive/40 bg-destructive/10 p-3 text-sm text-destructive">{(error as Error).message}</div>;
}

export function Pager({ page, onChange }: { page?: Page<unknown> | undefined; onChange: (p: number) => void }) {
  if (!page || Array.isArray(page) || page.totalPages <= 1) return null;
  return (
    <div className="mt-4 flex items-center justify-end gap-2 text-sm">
      <span className="text-muted-foreground">Página {page.number + 1} de {page.totalPages} · {page.totalElements} itens</span>
      <Button size="sm" variant="outline" disabled={page.number === 0} onClick={() => onChange(page.number - 1)}>Anterior</Button>
      <Button size="sm" variant="outline" disabled={page.number + 1 >= page.totalPages} onClick={() => onChange(page.number + 1)}>Próxima</Button>
    </div>
  );
}

export const selectCls = "h-9 rounded-md border border-input bg-background px-3 text-sm";

const statusTone: Record<string, "default" | "secondary" | "destructive" | "outline"> = {
  DISPONIVEL: "default", ATIVO: "default", ATIVA: "default",
  EMPRESTADO: "secondary", ATENDIDA: "secondary", DEVOLVIDO: "outline",
  MANUTENCAO: "outline", CANCELADA: "outline", PERDIDO: "destructive", ATRASADO: "destructive",
};
export function StatusBadge({ s }: { s: string }) {
  return <Badge variant={statusTone[s] ?? "outline"}>{s}</Badge>;
}
