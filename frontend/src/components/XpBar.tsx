interface XpBarProps {
  experience: number
  xpToNextLevel: number
}

export default function XpBar({ experience, xpToNextLevel }: XpBarProps) {
  const pct = Math.min(100, Math.round((experience / xpToNextLevel) * 100))
  return (
    <div>
      <div className="flex justify-between text-xs text-slate-400 mb-1">
        <span>XP</span>
        <span>{experience} / {xpToNextLevel}</span>
      </div>
      <div className="h-2 bg-slate-700 rounded-full overflow-hidden">
        <div
          className="h-full bg-amber-500 rounded-full transition-all"
          style={{ width: `${pct}%` }}
        />
      </div>
    </div>
  )
}
