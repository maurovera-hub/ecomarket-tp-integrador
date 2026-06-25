import { useState } from 'react'
import { formatearPrecio } from '../utils/formato'

// Vista de confirmacion del pedido. Permite agregar un mensaje opcional y
// confirma el carrito como orden. El mensaje vive en estado local y se envia
// al padre mediante onConfirmar.
export default function ConfirmarPedido({ carrito, onConfirmar, onCancelar, procesando }) {
  const [mensaje, setMensaje] = useState('')
  const items = carrito?.items ?? []

  return (
    <section>
      <div className="titulo-seccion">
        <h2>Confirmar pedido</h2>
      </div>

      <div className="orden-card">
        <h3>Resumen</h3>
        <ul>
          {items.map((item) => (
            <li key={item.id}>
              {item.cantidad} × {item.nombreProducto} —{' '}
              {formatearPrecio(item.subtotal)}
            </li>
          ))}
        </ul>
        <p className="precio">Total: {formatearPrecio(carrito?.total)}</p>
      </div>

      <div className="form-campo" style={{ marginTop: 16 }}>
        <label htmlFor="mensaje">Mensaje para el pedido (opcional)</label>
        <textarea
          id="mensaje"
          rows="3"
          maxLength={1000}
          placeholder="Ej: Entregar después de las 18 hs, tocar timbre 2..."
          value={mensaje}
          onChange={(e) => setMensaje(e.target.value)}
        />
      </div>

      <div className="resumen">
        <button className="btn-secundario" onClick={onCancelar} disabled={procesando}>
          Volver al carrito
        </button>
        <button
          className="btn-primario"
          onClick={() => onConfirmar(mensaje.trim())}
          disabled={procesando || items.length === 0}
        >
          {procesando ? 'Confirmando...' : 'Confirmar compra'}
        </button>
      </div>
    </section>
  )
}
