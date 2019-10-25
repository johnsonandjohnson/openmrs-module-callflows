import _ from 'lodash';

export default class ConfigModel {
  constructor(fetched) {
    this.initDefault();
    this.mergeWithFetched(fetched);
  }

  initDefault = () => {
    this.name = null;
    this.outgoingCallUriTemplate = null;
    this.outgoingCallPostHeadersMap = {};
    this.outgoingCallPostParams = {};
    this.outgoingCallMethod = null;
    this.outboundCallLimit = 0;
    this.outboundCallRetrySeconds = 0;
    this.outboundCallRetryAttempts = 0;
    this.callAllowed = false;
    this.servicesMap = {};
    this.testUsersMap = {};
    this.hasAuthRequired = false;
  }

  mergeWithFetched = (fetched) => {
    if (!!fetched) {
      if (!!fetched.name) {
        this.name = fetched.name;
      }
      if (!!fetched.outgoingCallUriTemplate) {
        this.outgoingCallUriTemplate = fetched.outgoingCallUriTemplate;
      }
      if (!!fetched.outgoingCallPostHeadersMap) {
        this.outgoingCallPostHeadersMap = fetched.outgoingCallPostHeadersMap;
      }
      if (!!fetched.outgoingCallPostParams) {
        this.outgoingCallPostParams = fetched.outgoingCallPostParams;
      }
      if (!!fetched.outgoingCallMethod) {
        this.outgoingCallMethod = fetched.outgoingCallMethod;
      }
      if (!!fetched.outboundCallLimit) {
        this.outboundCallLimit = fetched.outboundCallLimit;
      }
      if (!!fetched.outboundCallRetrySeconds) {
        this.outboundCallRetrySeconds = fetched.outboundCallRetrySeconds;
      }
      if (!!fetched.outboundCallRetryAttempts) {
        this.outboundCallRetryAttempts = fetched.outboundCallRetryAttempts;
      }
      if (!!fetched.callAllowed) {
        this.callAllowed = fetched.callAllowed;
      }
      if (!!fetched.servicesMap) {
        this.servicesMap = fetched.servicesMap;
      }
      if (!!fetched.testUsersMap) {
        this.testUsersMap = fetched.testUsersMap;
      }
      if (!!fetched.hasAuthRequired) {
        this.hasAuthRequired = fetched.hasAuthRequired;
      }
    }
  }
}
