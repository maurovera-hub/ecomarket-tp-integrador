import { formatearPrecio } from '../utils/formato'

// Vista del carrito. Es presentacional: recibe el carrito por props y avisa al
// padre (App) cada accion (cambiar cantidad, eliminar, vaciar, ir a confirmar).
export default function Carrito({
  carrito,
  onActualizarCantidad,
  onEliminarItem,
  onVaciar,
  onIrAConfirmar,
  onIrACatalogo,
  procesando,
}) {
  const items = carrito?.items ?? []

  if (items.length === 0) {
    return (
      <section>
        <div className="titulo-seccion">
          <h2>Tu carrito</h2>
        </div>
        <p className="estado">
          El carrito está vacío.{' '}
          <button className="btn-secundario" onClick={onIrACatalogo}>
            Ver catálogo
          </button>
        </p>
      </section>
    )
  }

  return (
    <section>
      <div className="titulo-seccion">
        <h2>Tu carrito</h2>
        <button className="btn-peligro" onClick={onVaciar} disabled={procesando}>
          Vaciar carrito
        </button>
      </div>

      <table className="tabla-carrito">
        <thead>
          <tr>
            <th>Producto</th>
            <th>Precio</th>
            <th>Cantidad</th>
            <th>Subtotal</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <tr key={item.id}>
              <td>{item.nombreProducto}</td>
              <td>{formatearPrecio(item.precioUnitario)}</td>
              <td>
                <div className="cantidad-control">
                  <button
                    className="btn-secundario"
                    aria-label="Disminuir"
                    disabled={procesando || item.cantidad <= 1}
                    onClick={() => onActualizarCantidad(item.id, item.cantidad - 1)}
                  >
                    −
                  </button>
                  <span>{item.cantidad}</span>
                  <button
                    className="btn-secundario"
                    aria-label="Aumentar"
                    disabled={procesando}
                    onClick={() => onActualizarCantidad(item.id, item.cantidad + 1)}
                  >
                    +
                  </button>
                </div>
              </td>
              <td>{formatearPrecio(item.subtotal)}</td>
              <td>
                <button
                  className="btn-peligro"
                  disabled={procesando}
                  onClick={() => onEliminarItem(item.id)}
                >
                  Quitar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="resumen">
        <span>
          Total: <span className="total">{formatearPrecio(carrito.total)}</span>
        </span>
        <button className="btn-primario" onClick={onIrAConfirmar} disabled={procesando}>
          Confirmar pedido
        </button>
      </div>
    </section>
  )
}
