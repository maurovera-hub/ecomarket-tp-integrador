import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import Catalogo from './Catalogo'
import { productosApi } from '../api/client'

// Mockeamos el cliente HTTP para no depender del backend real en los tests.
vi.mock('../api/client', () => ({
  productosApi: {
    listar: vi.fn(),
    obtener: vi.fn(),
    crear: vi.fn(),
    actualizar: vi.fn(),
    eliminar: vi.fn(),
  },
  mensajeDeError: (_e, porDefecto) => porDefecto,
}))

const productos = [
  {
    id: 1,
    nombre: 'Cepillo de bambú',
    descripcion: 'Biodegradable',
    precio: 2500,
    categoria: 'Higiene',
    stock: 10,
    imagenUrl: '',
  },
  {
    id: 2,
    nombre: 'Botella térmica',
    descripcion: 'Acero inoxidable',
    precio: 8900,
    categoria: 'Cocina',
    stock: 0,
    imagenUrl: '',
  },
]

describe('Catalogo', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('muestra el estado de carga y luego los productos del backend', async () => {
    productosApi.listar.mockResolvedValue(productos)
    render(<Catalogo onAgregarCarrito={vi.fn()} onNotificar={vi.fn()} />)

    expect(screen.getByText(/cargando catálogo/i)).toBeInTheDocument()

    expect(await screen.findByText('Cepillo de bambú')).toBeInTheDocument()
    expect(screen.getByText('Botella térmica')).toBeInTheDocument()
    expect(productosApi.listar).toHaveBeenCalledTimes(1)
  })

  it('muestra un error si falla la carga del catálogo', async () => {
    productosApi.listar.mockRejectedValue(new Error('falla'))
    render(<Catalogo onAgregarCarrito={vi.fn()} onNotificar={vi.fn()} />)

    expect(await screen.findByText(/no se pudo cargar el catálogo/i)).toBeInTheDocument()
  })

  it('agrega un producto al carrito mediante el callback del padre', async () => {
    productosApi.listar.mockResolvedValue(productos)
    const onAgregarCarrito = vi.fn()
    render(<Catalogo onAgregarCarrito={onAgregarCarrito} onNotificar={vi.fn()} />)

    await screen.findByText('Cepillo de bambú')
    // El primer producto tiene stock; su boton dice "Agregar".
    await userEvent.click(screen.getByRole('button', { name: /^agregar$/i }))

    expect(onAgregarCarrito).toHaveBeenCalledWith(productos[0], 1)
  })

  it('abre el formulario de nuevo producto', async () => {
    productosApi.listar.mockResolvedValue(productos)
    render(<Catalogo onAgregarCarrito={vi.fn()} onNotificar={vi.fn()} />)
    await screen.findByText('Cepillo de bambú')

    await userEvent.click(screen.getByRole('button', { name: /nuevo producto/i }))
    expect(screen.getByRole('heading', { name: /nuevo producto/i })).toBeInTheDocument()
  })

  it('crea un producto y vuelve a cargar el catálogo', async () => {
    productosApi.listar.mockResolvedValue(productos)
    productosApi.crear.mockResolvedValue({ id: 3 })
    const onNotificar = vi.fn()
    render(<Catalogo onAgregarCarrito={vi.fn()} onNotificar={onNotificar} />)
    await screen.findByText('Cepillo de bambú')

    await userEvent.click(screen.getByRole('button', { name: /nuevo producto/i }))
    await userEvent.type(screen.getByLabelText(/nombre/i), 'Jabón natural')
    await userEvent.type(screen.getByLabelText(/precio/i), '1200')
    await userEvent.type(screen.getByLabelText(/stock/i), '5')
    await userEvent.click(screen.getByRole('button', { name: /^guardar$/i }))

    await waitFor(() => expect(productosApi.crear).toHaveBeenCalledTimes(1))
    expect(productosApi.crear).toHaveBeenCalledWith(
      expect.objectContaining({ nombre: 'Jabón natural', precio: 1200, stock: 5 })
    )
    expect(onNotificar).toHaveBeenCalledWith('Producto creado', 'exito')
  })
})
