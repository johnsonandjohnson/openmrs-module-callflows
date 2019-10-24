import _ from 'lodash';

import ConfigModel from '../../shared/model/Config.model';
import MapEntry from '../../shared/utils/MapEntry';

export default class ConfigUI {
  constructor(configModel) {
    this.initDefault();
    this.mergeWithModel(configModel);
  }

  initDefault = () => {
    this.name = 'New config';
    this.outgoingCallUriTemplate = '';
    this.outgoingCallPostHeadersMap = [];
    this.outgoingCallPostParams = [];
    this.outgoingCallMethod = null;
    this.outboundCallLimit = 0;
    this.outboundCallRetrySeconds = 0;
    this.outboundCallRetryAttempts = 0;
    this.callAllowed = false;
    this.servicesMap = [];
    this.testUsersMap = [];
    this.hasAuthRequired = false;
  }

  mergeWithModel = (configModel) => {
    if (!!configModel) {
      if (!!configModel.name) {
        this.name = configModel.name;
      }
      if (!!configModel.outgoingCallUriTemplate) {
        this.outgoingCallUriTemplate = configModel.outgoingCallUriTemplate;
      }
      if (!!configModel.outgoingCallPostHeadersMap) {
        this.outgoingCallPostHeadersMap = MapEntry.jsonToList(configModel.outgoingCallPostHeadersMap);
      }
      if (!!configModel.outgoingCallPostParams) {
        this.outgoingCallPostParams = MapEntry.paramsToArray(configModel.outgoingCallPostParams);
      }
      if (!!configModel.outgoingCallMethod) {
        this.outgoingCallMethod = configModel.outgoingCallMethod;
      }
      if (!!configModel.outboundCallLimit) {
        this.outboundCallLimit = configModel.outboundCallLimit;
      }
      if (!!configModel.outboundCallRetrySeconds) {
        this.outboundCallRetrySeconds = configModel.outboundCallRetrySeconds;
      }
      if (!!configModel.outboundCallRetryAttempts) {
        this.outboundCallRetryAttempts = configModel.outboundCallRetryAttempts;
      }
      if (!!configModel.callAllowed) {
        this.callAllowed = configModel.callAllowed;
      }
      if (!!configModel.servicesMap) {
        this.servicesMap = MapEntry.jsonToList(configModel.servicesMap);
      }
      if (!!configModel.testUsersMap) {
        this.testUsersMap = MapEntry.jsonToList(configModel.testUsersMap);
      }
      if (!!configModel.hasAuthRequired) {
        this.hasAuthRequired = configModel.hasAuthRequired;
      }
    }
  }

  getModel = () => {
    let model = new ConfigModel();
    if (!!this.name) {
      model.name = this.name;
    }
    if (!!this.outgoingCallUriTemplate) {
      model.outgoingCallUriTemplate = this.outgoingCallUriTemplate;
    }
    if (!!this.outgoingCallPostHeadersMap) {
      model.outgoingCallPostHeadersMap = MapEntry.arrayToJson(this.outgoingCallPostHeadersMap);
    }
    if (!!this.outgoingCallPostParams) {
      model.outgoingCallPostParams = MapEntry.arrayToParams(this.outgoingCallPostParams);
    }
    if (!!this.outgoingCallMethod) {
      model.outgoingCallMethod = this.outgoingCallMethod;
    }
    if (!!this.outboundCallLimit) {
      model.outboundCallLimit = this.outboundCallLimit;
    }
    if (!!this.outboundCallRetrySeconds) {
      model.outboundCallRetrySeconds = this.outboundCallRetrySeconds;
    }
    if (!!this.outboundCallRetryAttempts) {
      model.outboundCallRetryAttempts = this.outboundCallRetryAttempts;
    }
    if (!!this.callAllowed) {
      model.callAllowed = this.callAllowed;
    }
    if (!!this.servicesMap) {
      model.servicesMap = MapEntry.arrayToJson(this.servicesMap);
    }
    if (!!this.testUsersMap) {
      model.testUsersMap = MapEntry.arrayToJson(this.testUsersMap);
    }
    if (!!this.hasAuthRequired) {
      model.hasAuthRequired = this.hasAuthRequired;
    }
    return model;
  }
}


