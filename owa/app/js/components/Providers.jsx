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
  Col,
  Row,
  Button
} from 'react-bootstrap';
import { Accordion } from '@openmrs/react-components';
import _ from 'lodash';

import AddButton from './add-button';
import {
  reset,
  getConfigs,
  postConfigs,
  updateAllConfigForms,
  updateConfigForm,
  addNewForm,
  removeForm,
  openModal,
  closeModal,
  focus,
  clearFocus
} from '../reducers/providers.reducer';
import RemoveButton from './RemoveButton';
import ConfigForm from './config-form';
import OpenMRSModal from './OpenMRSModal';
import * as Yup from "yup";
import * as Default from '../shared/utils/messages';
import { validateForm } from '../shared/utils/validation-util';
import { errorToast } from '../shared/utils/toast-display-util';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import DOMPurify from 'dompurify';

export class Providers extends React.Component {
  validationSchema = Yup.object().shape({
    name: Yup.string()
      .required(getIntl().formatMessage({ id: 'CALLFLOW_FIELD_REQUIRED', defaultMessage: Default.FIELD_REQUIRED }))
      .test('unique check', getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_NAME_IS_NOT_UNIQUE', defaultMessage: Default.CONFIG_FORM_NAME_IS_NOT_UNIQUE })
        , nameToValidate => {
          const { configForms } = this.props;
          return _(configForms)
            .filter(configForm => configForm.config.name === nameToValidate)
            .size() === 1;
        }),
  });

  constructor(props) {
    super(props);
    this.focusRef = null;
  }

  componentDidMount = () => {
    this.props.getConfigs();
    this.focusDiv();
  }

  componentDidUpdate = (prev) => {
    if (prev.configForms.length > this.props.configForms.length) {
      this.props.postConfigs(this.props.configForms);
    }

    if (!!this.focusRef) {
      this.focusDiv();
    }
  }

  handleSubmitConfigs = (event) => {
    event.preventDefault();

    const { updateAllConfigForms, postConfigs, configForms } = this.props;
    const newConfigForms = _.clone(configForms);

    const validationPromises = newConfigForms.map(configFormData => {
      const { config } = configFormData;
      return validateForm(config, this.validationSchema)
        .then(() => {
          config.errors = null;
        })
        .catch((errors) => {
          config.errors = errors;
          return Promise.reject();
        })
    });
    Promise.all(validationPromises)
      .then(() => {
        postConfigs(newConfigForms);
      })
      .catch(() => {
        updateAllConfigForms(newConfigForms);
        errorToast(getIntl().formatMessage({ id: 'CALLFLOW_GENERIC_INVALID_FORM', defaultMessage: Default.GENERIC_INVALID_FORM }));
      })
  }

  handleRemove = (event) => {
    this.props.openModal(event.target.id);
  }

  handleClose = () => {
    this.props.closeModal();
  }

  handleConfirm = () => {
    this.props.removeForm(this.props.toDeleteId, this.props.configForms);
  }

  getOffsetTop = (element) => {
    let offsetTop = 0;
    while (element) {
      offsetTop += element.offsetTop;
      element = element.offsetParent;
    }
    return offsetTop;
  }

  focusDiv = () => {
    if (!_.isEmpty(this.focusRef)) {
      window.scrollTo({ left: 0, top: this.getOffsetTop(this.focusRef), behavior: 'smooth' });
      this.focusRef = null;
      this.props.clearFocus();
    }
  }

  render() {
    const buttonLabel = 'Add Provider';
    const title = 'Providers';
    return (
      <div className="body-wrapper">
        <OpenMRSModal
          deny={this.handleClose}
          confirm={this.handleConfirm}
          show={this.props.showModal}
          title="Delete Provider"
          txt="Are you sure you want to delete this Provider?" />
        <div className="row">
          <div className="col-md-12 col-xs-12">
            <h2>{title}</h2>
          </div>
        </div>
        <div className="panel-body">
          <div className="row">
            <div className="col-md-12 col-xs-12">
              <AddButton
                handleAdd={this.props.addNewForm}
                txt={buttonLabel}
                buttonClass="confirm add-btn" />
            </div>
          </div>
          {this.props.configForms.map(item => (
            <Row key={DOMPurify.sanitize(item.localId)}>
              <Col sm={11}
                className="cfl-col-field-left">
                <Accordion title={DOMPurify.sanitize(item.config.name)}
                  border={true}
                  open={DOMPurify.sanitize(item.isOpenOnInit)}>
                  <div ref={(div) => {
                    if (item.localId === this.props.focusEntry) {
                      this.focusRef = div;
                    }
                  }}>
                    <ConfigForm
                      config={DOMPurify.sanitize(item.config)}
                      isOpenOnInit={DOMPurify.sanitize(item.isOpenOnInit)}
                      localId={DOMPurify.sanitize(item.localId)}
                      updateValues={this.props.updateConfigForm}
                      validationSchema={this.validationSchema} />
                  </div>
                </Accordion>
              </Col>
              <Col sm={1}
                className="cfl-col-field">
                <RemoveButton
                  buttonClass="col-remove-button"
                  handleRemove={this.handleRemove}
                  localId={DOMPurify.sanitize(item.localId)}
                  tooltip="Delete Provider" />
              </Col>
            </Row>
          ))}
          <Button className="btn confirm btn-xs"
            disabled={this.props.loading}
            onClick={this.handleSubmitConfigs}>
            {getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_SAVE_BUTTON', defaultMessage: Default.CONFIG_FORM_SAVE_BUTTON })}
          </Button>
        </div>
      </div>
    );
  }
}

export const mapStateToProps = state => ({
  configForms: state.providersReducer.configForms,
  showModal: state.providersReducer.showModal,
  toDeleteId: state.providersReducer.toDeleteId,
  focusEntry: state.providersReducer.focusEntry,
  loading: state.providersReducer.loading
});

const mapDispatchToProps = {
  reset,
  getConfigs,
  postConfigs,
  updateAllConfigForms,
  updateConfigForm,
  addNewForm,
  removeForm,
  openModal,
  closeModal,
  focus,
  clearFocus
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Providers);
