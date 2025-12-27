import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getUser, logout, User } from '../api/auth'
import './Dashboard.css'

function Dashboard() {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const userData = await getUser()
        setUser(userData)
      } catch {
        navigate('/login')
      } finally {
        setLoading(false)
      }
    }

    fetchUser()
  }, [navigate])

  if (loading) {
    return (
      <div style={{ color: 'white', textAlign: 'center' }}>
        <h2>Загрузка...</h2>
      </div>
    )
  }

  if (!user) {
    return null
  }

  const handleLogout = async () => {
    try {
      setError(null)
      await logout()
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Ошибка при выходе'
      setError(errorMessage)
      console.error('Logout error:', err)
    }
  }

  const avatarUrl = user.avatar || user.picture

  return (
    <div className="dashboard-container">
      <div className="dashboard-card">
        <div className="user-info">
          {avatarUrl && (
            <img src={avatarUrl} alt="Avatar" className="avatar" />
          )}
          <h1>Привет, {user.name}! 👋</h1>
          <p className="email">{user.email}</p>
        </div>

        <div className="dashboard-content">
          <h2>Добро пожаловать в CodePath</h2>
          <p>Вы успешно авторизовались!</p>
        </div>

        {error && (
          <div style={{
            backgroundColor: '#ff6b6b',
            color: 'white',
            padding: '10px',
            borderRadius: '4px',
            marginBottom: '10px',
            textAlign: 'center'
          }}>
            {error}
          </div>
        )}

        <button className="btn btn-logout" onClick={handleLogout}>
          Выйти
        </button>
      </div>
    </div>
  )
}

export default Dashboard
