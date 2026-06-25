import { formatearPrecio } from '../utils/formato'

const IMG_PLACEHOLDER =
  'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="240" height="160"><rect width="100%25" height="100%25" fill="%23e8f5e9"/><text x="50%25" y="50%25" font-size="48" text-anchor="middle" dy=".35em">🌿</text></svg>'

// Tarjeta de un producto del catalogo. No tiene estado propio: recibe el
// producto por props y dispara callbacks hacia el padre para cada accion.
export default function ProductoCard({ producto, onVerDetalle, onEditar, onEliminar, onAgregarCarrito }) {
  const sinStock = producto.stock <= 0

  return (
    <article className="card">
      <img
        className="card-img"
        src={producto.imagenUrl || IMG_PLACEHOLDER}
        alt={producto.nombre}
        onError={(e) => {
          e.currentTarget.src = IMG_PLACEHOLDER
        }}
      />
      <div className="card-body">
        {producto.categoria && <span className="categoria">{producto.categoria}</span>}
        <h3>{producto.nombre}</h3>
        <span className="precio">{formatearPrecio(producto.precio)}</span>
        <span className="stock">
          {sinStock ? 'Sin stock' : `${producto.stock} disponibles`}
        </span>
        <div className="card-acciones">
          <button
            className="btn-primario"
            disabled={sinStock}
            onClick={() => onAgregarCarrito(producto)}
          >
            {sinStock ? 'Agotado' : 'Agregar'}
          </button>
          <button className="btn-secundario" onClick={() => onVerDetalle(producto)}>
            Detalle
          </button>
          <button className="btn-secundario" onClick={() => onEditar(producto)}>
            Editar
          </button>
          <button className="btn-peligro" onClick={() => onEliminar(producto)}>
            Eliminar
          </button>
        </div>
      </div>
    </article>
  )
}
