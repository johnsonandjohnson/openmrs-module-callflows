import _ from 'lodash';
import uuid from 'uuid';

const KEY = 'key';
const VALUE = 'value';

export default class MapEntry {
  id: string;
  key: string;
  value: string;

  constructor(key='', value='', id?: string) {
    this.id = (!!id) ? id : uuid.v4();
    this.key = key;
    this.value = value;
  }

  static removeEmpty = (entries: MapEntry[]): MapEntry[] => {
    return _.filter(entries, (entry: MapEntry) => {
      return (!(_.isEmpty(entry.key)) || !(_.isEmpty(entry.value)));
    });
  }

  static jsonToArray = (json: any): MapEntry[] => {
    return _.map(json, (value: string, key: string) => (new MapEntry(key, value)));
  }

  static arrayToJson = (entries: MapEntry[]): any => {
    const nonEmpties: MapEntry[] = MapEntry.removeEmpty(entries);
    return _.chain(nonEmpties)
      .keyBy(KEY)
      .mapValues(VALUE)
      .value();
  }

  static paramsToArray = (str: string, separator: string, assign: string): MapEntry[] => {
    const params: string[][] = _.split(str, separator).map((param: string) => { return _.split(param, assign) });
    let entries: MapEntry[] = params.map((item: string[]) => {
      let key = (!!item[0]) ? item[0] : '';
      let value = (!!item[1]) ? item[1] : '';
      return new MapEntry(key, value);
    });

    return MapEntry.removeEmpty(entries);
  }

  static arrayToParams = (entries: MapEntry[], separator: string, assign: string): string => {
    let filtered: MapEntry[] = MapEntry.removeEmpty(entries);
    let params: string[] = filtered.map((entry: MapEntry) => {
      return entry.key + assign + entry.value;
    });
    return _.join(params, separator);
  }

  toString = () => {
    return this.key + ':' + this.value;
  }
}
