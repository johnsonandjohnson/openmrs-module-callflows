import React from 'react';
import { IconProp } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IUserNodeTemplate } from "../../../../shared/model/user-node-template.model";
import * as Default from '../../../../shared/utils/messages.js';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";

interface IProps {
    template: IUserNodeTemplate;
    templateKey: string;
    updateTemplateDirty: (templateKey: string, value: boolean) => void;
}

export class RenderedLabel extends React.Component<IProps> {

    dirtyIcon: IconProp = ['fas', 'star'];
    nonDirtyIcon: IconProp = ['far', 'star'];

    constructor(props: IProps) {
        super(props);
    }

    handleDirtyStatusChange = (event) => {
        event.preventDefault();
        this.props.updateTemplateDirty(this.props.templateKey, !this.props.template.dirty);
    };

    render = () => {
        const { template, templateKey } = this.props;
        return (<div>
            {templateKey}
            &nbsp;&nbsp;
                    {template.dirty ?
                (
                    <span onClick={this.handleDirtyStatusChange} title={getIntl().formatMessage({ id: 'CALLFLOW_DIRTY_LABEL', defaultMessage: Default.DIRTY_LABEL })}>
                        <FontAwesomeIcon size="1x" icon={this.dirtyIcon} />
                    </span>
                ) : (
                    <span onClick={this.handleDirtyStatusChange} title={getIntl().formatMessage({ id: 'CALLFLOW_NON_DIRTY_LABEL', defaultMessage: Default.NON_DIRTY_LABEL })}>
                        <FontAwesomeIcon size="1x" icon={this.nonDirtyIcon} />
                    </span>
                )
            }
        </div>);
    };
}
