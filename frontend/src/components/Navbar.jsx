// Barra de navegacion. Recibe la vista activa y la cantidad de items del
// carrito por props, y comunica los cambios de vista hacia arriba por callback.
export default function Navbar({ vista, onCambiarVista, cantidadItems }) {
  const tabs = [
    { id: 'catalogo', label: 'Catálogo' },
    { id: 'carrito', label: 'Carrito' },
    { id: 'historial', label: 'Historial' },
  ]

  return (
    <header className="navbar">
      <h1>
        <span aria-hidden="true">🌱</span> EcoMarket
      </h1>
      <nav>
        {tabs.map((tab) => (
          <button
            key={tab.id}
            className={`nav-btn ${vista === tab.id ? 'activo' : ''}`}
            onClick={() => onCambiarVista(tab.id)}
          >
            {tab.label}
            {tab.id === 'carrito' && cantidadItems > 0 && (
              <span className="badge">{cantidadItems}</span>
            )}
          </button>
        ))}
      </nav>
    </header>
  )
}
