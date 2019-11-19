export interface IElement {
  name: string;
  type: ElementType;
  txt: string;
  fieldType?: string; // todo define enum
  dtmfGrammar?: string;
  goodBye?: string; // exit
  noMatch?: string;
  noInput?: string;
  voiceGrammar?: string;
  fieldMeta?: string;
  reprompt?: number;
  bargeIn?: boolean;
  dtmf?: boolean;
  voice?: boolean;
}

export enum ElementType {
  FIELD = 'field',
  TXT = 'txt'
}

export const defaultTxtValue: Readonly<IElement> = {
  name: 'Txt',
  type: ElementType.TXT,
  txt: ''
}