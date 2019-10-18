export default class ConfigModel {
  constructor(fetched) {
    this.name = fetched.name;
    this.outgoingCallUriTemplate = fetched.outgoingCallUriTemplate;
    this.outgoingCallPostHeadersMap = fetched.outgoingCallPostHeadersMap;
    this.outgoingCallPostParams = fetched.outgoingCallPostParams;
    this.outgoingCallMethod = fetched.outgoingCallMethod;
    this.outboundCallLimit = fetched.outboundCallLimit;
    this.outboundCallRetrySeconds = fetched.outboundCallRetrySeconds;
    this.outboundCallRetryAttempts = fetched.outboundCallRetryAttempts;
    this.callAllowed = fetched.callAllowed;
    this.authToken = fetched.authToken;
    this.hasAuthRequired = fetched.hasAuthRequired;
    this.servicesMap = fetched.servicesMap;
    this.testUsersMap = fetched.testUsersMap;
  }
}

