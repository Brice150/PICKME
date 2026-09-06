/**
 * Comble ce que jsdom ne fournit pas.
 *
 * Les tests tournent sur jsdom, qui implémente le DOM mais pas les API que le navigateur ajoute
 * autour : le code les appelle sans garde, puisqu'en production elles sont là. On fournit le
 * minimum pour que le composant suive son chemin nominal, sans prétendre observer quoi que ce soit.
 */

/** Rien n'est mesuré ni localisé : rien ne peut être observé, et rien ne doit échouer. */
const ignore = (): void => undefined;

/** Taille annoncée pour toute image décodée : ce qui compte est qu'elle soit cohérente. */
const DECODED_SIZE = 800;

if (navigator.geolocation === undefined) {
  Object.defineProperty(navigator, 'geolocation', {
    value: {
      getCurrentPosition: ignore,
      watchPosition: ignore,
      clearWatch: ignore,
    },
    configurable: true,
  });
}

if (typeof globalThis.matchMedia === 'undefined') {
  globalThis.matchMedia = ((query: string) => ({
    matches: false,
    media: query,
    onchange: null,
    addEventListener: ignore,
    removeEventListener: ignore,
    addListener: ignore,
    removeListener: ignore,
    dispatchEvent: () => false,
  })) as unknown as typeof globalThis.matchMedia;
}

if (typeof globalThis.scrollTo === 'undefined') {
  globalThis.scrollTo = ignore as unknown as typeof globalThis.scrollTo;
}

/**
 * jsdom accepte une source d'image mais ne la décode jamais : `onload` ne part pas, et un code qui
 * redimensionne avant d'envoyer reste bloqué là. On annonce le décodage au tour suivant, ce que le
 * navigateur fait aussi, avec une taille arbitraire mais cohérente.
 */
const src = Object.getOwnPropertyDescriptor(HTMLImageElement.prototype, 'src');
if (src?.set && src.get) {
  const { get, set } = src;
  Object.defineProperty(HTMLImageElement.prototype, 'src', {
    configurable: true,
    get,
    set(this: HTMLImageElement, value: string) {
      set.call(this, value);
      setTimeout(() => {
        this.width = DECODED_SIZE;
        this.height = DECODED_SIZE;
        this.dispatchEvent(new Event('load'));
      });
    },
  });
}

/** Aucun pixel n'est peint : le contexte ne sert qu'à laisser passer le redimensionnement. */
HTMLCanvasElement.prototype.getContext = (() => ({
  drawImage: ignore,
})) as unknown as HTMLCanvasElement['getContext'];
HTMLCanvasElement.prototype.toDataURL = (): string => 'data:image/jpeg;base64,';
