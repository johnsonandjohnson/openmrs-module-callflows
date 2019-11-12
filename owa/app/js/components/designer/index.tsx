
import React from 'react';
import { Route, Switch } from 'react-router-dom';
import Designer from './designer';
import DesignerFlow from './designer-flow';

const Routes = ({ match }) => (
  <>
    <Switch>
      <Route exact path={`${match.url}`} component={Designer} />
      <Route exact path={`${match.url}/:flowName`} component={DesignerFlow} />
    </Switch>
  </>
);

export default Routes;