import { useEffect, useState } from 'react'
import Cargando from './Cargando'
import AlertaError from './AlertaError'
import { ordenesApi, mensajeDeError } from '../api/client'
import { formatearPrecio, formatearFecha } from '../utils/formato'

// Vista de historial. Pide las ordenes al backend (GET /api/ordenes) al montarse.
export default function HistorialOrdenes({ onIrACatalogo }) {
  const [ordenes, setOrdenes] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  const cargar = () => {
    setCargando(true)
    setError(null)
    ordenesApi
      .listar()
      .then(setOrdenes)
      .catch((e) => setError(mensajeDeError(e, 'No se pudo cargar el historial')))
      .finally(() => setCargando(false))
  }

  useEffect(() => {
    cargar()
  }, [])

  return (
    <section>
      <div className="titulo-seccion">
        <h2>Historial de órdenes</h2>
      </div>

      {cargando && <Cargando mensaje="Cargando historial..." />}
      <AlertaError mensaje={error} onReintentar={cargar} />

      {!cargando && !error && ordenes.length === 0 && (
        <p className="estado">
          Todavía no realizaste ninguna orden.{' '}
          <button className="btn-secundario" onClick={onIrACatalogo}>
            Ir al catálogo
          </button>
        </p>
      )}

      {ordenes.map((orden) => (
        <article className="orden-card" key={orden.id}>
          <div className="orden-head">
            <strong>Orden #{orden.id}</strong>
            <span>{formatearFecha(orden.fechaHora)}</span>
            <span className="precio">{formatearPrecio(orden.total)}</span>
          </div>
          <ul>
            {orden.items.map((item) => (
              <li key={item.id}>
                {item.cantidad} × {item.nombreProducto} —{' '}
                {formatearPrecio(item.subtotal)}
              </li>
            ))}
          </ul>
          {orden.mensaje && <p className="mensaje-cliente">“{orden.mensaje}”</p>}
        </article>
      ))}
    </section>
  )
}
