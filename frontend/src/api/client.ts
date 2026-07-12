import axios, { AxiosInstance, AxiosRequestConfig } from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL ?? '/api/v1'

function makeClient(baseURL: string): AxiosInstance {
  const client = axios.create({ baseURL, timeout: 30_000 })

  // Attach JWT on every request
  client.interceptors.request.use(cfg => {
    const token = localStorage.getItem('access_token')
    if (token && cfg.headers) cfg.headers.Authorization = `Bearer ${token}`
    return cfg
  })

  // 401 → redirect to login
  client.interceptors.response.use(
    res => res,
    err => {
      if (err.response?.status === 401) {
        localStorage.removeItem('access_token')
        localStorage.removeItem('refresh_token')
        window.location.href = '/login'
      }
      return Promise.reject(err)
    },
  )
  return client
}

export const authApi      = makeClient(`${BASE_URL}/auth`)
export const userApi      = makeClient(`${BASE_URL}/users`)
export const walletApi    = makeClient(`${BASE_URL}/wallets`)
export const paymentApi   = makeClient(`${BASE_URL}/payments`)
export const loanApi      = makeClient(`${BASE_URL}/loans`)
export const savingsApi   = makeClient(`${BASE_URL}/savings`)
export const coopApi      = makeClient(`${BASE_URL}/cooperatives`)
export const kycApi       = makeClient(`${BASE_URL}/kyc`)
export const notifApi     = makeClient(`${BASE_URL}/notifications`)
export const creditApi    = makeClient(`${BASE_URL}/credit`)

// ── Generic helpers ──────────────────────────────────────────────────────────
// Backend returns { success, message, data: T } — unwrap the inner data
export const unwrap = <T>(promise: Promise<{ data: { data?: T; success?: boolean; message?: string } | T }>) =>
  promise.then(r => {
    const payload = r.data as any
    // If response has a 'data' key, return it; otherwise return the whole response body
    return (payload?.data !== undefined ? payload.data : payload) as T
  })
