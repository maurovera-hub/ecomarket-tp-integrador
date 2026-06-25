import { useCallback, useEffect, useState } from 'react'
import ProductoCard from './ProductoCard'
import ProductoForm from './ProductoForm'
import ProductoDetalle from './ProductoDetalle'
import Cargando from './Cargando'
import AlertaError from './AlertaError'
import { productosApi, mensajeDeError } from '../api/client'

// Vista de catalogo. Es la "duena" de la lista de productos: la pide al backend
// (GET) y maneja la creacion, edicion y borrado (POST/PUT/DELETE). Para agregar
// al carrito y para notificar avisa al padre (App) mediante callbacks.
export default function Catalogo({ onAgregarCarrito, onNotificar }) {
  const [productos, setProductos] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  const [formAbierto, setFormAbierto] = useState(false)
  const [productoEnEdicion, setProductoEnEdicion] = useState(null)
  const [guardando, setGuardando] = useState(false)

  const [detalleId, setDetalleId] = useState(null)

  const cargar = useCallback(() => {
    setCargando(true)
    setError(null)
    productosApi
      .listar()
      .then(setProductos)
      .catch((e) => setError(mensajeDeError(e, 'No se pudo cargar el catálogo')))
      .finally(() => setCargando(false))
  }, [])

  useEffect(() => {
    cargar()
  }, [cargar])

  const abrirNuevo = () => {
    setProductoEnEdicion(null)
    setFormAbierto(true)
  }

  const abrirEdicion = (producto) => {
    setProductoEnEdicion(producto)
    setFormAbierto(true)
  }

  const guardar = (datos) => {
    setGuardando(true)
    const peticion = productoEnEdicion?.id
      ? productosApi.actualizar(productoEnEdicion.id, datos)
      : productosApi.crear(datos)

    peticion
      .then(() => {
        onNotificar(
          productoEnEdicion?.id ? 'Producto actualizado' : 'Producto creado',
          'exito'
        )
        setFormAbierto(false)
        setProductoEnEdicion(null)
        cargar()
      })
      .catch((e) => onNotificar(mensajeDeError(e, 'No se pudo guardar'), 'error'))
      .finally(() => setGuardando(false))
  }

  const eliminar = (producto) => {
    const ok = window.confirm(`¿Eliminar "${producto.nombre}" del catálogo?`)
    if (!ok) return
    productosApi
      .eliminar(producto.id)
      .then(() => {
        onNotificar('Producto eliminado', 'exito')
        cargar()
      })
      .catch((e) => onNotificar(mensajeDeError(e, 'No se pudo eliminar'), 'error'))
  }

  return (
    <section>
      <div className="titulo-seccion">
        <h2>Catálogo de productos</h2>
        <button className="btn-primario" onClick={abrirNuevo}>
          + Nuevo producto
        </button>
      </div>

      {cargando && <Cargando mensaje="Cargando catálogo..." />}
      <AlertaError mensaje={error} onReintentar={cargar} />

      {!cargando && !error && productos.length === 0 && (
        <p className="estado">No hay productos en el catálogo todavía.</p>
      )}

      {!cargando && productos.length > 0 && (
        <div className="grilla">
          {productos.map((p) => (
            <ProductoCard
              key={p.id}
              producto={p}
              onVerDetalle={(prod) => setDetalleId(prod.id)}
              onEditar={abrirEdicion}
              onEliminar={eliminar}
              onAgregarCarrito={(prod) => onAgregarCarrito(prod, 1)}
            />
          ))}
        </div>
      )}

      {formAbierto && (
        <ProductoForm
          producto={productoEnEdicion}
          guardando={guardando}
          onGuardar={guardar}
          onCancelar={() => {
            setFormAbierto(false)
            setProductoEnEdicion(null)
          }}
        />
      )}

      {detalleId != null && (
        <ProductoDetalle
          productoId={detalleId}
          onCerrar={() => setDetalleId(null)}
          onAgregarCarrito={(prod) => onAgregarCarrito(prod, 1)}
        />
      )}
    </section>
  )
}
