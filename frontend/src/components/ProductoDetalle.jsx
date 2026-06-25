import { useEffect, useState } from 'react'
import Modal from './Modal'
import Cargando from './Cargando'
import AlertaError from './AlertaError'
import { productosApi, mensajeDeError } from '../api/client'
import { formatearPrecio } from '../utils/formato'

// Modal de detalle de un producto. Vuelve a pedir el producto al backend por su
// id (consume GET /api/productos/{id}) para mostrar el dato fresco.
export default function ProductoDetalle({ productoId, onCerrar, onAgregarCarrito }) {
  const [producto, setProducto] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let activo = true
    setCargando(true)
    setError(null)
    productosApi
      .obtener(productoId)
      .then((data) => {
        if (activo) setProducto(data)
      })
      .catch((e) => {
        if (activo) setError(mensajeDeError(e, 'No se pudo cargar el producto'))
      })
      .finally(() => {
        if (activo) setCargando(false)
      })
    return () => {
      activo = false
    }
  }, [productoId])

  const footer = producto && (
    <>
      <button className="btn-secundario" onClick={onCerrar}>
        Cerrar
      </button>
      <button
        className="btn-primario"
        disabled={producto.stock <= 0}
        onClick={() => {
          onAgregarCarrito(producto)
          onCerrar()
        }}
      >
        {producto.stock <= 0 ? 'Sin stock' : 'Agregar al carrito'}
      </button>
    </>
  )

  return (
    <Modal titulo="Detalle del producto" onCerrar={onCerrar} footer={footer}>
      {cargando && <Cargando mensaje="Cargando producto..." />}
      <AlertaError mensaje={error} />
      {producto && (
        <div>
          {producto.imagenUrl && (
            <img
              src={producto.imagenUrl}
              alt={producto.nombre}
              style={{ width: '100%', height: 200, objectFit: 'cover', borderRadius: 8 }}
            />
          )}
          <h2 style={{ marginBottom: 4 }}>{producto.nombre}</h2>
          {producto.categoria && <span className="categoria">{producto.categoria}</span>}
          <p>{producto.descripcion || 'Sin descripción.'}</p>
          <p className="precio">{formatearPrecio(producto.precio)}</p>
          <p className="stock">
            {producto.stock > 0 ? `${producto.stock} unidades disponibles` : 'Sin stock'}
          </p>
        </div>
      )}
    </Modal>
  )
}
