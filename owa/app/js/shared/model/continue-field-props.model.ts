import { IFlowTestResponseBody } from '../model/flow-test-response-body.model';

export interface IContinueFieldProps{
  callId: string | null,
  currentNodeFields: Array<IFlowTestResponseBody>,
  params: any,
  nodeName?: string | null,
  continueNode?: boolean
}
