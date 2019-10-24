import _ from 'lodash';
import uuid from 'uuid';

const KEY = 'key';
const VALUE = 'value';

export default class MapEntry {
  constructor(key, value, id) {
    this.id = (!!id) ? id : uuid.v4();
    this.key = key;
    this.value = value;
  }

  static jsonToList = (json) => {
    return _.map(json, (value, key) => (new MapEntry(key, value)));
  }

  static arrayToJson = (mapEntryArray) => {
    return _.chain(mapEntryArray)
      .keyBy(KEY)
      .mapValues(VALUE)
      .value();
  }

  static paramsToArray = (str) => {
    const params = _.split(str, '&').map(param => { return _.split(param, '=') });
    return params.map(item => { return new MapEntry(item[0], item[1]); });
  }

  static arrayToParams = (arr) => {
    let params = arr.map(entry => {
      return entry.key + '=' + entry.value;
    });
    return _.join(params, '&');
  }

  toString = () => {
    return this.key + ':' + this.value;
  }
}
