import uuid from 'uuid';

import ConfigUI from './ConfigUI';

export default class ConfigFormData {
  constructor(fetched) {
    this.config = new ConfigUI(fetched);
    this.localId = uuid.v4();
    this.isOpen = !fetched;
  }
}
