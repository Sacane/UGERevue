export interface UserIdDTO {
  id: number,
  username: string,
  role: string
}

export interface UserConnectedDTO {
  id: number,
  username: string,
  role: string,
  accessToken: string
}

export interface NewQuestionDTO {
  title: string,
  description: string,
  javaFile: File,
  testFile?: File
}
