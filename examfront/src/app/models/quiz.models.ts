export interface QuizSubjectMeta {
  id: string;
  title: string;
  description: string;
  /** Optional Material icon name (see https://fonts.google.com/icons) */
  icon?: string;
}

export interface QuizQuestion {
  id: string;
  question: string;
  options: string[];
  /** Zero-based index of the correct option in `options` */
  correctIndex: number;
}

export interface QuizDataFile {
  subjects: QuizSubjectMeta[];
  questionsBySubject: Record<string, QuizQuestion[]>;
}
