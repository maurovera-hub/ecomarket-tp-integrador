import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import ConfirmarPedido from './ConfirmarPedido'

const carrito = {
  id: 1,
  total: 4500,
  items: [
    { id: 10, nombreProducto: 'Café orgánico', precioUnitario: 1500, cantidad: 3, subtotal: 4500 },
  ],
}

describe('ConfirmarPedido', () => {
  it('muestra el resumen del pedido', () => {
    render(<ConfirmarPedido carrito={carrito} />)
    expect(screen.getByText(/resumen/i)).toBeInTheDocument()
    expect(screen.getByText(/café orgánico/i)).toBeInTheDocument()
  })

  it('confirma el pedido enviando el mensaje escrito', async () => {
    const onConfirmar = vi.fn()
    render(<ConfirmarPedido carrito={carrito} onConfirmar={onConfirmar} />)

    const textarea = screen.getByLabelText(/mensaje para el pedido/i)
    await userEvent.type(textarea, 'Entregar a la tarde')
    await userEvent.click(screen.getByRole('button', { name: /confirmar compra/i }))

    expect(onConfirmar).toHaveBeenCalledWith('Entregar a la tarde')
  })

  it('confirma con mensaje vacío si no se escribe nada', async () => {
    const onConfirmar = vi.fn()
    render(<ConfirmarPedido carrito={carrito} onConfirmar={onConfirmar} />)

    await userEvent.click(screen.getByRole('button', { name: /confirmar compra/i }))
    expect(onConfirmar).toHaveBeenCalledWith('')
  })

  it('permite volver al carrito', async () => {
    const onCancelar = vi.fn()
    render(<ConfirmarPedido carrito={carrito} onCancelar={onCancelar} />)

    await userEvent.click(screen.getByRole('button', { name: /volver al carrito/i }))
    expect(onCancelar).toHaveBeenCalledTimes(1)
  })

  it('deshabilita confirmar si el carrito está vacío', () => {
    render(<ConfirmarPedido carrito={{ items: [], total: 0 }} />)
    expect(screen.getByRole('button', { name: /confirmar compra/i })).toBeDisabled()
  })
})
