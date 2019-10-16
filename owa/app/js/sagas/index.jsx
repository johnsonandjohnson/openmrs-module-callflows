import { sagas as openmrs } from '@openmrs/react-components';

const sagas = {
  openmrs
};

const initSagas = (sagaMiddleware) => {
  Object.values(sagas).forEach(sagaMiddleware.run.bind(sagaMiddleware));
};

export default initSagas;
