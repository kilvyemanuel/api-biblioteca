// Cliente HTTP para a API de Biblioteca (Spring Boot).
export type Papel = "MEMBRO" | "BIBLIOTECARIO" | "ADMIN";
export type StatusExemplar = "DISPONIVEL" | "EMPRESTADO" | "MANUTENCAO" | "PERDIDO";

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
export interface Usuario { id: number; nome: string; email: string; papel: Papel; ativo: boolean; criadoEm?: string }
export interface Categoria { id: number; nome: string; descricao?: string }
export interface Autor { id: number; nome: string; nacionalidade?: string }
export interface LivroResumo { id: number; titulo: string; isbn: string; categoria: string; autores: string[]; exemplaresDisponiveis: number }
export interface LivroDetalhe {
  id: number; titulo: string; isbn: string; anoPublicacao?: number; sinopse?: string;
  categoria: Categoria | string; autores: (Autor | string)[];
  totalExemplares?: number; exemplaresDisponiveis?: number;
}
export interface Exemplar { id: number; codigo: string; status: StatusExemplar; livroId?: number }
export interface Emprestimo {
  id: number; usuarioId?: number; usuarioNome?: string; usuario?: string | { nome: string };
  livroId?: number; tituloLivro?: string; livroTitulo?: string; livro?: string | { titulo: string };
  codigoExemplar?: string; exemplarCodigo?: string; exemplar?: string | { codigo: string };
  dataEmprestimo: string; dataPrevistaDevolucao: string; dataDevolucao?: string | null;
  renovacoes: number; multa: number; multaPaga: boolean; status: "ATIVO" | "DEVOLVIDO"; atrasado: boolean;
}
export interface Reserva {
  id: number; livroId?: number; tituloLivro?: string; livroTitulo?: string; livro?: string | { titulo: string };
  usuarioId?: number; usuarioNome?: string; status: "ATIVA" | "ATENDIDA" | "CANCELADA"; criadoEm?: string; criadaEm?: string;
}
export interface TokenResponse { token: string; tipo: string; expiraEm: string }

const URL_KEY = "biblioteca.apiUrl";
const TOKEN_KEY = "biblioteca.token";
export const DEFAULT_API_URL = "http://localhost:8080";

export const getApiUrl = () =>
  (typeof window !== "undefined" && localStorage.getItem(URL_KEY)) || DEFAULT_API_URL;
export const setApiUrl = (u: string) => localStorage.setItem(URL_KEY, u.replace(/\/+$/, ""));
export const getToken = () => (typeof window !== "undefined" ? localStorage.getItem(TOKEN_KEY) : null);
export const setToken = (t: string | null) =>
  t ? localStorage.setItem(TOKEN_KEY, t) : localStorage.removeItem(TOKEN_KEY);

export class ApiError extends Error {
  constructor(public status: number, message: string, public details?: unknown) { super(message); }
}

const STATUS_MSG: Record<number, string> = {
  400: "Dados inválidos.", 401: "Não autenticado ou credenciais inválidas.",
  403: "Seu perfil não tem permissão para isso.", 404: "Recurso não encontrado.",
  409: "Conflito com o estado atual.", 422: "Regra de negócio violada.",
};

export async function api<T = unknown>(path: string, opts: { method?: string; body?: unknown; query?: Record<string, unknown> } = {}): Promise<T> {
  const url = new URL(getApiUrl() + path);
  Object.entries(opts.query ?? {}).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== "") url.searchParams.set(k, String(v));
  });
  const headers: Record<string, string> = { Accept: "application/json" };
  if (opts.body !== undefined) headers["Content-Type"] = "application/json";
  const token = getToken();
  if (token) headers["Authorization"] = `Bearer ${token}`;
  let res: Response;
  try {
    res = await fetch(url, { method: opts.method ?? "GET", headers, body: opts.body !== undefined ? JSON.stringify(opts.body) : null });
  } catch {
    throw new ApiError(0, `Não foi possível conectar à API em ${getApiUrl()}. Verifique se ela está rodando e com CORS liberado.`);
  }
  if (res.status === 204) return undefined as T;
  const text = await res.text();
  const data = text ? safeJson(text) : undefined;
  if (!res.ok) {
    const pd = (data ?? {}) as { detail?: string; title?: string; message?: string; erros?: unknown; errors?: unknown; campos?: unknown };
    let msg = pd.detail || pd.message || pd.title || STATUS_MSG[res.status] || `Erro ${res.status}`;
    const fields = (pd.erros ?? pd.errors ?? pd.campos) as Record<string, string> | undefined;
    if (fields && typeof fields === "object" && !Array.isArray(fields)) {
      msg += " " + Object.entries(fields).map(([k, v]) => `${k}: ${v}`).join("; ");
    }
    if (res.status === 401 && token && !path.startsWith("/auth")) {
      setToken(null);
      window.dispatchEvent(new Event("biblioteca:logout"));
    }
    throw new ApiError(res.status, msg, data);
  }
  return data as T;
}
function safeJson(t: string) { try { return JSON.parse(t); } catch { return t; } }

// Normaliza respostas que podem vir como lista simples ou paginadas
export function asList<T>(d: T[] | Page<T> | undefined): T[] {
  if (!d) return [];
  return Array.isArray(d) ? d : d.content ?? [];
}

export const nomeDe = (v: unknown, key: "nome" | "titulo" | "codigo"): string =>
  v == null ? "" : typeof v === "string" ? v : String((v as Record<string, unknown>)[key] ?? "");

export const fmtData = (d?: string | null) => {
  if (!d) return "—";
  const [y, m, day] = d.slice(0, 10).split("-");
  return day ? `${day}/${m}/${y}` : d;
};
export const fmtMoeda = (n?: number) =>
  (n ?? 0).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });

const RANK: Record<Papel, number> = { MEMBRO: 1, BIBLIOTECARIO: 2, ADMIN: 3 };
/** Hierarquia: ADMIN ⊃ BIBLIOTECARIO ⊃ MEMBRO */
export const temPapel = (papel: Papel | undefined, minimo: Papel) => !!papel && RANK[papel] >= RANK[minimo];

export const rotuloUsuario = (nome?: string, id?: number) =>
  nome ? `${nome} (#${id})` : id != null ? `#${id}` : "—";
