import { ReactNode } from 'react'
import { clsx } from 'clsx'

interface Props {
  title: string
  value: string | number
  subtitle?: string
  icon?: ReactNode
  trend?: number
  color?: 'green'|'blue'|'purple'|'orange'|'red'
}

const colors = {
  green:  'bg-green-50  text-green-600',
  blue:   'bg-blue-50   text-blue-600',
  purple: 'bg-purple-50 text-purple-600',
  orange: 'bg-orange-50 text-orange-600',
  red:    'bg-red-50    text-red-600',
}

export default function StatCard({ title, value, subtitle, icon, trend, color = 'green' }: Props) {
  return (
    <div className="card flex items-start gap-4">
      {icon && (
        <div className={clsx('p-3 rounded-xl flex-shrink-0', colors[color])}>
          {icon}
        </div>
      )}
      <div className="min-w-0 flex-1">
        <p className="text-sm text-gray-500 font-medium truncate">{title}</p>
        <p className="text-2xl font-bold text-gray-900 mt-0.5 truncate">{value}</p>
        {(subtitle || trend !== undefined) && (
          <p className="text-xs text-gray-400 mt-1">
            {trend !== undefined && (
              <span className={trend >= 0 ? 'text-green-600 font-semibold' : 'text-red-500 font-semibold'}>
                {trend >= 0 ? '+' : ''}{trend}%{' '}
              </span>
            )}
            {subtitle}
          </p>
        )}
      </div>
    </div>
  )
}
