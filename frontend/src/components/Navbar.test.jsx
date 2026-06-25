import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import Navbar from './Navbar'

describe('Navbar', () => {
  it('muestra las tres secciones de navegación', () => {
    render(<Navbar vista="catalogo" onCambiarVista={vi.fn()} cantidadItems={0} />)
    expect(screen.getByRole('button', { name: /catálogo/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /carrito/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /historial/i })).toBeInTheDocument()
  })

  it('avisa al padre cuando se cambia de vista', async () => {
    const onCambiarVista = vi.fn()
    render(<Navbar vista="catalogo" onCambiarVista={onCambiarVista} cantidadItems={0} />)

    await userEvent.click(screen.getByRole('button', { name: /carrito/i }))
    expect(onCambiarVista).toHaveBeenCalledWith('carrito')
  })

  it('muestra el badge con la cantidad de items del carrito', () => {
    render(<Navbar vista="catalogo" onCambiarVista={vi.fn()} cantidadItems={4} />)
    expect(screen.getByText('4')).toBeInTheDocument()
  })

  it('no muestra badge cuando el carrito está vacío', () => {
    render(<Navbar vista="catalogo" onCambiarVista={vi.fn()} cantidadItems={0} />)
    // El boton "Carrito" no debe contener ningun numero.
    expect(screen.queryByText('0')).not.toBeInTheDocument()
  })
})
