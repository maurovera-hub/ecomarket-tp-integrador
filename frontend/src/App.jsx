import { useCallback, useEffect, useState } from 'react'
import Navbar from './components/Navbar'
import Catalogo from './components/Catalogo'
import Carrito from './components/Carrito'
import ConfirmarPedido from './components/ConfirmarPedido'
import HistorialOrdenes from './components/HistorialOrdenes'
import Toast from './components/Toast'
import { carritoApi, ordenesApi, mensajeDeError } from './api/client'

// Componente raiz. Concentra el estado compartido (carrito, vista actual y
// notificaciones) y lo distribuye a las vistas hijas via props. Las vistas le
// devuelven eventos por callbacks: el flujo de datos es unidireccional.
export default function App() {
  const [vista, setVista] = useState('catalogo')
  const [carrito, setCarrito] = useState({ items: [], total: 0 })
  const [procesando, setProcesando] = useState(false)
  const [toast, setToast] = useState(null)

  const notificar = useCallback((mensaje, tipo = 'exito') => {
    setToast({ mensaje, tipo })
  }, [])

  const recargarCarrito = useCallback(() => {
    carritoApi
      .obtener()
      .then(setCarrito)
      .catch((e) => notificar(mensajeDeError(e, 'No se pudo cargar el carrito'), 'error'))
  }, [notificar])

  // Carga inicial del carrito al montar la app.
  useEffect(() => {
    recargarCarrito()
  }, [recargarCarrito])

  const agregarAlCarrito = (producto, cantidad = 1) => {
    setProcesando(true)
    carritoApi
      .agregarItem(producto.id, cantidad)
      .then((nuevoCarrito) => {
        setCarrito(nuevoCarrito)
        notificar(`"${producto.nombre}" agregado al carrito`)
      })
      .catch((e) => notificar(mensajeDeError(e, 'No se pudo agregar al carrito'), 'error'))
      .finally(() => setProcesando(false))
  }

  const actualizarCantidad = (itemId, cantidad) => {
    if (cantidad < 1) return
    setProcesando(true)
    carritoApi
      .actualizarCantidad(itemId, cantidad)
      .then(setCarrito)
      .catch((e) => notificar(mensajeDeError(e, 'No se pudo actualizar la cantidad'), 'error'))
      .finally(() => setProcesando(false))
  }

  const eliminarItem = (itemId) => {
    setProcesando(true)
    carritoApi
      .eliminarItem(itemId)
      .then((nuevoCarrito) => {
        setCarrito(nuevoCarrito)
        notificar('Producto quitado del carrito')
      })
      .catch((e) => notificar(mensajeDeError(e, 'No se pudo quitar el producto'), 'error'))
      .finally(() => setProcesando(false))
  }

  const vaciarCarrito = () => {
    if (!window.confirm('¿Vaciar todo el carrito?')) return
    setProcesando(true)
    carritoApi
      .vaciar()
      .then((nuevoCarrito) => {
        setCarrito(nuevoCarrito)
        notificar('Carrito vaciado')
      })
      .catch((e) => notificar(mensajeDeError(e, 'No se pudo vaciar el carrito'), 'error'))
      .finally(() => setProcesando(false))
  }

  const confirmarOrden = (mensaje) => {
    setProcesando(true)
    ordenesApi
      .confirmar(mensaje)
      .then(() => {
        notificar('¡Pedido confirmado! Gracias por tu compra 🌱')
        setCarrito({ items: [], total: 0 })
        setVista('historial')
      })
      .catch((e) => notificar(mensajeDeError(e, 'No se pudo confirmar el pedido'), 'error'))
      .finally(() => setProcesando(false))
  }

  const cantidadItems = (carrito.items ?? []).reduce((acc, i) => acc + i.cantidad, 0)

  return (
    <>
      <Navbar vista={vista} onCambiarVista={setVista} cantidadItems={cantidadItems} />

      <main className="contenedor">
        {vista === 'catalogo' && (
          <Catalogo onAgregarCarrito={agregarAlCarrito} onNotificar={notificar} />
        )}

        {vista === 'carrito' && (
          <Carrito
            carrito={carrito}
            procesando={procesando}
            onActualizarCantidad={actualizarCantidad}
            onEliminarItem={eliminarItem}
            onVaciar={vaciarCarrito}
            onIrAConfirmar={() => setVista('confirmar')}
            onIrACatalogo={() => setVista('catalogo')}
          />
        )}

        {vista === 'confirmar' && (
          <ConfirmarPedido
            carrito={carrito}
            procesando={procesando}
            onConfirmar={confirmarOrden}
            onCancelar={() => setVista('carrito')}
          />
        )}

        {vista === 'historial' && (
          <HistorialOrdenes onIrACatalogo={() => setVista('catalogo')} />
        )}
      </main>

      <Toast toast={toast} onCerrar={() => setToast(null)} />
    </>
  )
}
