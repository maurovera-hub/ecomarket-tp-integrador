import { useEffect } from 'react'

// Notificacion temporal (se autocierra). Recibe el toast por props y avisa al
// padre cuando debe ocultarse (callback hacia arriba).
export default function Toast({ toast, onCerrar }) {
  useEffect(() => {
    if (!toast) return undefined
    const id = setTimeout(onCerrar, 2800)
    return () => clearTimeout(id)
  }, [toast, onCerrar])

  if (!toast) return null

  return (
    <div className={`toast ${toast.tipo}`} role="status">
      {toast.mensaje}
    </div>
  )
}
