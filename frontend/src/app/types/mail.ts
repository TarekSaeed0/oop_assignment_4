export enum Priority {
  LOW,
  NORMAL,
  HIGH,
  URGENT,
}

export type InboxMailResponce = InboxMail[];

export interface InboxMail {
  id: number;
  data: {
    sender: {
      name: string;
      email: string;
    };
    receiver: {
      name: string;
      email: string;
    };
    subject: string;
    body: string;
    priority: string;
    sentAt: Date;
    attachments: Attachment[];
  };
}

export type SentMailResponce = SentMail[];

export interface SentMail {
  id: number;
  data: {
    sender: {
      name: string;
      email: string;
    };
    receivers: {
      name: string;
      email: string;
    }[];
    subject: string;
    body: string;
    priority: string;
    sentAt: Date;
    attachments: Attachment[];
  };
}
export interface Attachment {
  id: number;
  name: string;
  type: string;
  size: number;
}
