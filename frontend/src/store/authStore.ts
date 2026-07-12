import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface AuthState {
  accessToken:  string | null
  refreshToken: string | null
  userId:       string | null
  isAuth:       boolean
  setTokens: (access: string, refresh: string, userId: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken:  null,
      refreshToken: null,
      userId:       null,
      isAuth:       false,
      setTokens: (access, refresh, userId) => {
        localStorage.setItem('access_token', access)
        localStorage.setItem('refresh_token', refresh)
        set({ accessToken: access, refreshToken: refresh, userId, isAuth: true })
      },
      logout: () => {
        localStorage.removeItem('access_token')
        localStorage.removeItem('refresh_token')
        set({ accessToken: null, refreshToken: null, userId: null, isAuth: false })
      },
    }),
    { name: 'auth' },
  ),
)
