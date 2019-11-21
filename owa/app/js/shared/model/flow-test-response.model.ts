export interface IFlowTestResponse {
  body: string
  callId: string,
  node: string,
  continueNode?: boolean,
  error: false
}
