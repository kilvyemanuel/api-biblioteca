import { createContext, useCallback, useContext, useEffect, useState, type ReactNode } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { api, getToken, setToken, temPapel, type Papel, type TokenResponse, type Usuario } from "./api";

interface AuthCtx {
  usuario: Usuario | null;
  pronto: boolean;
  login: (email: string, senha: string) => Promise<void>;
  logout: () => void;
  pode: (p: Papel) => boolean;
}
const Ctx = createContext<AuthCtx | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [pronto, setPronto] = useState(false);
  const qc = useQueryClient();

  const carregar = useCallback(async () => {
    if (!getToken()) { setUsuario(null); setPronto(true); return; }
    try { setUsuario(await api<Usuario>("/usuarios/me")); }
    catch { setUsuario(null); }
    finally { setPronto(true); }
  }, []);

  useEffect(() => {
    carregar();
    const onLogout = () => setUsuario(null);
    window.addEventListener("biblioteca:logout", onLogout);
    return () => window.removeEventListener("biblioteca:logout", onLogout);
  }, [carregar]);

  const login = async (email: string, senha: string) => {
    const r = await api<TokenResponse>("/auth/login", { method: "POST", body: { email: email.trim().toLowerCase(), senha } });
    setToken(r.token);
    qc.clear();
    await carregar();
  };
  const logout = () => { setToken(null); setUsuario(null); qc.clear(); };

  return (
    <Ctx.Provider value={{ usuario, pronto, login, logout, pode: (p) => temPapel(usuario?.papel, p) }}>
      {children}
    </Ctx.Provider>
  );
}

export function useAuth() {
  const c = useContext(Ctx);
  if (!c) throw new Error("useAuth fora do AuthProvider");
  return c;
}
