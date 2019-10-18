import axiosInstance from '../config/axios';
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';

export const ACTION_TYPES = {
  RESET: 'providersReducer/RESET',
  FETCH_CONFIGS: 'providersReducer/FETCH_CONFIGS',
  POST_CONFIG: 'providersReducer/POST_CONFIG'
};

const initialState = {
  configs: []
};

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.POST_CONFIG):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.POST_CONFIG):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.POST_CONFIG):
      return {
        ...state,
        configs: action.payload.data
      };
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
        configs: action.payload.data
      };
    case ACTION_TYPES.RESET: {
      return {
        ...state,
        configs: []
      };
    }
    default:
      return state;
  }
};

const callflowsPath = 'ws/callflows';

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

export const postConfig = () => async (dispatch) => {
  const requestUrl = callflowsPath + '/configs';
  const data = [
    {
    name: 'test1',
    outgoingCallMethod: 'GET',
    outgoingCallPostHeadersMap: {},
    outgoingCallPostParams: '',
    outgoingCallUriTemplate: '',
    servicesMap: {},
    outboundCallLimit: 0,
    outboundCallRetryAttempts: 0,
    outboundCallRetrySeconds: 0,
    callAllowed: true,
    testUsersMap: {},
    hasAuthRequired: false
  }
]; // ToDo add real data data
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
};
