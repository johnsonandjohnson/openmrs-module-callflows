import React from 'react';

import './tab.scss';
import { Controlled as CodeMirror } from 'react-codemirror2';
import { Tabs, Tab } from 'react-bootstrap'
import { RenderedLabel } from './rendered-label';
import { IUserNodeTemplate } from '../../../../shared/model/user-node-template.model';
import _ from 'lodash'
import * as Default from '../../../../shared/utils/messages.js'
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";

interface IProps {
    templates: Map<string, IUserNodeTemplate>;
    nodeIndex: number;
    updateTemplateContent: (templateKey: string, value: string) => void;
    updateTemplateDirtyStatus: (templateKey: string, value: boolean) => void;
}

export class RenderedSections extends React.Component<IProps> {

    options = {
        autofocus: true,
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

    renderBody = (templateKey: string) => {
        const template = this.props.templates[templateKey];
        return (!template) ? (<p>{getIntl().formatMessage({ id: 'CALLFLOW_MISSING_TEMPLATE', defaultMessage: Default.MISSING_TEMPLATE }) + templateKey}</p>) : (
            <CodeMirror
                value={template.content}
                options={this.options}
                onBeforeChange={(editor, data, value) => this.props.updateTemplateContent(templateKey, value)}
                onChange={(editor, data, value) => this.props.updateTemplateContent(templateKey, value)} />
        );
    };

    createTitle = (key: string) => {
        const template = this.props.templates[key];
        return (!template) ? null : (
            <RenderedLabel
                template={template}
                templateKey={key}
                updateTemplateDirty={this.props.updateTemplateDirtyStatus} />
        );
    };

    render = () => {
        const { nodeIndex, templates } = this.props;
        return (!templates) ? null : (
            <div className="body-wrapper">
                <Tabs id={nodeIndex + '-tabs'}>
                    {Object.keys(templates).map((templateKey: string) =>
                      // @ts-ignore additional parameter mismatch
                      <Tab mountOnEnter={true}
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
