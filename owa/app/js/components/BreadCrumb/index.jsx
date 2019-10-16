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

import './bread-crumb.scss';

const OMRS_ROUTE = '../../';
const MODULE_NAME = 'Callflows';

class BreadCrumb extends React.Component {
  constructor(props) {
    super(props);

    const { history } = this.props;
    this.state = {
      current: history.location.pathname.toLowerCase()
    };
  }

  componentDidMount = () => {
    const { history } = this.props;
    this.unlisten = history.listen((location) => {
      const current = location.pathname.toLowerCase();
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

  buildBreadCrumb = () => {
    const { current } = this.state;

    switch (current) {
      default:
        return (
          <div className="breadcrumb">
            {this.renderHomeCrumb()}
            {this.renderDelimiter()}
            {this.renderLastCrumb(MODULE_NAME)}
          </div>);
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