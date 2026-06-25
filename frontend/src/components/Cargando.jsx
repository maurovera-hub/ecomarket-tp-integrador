// Indicador de carga reutilizable.
export default function Cargando({ mensaje = 'Cargando...' }) {
  return (
    <div className="estado" role="status">
      <div className="spinner" />
      <p>{mensaje}</p>
    </div>
  )
}
