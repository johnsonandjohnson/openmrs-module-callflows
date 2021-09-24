import uuid from 'uuid';

import RendererUI from './RendererUI';

export default class RendererFormData {
  constructor(fetched) {
    this.renderer = new RendererUI(fetched);
    this.localId = uuid.v4();
    this.isOpen = !fetched;
  }
}