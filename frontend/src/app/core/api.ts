// src/app/core/api.ts
export function apiUrl(path: string): string {
    return new URL(path.replace(/^\//, ''), document.baseURI).toString();
}
