import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getUser } from '../api/auth'

function OAuthCallback() {
  const navigate = useNavigate()

  useEffect(() => {
    const checkAuth = async () => {
      try {
        await getUser()
        navigate('/dashboard')
      } catch {
        navigate('/login?error=true')
      }
    }

    checkAuth()
  }, [navigate])

  return (
    <div style={{ color: 'white', textAlign: 'center' }}>
      <h2>Авторизация...</h2>
      <p>Пожалуйста, подождите</p>
    </div>
  )
}

export default OAuthCallback
