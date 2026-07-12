import { authApi, unwrap } from './client'

export interface RegisterPayload {
  userId:      string
  phoneNumber: string
  email?:      string
  password?:   string
  pin:         string
  deviceId?:   string
}

export interface LoginPayload {
  identifier:  string        // phone, email or userId
  password?:   string
  pin?:        string
  loginMethod: 'PASSWORD' | 'PIN'
  deviceId?:   string
  ipAddress?:  string
}

export interface OtpSendPayload   { recipient: string; purpose: string }
export interface OtpVerifyPayload { recipient: string; purpose: string; otp: string }

export interface AuthResponse {
  accessToken:  string
  refreshToken: string
  tokenType:    string
  expiresIn:    number
  userId:       string
}

export const register     = (p: RegisterPayload)   => unwrap<void>(authApi.post('/register', p))
export const login        = (p: LoginPayload)      => unwrap<AuthResponse>(authApi.post('/login', p))
export const sendOtp      = (p: OtpSendPayload)    => unwrap<void>(authApi.post('/otp/send', p))
export const verifyOtp    = (p: OtpVerifyPayload)  => unwrap<AuthResponse>(authApi.post('/otp/verify', p))
export const refreshToken = (token: string)        => unwrap<AuthResponse>(authApi.post('/refresh', { refreshToken: token }))
export const logout       = ()                     => authApi.post('/logout').catch(() => {})
