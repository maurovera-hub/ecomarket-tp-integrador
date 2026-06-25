// Formatea un numero como precio en pesos argentinos.
export function formatearPrecio(valor) {
  const numero = Number(valor ?? 0)
  return numero.toLocaleString('es-AR', {
    style: 'currency',
    currency: 'ARS',
    minimumFractionDigits: 2,
  })
}

// Formatea una fecha ISO (LocalDateTime del backend) a algo legible.
export function formatearFecha(isoString) {
  if (!isoString) return ''
  const fecha = new Date(isoString)
  if (Number.isNaN(fecha.getTime())) return isoString
  return fecha.toLocaleString('es-AR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
