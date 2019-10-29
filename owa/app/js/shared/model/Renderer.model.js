import _ from 'lodash';

export default class RendererModel {
  constructor(fetched) {
      this.initDefault();
      this.mergeWithFetched(fetched);
  }


  initDefault = () => {
    this.name = null;
    this.mimeType = null;
    this.template = null;
  }

  mergeWithFetched = (fetched) => {
    if (!!fetched) {
      if (!!fetched.name) {
              this.name = fetched.name;
      }
      if (!!fetched.mimeType) {
              this.mimeType = fetched.mimeType;
      }
      if (!!fetched.template) {
              this.template = fetched.template;
      }
    }
  }
}
