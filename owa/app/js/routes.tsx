/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import React from 'react';
import { Switch } from 'react-router-dom';
import { Header } from '@openmrs/react-components';
import PrivateRoute from './components/private-route/private-route';

import App from './components/app';
import Providers from './components/Providers';
import Renderers from './components/Renderers';
import BreadCrumb from './components/bread-crumb';
import Designer from './components/designer/index';
import { CALLFLOWS_PRIVILEGE } from "./config/privileges";
import Customize from './components/customize/customize'
import { initializeLocalizationWrapper } from './components/localization-wrapper/localization-wrapper';
import messagesEN from "./translations/en.json";

initializeLocalizationWrapper({
  en: messagesEN,
});

export default (store) => (
  <div>
    <Customize />
    <Header />
    <BreadCrumb />
    <Switch>
      <PrivateRoute path="/providers" component={Providers} requiredPrivilege={CALLFLOWS_PRIVILEGE} />
      <PrivateRoute path="/renderers" component={Renderers} requiredPrivilege={CALLFLOWS_PRIVILEGE} />
      <PrivateRoute path="/designer" component={Designer} requiredPrivilege={CALLFLOWS_PRIVILEGE} />
      <PrivateRoute exact path="/" component={App} requiredPrivilege={CALLFLOWS_PRIVILEGE} />
    </Switch>
  </div>);
