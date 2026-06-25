// Muestra un mensaje de error con opcion de reintentar.
export default function AlertaError({ mensaje, onReintentar }) {
  if (!mensaje) return null
  return (
    <div className="alerta-error" role="alert">
      <span>⚠️ {mensaje}</span>
      {onReintentar && (
        <button className="btn-secundario" onClick={onReintentar}>
          Reintentar
        </button>
      )}
    </div>
  )
}
