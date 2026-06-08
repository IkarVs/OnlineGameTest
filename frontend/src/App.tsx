import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import HomePage from './pages/HomePage'
import CharacterPage from './pages/CharacterPage'
import VillagePage from './pages/VillagePage'
import MissionsPage from './pages/MissionsPage'
import InventoryPage from './pages/InventoryPage'
import { useGameStore } from './stores/gameStore'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { player } = useGameStore()
  if (!player) return <Navigate to="/" replace />
  return <>{children}</>
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route element={<Layout />}>
          <Route
            path="/village"
            element={<ProtectedRoute><VillagePage /></ProtectedRoute>}
          />
          <Route
            path="/character"
            element={<ProtectedRoute><CharacterPage /></ProtectedRoute>}
          />
          <Route
            path="/missions"
            element={<ProtectedRoute><MissionsPage /></ProtectedRoute>}
          />
          <Route
            path="/inventory"
            element={<ProtectedRoute><InventoryPage /></ProtectedRoute>}
          />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
