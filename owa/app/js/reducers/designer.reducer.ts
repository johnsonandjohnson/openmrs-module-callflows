import axiosInstance from '../config/axios';
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';
import ConfigFormData from '../components/ConfigForm/ConfigFormData';
import { IFlow } from '../shared/model/flow.model';

export const ACTION_TYPES = {
  RESET: 'designerReducer/RESET',
  FETCH_CONFIGS: 'designerReducer/FETCH_CONFIGS',
  FETCH_FLOWS: 'designerReducer/FETCH_FLOWS'
};

const initialState = {
  configForms: [] as ReadonlyArray<any>,
  showModal: false,
  toDeleteId: null,
  flows: [] as ReadonlyArray<IFlow>
};

export type DesignerState = Readonly<typeof initialState>;

export default (state: DesignerState = initialState, action): DesignerState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_CONFIGS):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.FETCH_CONFIGS):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.FETCH_CONFIGS):
      return {
        ...state,
        configForms: action.payload.data.map((fetched) => {
          return new ConfigFormData(fetched);
        })
      };
    case REQUEST(ACTION_TYPES.FETCH_FLOWS):
      console.log('Fetching flows..');
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.FETCH_FLOWS):
      console.log('Fetching flows failure');
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.FETCH_FLOWS):
      console.log('Fetching flows success');
      console.log(action.payload.data.results);
      return {
        ...state,
        flows: action.payload.data.results
      }
    default:
      return state;
  }
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

const callflowsPath = 'ws/callflows';

export const postConfigs = (configForms) => async (dispatch) => {
  const requestUrl = callflowsPath + '/configs';
  let data = configForms.map((form) => {
    return form.config.getModel();
  });
  await dispatch({
    type: ACTION_TYPES.FETCH_CONFIGS,
    payload: axiosInstance.post(requestUrl, data)
  });
};

export const getConfigs = () => async (dispatch) => {
  const requestUrl = callflowsPath + '/configs';
  await dispatch({
    type: ACTION_TYPES.FETCH_CONFIGS,
    payload: axiosInstance.get(requestUrl)
  });
  console.log('test');
};

export const getFlows = (flowName = '') => async (dispatch) => {
  const requestUrl = callflowsPath + `/flows?lookup=By+Name&term=${flowName}`;
  await dispatch({
    type: ACTION_TYPES.FETCH_FLOWS,
    payload: axiosInstance.get(requestUrl)
  });
};
