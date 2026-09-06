import { Mock, vi } from 'vitest';

/**
 * Un double de `T` dont les méthodes citées sont enregistrées.
 *
 * Le type croise `T` : le double se fournit tel quel là où le service est attendu, et les méthodes
 * gardent en plus l'API de `Mock`, donc `mockReturnValue` reste accessible depuis le test.
 */
export type SpyObj<T> = T & {
  [K in keyof T]: T[K] extends (...args: never[]) => unknown
    ? T[K] & Mock
    : T[K];
};

/**
 * Monte un double sur les seules méthodes que le test utilise, plus les propriétés qu'il doit
 * porter. Ce qui n'est pas cité reste absent : un appel imprévu échoue au lieu de passer inaperçu.
 */
export function createSpyObj<T>(
  methods: readonly (keyof T)[],
  properties: Partial<T> = {},
): SpyObj<T> {
  const spy: Record<PropertyKey, unknown> = { ...properties };
  for (const method of methods) {
    spy[method as PropertyKey] = vi.fn();
  }
  return spy as SpyObj<T>;
}
