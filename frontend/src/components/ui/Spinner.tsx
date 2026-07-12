interface Props { size?: 'sm' | 'md' | 'lg'; className?: string }

export default function Spinner({ size = 'md' }: Props) {
  const dim = size === 'sm' ? '16px' : size === 'lg' ? '48px' : '28px'
  const bw  = size === 'sm' ? '2px'  : '3px'
  return (
    <div style={{
      width: dim, height: dim, flexShrink: 0,
      borderRadius: '50%',
      border: `${bw} solid rgba(255,255,255,0.25)`,
      borderTopColor: '#fff',
      animation: 'spin 0.7s linear infinite',
    }} />
  )
}
