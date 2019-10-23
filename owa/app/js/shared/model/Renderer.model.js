import uuid from "uuid";

export class RendererModel {
    constructor(rendererResponse, isOpen) {
        this.uiLocalUuid = uuid.v4();
        this.id = rendererResponse ? rendererResponse.id : null;
        this.name = rendererResponse && rendererResponse.name ? rendererResponse.name : '';
        this.mimeType = rendererResponse && rendererResponse.mimeType ? rendererResponse.mimeType : '';
        this.template = rendererResponse && rendererResponse.template ? rendererResponse.template : '';
        this.isOpen = isOpen;
    }

    toRequest() {
        return({
            name: this.name,
            mimeType: this.mimeType, 
            template: this.template
        });
    }
}
