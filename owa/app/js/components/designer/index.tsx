
import React from 'react';
import { Route, Switch } from 'react-router-dom';
import Designer from './designer';
import DesignerFlow from './designer-flow';

const Routes = ({ match }) => (
  <>
    <Switch>
      <Route exact path={`${match.url}/new`} component={DesignerFlow} />
      <Route exact path={`${match.url}/edit/:flowName`} component={DesignerFlow} />
      <Route exact path={`${match.url}`} component={Designer} />
    </Switch>
  </>
);

export default Routes;
