export default interface IConfig {
  name: string | null;
  outgoingCallUriTemplate?: string | null;
  outgoingCallPostHeadersMap?: any;
  outgoingCallPostParams?: any;
  outgoingCallMethod?: string | null;
  outboundCallLimit?: number;
  outboundCallRetrySeconds?: number;
  outboundCallRetryAttempts?: number;
  callAllowed?: boolean;
  servicesMap?: any;
  testUsersMap?: any;
  hasAuthRequired?: boolean;
};

export const defaultValue: Readonly<IConfig> = {
  name: 'New Config',
  outgoingCallUriTemplate: null,
  outgoingCallPostHeadersMap: {},
  outgoingCallPostParams: {},
  outgoingCallMethod: null,
  outboundCallLimit: 0,
  outboundCallRetrySeconds: 0,
  outboundCallRetryAttempts: 0,
  callAllowed: false,
  servicesMap: {},
  testUsersMap: {},
  hasAuthRequired: false
};
