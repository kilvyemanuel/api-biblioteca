import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { useQueryClient } from "@tanstack/react-query";
import { DEFAULT_API_URL, getApiUrl, setApiUrl } from "@/lib/api";
import { PageHeader } from "@/components/biblioteca";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/configuracoes")({
  head: () => ({
    meta: [
      { title: "Configurações · Biblioteca" },
      { name: "description", content: "Defina o endereço da API da biblioteca." },
      { property: "og:title", content: "Configurações · Biblioteca" },
      { property: "og:description", content: "Defina o endereço da API da biblioteca." },
    ],
  }),
  component: Config,
});

function Config() {
  const qc = useQueryClient();
  const [url, setUrl] = useState("");
  const [teste, setTeste] = useState<string | null>(null);
  useEffect(() => setUrl(getApiUrl()), []);
  return (
    <div className="max-w-xl">
      <PageHeader title="Configurações" subtitle="Endereço da sua API Spring Boot." />
      <form className="grid gap-3 rounded-lg border border-border bg-card p-5" onSubmit={async (e) => {
        e.preventDefault(); setApiUrl(url); qc.clear(); toast.success("Endereço salvo");
        try { const r = await fetch(url.replace(/\/+$/, "") + "/categorias"); setTeste(r.ok ? "✅ API respondeu" : `⚠️ API respondeu com ${r.status}`); }
        catch { setTeste("❌ Não foi possível conectar (API desligada ou CORS bloqueado)."); }
      }}>
        <Label>URL base da API</Label>
        <Input value={url} onChange={(e) => setUrl(e.target.value)} placeholder={DEFAULT_API_URL} />
        <Button type="submit" className="w-fit">Salvar e testar</Button>
        {teste && <p className="text-sm">{teste}</p>}
      </form>
      <div className="mt-6 rounded-lg border border-border bg-muted p-4 text-sm text-muted-foreground">
        <p className="mb-2 font-medium text-foreground">Lembre de liberar CORS no Spring:</p>
        <pre className="overflow-x-auto text-xs">{`http.cors(Customizer.withDefaults());
// e um CorsConfigurationSource permitindo a origem deste site,
// métodos GET, POST, PUT, PATCH, DELETE e o header Authorization.`}</pre>
      </div>
    </div>
  );
}
