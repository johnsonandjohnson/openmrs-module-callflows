import _ from 'lodash';

import MapEntry from '../../shared/utils/map-entry-util';
import IConfig, { defaultValue as configDefault } from '../../shared/model/config.model';

export default class ConfigUI {
  name: string;
  outgoingCallUriTemplate?: string;
  outgoingCallPostHeadersMap?: MapEntry[];
  outgoingCallPostParams?: MapEntry[];
  outgoingCallMethod?: string | null;
  outboundCallLimit?: number;
  outboundCallRetrySeconds?: number;
  outboundCallRetryAttempts?: number;
  callAllowed?: boolean;
  servicesMap?: string;
  testUsersMap?: MapEntry[];
  hasAuthRequired?: boolean;

  constructor(model?: IConfig) {
    this.init();
    if (!!model) {
      this.mergeWithModel(model);
    }
  }

  init = () => {
    this.name = 'New config';
    this.outgoingCallUriTemplate = '';
    this.outgoingCallPostHeadersMap = [new MapEntry()];
    this.outgoingCallPostParams = [new MapEntry()];
    this.outgoingCallMethod = null;
    this.outboundCallLimit = 0;
    this.outboundCallRetrySeconds = 0;
    this.outboundCallRetryAttempts = 0;
    this.callAllowed = false;
    this.servicesMap = '';
    this.testUsersMap = [new MapEntry()];
    this.hasAuthRequired = false;
  }

  mergeWithModel = (model: IConfig) => {
    if (!!model) {
      if (!!model.name) {
        this.name = model.name;
      }
      if (!!model.outgoingCallUriTemplate) {
        this.outgoingCallUriTemplate = model.outgoingCallUriTemplate;
      }
      if (!_.isEmpty(model.outgoingCallPostHeadersMap)) {
        this.outgoingCallPostHeadersMap = MapEntry.jsonToArray(model.outgoingCallPostHeadersMap);
      }
      if (!_.isEmpty(model.outgoingCallPostParams)) {
        this.outgoingCallPostParams = MapEntry.paramsToArray(model.outgoingCallPostParams, '&', '=');
      }
      if (!!model.outgoingCallMethod) {
        this.outgoingCallMethod = model.outgoingCallMethod;
      }
      if (!!model.outboundCallLimit) {
        this.outboundCallLimit = model.outboundCallLimit;
      }
      if (!!model.outboundCallRetrySeconds) {
        this.outboundCallRetrySeconds = model.outboundCallRetrySeconds;
      }
      if (!!model.outboundCallRetryAttempts) {
        this.outboundCallRetryAttempts = model.outboundCallRetryAttempts;
      }
      if (!!model.callAllowed) {
        this.callAllowed = model.callAllowed;
      }
      if (!_.isEmpty(model.servicesMap)) {
        const params = MapEntry.jsonToArray(model.servicesMap);
        this.servicesMap = MapEntry.arrayToParams(params, ',', ':');
      }
      if (!_.isEmpty(model.testUsersMap)) {
        this.testUsersMap = MapEntry.jsonToArray(model.testUsersMap);
      }
      if (!!model.hasAuthRequired) {
        this.hasAuthRequired = model.hasAuthRequired;
      }
    }
  }

  getModel = (): IConfig => {
    let model: IConfig =  _.clone(configDefault);
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
      model.outgoingCallPostParams = MapEntry.arrayToParams(this.outgoingCallPostParams, '&', '=');
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
      let params = MapEntry.paramsToArray(this.servicesMap, ',', ':');
      model.servicesMap = MapEntry.arrayToJson(params);
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
