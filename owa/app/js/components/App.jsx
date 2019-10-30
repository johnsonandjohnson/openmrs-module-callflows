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

import Tile from './Tile';
import IconModel from '../shared/model/Icon.model';

class App extends React.Component {
  render() {
    return (
      <div className="body-wrapper">
        <div className="panel-body">
          <Tile name='Providers'
            href='#/providers'
            icons={[new IconModel(['fas', 'phone-alt'], '2x'),
              new IconModel(['fas', 'globe'])]} />
          <Tile name='Renderers' href='#/renderers'
            icons={[new IconModel(['fas', 'random'], '2x') ]} />
        </div>
      </div>);
  }
}

export default connect()(App);