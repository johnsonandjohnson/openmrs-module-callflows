import _ from 'lodash';

import RendererModel from '../../shared/model/Renderer.model';

export default class RendererUI {
    constructor(rendererModel) {
        this.initDefault();
        this.mergeWithModel(rendererModel);
    }

initDefault = () => {
    this.name = 'New renderer';
    this.mimeType = '';
    this.template = '';
}

mergeWithModel = (rendererModel) => {
    if (!!rendererModel) {
        if (rendererModel.name) {
            this.name = rendererModel.name;
        }
        if (rendererModel.mimeType) {
            this.mimeType = rendererModel.mimeType;
        }
        if (rendererModel.template) {
            this.template = rendererModel.template;
        }
    }
}

getModel = () => {
    let model = new RendererModel();
    if (!!this.name) {
        model.name = this.name;
    }
    if (!!this.mimeType) {
        model.mimeType = this.mimeType;
    }
    if (!!this.template) {
        model.template = this.template;
    }
    return model;
}

}