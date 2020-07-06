/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React, { ReactFragment } from 'react';
import { connect } from 'react-redux';
import { UnregisterCallback } from 'history';
import { Link, withRouter, RouteComponentProps } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import UrlPattern from 'url-pattern';
import { IRootState } from '../../reducers';

import './bread-crumb.scss';
import * as Msg from '../../shared/utils/messages';

export const DESIGNER_NEW_FLOW_ROUTE = '#/designer/new';
const DESIGNER_NEW_FLOW_PATTERN = new UrlPattern('/designer/new');
const DESIGNER_ROUTE = '/designer';
const DESIGNER_PATTERN = new UrlPattern('/designer*');
const PROVIDER_PATTERN = new UrlPattern('/providers*');
const RENDERERS_PATTERN = new UrlPattern('/renderers*');
const MODULE_ROUTE = '/';
const OMRS_ROUTE = '../../';
const SYSTEM_ADMINISTRATION_ROUTE = `${OMRS_ROUTE}coreapps/systemadministration/systemAdministration.page`;

interface IBreadCrumbProps extends DispatchProps, StateProps, RouteComponentProps {
};

interface IBreadCrumbState {
  current: string
};

class BreadCrumb extends React.PureComponent<IBreadCrumbProps, IBreadCrumbState> {
  unlisten: UnregisterCallback;
  
  constructor(props) {
    super(props);

    const { history } = this.props;
    this.state = {
      current: history.location.pathname
    };
  }

  componentDidMount = () => {
    const { history } = this.props;
    this.unlisten = history.listen((location) => {
      const current = location.pathname;
      this.setState({ current });
    });
  }

  componentWillUnmount = () => {
    this.unlisten();
  }

  renderDelimiter = () => {
    return (
      <span className="breadcrumb-link-item breadcrumb-delimiter">
        <FontAwesomeIcon size="sm" icon={['fas', 'chevron-right']} />
      </span>);
  }

  renderHomeCrumb = () => {
    return (
      <a href={OMRS_ROUTE} className="breadcrumb-link-item home-crumb">
        <FontAwesomeIcon icon={['fas', 'home']} />
      </a>);
  }

  renderCrumb = (link: string, txt: string, isAbsolute?: boolean) => {
    if (isAbsolute) {
      return (
        <a href={link} className="breadcrumb-link-item" >{txt}</a>
      );
    } else {
      return <Link to={link} className="breadcrumb-link-item">{txt}</Link>;
    }
  }

  renderLastCrumb = (txt: string) => {
    return <span className="breadcrumb-last-item">{txt}</span>;
  }

  renderCrumbs = (elements: Array<ReactFragment>) => {
    const delimiter = this.renderDelimiter();

    return (
      <React.Fragment>
        {this.renderHomeCrumb()}
        {elements.map((e, i) =>
          <React.Fragment key={`crumb-${i}`}>
            {delimiter}
            {e}
          </React.Fragment>)}
      </React.Fragment>
    );
  }

  buildPathDynamically = (pattern, path) => {
    return pattern.match(path)._.split('/')
      .filter(e => !!e)
      .map((e, i) => 
          <span key={`${pattern}-${i}`} >
            {this.renderLastCrumb(e)}
          </span>
        
      );
  }

  buildDesignerBreadCrumb = (path) => {
    const designerName = Msg.DESIGNER_NEW_FLOW_BREADCRUMB;
    const designerCrumbs = [
      this.renderCrumb(SYSTEM_ADMINISTRATION_ROUTE, Msg.SYSTEM_ADMINISTRATION_BREADCRUMB, true),
      this.renderCrumb(MODULE_ROUTE, Msg.MODULE_NAME)
    ];

    if (DESIGNER_NEW_FLOW_PATTERN.match(path)) {
      designerCrumbs.push(this.renderCrumb(DESIGNER_ROUTE, designerName));
      designerCrumbs.push(this.renderLastCrumb(Msg.DESIGNER_NEW_FLOW_BREADCRUMB_NEW));
    } else if (DESIGNER_PATTERN.match(path)._) {
      designerCrumbs.push(this.renderCrumb(DESIGNER_ROUTE, designerName));
      designerCrumbs.push(this.buildPathDynamically(DESIGNER_PATTERN, path));
    } else {
      designerCrumbs.push(this.renderLastCrumb(designerName));
    }
    return designerCrumbs;
  }

  getCrumbs = (path: string): Array<ReactFragment> => {
    const providerCrumbs = [
      this.renderCrumb(SYSTEM_ADMINISTRATION_ROUTE, Msg.SYSTEM_ADMINISTRATION_BREADCRUMB, true),
      this.renderCrumb(MODULE_ROUTE, Msg.MODULE_NAME),
      this.renderLastCrumb(Msg.PROVIDERS_BREADCRUMB)
    ];

    const rendererCrumbs = [
      this.renderCrumb(SYSTEM_ADMINISTRATION_ROUTE, Msg.SYSTEM_ADMINISTRATION_BREADCRUMB, true),
      this.renderCrumb(MODULE_ROUTE, Msg.MODULE_NAME),
      this.renderLastCrumb(Msg.RENDERERS_BREADCRUMB)
    ];

    if (!!DESIGNER_PATTERN.match(path.toLowerCase())) {
      return this.buildDesignerBreadCrumb(path);
    } else if (!!PROVIDER_PATTERN.match(path.toLowerCase())) {
      return providerCrumbs;
    } else if (!!RENDERERS_PATTERN.match(path.toLowerCase())) {
      return rendererCrumbs;
    } else {
      return [
        this.renderCrumb(SYSTEM_ADMINISTRATION_ROUTE, Msg.SYSTEM_ADMINISTRATION_BREADCRUMB, true),
        this.renderLastCrumb(Msg.MODULE_NAME)
      ];
    }
  }

  buildBreadCrumb = () => {
    const { current } = this.state;
    return (
      <div id="breadcrumbs" className="breadcrumb">
        {this.renderCrumbs(this.getCrumbs(current))}
      </div>
    );
  }

  render = () => {
    return this.buildBreadCrumb();
  }
}

const mapStateToProps = ({ }: IRootState) => ({
});

const mapDispatchToProps = ({
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default withRouter(connect(
  mapStateToProps,
  mapDispatchToProps
)(BreadCrumb));

