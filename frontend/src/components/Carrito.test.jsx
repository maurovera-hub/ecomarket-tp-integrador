import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import Carrito from './Carrito'

const carritoConItems = {
  id: 1,
  total: 4500,
  items: [
    {
      id: 10,
      productoId: 5,
      nombreProducto: 'Café orgánico',
      precioUnitario: 1500,
      cantidad: 3,
      subtotal: 4500,
    },
  ],
}

describe('Carrito', () => {
  it('muestra mensaje cuando el carrito está vacío', () => {
    render(<Carrito carrito={{ items: [], total: 0 }} />)
    expect(screen.getByText(/el carrito está vacío/i)).toBeInTheDocument()
  })

  it('renderiza los items y permite ir al catálogo desde el vacío', async () => {
    const onIrACatalogo = vi.fn()
    render(<Carrito carrito={{ items: [], total: 0 }} onIrACatalogo={onIrACatalogo} />)

    await userEvent.click(screen.getByRole('button', { name: /ver catálogo/i }))
    expect(onIrACatalogo).toHaveBeenCalledTimes(1)
  })

  it('muestra el nombre y la cantidad de cada item', () => {
    render(<Carrito carrito={carritoConItems} />)
    expect(screen.getByText('Café orgánico')).toBeInTheDocument()
    expect(screen.getByText('3')).toBeInTheDocument()
  })

  it('aumenta la cantidad llamando al callback con cantidad + 1', async () => {
    const onActualizarCantidad = vi.fn()
    render(<Carrito carrito={carritoConItems} onActualizarCantidad={onActualizarCantidad} />)

    await userEvent.click(screen.getByRole('button', { name: /aumentar/i }))
    expect(onActualizarCantidad).toHaveBeenCalledWith(10, 4)
  })

  it('elimina un item llamando al callback con el id', async () => {
    const onEliminarItem = vi.fn()
    render(<Carrito carrito={carritoConItems} onEliminarItem={onEliminarItem} />)

    await userEvent.click(screen.getByRole('button', { name: /quitar/i }))
    expect(onEliminarItem).toHaveBeenCalledWith(10)
  })

  it('dispara la confirmación del pedido', async () => {
    const onIrAConfirmar = vi.fn()
    render(<Carrito carrito={carritoConItems} onIrAConfirmar={onIrAConfirmar} />)

    await userEvent.click(screen.getByRole('button', { name: /confirmar pedido/i }))
    expect(onIrAConfirmar).toHaveBeenCalledTimes(1)
  })

  it('permite vaciar el carrito', async () => {
    const onVaciar = vi.fn()
    render(<Carrito carrito={carritoConItems} onVaciar={onVaciar} />)

    await userEvent.click(screen.getByRole('button', { name: /vaciar carrito/i }))
    expect(onVaciar).toHaveBeenCalledTimes(1)
  })
})
