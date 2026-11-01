import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { toast } from "sonner";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { ErrorBox } from "@/components/biblioteca";
import { api } from "@/lib/api";
import { useAuth } from "@/lib/auth";

export const Route = createFileRoute("/registro")({
  head: () => ({
    meta: [
      { title: "Criar conta · Biblioteca" },
      { name: "description", content: "Cadastre-se como membro da biblioteca." },
      { property: "og:title", content: "Criar conta · Biblioteca" },
      { property: "og:description", content: "Cadastre-se como membro da biblioteca." },
    ],
  }),
  component: Registro,
});

function Registro() {
  const { login } = useAuth();
  const nav = useNavigate();
  const [v, setV] = useState({ nome: "", email: "", senha: "" });
  const [erro, setErro] = useState<unknown>(null);
  const [loading, setLoading] = useState(false);
  return (
    <div className="mx-auto max-w-sm rounded-lg border border-border bg-card p-8">
      <h1 className="mb-1 font-display text-3xl">Criar conta</h1>
      <p className="mb-6 text-sm text-muted-foreground">Todo cadastro começa como membro.</p>
      <ErrorBox error={erro} />
      <form className="grid gap-4" onSubmit={async (e) => {
        e.preventDefault(); setErro(null); setLoading(true);
        try {
          const email = v.email.trim().toLowerCase();
          await api("/auth/registro", { method: "POST", body: { ...v, email } });
          await login(email, v.senha);
          toast.success("Conta criada!"); nav({ to: "/" });
        } catch (err) { setErro(err); } finally { setLoading(false); }
      }}>
        <div><Label>Nome</Label><Input required value={v.nome} onChange={(e) => setV({ ...v, nome: e.target.value })} /></div>
        <div><Label>E-mail</Label><Input type="email" required value={v.email} onChange={(e) => setV({ ...v, email: e.target.value })} /></div>
        <div><Label>Senha</Label><Input type="password" required minLength={6} value={v.senha} onChange={(e) => setV({ ...v, senha: e.target.value })} /></div>
        <Button type="submit" disabled={loading}>Cadastrar</Button>
      </form>
      <p className="mt-4 text-sm text-muted-foreground">Já tem conta? <Link to="/login" className="text-primary underline">Entrar</Link></p>
    </div>
  );
}
