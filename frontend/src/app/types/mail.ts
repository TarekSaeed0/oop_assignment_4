export enum Priority {
  LOW, NORMAL, HIGH, URGENT
}

export type MailResponce = Mail[]

export interface Mail {
  id: number,
  data: {
    sender: {
      name: string,
      email: string
    },
    subject: string,
    body: string,
    priority: Priority,
    sentAt: Date,
    attachments: Attachment[]
  }
}

export interface Attachment {

}
