import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { ErrorBox } from "@/components/biblioteca";
import { useAuth } from "@/lib/auth";

export const Route = createFileRoute("/login")({
  head: () => ({
    meta: [
      { title: "Entrar · Biblioteca" },
      { name: "description", content: "Acesse sua conta da biblioteca." },
      { property: "og:title", content: "Entrar · Biblioteca" },
      { property: "og:description", content: "Acesse sua conta da biblioteca." },
    ],
  }),
  component: Login,
});

function Login() {
  const { login } = useAuth();
  const nav = useNavigate();
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState<unknown>(null);
  const [loading, setLoading] = useState(false);
  return (
    <div className="mx-auto max-w-sm rounded-lg border border-border bg-card p-8">
      <h1 className="mb-6 font-display text-3xl">Entrar</h1>
      <ErrorBox error={erro} />
      <form className="grid gap-4" onSubmit={async (e) => {
        e.preventDefault(); setErro(null); setLoading(true);
        try { await login(email, senha); nav({ to: "/" }); } catch (err) { setErro(err); } finally { setLoading(false); }
      }}>
        <div><Label>E-mail</Label><Input type="email" required value={email} onChange={(e) => setEmail(e.target.value)} /></div>
        <div><Label>Senha</Label><Input type="password" required value={senha} onChange={(e) => setSenha(e.target.value)} /></div>
        <Button type="submit" disabled={loading}>{loading ? "Entrando…" : "Entrar"}</Button>
      </form>
      <p className="mt-4 text-sm text-muted-foreground">Não tem conta? <Link to="/registro" className="text-primary underline">Cadastre-se</Link></p>
    </div>
  );
}
