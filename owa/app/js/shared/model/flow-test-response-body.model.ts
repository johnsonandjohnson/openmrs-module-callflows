export interface IFlowTestResponseBody {
  txt: string,
  field?: string,
  type?: string,
  bargeIn?: boolean,
  dtmf?: boolean,
  voice?: boolean,
  meta?: any,
  reprompt?: number
}
