/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { connect } from 'react-redux';
import {
  reset,
  getConfigs,
  postConfigs,
  getFlows
} from '../../reducers/designer.reducer';
import { IRootState } from '../../reducers';
import DesignerTable from './designer-table';
import LinkAddButton from '../link-add-button';
import * as Default from '../../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import { DESIGNER_NEW_FLOW_ROUTE as createLink} from '../bread-crumb/index';

export interface IDesignerProps extends StateProps, DispatchProps {
};
export interface IDesignerState {
};

export class Designer extends React.PureComponent<IDesignerProps, IDesignerState> {

  componentDidMount = () => {
    this.props.getFlows();
  }

  componentDidUpdate = (prev) => {
    if (prev.configForms.length > this.props.configForms.length) {
      this.props.postConfigs(this.props.configForms);
    }
  }

  submitConfigs = () => {
    this.props.postConfigs(this.props.configForms);
  }

  render() {
    return (
      <div className="body-wrapper">
        <div className="row">
          <div className="col-md-12 col-xs-12">
            <h2>{getIntl().formatMessage({ id: 'CALLFLOW_DESIGNER_FLOW_TEST_TITLE', defaultMessage: Default.DESIGNER_FLOW_TEST_TITLE })}</h2>
          </div>
        </div>
        <div className="row">
          <div className="col-md-2 col-md-offset-10 col-xs-2 col-xs-offset-10">
            <LinkAddButton
              link={createLink}
              buttonClass="confirm add-btn"
              txt={getIntl().formatMessage({ id: 'CALLFLOW_DESIGNER_FLOW_TEST_CREATE_BTN', defaultMessage: Default.DESIGNER_FLOW_TEST_CREATE_BTN })} />
          </div>
        </div>
        <div className="panel-body">
          <DesignerTable />
        </div>
      </div>
    );
  }
}

export const mapStateToProps = ({ designerReducer }: IRootState) => (designerReducer);

const mapDispatchToProps = ({
  reset,
  getConfigs,
  postConfigs,
  getFlows
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Designer);
