/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import { combineReducers } from 'redux';
import { reducers as openmrs } from '@openmrs/react-components';

import designerReducer, { DesignerState } from './designer.reducer';
import providersReducer, { ProvidersState } from './providers.reducer';
import renderersReducer, { RenderersState } from './renderersReducer';
import privateRouteReducer, { PrivateRouteState }
  from '@bit/soldevelo-omrs.cfl-components.private-route/private-route.reducer';
import customizeReducer, { CustomizeState } 
  from '@bit/soldevelo-omrs.cfl-components.customize/customize.reducer';

export interface IRootState {
  readonly designerReducer: DesignerState;
  readonly providersReducer: ProvidersState;
  readonly renderersReducer: RenderersState;
  readonly privateRouteReducer: PrivateRouteState;
  readonly openmrs: any;
  readonly customizeReducer: CustomizeState;
}

export default combineReducers({
  openmrs,
  designerReducer,
  providersReducer,
  renderersReducer,
  privateRouteReducer,
  customizeReducer
});
