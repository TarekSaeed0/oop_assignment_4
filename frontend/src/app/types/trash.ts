export enum MailSource {
  INBOX = 'INBOX',
  SENT = 'SENT'
}

export interface SenderDTO {
  name: string;
  email: string;
}

export interface DeletedMailDTO {
  id: number;
  subject: string;
  body: string;
  priority: string;
  date: string; // LocalDateTime comes as string
  attachments: any[]; // Define attachment type if needed
  sender: SenderDTO;
  receivers: SenderDTO[]; 
  source: MailSource;
}