import _ from 'lodash';

export default class ConfigModel {
  constructor(fetched) {
    this.name = fetched.name;
    this.outgoingCallUriTemplate = (!!fetched.outgoingCallUriTemplate)
      ? fetched.outgoingCallUriTemplate
      : null;
    this.outgoingCallPostHeadersMap = fetched.outgoingCallPostHeadersMap
      ? fetched.outgoingCallPostHeadersMap
      : null;
    this.outgoingCallPostParams = fetched.outgoingCallPostParams
      ? fetched.outgoingCallPostParams
      : null;
    this.outgoingCallMethod = fetched.outgoingCallMethod
      ? fetched.outgoingCallMethod
      : null;
    this.outboundCallLimit = fetched.outboundCallLimit
      ? fetched.outboundCallLimit
      : null;
    this.outboundCallRetrySeconds = fetched.outboundCallRetrySeconds
      ? fetched.outboundCallRetrySeconds
      : null;
    this.outboundCallRetryAttempts = fetched.outboundCallRetryAttempts
      ? fetched.outboundCallRetryAttempts
      : null;
    this.callAllowed = fetched.callAllowed
      ? fetched.callAllowed
      : null;
    this.authToken = fetched.authToken
      ? fetched.authToken
      : null;
    this.hasAuthRequired = fetched.hasAuthRequired
      ? fetched.hasAuthRequired
      : null;
    this.servicesMap = fetched.servicesMap
      ? _.map(fetched.servicesMap, (value, prop) => ({ prop, value }))
      : [];
    this.testUsersMap = fetched.testUsersMap
      ? fetched.testUsersMap
      : null;
  }
}


