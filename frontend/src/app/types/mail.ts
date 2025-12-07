export enum Priority {
  LOW, NORMAL, HIGH, URGENT
}

export type MailResponce = Mail[]

export interface Mail {
  id: number,
  data: {
    sender?: {
      name: string,
      email: string
    },
    receiver?: {
      name: string,
      email: string
    },
    subject: string,
    body: string,
    priority: string,
    sentAt: Date,
    attachments: Attachment[]
  }
}

export interface Attachment {

}
