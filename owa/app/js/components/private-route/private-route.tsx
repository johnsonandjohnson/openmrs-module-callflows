/*
 *  This Source Code Form is subject to the terms of the Mozilla Public License,
 *  v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 *  the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *  <p>
 *  Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 *  graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { connect } from 'react-redux';
import { Route, RouteComponentProps, RouteProps } from "react-router";
import { getPrivileges } from "./private-route.reducer";
import './private-route.css';

export interface IPrivateRouteProps extends StateProps, DispatchProps, RouteProps {
  _component: ((props: RouteComponentProps<any>) => React.ReactNode),
  requiredPrivilege?: string
}

export interface IPrivateRouteState {}

export class PrivateRoute extends React.PureComponent<IPrivateRouteProps, IPrivateRouteState> {

  private readonly _loginUrl = '/openmrs/login.htm';
  private readonly _unauthorizedMessage = 'You are not authorized to see this page.';
  private readonly _loadingMessage = 'Loading...';

  componentDidMount = () => {
    this.props.getPrivileges();
  }

  render() {
    const {
      loading,
      authenticated,
      privileges,
      requiredPrivilege
    } = this.props;

    if (loading) {
      return this.displayLoading();
    }
    if (!authenticated) {
      this.redirectToLogin();
    }
    if (requiredPrivilege && !privileges.includes(requiredPrivilege)) {
      return this.renderUnauthorizedPage();
    }

    return <Route {...this.props} path={this.props.path} render={this.props._component} />;
  }

  private redirectToLogin() {
    // eslint-disable-next-line no-restricted-globals
    location.href = this._loginUrl;
  }

  private displayLoading() {
    return <div>{this._loadingMessage}</div>
  }

  private renderUnauthorizedPage() {
    return (
        <div className="page-container">
          <div className="page danger">
            <div className="page-content">
              <span className="toast-item-image toast-item-image-alert"/>
              <div className="message">
                {this._unauthorizedMessage}
              </div>
            </div>
          </div>
        </div>
    );
  }
}

export const mapStateToProps = ({ privateRouteReducer }: any) => ({
  authenticated: privateRouteReducer.authenticated,
  privileges: privateRouteReducer.privileges,
  loading: privateRouteReducer.loading
});

const mapDispatchToProps = ({
  getPrivileges
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

// @ts-ignore
export default connect(mapStateToProps, mapDispatchToProps)(PrivateRoute);
