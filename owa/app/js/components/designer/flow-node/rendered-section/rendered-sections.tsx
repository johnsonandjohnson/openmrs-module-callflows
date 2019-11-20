import React from 'react';

import './tab.scss';
import { Controlled as CodeMirror } from 'react-codemirror2';
import { Tabs, Tab } from 'react-bootstrap'
import { RenderedLabel } from './rendered-label';
import { IUserNode } from '../../../../shared/model/user-node.model';
import { IUserNodeTemplate } from '../../../../shared/model/user-node-template.model';
import _ from 'lodash'
import * as Msg from '../../../../shared/utils/messages.js'

interface IProps {
    node: IUserNode;
    nodeIndex: number;
    updateNode: (node: any, nodeIndex: number) => void;
}

interface IState {
    templates: Map<string, IUserNodeTemplate>;
}

export class RenderedSections extends React.Component<IProps, IState> {

    constructor(props: IProps) {
        super(props);
        this.state = {
            templates: props.node.templates,
        }
    }

    options = {
        lineNumbers: true,
        lineWrapping: true,
        addModeClass: true,
        tabSize: 2,
        mode: 'xml', // may be: javascript,xml,sql,velocity
        theme: 'default',
        extraKeys: {
            'F11': function (cm) {
                cm.setOption('fullScreen', !cm.getOption('fullScreen'));
            },
            'Esc': function (cm) {
                if (cm.getOption('fullScreen')) cm.setOption('fullScreen', false);
            }
        }
    };

    updateStateTemplateContent = (templateKey: string, value: string) => {
        let changedTemplates = _.clone(this.state.templates);
        changedTemplates[templateKey].content = value;
        this.setState({ ...this.state, templates: changedTemplates })
    };

    handleTamplateDirtyChange = (templateKey: string, value: boolean) => {
        let changedTemplates = _.clone(this.state.templates);
        changedTemplates[templateKey].dirty = value;
        this.setState({ ...this.state, templates: changedTemplates })
        this.handleTemplateUpdate();
    };

    handleTemplateContentValueChange = (templateKey: string, value: string) => {
        this.updateStateTemplateContent(templateKey, value);
        this.handleTemplateUpdate();
    };

    handleTemplateUpdate = () => {
        const node = this.props.node;
        node.templates = this.state.templates;
        this.props.updateNode(node, this.props.nodeIndex);
    };

    renderBody = (templateKey: string) => {
        let template = this.state.templates[templateKey];
        return (!template) ? (<p>{Msg.MISSING_TEMPLATE + templateKey}</p>) : (
            <CodeMirror
                value={template.content}
                options={this.options}
                onBeforeChange={(editor, data, value) => this.updateStateTemplateContent(templateKey, value)}
                onChange={(editor, data, value) => this.handleTemplateContentValueChange(templateKey, value)} />
        );
    };

    createTitle = (key: string) => {
        const template = this.state.templates[key];
        return (!template) ? null : (
            <RenderedLabel
                template={template}
                templateKey={key}
                updateTemplateDirty={this.handleTamplateDirtyChange} />
        );
    };

    render = () => {
        let { nodeIndex } = this.props;
        let { templates } = this.state;
        return (!templates) ? null : (
            <div className="body-wrapper">
                <Tabs id={nodeIndex + '-tabs'}>
                    {Object.keys(templates).map((templateKey: string) =>
                        <Tab
                            key={nodeIndex + '-' + templateKey}
                            eventKey={nodeIndex + '-' + templateKey}
                            title={this.createTitle(templateKey)}>
                            {this.renderBody(templateKey)}
                        </Tab>
                    )}
                </Tabs>
            </div>);
    };
}
