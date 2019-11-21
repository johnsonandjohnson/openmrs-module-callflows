/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { Link, withRouter } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import UrlPattern from 'url-pattern';

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

class BreadCrumb extends React.Component {
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
      <span className="breadcrumb-link-item">
        <FontAwesomeIcon size="xs" icon={['fas', 'chevron-right']} />
      </span>);
  }

  renderHomeCrumb = () => {
    return (
      <a href={OMRS_ROUTE} className="breadcrumb-link-item">
        <FontAwesomeIcon icon={['fas', 'home']} />
      </a>);
  }

  renderCrumb = (link, txt) => {
    return <Link to={link} className="breadcrumb-link-item">{txt}</Link>;
  }

  renderLastCrumb = (txt) => {
    return <span className="breadcrumb-last-item">{txt}</span>;
  }

  renderCrumbs = elements => {
    const delimiter = this.renderDelimiter();
    const lastElementId = elements.length - 1;
    return (
      <React.Fragment>
        {this.renderHomeCrumb()}
        {elements.map((e, i) =>
          <React.Fragment key={`crumb-${i}`}>
            {e}
            {i !== lastElementId && delimiter}
          </React.Fragment>)}
      </React.Fragment>
    );
  }

  buildPathDynamically = (pattern, path) => {
    return pattern.match(path)._.split('/')
      .filter(e => !!e)
      .map((e) => {
        return (
          <span>
            {this.renderLastCrumb(e)}
          </span>
        );
      });
  }

  buildDesignerBreadCrumb = (path) => {
    const designerName = Msg.DESIGNER_NEW_FLOW_BREADCRUMB;
    const designerCrumbs = [
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
    return (
      <div className="breadcrumb">
        {this.renderCrumbs(designerCrumbs)}
      </div>
    );
  }

  buildBreadCrumb = () => {
    const { current } = this.state;

    const providerCrumbs = [
      this.renderCrumb(MODULE_ROUTE, Msg.MODULE_NAME),
      this.renderLastCrumb('Providers')
    ];

    const rendererCrumbs = [
      this.renderCrumb(MODULE_ROUTE, Msg.MODULE_NAME),
      this.renderLastCrumb('Renderers')
    ];

    if (!!DESIGNER_PATTERN.match(current.toLowerCase())) {
      return this.buildDesignerBreadCrumb(current);
    } else if (!!PROVIDER_PATTERN.match(current.toLowerCase())) {
      return (
        <div className="breadcrumb">
          {this.renderCrumbs(providerCrumbs)}
        </div>
      );
    } else if (!!RENDERERS_PATTERN.match(current.toLowerCase())) {
      return (
        <div className="breadcrumb">
          {this.renderCrumbs(rendererCrumbs)}
        </div>
      );
    } else {
      return (
        <div className="breadcrumb">
          {this.renderCrumbs([this.renderLastCrumb(Msg.MODULE_NAME)])}
        </div>
      );
    }
  }

  render = () => {
    return this.buildBreadCrumb();
  }
}

BreadCrumb.propTypes = {
  history: PropTypes.shape({}).isRequired
};

export default withRouter(connect()(BreadCrumb));
