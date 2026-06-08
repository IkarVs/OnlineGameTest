import axios from 'axios'

const client = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

client.interceptors.response.use(
  (res) => res,
  (err) => {
    const message = err.response?.data?.message ?? err.response?.data ?? err.message ?? 'Erreur inconnue'
    return Promise.reject(new Error(String(message)))
  },
)

export default client
