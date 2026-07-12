import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { login } from '../../api/auth'
import { useAuthStore } from '../../store/authStore'
import Spinner from '../../components/ui/Spinner'

export default function LoginPage() {
  const [identifier, setIdentifier] = useState('')
  const [password,   setPassword]   = useState('')
  const [showPass,   setShowPass]   = useState(false)
  const [loading,    setLoading]    = useState(false)
  const [error,      setError]      = useState('')
  const setTokens = useAuthStore(s => s.setTokens)
  const navigate  = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true); setError('')
    try {
      const res = await login({ identifier, password, loginMethod: 'PASSWORD' })
      setTokens(res.accessToken, res.refreshToken, res.userId)
      navigate('/dashboard')
    } catch (err: any) {
      const msg = err?.response?.data?.message
        ?? err?.response?.data?.error
        ?? 'Login failed. Check your credentials.'
      setError(msg)
    } finally { setLoading(false) }
  }

  const inputStyle: React.CSSProperties = {
    width: '100%', boxSizing: 'border-box',
    background: 'rgba(255,255,255,0.08)',
    border: '1px solid rgba(255,255,255,0.2)',
    borderRadius: '12px', padding: '0.75rem 1rem',
    color: '#fff', fontSize: '0.9rem', outline: 'none',
  }

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center',
      justifyContent: 'center', padding: '1rem',
      background: 'linear-gradient(135deg,#0f172a 0%,#1e293b 50%,#14532d 100%)',
      position: 'relative', overflow: 'hidden',
    }}>
      {/* Blobs */}
      <div style={{ position:'absolute', top:'-8rem', right:'-8rem', width:'22rem', height:'22rem',
        background:'rgba(22,163,74,0.15)', borderRadius:'50%', filter:'blur(60px)', pointerEvents:'none' }} />
      <div style={{ position:'absolute', bottom:'-8rem', left:'-8rem', width:'18rem', height:'18rem',
        background:'rgba(22,163,74,0.08)', borderRadius:'50%', filter:'blur(60px)', pointerEvents:'none' }} />

      <div style={{ position:'relative', width:'100%', maxWidth:'420px' }}>
        {/* Logo */}
        <div style={{ textAlign:'center', marginBottom:'2rem' }}>
          <div style={{
            display:'inline-flex', width:'64px', height:'64px',
            background:'#16a34a', borderRadius:'16px',
            alignItems:'center', justifyContent:'center',
            marginBottom:'1rem', boxShadow:'0 20px 40px rgba(22,163,74,0.35)',
          }}>
            <span style={{ color:'#fff', fontWeight:900, fontSize:'1.5rem' }}>ET</span>
          </div>
          <h1 style={{ color:'#fff', fontWeight:800, fontSize:'1.875rem', margin:0 }}>Welcome back</h1>
          <p style={{ color:'#94a3b8', marginTop:'0.25rem', fontSize:'0.875rem' }}>
            National Digital Microfinance Platform 🇪🇹
          </p>
        </div>

        {/* Card */}
        <div style={{
          background: 'rgba(255,255,255,0.07)',
          backdropFilter: 'blur(20px)',
          border: '1px solid rgba(255,255,255,0.12)',
          borderRadius: '24px', padding: '2rem',
          boxShadow: '0 25px 50px rgba(0,0,0,0.4)',
        }}>
          <form onSubmit={handleSubmit}>
            {error && (
              <div style={{
                background:'rgba(239,68,68,0.15)', border:'1px solid rgba(239,68,68,0.4)',
                color:'#fca5a5', fontSize:'0.875rem', borderRadius:'12px',
                padding:'0.75rem 1rem', marginBottom:'1.25rem',
              }}>{error}</div>
            )}

            {/* Demo hint */}
            <div style={{
              background:'rgba(22,163,74,0.1)', border:'1px solid rgba(22,163,74,0.25)',
              borderRadius:'12px', padding:'0.75rem 1rem', marginBottom:'1.25rem',
              fontSize:'0.8rem', color:'#86efac',
            }}>
              <strong>📋 Demo login:</strong> Phone <code>+251911234567</code> · Password <code>Test@1234</code>
            </div>

            {/* Phone */}
            <div style={{ marginBottom:'1rem' }}>
              <label style={{ display:'block', color:'#cbd5e1', fontSize:'0.875rem',
                fontWeight:500, marginBottom:'0.5rem' }}>Phone or Email</label>
              <input
                type="text" value={identifier} required
                onChange={e => setIdentifier(e.target.value)}
                placeholder="+251911234567"
                style={inputStyle}
              />
            </div>

            {/* Password */}
            <div style={{ marginBottom:'1.5rem' }}>
              <label style={{ display:'block', color:'#cbd5e1', fontSize:'0.875rem',
                fontWeight:500, marginBottom:'0.5rem' }}>Password</label>
              <div style={{ position:'relative' }}>
                <input
                  type={showPass ? 'text' : 'password'}
                  value={password} required
                  onChange={e => setPassword(e.target.value)}
                  placeholder="Test@1234"
                  style={{ ...inputStyle, paddingRight:'3rem' }}
                />
                <button type="button" onClick={() => setShowPass(v => !v)}
                  style={{ position:'absolute', right:'0.875rem', top:'50%',
                    transform:'translateY(-50%)', background:'none', border:'none',
                    color:'#94a3b8', cursor:'pointer', fontSize:'1.1rem' }}>
                  {showPass ? '🙈' : '👁'}
                </button>
              </div>
            </div>

            {/* Submit */}
            <button type="submit" disabled={loading} style={{
              width:'100%', background: loading ? '#15803d' : '#16a34a',
              color:'#fff', fontWeight:700, padding:'0.875rem',
              borderRadius:'12px', border:'none', cursor: loading ? 'not-allowed' : 'pointer',
              fontSize:'1rem', display:'flex', alignItems:'center',
              justifyContent:'center', gap:'0.5rem',
              boxShadow:'0 8px 20px rgba(22,163,74,0.3)',
              transition:'background 0.2s',
            }}>
              {loading && <Spinner size="sm" />}
              {loading ? 'Signing in…' : 'Sign In'}
            </button>
          </form>

          <p style={{ textAlign:'center', color:'#64748b', fontSize:'0.8rem', marginTop:'1.5rem' }}>
            No account?{' '}
            <Link to="/register" style={{ color:'#4ade80', fontWeight:600, textDecoration:'none' }}>
              Create one
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
