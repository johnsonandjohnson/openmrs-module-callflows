export interface IElement {
  name: string;
  type: string; // todo maybe we can define enum
  fieldType: string; // todo define enum
  txt: string;
  dtmfGrammar: string;
  goodBye: string; // exit
  noMatch: string;
  noInput: string;
  voiceGrammar: string;
  fieldMeta: string;
  reprompt: number;
  bargeIn: boolean;
  dtmf: boolean;
  voice: boolean;
}