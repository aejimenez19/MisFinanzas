/**
 * Ejemplo del archivo de entorno de producción.
 *
 * Este archivo NO se versiona. En Render (Static Site) se genera el archivo
 * src/environments/environment.prod.ts real durante el build, inyectando la URL:
 *
 *   Build command (Render):
 *   echo "export const environment = { production: true, apiUrl: '$API_URL' };" \
 *     > src/environments/environment.prod.ts && ng build
 *
 * Crea una variable de entorno API_URL en Render con la URL de tu API.
 */
export const environment = {
  production: true,
  apiUrl: 'https://misfinanzas-mrcb.onrender.com'
};