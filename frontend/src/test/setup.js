// Configuracion global de los tests: agrega los matchers de jest-dom
// (toBeInTheDocument, etc.) y limpia el DOM despues de cada test.
import '@testing-library/jest-dom/vitest'
import { cleanup } from '@testing-library/react'
import { afterEach } from 'vitest'

afterEach(() => {
  cleanup()
})
