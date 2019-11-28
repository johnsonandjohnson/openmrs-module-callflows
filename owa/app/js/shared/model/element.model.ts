export interface IElement {
  name: string;
  type: ElementType;
  txt: string;
  fieldType?: FieldType;
  dtmfGrammar?: string;
  goodBye?: string;
  noMatch?: string;
  noInput?: string;
  voiceGrammar?: string;
  fieldMeta?: string;
  reprompt?: string;
  bargeIn?: boolean;
  dtmf?: boolean;
  voice?: boolean;
}

export enum FieldType {
  DIGITS = 'digits',
  DATE = 'date',
  BOOLEAN = 'boolean',
  CURRENCY = 'currency',
  NUMBER = 'number',
  PHONE = 'phone',
  TIME = 'time'
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

export const defaultFieldValue: Readonly<IElement> = {
  name: '',
  type: ElementType.FIELD,
  txt: '',
  fieldType: FieldType.DIGITS,
  noInput: '',
  noMatch: '',
  goodBye: '',
  bargeIn: false,
  dtmf: false,
  voice: false
}
