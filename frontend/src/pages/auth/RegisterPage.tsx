import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { register } from '../../api/auth'
import Spinner from '../../components/ui/Spinner'

export default function RegisterPage() {
  const [form, setForm] = useState({
    userId:      '',
    phoneNumber: '',
    email:       '',
    password:    '',
    pin:         '',
  })
  const [loading, setLoading] = useState(false)
  const [error,   setError]   = useState('')
  const [success, setSuccess] = useState(false)
  const navigate = useNavigate()

  const set = (k: string) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm(f => ({ ...f, [k]: e.target.value }))

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true); setError('')
    try {
      await register(form)
      setSuccess(true)
      setTimeout(() => navigate('/login'), 2500)
    } catch (err: any) {
      const msg = err?.response?.data?.message
        ?? err?.response?.data?.errors?.[0]
        ?? 'Registration failed. Check the form and try again.'
      setError(msg)
    } finally { setLoading(false) }
  }

  return (
    <div className="min-h-screen flex items-center justify-center p-4"
      style={{ background: 'linear-gradient(135deg,#111827 0%,#1f2937 50%,#14532d 100%)' }}>

      {/* Background blobs */}
      <div style={{ position:'absolute',top:'-10rem',right:'-10rem',width:'24rem',height:'24rem',
        background:'rgba(22,163,74,0.15)',borderRadius:'50%',filter:'blur(60px)',pointerEvents:'none' }}/>
      <div style={{ position:'absolute',bottom:'-10rem',left:'-10rem',width:'20rem',height:'20rem',
        background:'rgba(22,163,74,0.08)',borderRadius:'50%',filter:'blur(60px)',pointerEvents:'none' }}/>

      <div style={{ position:'relative',width:'100%',maxWidth:'460px' }}>
        {/* Logo */}
        <div style={{ textAlign:'center',marginBottom:'2rem' }}>
          <div style={{ display:'inline-flex',width:'64px',height:'64px',
            background:'#16a34a',borderRadius:'16px',alignItems:'center',
            justifyContent:'center',marginBottom:'1rem',
            boxShadow:'0 20px 40px rgba(22,163,74,0.35)' }}>
            <span style={{ color:'#fff',fontWeight:900,fontSize:'1.5rem' }}>ET</span>
          </div>
          <h1 style={{ color:'#fff',fontWeight:800,fontSize:'1.875rem',margin:0 }}>Create Account</h1>
          <p style={{ color:'#9ca3af',marginTop:'0.25rem',fontSize:'0.875rem' }}>
            National Digital Microfinance Platform 🇪🇹
          </p>
        </div>

        {/* Card */}
        <div style={{ background:'rgba(255,255,255,0.08)',backdropFilter:'blur(20px)',
          border:'1px solid rgba(255,255,255,0.15)',borderRadius:'24px',
          padding:'2rem',boxShadow:'0 25px 50px rgba(0,0,0,0.4)' }}>

          {success ? (
            <div style={{ textAlign:'center',padding:'2rem 0' }}>
              <div style={{ fontSize:'3.5rem',marginBottom:'1rem' }}>✅</div>
              <p style={{ color:'#86efac',fontWeight:700,fontSize:'1.25rem' }}>Account created!</p>
              <p style={{ color:'#6b7280',fontSize:'0.875rem',marginTop:'0.5rem' }}>
                Redirecting to login…
              </p>
            </div>
          ) : (
            <form onSubmit={handleSubmit}>
              {error && (
                <div style={{ background:'rgba(239,68,68,0.15)',border:'1px solid rgba(239,68,68,0.4)',
                  color:'#fca5a5',fontSize:'0.875rem',borderRadius:'12px',
                  padding:'0.75rem 1rem',marginBottom:'1.25rem' }}>
                  {error}
                </div>
              )}

              {/* Demo hint box */}
              <div style={{ background:'rgba(22,163,74,0.12)',border:'1px solid rgba(22,163,74,0.3)',
                borderRadius:'12px',padding:'0.75rem 1rem',marginBottom:'1.25rem',
                fontSize:'0.8rem',color:'#86efac' }}>
                <strong>📋 Example values:</strong><br/>
                Phone: <code>+251911234567</code> &nbsp;|&nbsp;
                Password: <code>Test@1234</code> &nbsp;|&nbsp;
                PIN: <code>1234</code>
              </div>

              {/* userId */}
              <div style={{ marginBottom:'1rem' }}>
                <label style={{ display:'block',color:'#d1d5db',fontSize:'0.875rem',
                  fontWeight:500,marginBottom:'0.5rem' }}>User ID</label>
                <input type="text" value={form.userId} onChange={set('userId')}
                  placeholder="e.g. user001" required
                  style={{ width:'100%',background:'rgba(255,255,255,0.08)',
                    border:'1px solid rgba(255,255,255,0.15)',borderRadius:'12px',
                    padding:'0.75rem 1rem',color:'#fff',fontSize:'0.875rem',
                    outline:'none',boxSizing:'border-box' }} />
              </div>

              {/* phoneNumber */}
              <div style={{ marginBottom:'1rem' }}>
                <label style={{ display:'block',color:'#d1d5db',fontSize:'0.875rem',
                  fontWeight:500,marginBottom:'0.5rem' }}>Phone Number *</label>
                <input type="tel" value={form.phoneNumber} onChange={set('phoneNumber')}
                  placeholder="+251911234567" required
                  style={{ width:'100%',background:'rgba(255,255,255,0.08)',
                    border:'1px solid rgba(255,255,255,0.15)',borderRadius:'12px',
                    padding:'0.75rem 1rem',color:'#fff',fontSize:'0.875rem',
                    outline:'none',boxSizing:'border-box' }} />
                <p style={{ color:'#6b7280',fontSize:'0.75rem',marginTop:'0.25rem' }}>
                  Format: +251 followed by 9 digits (e.g. +251911234567)
                </p>
              </div>

              {/* email */}
              <div style={{ marginBottom:'1rem' }}>
                <label style={{ display:'block',color:'#d1d5db',fontSize:'0.875rem',
                  fontWeight:500,marginBottom:'0.5rem' }}>Email (optional)</label>
                <input type="email" value={form.email} onChange={set('email')}
                  placeholder="you@example.com"
                  style={{ width:'100%',background:'rgba(255,255,255,0.08)',
                    border:'1px solid rgba(255,255,255,0.15)',borderRadius:'12px',
                    padding:'0.75rem 1rem',color:'#fff',fontSize:'0.875rem',
                    outline:'none',boxSizing:'border-box' }} />
              </div>

              {/* password */}
              <div style={{ marginBottom:'1rem' }}>
                <label style={{ display:'block',color:'#d1d5db',fontSize:'0.875rem',
                  fontWeight:500,marginBottom:'0.5rem' }}>Password</label>
                <input type="password" value={form.password} onChange={set('password')}
                  placeholder="Test@1234"
                  style={{ width:'100%',background:'rgba(255,255,255,0.08)',
                    border:'1px solid rgba(255,255,255,0.15)',borderRadius:'12px',
                    padding:'0.75rem 1rem',color:'#fff',fontSize:'0.875rem',
                    outline:'none',boxSizing:'border-box' }} />
                <p style={{ color:'#6b7280',fontSize:'0.75rem',marginTop:'0.25rem' }}>
                  Must have uppercase, lowercase, digit & special char (e.g. Test@1234)
                </p>
              </div>

              {/* PIN */}
              <div style={{ marginBottom:'1.5rem' }}>
                <label style={{ display:'block',color:'#d1d5db',fontSize:'0.875rem',
                  fontWeight:500,marginBottom:'0.5rem' }}>4–6 Digit PIN *</label>
                <input type="password" value={form.pin} onChange={set('pin')}
                  placeholder="1234" maxLength={6} required
                  style={{ width:'100%',background:'rgba(255,255,255,0.08)',
                    border:'1px solid rgba(255,255,255,0.15)',borderRadius:'12px',
                    padding:'0.75rem 1rem',color:'#fff',fontSize:'0.875rem',
                    outline:'none',boxSizing:'border-box' }} />
              </div>

              <button type="submit" disabled={loading}
                style={{ width:'100%',background:'#16a34a',color:'#fff',fontWeight:700,
                  padding:'0.875rem',borderRadius:'12px',border:'none',cursor:'pointer',
                  fontSize:'1rem',display:'flex',alignItems:'center',justifyContent:'center',
                  gap:'0.5rem',opacity:loading ? 0.6 : 1,
                  boxShadow:'0 8px 20px rgba(22,163,74,0.3)' }}>
                {loading && <Spinner size="sm" />}
                {loading ? 'Creating account…' : 'Create Account'}
              </button>
            </form>
          )}

          <p style={{ textAlign:'center',color:'#6b7280',fontSize:'0.8rem',marginTop:'1.5rem' }}>
            Already have an account?{' '}
            <Link to="/login" style={{ color:'#4ade80',fontWeight:600,textDecoration:'none' }}>
              Sign in
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
