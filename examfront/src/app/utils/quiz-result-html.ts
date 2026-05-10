import { QuizAttemptDetail, QuizQuestionResult } from '../models/quiz-attempt.models';

function escapeHtml(text: string): string {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

export function buildQuizResultHtml(detail: QuizAttemptDetail, completedLabel: string): string {
  const pct =
    detail.totalQuestions > 0
      ? Math.round((100 * detail.score) / detail.totalQuestions)
      : 0;

  const rows = detail.details.map((d: QuizQuestionResult, i: number) => {
    const sel =
      d.selectedIndex != null && d.options[d.selectedIndex] != null
        ? escapeHtml(d.options[d.selectedIndex])
        : '— (skipped)';
    const correct = escapeHtml(d.options[d.correctIndex] ?? '');
    const ok = d.selectedIndex === d.correctIndex;
    return `<tr class="${ok ? 'ok' : 'bad'}">
      <td>${i + 1}</td>
      <td>${escapeHtml(d.question)}</td>
      <td>${sel}</td>
      <td>${ok ? '—' : correct}</td>
    </tr>`;
  });

  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <title>Quiz result — ${escapeHtml(detail.subjectTitle)}</title>
  <style>
    body { font-family: Roboto, Arial, sans-serif; margin: 24px; color: #222; }
    h1 { font-size: 1.4rem; }
    .meta { color: #555; margin-bottom: 16px; }
    table { border-collapse: collapse; width: 100%; font-size: 0.9rem; }
    th, td { border: 1px solid #ccc; padding: 8px 10px; text-align: left; vertical-align: top; }
    th { background: #3949ab; color: #fff; }
    tr.ok { background: #e8f5e9; }
    tr.bad { background: #ffebee; }
    .score { font-size: 1.2rem; font-weight: 700; color: #3949ab; margin: 12px 0; }
  </style>
</head>
<body>
  <h1>${escapeHtml(detail.subjectTitle)}</h1>
  <div class="meta">
    Attempt #${detail.attemptNumber} · ${escapeHtml(completedLabel)}<br/>
    Subject id: ${escapeHtml(detail.subjectId)}
  </div>
  <div class="score">Score: ${detail.score} / ${detail.totalQuestions} (${pct}%)</div>
  <table>
    <thead><tr><th>#</th><th>Question</th><th>Your answer</th><th>Correct (if wrong)</th></tr></thead>
    <tbody>${rows.join('')}</tbody>
  </table>
</body>
</html>`;
}

export function triggerHtmlDownload(filename: string, html: string): void {
  const blob = new Blob([html], { type: 'text/html;charset=utf-8' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
}
