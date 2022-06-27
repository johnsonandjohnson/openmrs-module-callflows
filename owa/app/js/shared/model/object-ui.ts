import _ from 'lodash';
import uuid from 'uuid';

export class ObjectUI<T> {
  model: T;
  localId: string;

  constructor() {
    this.localId = uuid.v4();
  }
  getModel = (): T => _.clone(this.model);
}