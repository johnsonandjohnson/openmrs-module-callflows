import uuid from 'uuid';

import ConfigUI from './config-ui';
import IConfig from '../../shared/model/config.model';

export default class ConfigFormData {
  config: ConfigUI;
  localId: string;
  isOpen: boolean;

  constructor(fetched?: IConfig) {
    this.config = new ConfigUI(fetched);
    this.localId = uuid.v4();
    this.isOpen = !fetched;
  }
}
