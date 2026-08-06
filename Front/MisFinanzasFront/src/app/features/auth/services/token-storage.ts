const ACCESS_TOKEN_KEY = 'mf_access_token';
const REFRESH_TOKEN_KEY = 'mf_refresh_token';
const REMEMBER_KEY = 'mf_remember';

interface TokenPair {
  accessToken: string;
  refreshToken: string;
  remember: boolean;
}

function store(kind: 'access' | 'refresh', value: string, remember: boolean): void {
  const key = kind === 'access' ? ACCESS_TOKEN_KEY : REFRESH_TOKEN_KEY;
  const target = remember ? localStorage : sessionStorage;
  target.setItem(key, value);
  target.setItem(REMEMBER_KEY, remember ? 'true' : 'false');
}

function read(kind: 'access' | 'refresh'): string | null {
  const key = kind === 'access' ? ACCESS_TOKEN_KEY : REFRESH_TOKEN_KEY;
  return localStorage.getItem(key) ?? sessionStorage.getItem(key);
}

function clear(): void {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(REMEMBER_KEY);
  sessionStorage.removeItem(ACCESS_TOKEN_KEY);
  sessionStorage.removeItem(REFRESH_TOKEN_KEY);
  sessionStorage.removeItem(REMEMBER_KEY);
}

export function saveTokenPair(tokens: TokenPair): void {
  store('access', tokens.accessToken, tokens.remember);
  store('refresh', tokens.refreshToken, tokens.remember);
}

export function getAccessToken(): string | null {
  return read('access');
}

export function getStoredRefreshToken(): string | null {
  return read('refresh');
}

export function getStoredRemember(): boolean {
  const raw = localStorage.getItem(REMEMBER_KEY) ?? sessionStorage.getItem(REMEMBER_KEY);
  return raw === 'true';
}

export function clearTokenPair(): void {
  clear();
}