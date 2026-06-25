import { useState } from 'react'
import Modal from './Modal'

// Formulario para crear o editar un producto. Es un componente controlado:
// mantiene los valores con useState y envia los datos al padre via onGuardar.
export default function ProductoForm({ producto, onGuardar, onCancelar, guardando }) {
  const esEdicion = Boolean(producto?.id)
  const [valores, setValores] = useState({
    nombre: producto?.nombre ?? '',
    descripcion: producto?.descripcion ?? '',
    precio: producto?.precio ?? '',
    categoria: producto?.categoria ?? '',
    stock: producto?.stock ?? '',
    imagenUrl: producto?.imagenUrl ?? '',
  })
  const [errores, setErrores] = useState({})

  const cambiar = (campo) => (e) => {
    setValores((prev) => ({ ...prev, [campo]: e.target.value }))
  }

  const validar = () => {
    const nuevos = {}
    if (!valores.nombre.trim()) nuevos.nombre = 'El nombre es obligatorio'
    if (valores.precio === '' || Number(valores.precio) < 0)
      nuevos.precio = 'El precio debe ser 0 o mayor'
    if (valores.stock === '' || Number(valores.stock) < 0 || !Number.isInteger(Number(valores.stock)))
      nuevos.stock = 'El stock debe ser un entero 0 o mayor'
    setErrores(nuevos)
    return Object.keys(nuevos).length === 0
  }

  const enviar = (e) => {
    e.preventDefault()
    if (!validar()) return
    onGuardar({
      nombre: valores.nombre.trim(),
      descripcion: valores.descripcion.trim(),
      precio: Number(valores.precio),
      categoria: valores.categoria.trim(),
      stock: Number(valores.stock),
      imagenUrl: valores.imagenUrl.trim(),
    })
  }

  const footer = (
    <>
      <button type="button" className="btn-secundario" onClick={onCancelar} disabled={guardando}>
        Cancelar
      </button>
      <button type="submit" form="form-producto" className="btn-primario" disabled={guardando}>
        {guardando ? 'Guardando...' : 'Guardar'}
      </button>
    </>
  )

  return (
    <Modal
      titulo={esEdicion ? 'Editar producto' : 'Nuevo producto'}
      onCerrar={onCancelar}
      footer={footer}
    >
      <form id="form-producto" onSubmit={enviar} noValidate>
        <div className="form-campo">
          <label htmlFor="nombre">Nombre *</label>
          <input id="nombre" value={valores.nombre} onChange={cambiar('nombre')} />
          {errores.nombre && <span className="form-error">{errores.nombre}</span>}
        </div>
        <div className="form-campo">
          <label htmlFor="descripcion">Descripción</label>
          <textarea
            id="descripcion"
            rows="3"
            value={valores.descripcion}
            onChange={cambiar('descripcion')}
          />
        </div>
        <div className="form-campo">
          <label htmlFor="precio">Precio *</label>
          <input
            id="precio"
            type="number"
            min="0"
            step="0.01"
            value={valores.precio}
            onChange={cambiar('precio')}
          />
          {errores.precio && <span className="form-error">{errores.precio}</span>}
        </div>
        <div className="form-campo">
          <label htmlFor="categoria">Categoría</label>
          <input id="categoria" value={valores.categoria} onChange={cambiar('categoria')} />
        </div>
        <div className="form-campo">
          <label htmlFor="stock">Stock *</label>
          <input
            id="stock"
            type="number"
            min="0"
            step="1"
            value={valores.stock}
            onChange={cambiar('stock')}
          />
          {errores.stock && <span className="form-error">{errores.stock}</span>}
        </div>
        <div className="form-campo">
          <label htmlFor="imagenUrl">URL de imagen</label>
          <input id="imagenUrl" value={valores.imagenUrl} onChange={cambiar('imagenUrl')} />
        </div>
      </form>
    </Modal>
  )
}
