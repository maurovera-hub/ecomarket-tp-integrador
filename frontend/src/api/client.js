import axios from 'axios'

// La URL del backend se toma de la variable de entorno VITE_API_URL
// (ver .env). Si no esta definida, se usa el backend local por defecto.
const baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const http = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' },
})

// Normaliza el mensaje de error que devuelve el backend para mostrarlo en la UI.
export function mensajeDeError(error, porDefecto = 'Ocurrio un error inesperado') {
  return (
    error?.response?.data?.message ||
    error?.response?.data?.error ||
    error?.message ||
    porDefecto
  )
}

export const productosApi = {
  listar: () => http.get('/api/productos').then((r) => r.data),
  obtener: (id) => http.get(`/api/productos/${id}`).then((r) => r.data),
  crear: (data) => http.post('/api/productos', data).then((r) => r.data),
  actualizar: (id, data) => http.put(`/api/productos/${id}`, data).then((r) => r.data),
  eliminar: (id) => http.delete(`/api/productos/${id}`).then((r) => r.data),
}

export const carritoApi = {
  obtener: () => http.get('/api/carrito').then((r) => r.data),
  agregarItem: (productoId, cantidad) =>
    http.post('/api/carrito/items', { productoId, cantidad }).then((r) => r.data),
  actualizarCantidad: (itemId, cantidad) =>
    http.put(`/api/carrito/items/${itemId}`, { cantidad }).then((r) => r.data),
  eliminarItem: (itemId) => http.delete(`/api/carrito/items/${itemId}`).then((r) => r.data),
  vaciar: () => http.delete('/api/carrito').then((r) => r.data),
}

export const ordenesApi = {
  confirmar: (mensaje) => http.post('/api/ordenes', { mensaje }).then((r) => r.data),
  listar: () => http.get('/api/ordenes').then((r) => r.data),
  obtener: (id) => http.get(`/api/ordenes/${id}`).then((r) => r.data),
}

export default http
