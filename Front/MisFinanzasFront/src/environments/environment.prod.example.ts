/**
 * Plantilla de referencia para el archivo de entorno de producción.
 *
 * El archivo real src/environments/environment.prod.ts se versiona en el
 * repositorio (la URL pública de la API no es un dato sensible) y se usa en
 * el build de producción mediante fileReplacements.
 *
 * Si necesitas cambiarla entre despliegues, edita environment.prod.ts
 * directamente o inyéctala en build con una variable de entorno.
 */
export const environment = {
  production: true,
  apiUrl: 'https://misfinanzas-mrcb.onrender.com'
};