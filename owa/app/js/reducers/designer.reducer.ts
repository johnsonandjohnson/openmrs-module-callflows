import axiosInstance from '../config/axios';
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';
import ConfigFormData from '../components/ConfigForm/ConfigFormData';
import { IFlow, defaultValue } from '../shared/model/flow.model';

export const ACTION_TYPES = {
  RESET: 'designerReducer/RESET',
  FETCH_CONFIGS: 'designerReducer/FETCH_CONFIGS',
  FETCH_FLOWS: 'designerReducer/FETCH_FLOWS',
  FETCH_FLOW: 'designerReducer/FETCH_FLOW'
};

const initialState = {
  configForms: [] as ReadonlyArray<any>,
  showModal: false,
  toDeleteId: null,
  pages: 0,
  loading: false,
  data: [],
  flow: defaultValue as unknown as IFlow
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
        data: action.payload.data.results
      }
    case REQUEST(ACTION_TYPES.FETCH_FLOW):
      console.log('Fetching flows..');
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.FETCH_FLOW):
      console.log('Fetching flow failure');
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.FETCH_FLOW):
      console.log('Fetching flows success');
      console.log(action.payload.data.results);
      return {
        ...state,
        flow: action.payload.data.results[0]
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

export const getFlows = (filters: any = {}) => async (dispatch) => {
  const term = filters.flowName ? filters.flowName : '';
  const requestUrl = callflowsPath + `/flows?lookup=By+Name&term=${term}`;
  await dispatch({
    type: ACTION_TYPES.FETCH_FLOWS,
    payload: axiosInstance.get(requestUrl)
  });
};

export const getFlow = (flowName: string) => async (dispatch) => {
  //currently there is no endpoint for fetching one instance
  const requestUrl = callflowsPath + `/flows?lookup=By+Name&term=${flowName}`;
  await dispatch({
    type: ACTION_TYPES.FETCH_FLOW,
    payload: axiosInstance.get(requestUrl)
  });
};
