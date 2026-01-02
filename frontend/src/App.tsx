import { useState, useEffect } from 'react'
import './App.css'

interface Item {
  id: number
  name: string
  description: string
  createdAt: string
}

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

function App() {
  const [items, setItems] = useState<Item[]>([])
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchItems = async () => {
    try {
      const response = await fetch(`${API_URL}/items`)
      if (!response.ok) throw new Error('Failed to fetch items')
      const data = await response.json()
      setItems(data)
      setError(null)
    } catch (err) {
      setError('Не удалось загрузить данные с сервера')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchItems()
  }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!name.trim()) return

    try {
      const response = await fetch(`${API_URL}/items`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name, description }),
      })
      
      if (!response.ok) throw new Error('Failed to create item')
      
      setName('')
      setDescription('')
      fetchItems()
    } catch (err) {
      setError('Не удалось создать элемент')
      console.error(err)
    }
  }

  const handleDelete = async (id: number) => {
    try {
      const response = await fetch(`${API_URL}/items/${id}`, {
        method: 'DELETE',
      })
      
      if (!response.ok) throw new Error('Failed to delete item')
      
      fetchItems()
    } catch (err) {
      setError('Не удалось удалить элемент')
      console.error(err)
    }
  }

  return (
    <div className="container">
      <h1>📝 Простое CRUD приложение</h1>
      
      <form onSubmit={handleSubmit} className="form">
        <input
          type="text"
          placeholder="Название"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Описание"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <button type="submit">Добавить</button>
      </form>

      {error && <div className="error">{error}</div>}

      {loading ? (
        <p>Загрузка...</p>
      ) : items.length === 0 ? (
        <p className="empty">Список пуст. Добавьте первый элемент!</p>
      ) : (
        <ul className="items-list">
          {items.map((item) => (
            <li key={item.id} className="item">
              <div className="item-content">
                <strong>{item.name}</strong>
                {item.description && <p>{item.description}</p>}
              </div>
              <button 
                onClick={() => handleDelete(item.id)} 
                className="delete-btn"
              >
                ✕
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

export default App
