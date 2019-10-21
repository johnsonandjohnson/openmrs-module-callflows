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
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Accordion } from '@openmrs/react-components';
import { Button } from 'react-bootstrap';

import AddButton from './AddButton';

export class Renderers extends React.Component {

  // constructor(props) {
  //   super(props);
  //   this.handleAdd = this.handleAdd.bind(this);
  //   this.newEntry = null;
  // }

  handleAdd = () => {
    return null;
  }
  
// render() {
//   return (
//     <h1>ABCDE</h1>
//   );
// }

  // render() {
  //   return( 
  //     <div>
  //        <h1>AAAAAAAAAAAAAA</h1>
  //     </div>
  //   );
   
  render () {
    return (
      <div className="body-wrapper">
       <h1>TEST TEXT</h1>
      </div>
    );
  

  // render() {
  //   const buttonLabel = 'Add Renderer';
  //   const title = 'Renderers';
  //   return (
  //     <div className="body-wrapper">
  //       <div className="row">
  //         <div className="col-md-12 col-xs-12">
  //           <h2>{title}</h2>
  //         </div>
  //       </div>
  //       <div className="row">
  //         <AddButton handleAdd={this.handleAdd} txt={buttonLabel} />
  //       </div>
  //       <div className="panel-body">
  //         <Accordion title="test" border="true">
  //           <div>form</div>
  //         </Accordion>
  //       </div>
  //       <Button className="btn cancel btn-xs" >CANCEL</Button>
  //       <Button className="btn confirm btn-xs">SAVE</Button>
  //     </div>
  //   );
  // }
  }
}

// export default connect()
// (Renderers);