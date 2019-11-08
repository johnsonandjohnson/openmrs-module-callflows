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

import { getFlows } from '../../reducers/designer.reducer';
import { IRootState } from '../../reducers';
import Table from '../table';
import DesignerFilters from './designer-filter';

export interface IDesignerTableProps extends StateProps, DispatchProps {
};

export interface IDesignerTableState {
};

export class DesignerTable extends React.PureComponent<IDesignerTableProps, IDesignerTableState> {
  constructor(props) {
    super(props);
  }

  render = () => {
    const columns = [
      {
        Header: 'Name',
        accessor: 'name',
        Cell: props => {
          const link = `#designer/${props.value}`;
          return ( //TODO: use Link
            <span>
              {props.value}
              <a href={link}>Edit</a>
            </span>
          );
        }
      },
    ];

    return (
      <Table
        filtersComponent={DesignerFilters}
        data={this.props.data}
        columns={columns}
        loading={this.props.loading}
        pages={this.props.pages}
        fetchDataCallback={this.props.getFlows}
      />
    );
  }
}

const mapStateToProps = ({ designerReducer }: IRootState) => (designerReducer);

const mapDispatchToProps = {
  getFlows
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(DesignerTable);
